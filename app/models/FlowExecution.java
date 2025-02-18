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
@Table(name = "flow_execution")
public class FlowExecution extends Model {

  private static final long serialVersionUID = -530850411828978454L;

  public static class TABLE {
    public static final String TABLE_NAME = "flow_execution";
    public static final String id = "id";
    public static final String flowExecId = "flowExecId";
    public static final String flowExecUrl = "flowExecUrl";
    public static final String flowDefinition = "flowDefinition";
    public static final String createdTs = "createdTs";
    public static final String updatedTs = "updatedTs";
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Integer id;

  @Column(nullable = false)
  public String flowExecId;

  @Column(nullable = false)
  public String flowExecUrl;

  @Column(nullable = false)
  @ManyToOne(cascade = CascadeType.ALL)
  @JoinTable(name = "flow_definition", joinColumns = {@JoinColumn(name = "flow_definition_id", referencedColumnName = "id")})
  public FlowDefinition flowDefinition;

  @Column(nullable = false)
  public Timestamp createdTs;

  @Column(nullable = false)
  @UpdatedTimestamp
  public Timestamp updatedTs;

  public static Finder<Integer, FlowExecution> find =
      new Finder<Integer, FlowExecution>(FlowExecution.class);

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
