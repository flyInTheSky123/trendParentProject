package com.person.trend.service;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.person.trend.client.IndexDataClient;
import com.person.trend.pojo.AnnualProfit;
import com.person.trend.pojo.IndexData;
import com.person.trend.pojo.Profit;
import com.person.trend.pojo.Trade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BackTestService {
    // @Qualifier("INDEX-DATA-SERVICE")

    @Autowired
    IndexDataClient indexDataClient;

    public List<IndexData> listIndexData(String code) {
        List<IndexData> indexDatas = indexDataClient.getIndexData(code);
        //将顺序倒换
        Collections.reverse(indexDatas);
        return indexDatas;
    }


    //simulate 有参数 int ma(隔几天，默认20天) int buyRate int saleRate int serviceCharge  list<IndexDate> indexes

    public Map<String, Object> simulate(int ma, float saleRate, float buyRate, float serviceCharge, List<IndexData> indexes) {

        //利润集合
        ArrayList<Profit> profits = new ArrayList<>();

        //交易记录集合

        ArrayList<Trade> trades = new ArrayList<>();

        //初始金额
        float initCash = 1000;
        //金额
        float cash = initCash;
        //最终手中剩余的钱
        float value = 0;
        //份额
        float share = 0;

        //成功与失败次数（即 卖出时卖出开盘点>买进开盘点）
        int winCount = 0;
        int lossCount = 0;

        //总的失败/成功率
        float totalWinRate = 0;
        float totalLossRate = 0;

        //平均成功/失败率（win/total）
        float avgWinRate = 0;
        float avgLossRate = 0;


        float init = 0;
        if (!indexes.isEmpty())
            init = indexes.get(0).getClosePoint();

        for (int i = 0; i < indexes.size(); i++) {
            float closePoint = indexes.get(i).getClosePoint();

            float avg = getMA(i, ma, indexes);
            float max = getMax(i, ma, indexes);

            //avg为平均开盘点，max为ma范围内的最大的开盘点。
            //定义： 增长的开盘点=当前开盘点/平均开盘点
            float increase_rate = closePoint / avg;
            //定义： 减少的开盘点=当前开盘点/最高开盘点
            float decrease_rate = closePoint / max;

            //当arg！=0 时，表示已经有ma个日期的平均值作为比较
            if (avg != 0) {
                // >buyRate时就是呈现出上升趋势时，进行购买。
                if (increase_rate > buyRate) {
                    //如果没有购买则进行购买
                    if (share == 0) {
                        share = cash / closePoint;
                        cash = 0;
                        //添加购买记录
                        Trade trade = new Trade();
                        trade.setBuyClosePoint(closePoint);
                        trade.setBuyDate(indexes.get(i).getDate());
                        trade.setSellClosePoint(0);
                        trade.setSellDate("n/a");
                        trades.add(trade);


                    }
                } //当出现下跌趋势【小于saleRate（提前设置好的）时是下跌趋势】时，出售。
                else if (decrease_rate < saleRate) {
                    //当前还有份额没有卖出。
                    if (share != 0) {
                        //当呈现出下降趋势时，卖出.
                        //当前的金额 =份额*开盘指数*手续费
                        cash = share * closePoint * (1 - serviceCharge);
                        share = 0;

                        //添加出售记录
                        Trade trade = trades.get(trades.size() - 1);
                        trade.setSellDate(indexes.get(i).getDate());
                        trade.setSellClosePoint(closePoint);
                        //添加盈亏利率记录
                        float rate = cash / initCash;
                        trade.setRate(rate);

                        //添加 成功/失败（即 盈利/亏损）信息
                        if (trade.getSellClosePoint() - trade.getBuyClosePoint() > 0) {
                            //盈利时
                            totalWinRate += (trade.getSellClosePoint() - trade.getBuyClosePoint()) / trade.getBuyClosePoint();
                            winCount++;
                        } else {
                            //亏损
                            totalLossRate += (trade.getSellClosePoint() - trade.getBuyClosePoint()) / trade.getBuyClosePoint();
                            lossCount++;
                        }


                    }
                } else {
                }
            }

            if (share != 0) {
                value = share * closePoint;
            } else {
                value = cash;
            }
            //盈亏比率  剩余的钱/初始值的钱
            float rate = value / initCash;


            Profit profit = new Profit();
            profit.setValue(rate * init);
            profit.setDate(indexes.get(i).getDate());

            System.out.println("profit.value:" + profit.getValue());
            profits.add(profit);

        }
        //计算平均亏损/盈利率
        avgWinRate = totalWinRate / winCount;
        avgLossRate = totalLossRate / lossCount;

        //计算完整时间范围内，每一年的指数投资收益和趋势投资收益
        List<AnnualProfit> annualProfits = caculateAnnualProfits(indexes, profits);

        HashMap<String, Object> maps = new HashMap<>();
        maps.put("profits", profits);
        maps.put("trades", trades);
        maps.put("winCount", winCount);
        maps.put("lossCount", lossCount);
        maps.put("avgWinRate", avgWinRate);
        maps.put("avgLossRate", avgLossRate);
        maps.put("annualProfits", annualProfits);
        return maps;
    }

    //取ma中的最大值
    private static float getMax(int i, int ma, List<IndexData> indexDatas) {
        int start = i - 1 - ma;
        if (start < 0)
            start = 0;
        int now = i - 1;

        if (start < 0)
            return 0;

        float max = 0;
        for (int j = start; j < now; j++) {
            float closePoint = indexDatas.get(j).getClosePoint();

            if (closePoint > max) {
                max = closePoint;
            }
        }
        return max;
    }

    //取ma(ma是指多少天)的平均数，返回值为ma的平均开盘点
    private static float getMA(int i, int ma, List<IndexData> indexDatas) {
        int start = i - 1 - ma;
        int now = i - 1;

        if (start < 0)
            return 0;
        float num = 0;
        for (int j = start; j < now; j++) {
            float closePoint = indexDatas.get(j).getClosePoint();
            num += closePoint;
        }
        return num / ma;

    }

    //获取两段时间间隔多少年
    public float getYear(List<IndexData> indexDatas) {

        float year = 0;
        //获取开始年份，并且转换为date类型
        String startDate = indexDatas.get(0).getDate();
        DateTime start = DateUtil.parse(startDate);

        //结束年份
        String endDate = indexDatas.get(indexDatas.size() - 1).getDate();
        DateTime end = DateUtil.parse(endDate);
        //计算时间范围内有多少天
        long days = DateUtil.between(start, end, DateUnit.DAY);
        year = days / 365f;
        return year;
    }

    //计算完整时间范围内，每一年的指数投资收益和趋势投资收益
    private List<AnnualProfit> caculateAnnualProfits(List<IndexData> indexDatas, List<Profit> profits) {
        List<AnnualProfit> result = new ArrayList<>();
        //获取数据的开始与结束日期（数据是以时间前后顺序，IndexData与Profit的开始与结束时间是一致的）
        //起始年份
        String strStartDate = indexDatas.get(0).getDate();
        //最后结束年份
        String strEndDate = indexDatas.get(indexDatas.size() - 1).getDate();

        Date startDate = DateUtil.parse(strStartDate);
        Date endDate = DateUtil.parse(strEndDate);

        int startYear = DateUtil.year(startDate);
        int endYear = DateUtil.year(endDate);

        for (int year = startYear; year <= endYear; year++) {
            AnnualProfit annualProfit = new AnnualProfit();
            annualProfit.setYear(year);

            //分别获取指定year的指数投资收益率 和 趋势投资收益
            float indexIncome = getIndexIncome(year, indexDatas);
            float trendIncome = getTrendIncome(year, profits);
            annualProfit.setIndexIncome(indexIncome);
            annualProfit.setTrendIncome(trendIncome);
            result.add(annualProfit);

        }
        return result;
    }

    private float getIndexIncome(int year, List<IndexData> indexDatas) {
        IndexData first = null;
        IndexData last = null;

        for (IndexData indexData : indexDatas) {
            String strDate = indexData.getDate();
            //获取当前的日期的年份
            int currentYear = getYear(strDate);

            if (currentYear == year) {
                //比较年份，如果相等则获取指数数据

                if (null == first)
                    //当第一条数据为空时，则进行设置（提供的数据都是有时间顺序的）
                    first = indexData;
                //最后一条数据（会根据for和if条件进行自动获取到）
                last = indexData;
            }
        }
        //返回今年year 的收益利率
        return (last.getClosePoint() - first.getClosePoint()) / first.getClosePoint();
    }

    //获取趋势投资的收益率
    private float getTrendIncome(int year, List<Profit> profits) {
        //该年份的第一条指数数据
        Profit first = null;
        Profit last = null;

        for (Profit profit : profits) {
            //获取当前profit的年份year。
            String strDate = profit.getDate();
            int currentYear = getYear(strDate);

            if (currentYear == year) {
                //如果相等，说明是我们想要的这个年份，则进行获取收益
                if (null == first)
                    first = profit;
                last = profit;
            }
            if (currentYear > year)
                break;
        }
        return (last.getValue() - first.getValue()) / first.getValue();
    }


    //获取一个字符串日期 的 year年。
    private int getYear(String date) {
        String strYear = StrUtil.subBefore(date, "-", false);
        return Convert.toInt(strYear);
    }


}
