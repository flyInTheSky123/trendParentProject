package com.person.trend;

import cn.hutool.core.util.NetUtil;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {
    public static void main(String[] args) {
//        注册中心端口
        int port = 8761;
        if (!NetUtil.isUsableLocalPort(port)){
            System.err.printf("该端口 %d 已经被占用 ！",port);
        }
        new SpringApplicationBuilder(EurekaServerApplication.class).properties("server.port ="+port).run(args);


    }
}
