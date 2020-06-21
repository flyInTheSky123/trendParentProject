package com.person.trend.controller;

import com.person.trend.config.IpConfiguration;
import com.person.trend.pojo.IndexData;
import com.person.trend.service.IndexDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class IndexDataController {
    @Autowired
    IpConfiguration ipConfiguration;

    @Autowired
    IndexDataService indexDataService;

    //  http://127.0.0.1:8021/indexData/000300
    @GetMapping("/data/{code}")
    public List<IndexData> get(@PathVariable("code") String code) {
        System.out.println("当前IndexDataApplication 运行的端口是：" + ipConfiguration.getPort());
        List<IndexData> indexData = indexDataService.get(code);
        //  System.out.println("indexData:"+indexData.size());
        return indexData;

    }
}
