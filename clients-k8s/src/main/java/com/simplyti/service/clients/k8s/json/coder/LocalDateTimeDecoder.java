package com.simplyti.service.clients.k8s.json.coder;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import com.dslplatform.json.JsonReader;
import com.dslplatform.json.JsonReader.ReadObject;

public class LocalDateTimeDecoder implements ReadObject<LocalDateTime> {

	@Override
	public LocalDateTime read(@SuppressWarnings("rawtypes") JsonReader reader) throws IOException {
		return ZonedDateTime.parse(reader.readString(),DateTimeFormatter.ISO_ZONED_DATE_TIME).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
	}

}
