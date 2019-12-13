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
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.DefaultSerializers.EnumSerializer;
import com.esotericsoftware.kryo.serializers.FieldSerializer;
import com.esotericsoftware.kryo.util.DefaultInstantiatorStrategy;
import io.zeebe.containers.exporter.RecordReader;
import io.zeebe.containers.exporter.RecordWriter;
import io.zeebe.protocol.impl.record.CopiedRecord;
import io.zeebe.protocol.impl.record.UnifiedRecordValue;
import io.zeebe.protocol.record.Record;
import io.zeebe.protocol.record.RecordType;
import io.zeebe.protocol.record.RejectionType;
import io.zeebe.protocol.record.ValueType;
import io.zeebe.protocol.record.intent.Intent;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import org.objenesis.strategy.StdInstantiatorStrategy;

public class KryoProvider implements RecordWriter.Provider, RecordReader.Provider {

  @Override
  public RecordWriter newWriter(final WritableByteChannel outputChannel) {
    return new KryoRecordWriter(
        newKryoInstance(), new Output(Channels.newOutputStream(outputChannel)));
  }

  @Override
  public RecordReader newReader(final ReadableByteChannel inputChannel) {
    return new KryoRecordReader(
        newKryoInstance(), new Input(Channels.newInputStream(inputChannel)));
  }

  private Kryo newKryoInstance() {
    final Kryo kryo = new Kryo();
    kryo.setRegistrationRequired(false);
    kryo.setWarnUnregisteredClasses(false);
    kryo.addDefaultSerializer(Record.class, FieldSerializer.class);
    kryo.addDefaultSerializer(Intent.class, EnumSerializer.class);
    kryo.addDefaultSerializer(UnifiedRecordValue.class, UnifiedRecordValueSerializer.class);

    kryo.register(CopiedRecord.class);
    kryo.register(RecordType.class);
    kryo.register(ValueType.class);
    kryo.register(RejectionType.class);
    kryo.setInstantiatorStrategy(new DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));

    return kryo;
  }
}
