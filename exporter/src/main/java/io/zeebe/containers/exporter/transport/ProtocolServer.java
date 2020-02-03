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
import io.zeebe.containers.exporter.ZeebeContainerConfig;
import io.zeebe.exporter.api.context.Controller;
import io.zeebe.protocol.record.Record;
import io.zeebe.protocol.record.RecordValue;
import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.BlockingQueue;
import org.slf4j.Logger;

public interface ProtocolServer {
  void open(final int port) throws IOException;

  void close(@NonNull final Duration shutdownPeriod);

  @FunctionalInterface
  interface Provider {
    @NonNull
    ProtocolServer provideProtocolServer(
        @NonNull final String name,
        @NonNull final BlockingQueue<Record<RecordValue>> transferQueue,
        @NonNull final Controller controller,
        @NonNull final ZeebeContainerConfig config,
        @NonNull final Logger logger);

    static Provider tcpProvider() {
      return new TcpProtocolServerProvider();
    }
  }
}
