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

import io.ebean.annotation.UpdatedTimestamp;
import java.sql.Timestamp;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import io.ebean.Model;
import io.ebean.Finder;

@Entity
@Table(name = "job_suggested_param_value")
public class JobSuggestedParamValue extends Model {

  private static final long serialVersionUID = 1L;

  public static class TABLE {
    public static final String TABLE_NAME = "job_suggested_param_value";
    public static final String id = "id";
    public static final String jobSuggestedParamSet = "jobSuggestedParamSet";
    public static final String tuningParameter = "tuningParameter";
    public static final String paramValue = "paramValue";
    public static final String createdTs = "createdTs";
    public static final String updatedTs = "updatedTs";
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Integer id;
  public Double paramValue;

  @ManyToOne(cascade = CascadeType.ALL)
  @JoinTable(name = "job_suggested_param_set", joinColumns = {@JoinColumn(name = "job_suggested_param_set_id", referencedColumnName = "id")})
  public JobSuggestedParamSet jobSuggestedParamSet;

  @ManyToOne(cascade = CascadeType.ALL)
  @JoinTable(name = "tuning_parameter", joinColumns = {@JoinColumn(name = "tuning_parameter_id", referencedColumnName = "id")})
  public TuningParameter tuningParameter;

  @Column(nullable = false)
  public Timestamp createdTs;

  @Column(nullable = false)
  @UpdatedTimestamp
  public Timestamp updatedTs;

  public static Finder<Long, JobSuggestedParamValue> find =
      new Finder<Long, JobSuggestedParamValue>(JobSuggestedParamValue.class);

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
