package com.person.trend.util;

import cn.hutool.http.HttpUtil;

import java.util.HashMap;

//因为 http://localhost:8041/actuator/bus-refresh只能通过post方式进行刷新，所以这里创建一个class进行运行。
public class FreshUtil {
    public static void main(String[] args) {
        HashMap<String, String> header = new HashMap<>();
        header.put("Content-Type", "application/json;charset=utf-8");
        System.out.println("请先等待，config-service获取配置信息");
        String result = HttpUtil.createPost("http://localhost:8041/actuator/bus-refresh").addHeaders(header).execute().body();
        System.out.println("result:" + result);
        System.out.println("刷新完成");
    }
}
