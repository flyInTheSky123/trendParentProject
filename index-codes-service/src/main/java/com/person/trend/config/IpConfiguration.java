package com.person.trend.config;

import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;


//获取当前运行的微服务是哪一个端口
@Component
public class IpConfiguration implements ApplicationListener<WebServerInitializedEvent> {

    private int port;

    //获取端口
    @Override
    public void onApplicationEvent(WebServerInitializedEvent event) {
     this.port=  event.getWebServer().getPort();

    }

    //返回端口
    public int getPort(){
       return this.port;
    }
}
