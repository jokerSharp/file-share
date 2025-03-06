package io.project.config;

import io.project.utils.CryptoUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NodeConfig {

    @Value("${salt}")
    private String salt;

    @Bean
    public CryptoUtils getCryptoUtils() {
        return new CryptoUtils(salt);
    }
}
