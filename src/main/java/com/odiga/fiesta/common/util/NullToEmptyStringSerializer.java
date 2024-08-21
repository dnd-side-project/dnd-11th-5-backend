package com.odiga.fiesta.common.util;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class NullToEmptyStringSerializer extends JsonSerializer<Object> {
	@Override
	public void serialize(Object o, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
		throws IOException {
		jsonGenerator.writeString("");
	}
}
