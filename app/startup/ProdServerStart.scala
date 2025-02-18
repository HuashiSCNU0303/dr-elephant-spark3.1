/*
 * Copyright (C) from 2022 The Play Framework Contributors <https://github.com/playframework>, 2011-2021 Lightbend Inc. <https://www.lightbend.com>
 */

package startup

import java.io._
import java.nio.file.FileAlreadyExistsException
import java.nio.file.Files
import java.nio.file.StandardOpenOption

import play.api._

import scala.util.control.NonFatal
import play.core.server._

import com.linkedin.drelephant.DrElephant

/**
 * Used to start servers in 'prod' mode, the mode that is
 * used in production. The application is loaded and started
 * immediately.
 */
object ProdServerStart {

  /**
   * Start a prod mode server from the command line.
   */
  def main(args: Array[String]): Unit = {
    val process = new RealServerProcess(args)
    start(process)
  }

  /**
   * Starts a Play server and application for the given process. The settings
   * for the server are based on values passed on the command line and in
   * various system properties. Crash out by exiting the given process if there
   * are any problems.
   * @param process The process (real or abstract) to use for starting the
   * server.
   */
  def start(process: ServerProcess): ReloadableServer = {
    try {

      // Read settings
      val config: ServerConfig = readServerConfigSettings(process)

      // Create a PID file before we do any real work
      val pidFile = createPidFile(process, config.configuration)
      
      var _drElephantThread: Thread = null

      try {
        // Start the application
        val application: Application = {
          val environment = Environment(config.rootDir, process.classLoader, Mode.Prod)
          val context     = ApplicationLoader.createContext(environment)
          val loader      = ApplicationLoader(context)
          loader.load(context)
        }
        Play.start(application)

        // Start the server
        val serverProvider: ServerProvider = ServerProvider.fromConfiguration(process.classLoader, config.configuration)
        val server                         = serverProvider.createServer(config, application)
        process.addShutdownHook {
          if (_drElephantThread != null) {
            DrElephant.getInstance.kill()
            _drElephantThread.interrupt()
            try _drElephantThread.join()
            catch {
              case e: InterruptedException =>
                Thread.currentThread.interrupt()
            }
          }
        
          server.stop()
          pidFile.foreach(_.delete())
          assert(!pidFile.exists(_.exists), "PID file should not exist!")
        }
        
        println("Application starts...")
        _drElephantThread = new Thread(DrElephant.getInstance)
        _drElephantThread.start()
        
        server
      } catch {
        case NonFatal(e) =>
          // Clean up pidfile when the server fails to start
          pidFile.foreach(_.delete())
          throw e
      }
    } catch {
      case ServerStartException(message, cause) =>
        process.exit(message, cause)
      case NonFatal(e) =>
        process.exit("Oops, cannot start the server.", cause = Some(e))
    }
  }

  /**
   * Read the server config from the current process's command
   * line args and system properties.
   */
  def readServerConfigSettings(process: ServerProcess): ServerConfig = {
    val configuration: Configuration = {
      val rootDirArg: Option[File] = process.args.headOption.map(new File(_))
      val rootDirConfig            = rootDirArg.fold(Map.empty[String, String])(dir => ServerConfig.rootDirConfig(dir))
      Configuration.load(process.classLoader, process.properties, rootDirConfig, true)
    }

    val rootDir: File = {
      val path = configuration
        .getOptional[String]("play.server.dir")
        .getOrElse(throw ServerStartException("No root server path supplied"))
      val file = new File(path)
      if (!(file.exists && file.isDirectory)) {
        throw ServerStartException(s"Bad root server path: $path")
      }
      file
    }

    def parsePort(portType: String): Option[Int] = {
      configuration.getOptional[String](s"play.server.${portType}.port").flatMap {
        case "disabled" => None
        case str =>
          val i = try Integer.parseInt(str)
          catch {
            case _: NumberFormatException => throw ServerStartException(s"Invalid ${portType.toUpperCase} port: $str")
          }
          Some(i)
      }
    }

    val httpPort  = parsePort("http")
    val httpsPort = parsePort("https")
    if (httpPort.orElse(httpsPort).isEmpty) throw ServerStartException("Must provide either an HTTP or HTTPS port")

    val address = configuration.getOptional[String]("play.server.http.address").getOrElse("0.0.0.0")

    ServerConfig(
      rootDir = rootDir,
      port = httpPort,
      sslPort = httpsPort,
      address = address,
      mode = Mode.Prod,
      properties = process.properties,
      configuration = configuration
    )
  }

  /**
   * Create a pid file for the current process.
   */
  def createPidFile(process: ServerProcess, configuration: Configuration): Option[File] = {
    val pidFilePath = configuration
      .getOptional[String]("play.server.pidfile.path")
      .getOrElse(throw ServerStartException("Pid file path not configured"))
    if (pidFilePath == "/dev/null") None
    else {
      val pidFile = new File(pidFilePath).getAbsoluteFile
      val pid     = process.pid.getOrElse(throw ServerStartException("Couldn't determine current process's pid"))
      val out = try Files.newOutputStream(pidFile.toPath, StandardOpenOption.CREATE_NEW)
      catch {
        case _: FileAlreadyExistsException =>
          throw ServerStartException(s"This application is already running (Or delete ${pidFile.getPath} file).")
      }
      try out.write(pid.getBytes)
      finally out.close()

      Some(pidFile)
    }
  }

}
