package ru.practicum.ewm.stats.avro.codec;

import org.apache.avro.Schema;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.avro.specific.SpecificRecord;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public final class AvroCodec {

    private AvroCodec() {
    }

    public static byte[] serialize(SpecificRecord record) {
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            SpecificDatumWriter<SpecificRecord> writer = new SpecificDatumWriter<>(record.getSchema());
            BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(output, null);
            writer.write(record, encoder);
            encoder.flush();
            return output.toByteArray();
        } catch (IOException e) {
            throw new AvroCodecException("Failed to serialize Avro record", e);
        }
    }

    public static <T extends SpecificRecord> T deserialize(byte[] payload, Class<T> recordType) {
        try {
            Schema schema = recordType.getDeclaredConstructor().newInstance().getSchema();
            SpecificDatumReader<T> reader = new SpecificDatumReader<>(schema);
            BinaryDecoder decoder = DecoderFactory.get().binaryDecoder(payload, null);
            return reader.read(null, decoder);
        } catch (ReflectiveOperationException | IOException e) {
            throw new AvroCodecException("Failed to deserialize Avro record: " + recordType.getSimpleName(), e);
        }
    }
}
