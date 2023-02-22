# Dr. Elephant

This repository adds support for Spark 3.1 for **Dr. Elephant**, updates the play framework to 2.6.19.

It works well on Spark applications with Scala 2.12 and Spark 3.1.1, but isn't fully tested on Mapreduce applications.

Detailed usage of **Dr. Elephant**: https://github.com/linkedin/dr-elephant


## Setup

Basically, you should follow the instructions here: [Click here](https://github.com/linkedin/dr-elephant/wiki/Quick-Setup-Instructions-(Must-Read))

Some extra steps:
1. It uses sbt instead of play or activator command, so you must have sbt installed before setup.

2. After successfully compiling the **Dr. Elephant**, you should edit the file `bin/dr-elephant` in the distribution, replace `declare -a app_mainclass=(play.core.server.ProdServerStart)` with `declare -a app_mainclass=(startup.ProdServerStart)`
(That's a naive solution to remove GlobalSettings. The dependency injection and eager binding don't work well. If there is any better solution, please open a pull request)
