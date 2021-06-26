package com.taohongxu.blockchain.Config;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

@Configuration
public class eventConfig {
    @EventListener
    public void listen(ApplicationEvent applicationEvent){}
}
