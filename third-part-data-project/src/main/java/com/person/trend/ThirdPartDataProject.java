package com.person.trend;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.NetUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

//这是一个模拟的本地的第三方数据服务
@SpringBootApplication
@EnableEurekaClient
public class ThirdPartDataProject {
    public static void main(String[] args) {
        int port = 8090;
        int eurekaServerPort = 8761;

        //1，首先判断eureka服务器是否开启
        if (NetUtil.isUsableLocalPort(eurekaServerPort)) {
            //该端口能使用，说明eureka没开启,退出
            System.err.printf("检测到端口 %d 没有启用，说明eureka服务器没有开启，请先开启 eureka服务器", eurekaServerPort);
            System.exit(1);
        }


        //如果启动时就已经代理端口参数，则就是用该参数
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

        new SpringApplicationBuilder(ThirdPartDataProject.class).properties("server.port="+port).run(args);


    }
}