package io.zeebe.containers.qa;

import io.zeebe.containers.ZeebeBrokerContainer;
import io.zeebe.containers.exporter.ZeebeContainerExporter;
import io.zeebe.exporter.api.Exporter;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.testcontainers.utility.MountableFile;

@Timeout(5_000)
@Execution(ExecutionMode.CONCURRENT)
class ZeebeContainerExporterTest {
  private static final ZeebeBrokerContainer BROKER =
      new ZeebeBrokerContainer(Exporter.class.getPackage().getImplementationVersion())
          .withCopyFileToContainer(
              MountableFile.forHostPath(
                  ZeebeContainerExporter.class
                      .getProtectionDomain()
                      .getCodeSource()
                      .getLocation()
                      .getPath()))
          .withConfigurationResource("zeebe.cfg.toml");

  @BeforeAll
  void setUp() {
    BROKER.start();
  }

  @AfterAll
  void tearDown() {
    BROKER.stop();
  }

  @Test
  void shouldExport() {}
}
