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

import io.zeebe.containers.exporter.transport.ProtocolServer;
import io.zeebe.exporter.api.Exporter;
import io.zeebe.exporter.api.context.Context;
import io.zeebe.exporter.api.context.Controller;
import io.zeebe.protocol.record.Record;
import io.zeebe.protocol.record.RecordValue;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZeebeContainerExporter implements Exporter {
  private final ProtocolServer.Provider serverProvider;

  private ZeebeContainerConfig config;
  private Controller controller;
  private String id;
  private Logger logger;
  private ProtocolServer server;
  private BlockingQueue<Record<RecordValue>> recordQueue;

  public ZeebeContainerExporter(final ProtocolServer.Provider serverProvider) {
    this.serverProvider = serverProvider;
  }

  public ZeebeContainerExporter() {
    this(ProtocolServer.Provider.tcpProvider());
  }

  @Override
  public void configure(final Context context) {
    config = context.getConfiguration().instantiate(ZeebeContainerConfig.class);
    logger = context.getLogger();
    id = context.getConfiguration().getId();
    recordQueue = new ArrayBlockingQueue<>(config.getMaxRecordQueueSize());
    server =
        serverProvider.provideProtocolServer(
            id,
            recordQueue,
            controller,
            config,
            LoggerFactory.getLogger(logger.getName() + ".server"));

    logger.debug("{} - Configured exporter: {}", id, this);
  }

  @Override
  public void open(final Controller controller) {
    this.controller = controller;

    try {
      server.open(config.getPort());
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }

    logger.debug("{} - Opened exporter", id);
  }

  @Override
  public void close() {
    if (server != null) {
      server.close(config.getServerShutdownPeriod());
      server = null;
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public void export(final Record record) {
    if (recordQueue.offer(record)) {
      logger.trace("{} - Exported record {}", id, record);
    } else {
      throw new RecordQueueOverflowException(config.getMaxRecordQueueSize());
    }
  }

  @Override
  public String toString() {
    return "ZeebeContainerExporter{"
        + ", config="
        + config
        + ", id='"
        + id
        + "', serverProvider='"
        + serverProvider.getClass().getName()
        + '\''
        + '}';
  }
}
