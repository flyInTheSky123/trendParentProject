package com.person.trend.controller;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.person.trend.pojo.AnnualProfit;
import com.person.trend.pojo.IndexData;
import com.person.trend.pojo.Profit;
import com.person.trend.pojo.Trade;
import com.person.trend.service.BackTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class BackTestController {

 /* 这是没有根据时间进行查找的数据
   @Autowired
    BackTestService backTestService;
// http://localhost:8051/simulate/000300
    //通过code获取相应的指数数据（即 INDEX-DATA-SERVICE）
    @GetMapping("/simulate/{code}")
    @CrossOrigin    //跨越注解
    public Map<String, Object> backTest(@PathVariable("code") String code) throws Exception {
        List<IndexData> datas = backTestService.listIndexData(code);
        Map<String, Object> maps = new HashMap<>();
        maps.put("indexDatas", datas);
        return maps;
    }
    */

    //改造成能根据时间进行查询数据
    @Autowired
    BackTestService backTestService;

    // 查询地址（没有使用网关时） http://localhost:8051/simulate/000300
    //通过code获取相应的指数数据（即 INDEX-DATA-SERVICE）
    //var url = "http://127.0.0.1:8031/api-backtest/simulate/" + vue.currentIndex + "/" + vue.ma + "/" + vue.serviceCharge + "/" + vue.buyRate + " / " + vue.sellRate + " / " + vue.startDate + " / " + vue.endDate + " / ";
    //http://127.0.0.1:8031/api-backtest/simulate/000300/20/0.0015/1.01%20/%200.99%20/%202005-04-11%20/%202019-05-09%20/
    @GetMapping("/simulate/{code}/{ma}/{serviceCharge}/{buyRate}/{sellRate}/{startDate}/{endDate}")
    @CrossOrigin    //跨越注解
    public Map<String, Object> backTest(@PathVariable("code") String code,
                                        @PathVariable("ma") int ma,
                                        @PathVariable("serviceCharge") float serviceCharge,
                                        @PathVariable("buyRate") float buyRate,
                                        @PathVariable("sellRate") float sellRate,
                                        @PathVariable("startDate") String strStartDate,
                                        @PathVariable("endDate") String strEndDate) throws Exception {
        List<IndexData> allIndexDatas = backTestService.listIndexData(code);

        System.out.println("strStartDate: " + strStartDate);
        System.out.println("strEndDate:" + strEndDate);

        //获取该code下数据的开始时间 与 结束时间
        String startDate = allIndexDatas.get(0).getDate();
        String endDate = allIndexDatas.get(allIndexDatas.size() - 1).getDate();

        Comparator<IndexData> comparingIndexes = Comparator.comparing(IndexData::getDate);
        //获取时间参数范围内的数据
        allIndexDatas = filterByDateRange(allIndexDatas, strStartDate, strEndDate);

        // System.out.println("startDate:"+startDate);
        //  System.out.println("endDate:"+endDate);
        //ma 表示在一定天数范围内 ，sellRate出售线，buyRate购买线，serviceCharge服务费用。
        // int ma = 20;

        // float sellRate = 0.95f;
        //   float buyRate = 1.05f;
        // float serviceCharge = charge;
        System.out.println("-------------serviceCharge: "+serviceCharge);
        Map<String, ?> simulateResult = backTestService.simulate(ma, sellRate, buyRate, serviceCharge, allIndexDatas);
        List<Profit> profits = (List<Profit>) simulateResult.get("profits");
        List<Trade> trades = (List<Trade>) simulateResult.get("trades");

        //获取年
        float year = backTestService.getYear(allIndexDatas);

        //计算指数投资收益率，年化收益率 和趋势投资收益率( (b/a) )，年化收益率（ (b/a)^1/n n代表几年）
        float indexInComeTotal = (allIndexDatas.get(allIndexDatas.size() - 1).getClosePoint() - allIndexDatas.get(0).getClosePoint()) / allIndexDatas.get(0).getClosePoint();
        //这里 投资收益率+1（本金）是总共收益 ，【总收益^(1/n)](年总收益率)-1（本金）=年化收益率
        float indexInComeAnnual = (float) Math.pow(indexInComeTotal + 1, 1 / year) - 1;

        //趋势投资收益( (b/a) )，年化收益率（ (b/a)^1/n n代表几年）
        float trendInComeTotal = (profits.get(profits.size() - 1).getValue() - profits.get(0).getValue()) / profits.get(0).getValue();
        float trendInComeAnnual = (float) (Math.pow(trendInComeTotal + 1, 1 / year) - 1);

        //亏盈率
        int winCount = (Integer) simulateResult.get("winCount");
        int lossCount = (Integer) simulateResult.get("lossCount");

        //平均亏盈率
        float avgWinRate = (Float) simulateResult.get("avgWinRate");
        float avgLossRate = (Float) simulateResult.get("avgLossRate");

        //获取annualProfits (即每年的指数投资 与趋势投资的 收益率)
        //  List<AnnualProfit> annualProfits = (List<AnnualProfit>) simulateResult.get("annualProfits");
        List<AnnualProfit> annualProfits = (List<AnnualProfit>) simulateResult.get("annualProfits");


        Map<String, Object> result = new HashMap<>();
        //该code下的该时间范围内的指数数据
        result.put("indexDatas", allIndexDatas);
        //开始数据
        result.put("indexStartDate", startDate);
        //结束数据
        result.put("indexEndDate", endDate);
        //趋势透支带来的利润
        result.put("profits", profits);
        //趋势投资交易记录
        result.put("trades", trades);
        //返回投资收益与年化收益
        result.put("year", year);
        result.put("indexIncomeTotal", indexInComeTotal);
        result.put("indexIncomeAnnual", indexInComeAnnual);
        result.put("trendIncomeTotal", trendInComeTotal);
        result.put("trendIncomeAnnual", trendInComeAnnual);

        //返回盈亏
        result.put("winCount", winCount);
        result.put("lossCount", lossCount);
        result.put("avgLossRate", avgLossRate);
        result.put("avgWinRate", avgWinRate);

        //返回每年的指数投资 与趋势投资的 收益率
        // result.put("annualProfits", annualProfits);
        result.put("annualProfits", annualProfits);

        return result;
    }

    private List<IndexData> filterByDateRange(List<IndexData> allIndexDatas, String strStartDate, String strEndDate) {
        //判断参数是否为null或者 ''
        if (StrUtil.isBlankOrUndefined(strStartDate) || StrUtil.isBlankOrUndefined(strEndDate))
            return allIndexDatas;

//        使用二分搜索技术进行查找
        Comparator<IndexData> comparator = Comparator.comparing(IndexData::getDate);
        int startIndex = Math.max(Math.abs(Collections.binarySearch(allIndexDatas, new IndexData(strStartDate, 0), comparator) + 1) - 1, 0);
        int endIndex = Math.min(Math.abs(Collections.binarySearch(allIndexDatas, new IndexData(strEndDate, 0), comparator) + 1), allIndexDatas.size());
        return allIndexDatas.subList(startIndex, endIndex);
    }
}
