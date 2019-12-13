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
package io.zeebe.containers.exporter.kryo;

import static org.assertj.core.api.Assertions.assertThat;

import io.zeebe.containers.exporter.RecordReader;
import io.zeebe.containers.exporter.RecordWriter;
import io.zeebe.protocol.impl.record.CopiedRecord;
import io.zeebe.protocol.impl.record.RecordMetadata;
import io.zeebe.protocol.impl.record.value.message.MessageRecord;
import io.zeebe.protocol.record.Record;
import io.zeebe.protocol.record.RecordType;
import io.zeebe.protocol.record.intent.MessageIntent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.ByteChannel;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

@Timeout(5_000)
@Execution(ExecutionMode.CONCURRENT)
class KryoRecordTest {

  @Test
  void shouldWriteReadRecord(@TempDir final Path temp) throws IOException {
    // given
    final Record record = newRecord();
    final KryoProvider provider = new KryoProvider();
    final Path channelPath = temp.resolve("ipc.bin");
    final RecordWriter writer = provider.newWriter(newChannel(channelPath));
    final RecordReader reader = provider.newReader(newChannel(channelPath));

    // when
    writer.write(record);
    final Record copy = reader.read();

    // then
    assertThat(copy).isEqualToComparingFieldByField(record);
  }

  private Record<?> newRecord() {
    final MessageRecord value =
        new MessageRecord()
            .setMessageId("id")
            .setCorrelationKey("key")
            .setName("name")
            .setTimeToLive(1L);
    final RecordMetadata metadata =
        new RecordMetadata().intent(MessageIntent.PUBLISH).recordType(RecordType.COMMAND);
    return new CopiedRecord<>(value, metadata, 1L, 2, 3L, 4L, 5L);
  }

  private ByteChannel newChannel(final Path path) throws FileNotFoundException {
    return new RandomAccessFile(path.toFile(), "rw").getChannel(); // NOSONAR
  }
}
