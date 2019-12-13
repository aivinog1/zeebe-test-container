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

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import io.zeebe.containers.exporter.RecordWriter;
import io.zeebe.protocol.record.Record;
import java.io.Closeable;

class KryoRecordWriter implements RecordWriter, Closeable {
  private final Kryo kryo;
  private final Output output;

  KryoRecordWriter(final Kryo kryo, final Output output) {
    this.kryo = kryo;
    this.output = output;
  }

  KryoRecordWriter() {
    this(new Kryo(), new Output(32 * 1024 * 1024));
  }

  @Override
  public void write(final Record record) {
    kryo.writeClassAndObject(output, record);
    output.flush();
  }

  @Override
  public void close() {
    output.close();
  }
}
