package com.person.trend.util;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.http.HttpUtil;

//一直不断的访问，然后查看断路器监控。这是一个测试类。
public class AccessService {

    public static void main(String[] args) {

        while(true) {
            ThreadUtil.sleep(1000);
            try {
                String html= HttpUtil.get("http://127.0.0.1:8051/simulate/399975/20/1.01/0.99/0/null/null/");
                System.out.println("html length:" + html.length());
            }
            catch(Exception e) {
                System.err.println(e.getMessage());
            }

        }

    }
}