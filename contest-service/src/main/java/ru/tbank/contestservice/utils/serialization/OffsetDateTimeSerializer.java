package ru.tbank.contestservice.utils.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class OffsetDateTimeSerializer extends StdSerializer<OffsetDateTime> {

    private static final ZoneOffset DEFAULT_ZONE_OFFSET = ZoneOffset.ofHours(3);
    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    public OffsetDateTimeSerializer() {
        super(OffsetDateTime.class);
    }

    @Override
    public void serialize(OffsetDateTime value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        if (value != null) {
            String formatted = value
                    .withOffsetSameInstant(DEFAULT_ZONE_OFFSET)
                    .format(formatter);
            gen.writeString(formatted);
        }
    }
}
