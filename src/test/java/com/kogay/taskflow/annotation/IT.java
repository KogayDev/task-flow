package com.kogay.taskflow.annotation;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@SpringBootTest
@ActiveProfiles("integration-test")
@Transactional
@WithMockUser(username = "test@gmail.com", password = "test", authorities = {"USER", "ADMIN"})
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface IT {
}

