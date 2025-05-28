package com.nikolayIvanov.employee;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ServerPortLogger implements ApplicationListener<ApplicationReadyEvent> {
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        Environment env = event.getApplicationContext().getEnvironment();
        String port = env.getProperty("local.server.port");
        String contextPath = env.getProperty("server.servlet.context-path", "");
        log.info("Application started at: http://localhost:{}{}/api/upload", port, contextPath);
    }
}

