package com.person.trend.config;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

//为解决redis缓存错误信息时 的解决方案

@Component
public class SpringContextUtil implements ApplicationContextAware {

    private SpringContextUtil() {
        System.out.println("SpringContextUtil()");
    }

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext){
       // System.out.println("applicationContext:"+applicationContext);
        SpringContextUtil.applicationContext = applicationContext;
    }

    public static <T> T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }

}