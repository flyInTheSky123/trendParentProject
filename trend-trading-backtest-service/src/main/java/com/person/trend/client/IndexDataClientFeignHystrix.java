package com.person.trend.client;

import cn.hutool.core.collection.CollectionUtil;
import com.person.trend.pojo.IndexData;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;


//当与IndexDataClient中的 INDEX-DATA-SERVICE数据视图微服务 断开时或者没有连接上时，就会使用断路器熔断从而使用这个类
@Component
public class IndexDataClientFeignHystrix implements IndexDataClient {
    @Override
    public List<IndexData> getIndexData(String code) {
        IndexData indexData = new IndexData();
        indexData.setClosePoint(0);
        indexData.setDate("0000-00-00");
        return CollectionUtil.toList(indexData);
    }
}
