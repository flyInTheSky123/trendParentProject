package com.person.trend;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.NetUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableEurekaClient //注册客服端
@EnableHystrix     //断路器注解
@EnableCaching     //redis缓存，添加对端口的判断
public class IndexGatherStoreService {
    public static void main(String[] args) {


        int defaultPort=8001;
        int eurekaServerPort=8761;
        int redisPort=6379;
        int port=defaultPort;
        //判断eureka是否已经启动;
        if (NetUtil.isUsableLocalPort(eurekaServerPort)){
            //说明没有启动，退出
            System.err.printf("端口 %d 没有占用，说明eureka服务器没有启动，请先启动",eurekaServerPort);
            System.exit(1);
        }

        if (NetUtil.isUsableLocalPort(redisPort)){
            System.err.printf("redis的端口%d 没有开启，请启动redis",redisPort);
            System.exit(1);
        }

        //如果启动的参数中带有默认端口参数，则使用默认参数；
        if (args!=null && args.length!=0){
            for (String arg:args){
                if (arg.startsWith("port=")){
                    String strPort = StrUtil.subAfter(arg, "port=", true);
                    if (NumberUtil.isNumber(strPort)){
                        port = Convert.toInt(strPort);
                    }
                }
            }
        }


        //判断端口是否已经被占用
        if (!NetUtil.isUsableLocalPort(port)){
            System.err.printf("该端口 %d 已经被占用",port);
            System.exit(1);
        }

        new SpringApplicationBuilder(IndexGatherStoreService.class).properties("server.port="+port).run(args);
    }

    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
