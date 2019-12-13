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
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import io.zeebe.protocol.impl.record.UnifiedRecordValue;
import org.agrona.DirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;

public class UnifiedRecordValueSerializer extends Serializer<UnifiedRecordValue> {
  @Override
  public void write(final Kryo kryo, final Output output, final UnifiedRecordValue value) {
    final int length = value.getLength();
    final byte[] bytes = new byte[length];
    value.write(new UnsafeBuffer(bytes), 0);

    output.writeInt(length);
    output.write(bytes);
  }

  @Override
  public UnifiedRecordValue read(
      final Kryo kryo, final Input input, final Class<? extends UnifiedRecordValue> aClass) {
    final UnifiedRecordValue value = kryo.newInstance(aClass);
    final int length = input.readInt();
    final byte[] bytes = input.readBytes(length);
    final DirectBuffer buffer = new UnsafeBuffer(bytes);

    value.wrap(buffer, 0, bytes.length);
    return value;
  }
}
