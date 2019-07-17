package com.datastax.kgdo;

import com.datastax.dse.driver.api.core.DseSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DriverConfiguration {

    @Bean
    DseSession session() {
        return DseSession.builder().build();
    }
}
