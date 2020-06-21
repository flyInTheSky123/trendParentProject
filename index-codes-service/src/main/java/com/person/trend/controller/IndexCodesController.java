package com.person.trend.controller;

import com.person.trend.config.IpConfiguration;
import com.person.trend.pojo.Index;
import com.person.trend.service.IndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class IndexCodesController {

    @Autowired
    IndexService indexService;

    @Autowired
    IpConfiguration ipConfiguration;

    @GetMapping("/codes")
    @CrossOrigin   //表示可以跨域
    public List<Index> get(){
        System.out.println("运行的端口是："+ipConfiguration.getPort());
        return indexService.get();
    }
}
