package com.odiga.fiesta;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import com.odiga.fiesta.config.S3MockConfig;

@ActiveProfiles("test")
@SpringBootTest
@Import(S3MockConfig.class)
class FiestaApplicationTests {

	@Test
	void contextLoads() {
	}
}
