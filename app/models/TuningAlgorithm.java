/*
 * Copyright 2016 LinkedIn Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package models;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import io.ebean.annotation.UpdatedTimestamp;

import io.ebean.Model;
import io.ebean.Finder;


@Entity
@Table(name = "tuning_algorithm")
public class TuningAlgorithm extends Model {

  private static final long serialVersionUID = 1L;

  public enum JobType {
    PIG, HIVE, SPARK
  }

  public enum OptimizationAlgo {
    PSO
  }

  public enum OptimizationMetric {
    RESOURCE, EXECUTION_TIME
  }

  public static class TABLE {
    public static final String TABLE_NAME = "tuning_algorithm";
    public static final String id = "id";
    public static final String jobType = "jobType";
    public static final String optimizationAlgo = "optimizationAlgo";
    public static final String optimizationAlgoVersion = "optimizationAlgoVersion";
    public static final String optimizationMetric = "optimizationMetric";
    public static final String createdTs = "createdTs";
    public static final String updatedTs = "updatedTs";
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Integer id;

  @Enumerated(EnumType.STRING)
  public JobType jobType;

  @Enumerated(EnumType.STRING)
  public OptimizationAlgo optimizationAlgo;

  public Integer optimizationAlgoVersion;

  @Enumerated(EnumType.STRING)
  public OptimizationMetric optimizationMetric;

  @Column(nullable = false)
  public Timestamp createdTs;

  @Column(nullable = false)
  @UpdatedTimestamp
  public Timestamp updatedTs;

  public static Finder<Integer, TuningAlgorithm> find =
      new Finder<Integer, TuningAlgorithm>(TuningAlgorithm.class);

  @Override
  public void save() {
    this.updatedTs = new Timestamp(System.currentTimeMillis());
    super.save();
  }

  @Override
  public void update() {
    this.updatedTs = new Timestamp(System.currentTimeMillis());
    super.update();
  }
}
