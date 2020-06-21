package com.person.trend;

import brave.sampler.Sampler;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.NetUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;

import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@SpringBootApplication
@EnableEurekaClient
@EnableDiscoveryClient
public class BackTestViewApplication {
    public static void main(String[] args) {
        int port = 0;
        int defaultPort = 8041;
        int eurekaServerPort = 8761;
        int configServicePort = 8060;
        int rabbitMQPort = 5672;

        //判断rabbitmq端口是否开启
        if (NetUtil.isUsableLocalPort(rabbitMQPort)) {

            System.err.printf("根据端口%d未开启，请先开打rabbitMQ",rabbitMQPort);
            System.exit(1);
        }


        //判断注册服务中心是否开启
        if (NetUtil.isUsableLocalPort(eurekaServerPort)) {
            System.err.printf("根据端口%d，eureka服务没有开启，请先开启eureka！", eurekaServerPort);
            System.exit(1);
        }
        //判断配置服务器是否开启
        if (NetUtil.isUsableLocalPort(configServicePort)) {
            System.err.printf("根据端口%d ,index-config-service（配置服务器没有开启），请先开启 ", configServicePort);
            System.exit(1);

        }

        //从args中判断是否带有默认端口
        if (args != null && args.length != 0) {
            for (String arg : args) {
                //如果有参数，则判断是否存在port=
                if (arg.startsWith("port=")) {
                    //截取端口号
                    String strPort = StrUtil.subAfter(arg, "port=", true);
                    if (NumberUtil.isNumber(strPort)) {
                        //转换为int类型
                        port = Convert.toInt(strPort);
                    }

                }
            }
        }

        //判断port是否已经有了，没有则进行手动输入
        if (port == 0) {
            Future<Integer> future = ThreadUtil.execAsync(() -> {
                //端口
                int p = 0;
                System.out.printf("请输入端口号，建议输入端口%d，如果超时则默认端口%d", defaultPort, defaultPort);
                Scanner scanner = new Scanner(System.in);

                while (true) {
                    String strPort = scanner.nextLine();
                    //判断输入的端口，是不是整数
                    if (!NumberUtil.isInteger(strPort)) {
                        System.err.println("请重新输入整数端口！");
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
                port  = future.get(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                port = defaultPort;
                e.printStackTrace();
            } catch (ExecutionException e) {
                port = defaultPort;

                e.printStackTrace();
            } catch (TimeoutException e) {
                port = defaultPort;
                e.printStackTrace();
            }
        }


        //创建
        new SpringApplicationBuilder(BackTestViewApplication.class).properties("server.port=" + port).run(args);

    }

    //zipkin 链路器.都增加 Sampler。 表示一直在取样
    @Bean
    public Sampler defaultSampler() {
        return Sampler.ALWAYS_SAMPLE;
    }
}
