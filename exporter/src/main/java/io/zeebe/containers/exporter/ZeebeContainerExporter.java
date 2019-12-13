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

import io.zeebe.containers.exporter.kryo.KryoProvider;
import io.zeebe.exporter.api.Exporter;
import io.zeebe.exporter.api.context.Context;
import io.zeebe.exporter.api.context.Controller;
import io.zeebe.protocol.record.Record;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SocketChannel;
import org.slf4j.Logger;

public class ZeebeContainerExporter implements Exporter {
  private final SocketProvider socketProvider;
  private final RecordWriter.Provider recordWriterProvider;

  private ZeebeContainerConfig config;
  private Controller controller;
  private String id;
  private Logger logger;
  private RecordWriter recordWriter;
  private SocketChannel socket;

  public ZeebeContainerExporter(
      final SocketProvider socketProvider, final RecordWriter.Provider recordWriterProvider) {
    this.socketProvider = socketProvider;
    this.recordWriterProvider = recordWriterProvider;
  }

  public ZeebeContainerExporter() {
    this(new UnixSocketProvider(), new KryoProvider());
  }

  @Override
  public void configure(final Context context) throws Exception {
    config = context.getConfiguration().instantiate(ZeebeContainerConfig.class);
    logger = context.getLogger();
    id = context.getConfiguration().getId();

    logger.debug("{} - Configured exporter: {}", id, this);
  }

  @Override
  public void open(final Controller controller) {
    this.controller = controller;

    try {
      socket = socketProvider.provide(config);
      recordWriter = recordWriterProvider.newWriter(socket);
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }

    logger.debug("{} - Opened exporter", id);
  }

  @Override
  public void close() {
    if (socket != null) {
      try {
        socket.close();
      } catch (final ClosedChannelException ignored) {
        // can be safely ignored
      } catch (final IOException e) {
        logger.error("{} - Failed to close output socket channel", id, e);
      }
    }
  }

  @Override
  public void export(final Record record) {
    try {
      recordWriter.write(record);
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }

    logger.trace("{} - Exported record {}", id, record);
    controller.updateLastExportedRecordPosition(record.getPosition());
  }

  @Override
  public String toString() {
    return "ZeebeContainerExporter{"
        + ", config="
        + config
        + ", id='"
        + id
        + "', socketProvider='"
        + socketProvider.getClass().getName()
        + "', recordWriterProvider='"
        + recordWriterProvider.getClass().getName()
        + '\''
        + '}';
  }
}
