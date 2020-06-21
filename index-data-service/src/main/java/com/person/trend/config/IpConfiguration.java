package com.person.trend.config;

import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

//监听当前运行的接口
@Component
public class IpConfiguration implements ApplicationListener<WebServerInitializedEvent> {
    int port=0;

    @Override
    public void onApplicationEvent(WebServerInitializedEvent event) {
       this.port=event.getWebServer().getPort();

    }
    public int getPort(){
        return this.port;
    }
}
