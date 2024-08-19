package com.odiga.fiesta;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
@Import(S3MockConfig.class)
class FiestaApplicationTests {

	@Test
	void contextLoads() {
	}
}
