package com.person.trend.controller;

import com.person.trend.pojo.IndexData;
import com.person.trend.service.IndexDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class IndexDtasController {
    @Autowired
    private IndexDataService indexDataService;


    //刷新
    @GetMapping("/freshIndexData/{code}")
    public List<IndexData> fresh(@PathVariable("code") String code) {

        List<IndexData> fresh = indexDataService.fresh(code);
        return fresh;
    }

    //删除
    @GetMapping("/removeIndexData/{code}")
    public String remove(@PathVariable("code") String code) {
        indexDataService.remove(code);
        return "reomove success";
    }

    //获取
    @GetMapping("/getIndexData/{code}")
    public List<IndexData> get(@PathVariable("code") String code){
        return indexDataService.get(code);
    }


}
