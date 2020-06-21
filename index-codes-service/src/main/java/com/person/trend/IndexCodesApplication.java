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
@EnableCaching    //缓存
public class IndexCodesApplication {

    public static void main(String[] args) {

        int port = 0;
        int defaultPort = 8011;
        int redisPort = 6379;
        int eurekaPort = 8761;

        if (NetUtil.isUsableLocalPort(redisPort)) {
            System.err.printf(" 端口 %d 没有打开，判断redis没有开启，请开启redis！", redisPort);
            System.exit(1);
        }

        if (NetUtil.isUsableLocalPort(eurekaPort)) {
            System.err.printf(" 端口 %d 没有打开，判断eureka注册服务中心没有开启，请开启！", eurekaPort);
            System.exit(1);

        }

        if (args != null && args.length != 0) {
            //判断运行的参数是否有默认端口port
            for (String arg : args) {
                if (arg.startsWith("port=")) {
                    //有默认端口，则截取该端口
                    String strPort = StrUtil.subAfter(arg, "port=", true);
                    //判断端口是否数字
                    if (NumberUtil.isNumber(strPort)) {
                        //转化为int类型，如果是null，则不会报错空指针异常
                        port = Convert.toInt(strPort);
                    }


                }
            }

        }

        if (port == 0) {
            //如果端口为0，说明没有默认的端口运行，就需要手动的输入类型
            Future<Integer> future = ThreadUtil.execAsync(() -> {
                int p = 0;

                System.out.printf("请在5秒内输入端口号，推荐 %d ，如果超时没有输入，则默认使用端口 %d", defaultPort, defaultPort);

                Scanner scanner = new Scanner(System.in);

                while (true) {
                    String str = scanner.nextLine();

                    if (!NumberUtil.isInteger(str)) {
                        System.out.println("请输入数字！");
                        continue;
                    } else {
                        p = Convert.toInt(str);
                        scanner.close();
                        break;
                    }
                }

                return p;
            });

            try {
                //如果输入时间超过 5秒，则报错，使用默认的port。
                port= future.get(5, TimeUnit.SECONDS);
            } catch (InterruptedException |TimeoutException |ExecutionException e) {
                //超时后，默认使用 defaultPort。
               port=defaultPort;
            }

        }
        if(!NetUtil.isUsableLocalPort(port)) {
            System.err.printf("端口%d被占用了，无法启动%n", port );
            System.exit(1);
        }

        //使用端口port进行运行
        new SpringApplicationBuilder(IndexCodesApplication.class).properties("server.port="+port).run(args);
    }

    //zipkin 链路器
    @Bean
    public Sampler defaultSampler() {
        return Sampler.ALWAYS_SAMPLE;
    }

}
