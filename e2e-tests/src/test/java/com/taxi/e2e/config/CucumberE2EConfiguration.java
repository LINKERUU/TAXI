package com.taxi.e2e.config;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@CucumberContextConfiguration
@SpringBootTest(
        classes = E2ETestConfig.class,
        webEnvironment = SpringBootTest.WebEnvironment.NONE
)
public class CucumberE2EConfiguration {
}
