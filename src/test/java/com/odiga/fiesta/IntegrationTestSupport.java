package com.odiga.fiesta;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.odiga.fiesta.config.S3MockConfig;

@ActiveProfiles("test")
@SpringBootTest
@Import(S3MockConfig.class)
public abstract class IntegrationTestSupport {
}
