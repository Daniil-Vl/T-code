package ru.tbank.contestservice.utils.serialization;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class OffsetDateTimeDeserializer extends StdDeserializer<OffsetDateTime> {

    private static final ZoneOffset DEFAULT_ZONE_OFFSET = ZoneOffset.ofHours(3);
    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    protected OffsetDateTimeDeserializer() {
        super(OffsetDateTime.class);
    }

    @Override
    public OffsetDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        String dateAsString = p.getText();
        if (dateAsString == null) {
            throw new IOException("OffsetDateTime argument is null");
        }
        return OffsetDateTime.parse(dateAsString, formatter).withOffsetSameInstant(DEFAULT_ZONE_OFFSET);
    }
}
