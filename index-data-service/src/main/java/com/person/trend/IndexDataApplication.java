package com.person.trend;

import brave.sampler.Sampler;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.NetUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;

import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@SpringBootApplication
@EnableEurekaClient
@EnableCaching
public class IndexDataApplication {
    public static void main(String[] args) {
        int port = 0;
        int defaultPort = 8021;
        int redisPort = 6379;
        int eurekaServerPort = 8761;

        if (NetUtil.isUsableLocalPort(redisPort)) {
            System.out.printf("根据端口 %，redis没有打开", redisPort);
            System.exit(1);
        }

        if (NetUtil.isUsableLocalPort(eurekaServerPort)) {
            System.out.printf("根据端口 %，rureka没有打开", eurekaServerPort);
            System.exit(1);
        }


        //判断运行参数中有没有端口参数
        if (null != args && args.length != 0) {
            for (String arg : args) {
                if (arg.startsWith("port=")) {
                    String strPort = StrUtil.subAfter(arg, "port=", true);
                    if (NumberUtil.isInteger(strPort)) {
                        port = Convert.toInt(strPort);
                    }
                }

            }
        }


        //判断当前port是否为0
        if (port == 0) {
            //如果端口为0，说明没有默认的端口运行，就需要手动的输入类型
            Future<Integer> future = ThreadUtil.execAsync(() -> {
                int p = 0;

                System.out.printf("请在5秒内输入端口号 ，推荐使用 %d 端口，如果超时没有输入 则使用默认端口%d", defaultPort, defaultPort);
                Scanner scanner = new Scanner(System.in);

                while (true) {
                    String strPort = scanner.nextLine();
                    if (!NumberUtil.isNumber(strPort)) {
                        System.err.println("只能是数字");
                        continue;

                    } else {
                        p = Convert.toInt(strPort);
                        scanner.close();
                        break;
                    }
                }

                return p;
            });

            try {
                //获取输入的端口参数
                port = future.get(5, TimeUnit.SECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                //如果报错，则使用默认的端口
                port=defaultPort;
            }

        }


        if (!NetUtil.isUsableLocalPort(port)){
            System.err.printf("该端口%d,已经被占用，请重新输入!",port);
            System.exit(1);
        }

        new SpringApplicationBuilder(IndexDataApplication.class).properties("server.port="+port).run(args);

    }

    //zipkin 链路器
    @Bean
    public Sampler defaultSampler() {
        return Sampler.ALWAYS_SAMPLE;
    }
}
