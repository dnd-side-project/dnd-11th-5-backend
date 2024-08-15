package com.odiga.fiesta;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import com.amazonaws.services.s3.AmazonS3Client;
import com.odiga.fiesta.common.config.S3Config;

@TestConfiguration
public class S3MockConfig extends S3Config {

	@Bean
	@Primary
	@Override
	public AmazonS3Client amazonS3Client() {
		return Mockito.mock(AmazonS3Client.class);
	}
}
