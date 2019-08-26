package com.simplyti.service.toremove;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import com.dslplatform.json.JsonWriter;
import com.dslplatform.json.JsonWriter.WriteObject;

public class LocalDateTimeWriter implements WriteObject<LocalDateTime> {

	@Override
	public void write(JsonWriter writer, LocalDateTime value) {
		String time = value.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneOffset.UTC).format(DateTimeFormatter.ISO_ZONED_DATE_TIME);
		writer.writeString(time);
	}

}
