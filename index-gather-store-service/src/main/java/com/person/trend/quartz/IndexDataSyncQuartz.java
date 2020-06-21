package com.person.trend.quartz;

import cn.hutool.core.date.DateUtil;
import com.person.trend.pojo.Index;
import com.person.trend.service.IndexDataService;
import com.person.trend.service.IndexService;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.List;

//定时器：指数数据 与 指数 定时进行fresh 从而获取数据
public class IndexDataSyncQuartz extends QuartzJobBean {

    @Autowired
    IndexDataService indexDataService;

    @Autowired
    IndexService indexService;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        System.out.println("定时开启" + DateUtil.now());

        List<Index> fresh = indexService.fresh();
        for (Index index : fresh) {
            indexDataService.fresh(index.getCode());
        }
        System.out.println("定时结束" + DateUtil.now());
    }
}
