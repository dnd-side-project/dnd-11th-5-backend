package com.odiga.fiesta.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.odiga.fiesta.common.util.NullToEmptyStringSerializer;

@Configuration
public class JsonCustomConfig {
	@Bean
	public MappingJackson2HttpMessageConverter httpMessageConverter(ObjectMapper objectMapper) {
		objectMapper.getSerializerProvider()
			.setNullValueSerializer(new NullToEmptyStringSerializer());
		MappingJackson2HttpMessageConverter httpMessageConverter
			= new MappingJackson2HttpMessageConverter(objectMapper);
		return httpMessageConverter;
	}
}
