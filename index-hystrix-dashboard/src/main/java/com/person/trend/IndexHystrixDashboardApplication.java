package com.person.trend;

import cn.hutool.core.util.NetUtil;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;

//断路器监控
@SpringBootApplication
@EnableHystrixDashboard
//断路器监控
public class IndexHystrixDashboardApplication {
    public static void main(String[] args) {
        int port = 8070;
        int eurekaServicePort = 8761;

        if (NetUtil.isUsableLocalPort(eurekaServicePort)) {
            System.err.printf("根据端口%d未开启，eureka服务器没有开启，请开启", eurekaServicePort);
            System.exit(1);
        }
        if (!NetUtil.isUsableLocalPort(port)) {
            System.err.printf("端口%d被占用了，无法启动%n", port);
            System.exit(1);
        }

        new SpringApplicationBuilder(IndexHystrixDashboardApplication.class).properties("server.port=" + port).run(args);


    }
}
