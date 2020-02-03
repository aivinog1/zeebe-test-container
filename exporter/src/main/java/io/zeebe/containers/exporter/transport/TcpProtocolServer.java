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
package io.zeebe.containers.exporter.transport;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.zeebe.containers.exporter.protocol.ImmutableRecord;
import io.zeebe.exporter.api.context.Controller;
import io.zeebe.protocol.record.Record;
import io.zeebe.protocol.record.RecordValue;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;

final class TcpProtocolServer implements ProtocolServer {

  private final Logger logger;
  private final ExecutorService serverExecutor;
  private final BlockingQueue<Record<RecordValue>> transferQueue;
  private final List<Record<RecordValue>> responseBatch;
  private final int maxResponseBatchSize;
  private final Controller controller;

  private ServerSocket socket;

  TcpProtocolServer(
      @NonNull final String name,
      @NonNull final BlockingQueue<Record<RecordValue>> transferQueue,
      @NonNull final Controller controller,
      final int maxResponseBatchSize,
      @NonNull final Logger logger) {
    this.logger = logger;
    this.transferQueue = transferQueue;
    this.controller = controller;

    this.maxResponseBatchSize = maxResponseBatchSize;
    this.responseBatch = new ArrayList<>(maxResponseBatchSize);
    this.serverExecutor = Executors.newSingleThreadExecutor(r -> new Thread(r, name + "-server"));
  }

  @Override
  public void open(final int port) throws IOException {
    socket = new ServerSocket(port);
    serverExecutor.submit(this::receiveRequests);
  }

  @Override
  public void close(@NonNull final Duration shutdownPeriod) {
    if (socket != null) {
      try {
        socket.close();
      } catch (final IOException e) {
        logger.error("Failed to close server socket", e);
      }

      socket = null;
    }

    serverExecutor.shutdownNow();
    try {
      serverExecutor.awaitTermination(shutdownPeriod.toNanos(), TimeUnit.NANOSECONDS);
    } catch (final InterruptedException e) {
      Thread.currentThread().interrupt();
      logger.warn("Interrupted while awaiting serverExecutor shutdown, probably safe to ignore...");
    }
  }

  private void receiveRequests() {
    while (!Thread.currentThread().isInterrupted()) {
      try (final Socket requestSocket = socket.accept()) {
        transferQueue.drainTo(responseBatch, maxResponseBatchSize);

        try (BufferedOutputStream stream =
                new BufferedOutputStream(requestSocket.getOutputStream());
            ObjectOutputStream output = new ObjectOutputStream(stream)) {
          writeResponse(output);
        }

        responseBatch.clear();
      } catch (final IOException e) {
        logger.error("Failed to accept new request", e);
      }
    }
  }

  private void writeResponse(final ObjectOutputStream output) throws IOException {
    output.writeInt(responseBatch.size());
    for (final Record<RecordValue> record : responseBatch) {
      writeRecord(output, record);
      controller.updateLastExportedRecordPosition(record.getPosition());
    }
  }

  private void writeRecord(final ObjectOutputStream output, final Record<RecordValue> record)
      throws IOException {
    final ImmutableRecord<RecordValue> protocolRecord =
        ImmutableRecord.builder().from(record).build();
    output.writeObject(protocolRecord);
  }
}
