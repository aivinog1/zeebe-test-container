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
package io.zeebe.containers.exporter;

import java.util.Objects;

public class ZeebeContainerConfig {
  private String socketPath;

  public ZeebeContainerConfig() {}

  public ZeebeContainerConfig(final String socketPath) {
    this.socketPath = socketPath;
  }

  public String getSocketPath() {
    return socketPath;
  }

  public void setSocketPath(final String socketPath) {
    this.socketPath = socketPath;
  }

  @Override
  public int hashCode() {
    return Objects.hash(getSocketPath());
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    final ZeebeContainerConfig that = (ZeebeContainerConfig) o;
    return Objects.equals(getSocketPath(), that.getSocketPath());
  }

  @Override
  public String toString() {
    return "ZeebeContainerConfig{" + "socketPath='" + socketPath + '\'' + '}';
  }
}
