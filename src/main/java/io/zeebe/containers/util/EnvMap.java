/*
 * Copyright © 2019 camunda services GmbH (info@camunda.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.zeebe.containers.util;

import io.zeebe.containers.EnvVar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class EnvMap {
  private final Map<String, String> map;

  public EnvMap() {
    this.map = new HashMap<>();
  }

  public Map<String, String> getMap() {
    return map;
  }

  public void put(EnvVar envVar, String value) {
    map.put(envVar.getVariableName(), value);
  }

  public void put(EnvVar envVar, boolean value) {
    map.put(envVar.getVariableName(), String.valueOf(value));
  }

  public void put(EnvVar envVar, int value) {
    map.put(envVar.getVariableName(), String.valueOf(value));
  }

  public void put(EnvVar envVar, Collection<String> value) {
    map.put(envVar.getVariableName(), String.join(",", value));
  }
}
