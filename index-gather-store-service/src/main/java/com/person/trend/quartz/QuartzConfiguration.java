package com.person.trend.quartz;

import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//定时器配置
@Configuration
public class QuartzConfiguration {
    //定时时间，单位为秒。
    private static final int interval = 1;

    @Bean
    public JobDetail weatherDataSyncJobDetail() {
        return JobBuilder.newJob(IndexDataSyncQuartz.class).withIdentity("IndexDataSyncQuartz")
                .storeDurably().build();
    }

    @Bean
    public Trigger weatherDataSyncTrigger() {
        SimpleScheduleBuilder schedBuilder = SimpleScheduleBuilder.simpleSchedule()
                .withIntervalInMinutes(interval).repeatForever();

        return TriggerBuilder.newTrigger().forJob(weatherDataSyncJobDetail())
                .withIdentity("indexDataSyncTrigger").withSchedule(schedBuilder).build();
    }
}
