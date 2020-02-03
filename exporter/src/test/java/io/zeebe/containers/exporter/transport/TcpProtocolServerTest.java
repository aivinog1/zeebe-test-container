package io.zeebe.containers.exporter.transport;

import io.zeebe.exporter.api.context.Controller;
import io.zeebe.protocol.record.Record;
import io.zeebe.protocol.record.RecordValue;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.slf4j.helpers.NOPLogger;

@Timeout(5_000)
@Execution(ExecutionMode.CONCURRENT)
final class TcpProtocolServerTest {
  private final int port = findNextAvailablePort();
  private final String name = "test";
  private final int queueSize = 10;
  private final int maxBatchSize = 10;
  private final BlockingQueue<Record<RecordValue>> recordQueue =
      new ArrayBlockingQueue<>(queueSize);
  private final TestController controller = new TestController();
  private final TcpProtocolServer server =
      new TcpProtocolServer(name, recordQueue, controller, maxBatchSize, NOPLogger.NOP_LOGGER);

  @Test
  void shouldStartServerForPort() throws IOException {
    // given
    final int port = findNextAvailablePort();

    // when
    server.open(port);

    // then
  }

  private int findNextAvailablePort() {
    return new InetSocketAddress(0).getPort();
  }

  private static final class TestController implements Controller {
    private volatile long position;

    @Override
    public void updateLastExportedRecordPosition(final long l) {
      this.position = l;
    }

    @Override
    public void scheduleTask(final Duration duration, final Runnable runnable) {
      throw new UnsupportedOperationException();
    }
  }
}
