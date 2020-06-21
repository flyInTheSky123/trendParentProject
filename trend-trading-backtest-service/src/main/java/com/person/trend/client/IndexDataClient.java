package com.person.trend.client;

import com.person.trend.pojo.IndexData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

//feign用于微服务之间的数据交流
//feign客户端，value是要在注册中心中查找的Application ，fallback 后面的类是 断路器执行的类。
@Component
@FeignClient(value = "INDEX-DATA-SERVICE",fallback = IndexDataClientFeignHystrix.class)
public interface IndexDataClient {
    @GetMapping("/data/{code}")
    public List<IndexData> getIndexData(@PathVariable("code") String code);
}