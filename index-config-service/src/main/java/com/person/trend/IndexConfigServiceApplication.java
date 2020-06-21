package com.person.trend;

import cn.hutool.core.util.NetUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableConfigServer
@EnableEurekaClient
@EnableDiscoveryClient
public class IndexConfigServiceApplication {
    public static void main(String[] args) {
        //配置服务器的端口
        int port = 8060;

        //注册服务中心端口
        int eurekaPort = 8761;

        if (NetUtil.isUsableLocalPort(eurekaPort)) {
            System.err.printf("该端口%d 没有被eureka占用， 请先打开eureka服务器：", eurekaPort);
            System.exit(1);
        }
        if (!NetUtil.isUsableLocalPort(port)) {
            System.err.printf("该端口%d 已经被占用了", port);
            System.exit(1);
        }

        new SpringApplicationBuilder(IndexConfigServiceApplication.class).properties("server.port="+port).run(args);
    }
}
