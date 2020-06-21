package com.person.trend.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.person.trend.pojo.Index;
import com.person.trend.config.SpringContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@CacheConfig(cacheNames = "indexes")  //redis注解缓存
public class IndexService {
    private List<Index> indexList;

    @Autowired
    private RestTemplate restTemplate;

    //htstrix 断路器，当第三方 连接失败时,使用third_part_connection_fail方法，
    @HystrixCommand(fallbackMethod = "third_part_connection_fail")
    public List<Index> fresh() {
        indexList = fetch_indexs_from_third();
        IndexService indexService = SpringContextUtil.getBean(IndexService.class);
        indexService.remove();
        return indexService.store();
    }


    //使用restTemplate 拿到数据
    public List<Index> fetch_indexs_from_third() {
        List<Map> maps = restTemplate.getForObject("http://127.0.0.1:8090/indexes/codes.json", List.class);
        return toJson(maps);
    }


    //    当第三方数据提供发 无法连接时
    public List<Index> third_part_connection_fail() {
       // System.out.println("third_part_connection_fail()");
        Index index = new Index();
        index.setCode("0000000");
        index.setName("无效指数");

        return CollectionUtil.toList(index);
    }

    //将map集合类型转换为 json类型
    public List<Index> toJson(List<Map> maps) {
        ArrayList<Index> indexs = new ArrayList<>();

        for (Map map : maps) {
            Index index = new Index();
            String code = map.get("code").toString();
            String name = map.get("name").toString();

            index.setCode(code);
            index.setName(name);

            indexs.add(index);
        }
        return indexs;

    }

    //解决因为redis因为第三方没有连接上时，存储了错误信息
    //调用时 删除redis数据
    @CacheEvict(allEntries = true)
    public void remove() {
        System.out.println("删除redis数据");
    }


    //从redis中获取数据
    @Cacheable(key = "'all_codes'")
    public List<Index> get() {
        return CollUtil.toList();
    }

    //保存redis数据
    @Cacheable(key = "'all_codes'")
    public List<Index> store() {
       // System.out.println(this);
        return indexList;
    }
}

