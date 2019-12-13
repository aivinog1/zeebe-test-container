package io.zeebe.containers.exporter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

@Timeout(5_000)
@Execution(ExecutionMode.CONCURRENT)
class ZeebeContainerExporterTest {
  @Test
  void shouldExportRecords() {

  }
}
