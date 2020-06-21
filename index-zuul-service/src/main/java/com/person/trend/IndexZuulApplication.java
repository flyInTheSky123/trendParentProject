package com.person.trend;

import brave.sampler.Sampler;
import cn.hutool.core.util.NetUtil;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableEurekaClient
@EnableZuulProxy
@EnableDiscoveryClient
public class IndexZuulApplication {
    public static void main(String[] args) {

//http://localhost:8031/api-codes/codes
// http://localhost:8031/backtest/simulate/000300
        int port = 8031;
        if (!NetUtil.isUsableLocalPort(port)) {
            System.out.printf("该端口%d，已经被占用了！", port);
            System.exit(1);
        }
        new SpringApplicationBuilder(IndexZuulApplication.class).properties("server.port=" + port).run(args);


    }
    //zipkin 链路器
    @Bean
    public Sampler defaultSampler() {
        return Sampler.ALWAYS_SAMPLE;
    }
}
