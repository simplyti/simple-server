package com.simplyti.service.clients.k8s.json.coder;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import com.jsoniter.JsonIterator;
import com.jsoniter.spi.Decoder;

public class LocalDateTimeDecoder implements Decoder {

	@Override
	public Object decode(JsonIterator iter) throws IOException {
		return ZonedDateTime.parse(iter.readString(),DateTimeFormatter.ISO_ZONED_DATE_TIME).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
	}

}
