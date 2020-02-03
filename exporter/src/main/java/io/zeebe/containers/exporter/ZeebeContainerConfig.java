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

import io.zeebe.util.DurationUtil;
import java.time.Duration;

public class ZeebeContainerConfig {
  private int port;
  private String serverShutdown;
  private int maxRecordQueueSize;
  private int maxResponseBatchSize;

  public ZeebeContainerConfig() {}

  public ZeebeContainerConfig(
      final int port,
      final String serverShutdown,
      final int maxRecordQueueSize,
      final int maxResponseBatchSize) {
    this.port = port;
    this.serverShutdown = serverShutdown;
    this.maxRecordQueueSize = maxRecordQueueSize;
    this.maxResponseBatchSize = maxResponseBatchSize;
  }

  public int getPort() {
    return port;
  }

  public void setPort(final int port) {
    this.port = port;
  }

  public Duration getServerShutdownPeriod() {
    return DurationUtil.parse(serverShutdown);
  }

  public String getServerShutdown() {
    return serverShutdown;
  }

  public void setServerShutdown(final String serverShutdown) {
    this.serverShutdown = serverShutdown;
  }

  public int getMaxRecordQueueSize() {
    return maxRecordQueueSize;
  }

  public void setMaxRecordQueueSize(final int maxRecordQueueSize) {
    this.maxRecordQueueSize = maxRecordQueueSize;
  }

  public int getMaxResponseBatchSize() {
    return maxResponseBatchSize;
  }

  public void setMaxResponseBatchSize(final int maxResponseBatchSize) {
    this.maxResponseBatchSize = maxResponseBatchSize;
  }
}
