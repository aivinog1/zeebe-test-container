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
