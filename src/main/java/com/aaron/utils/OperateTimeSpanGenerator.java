package com.aaron.utils;

import cn.hutool.core.date.DateUtil;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 割接文档操作时间范围生成工具
 *
 * @author liurong
 * @version 1.0
 * @date 2022/8/30 10:26
 */
public class OperateTimeSpanGenerator {

    public static List<String> genOperateTimeSpan(Integer hour, Integer minute, List<Integer> intervals) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Date date = new Date();
        date.setHours(hour);
        date.setMinutes(minute);
        date.setSeconds(0);
        List<String> list = new ArrayList<>();
        for (Integer interval : intervals) {
            list.add(sdf.format(date)+"-"+sdf.format(new Date(date.getTime() + interval * 60 * 1000L)));
            date = DateUtil.offsetMinute(date, interval);
        }
        return list;
    }

    public static List<String> genOperateTimeSpanAndOther(Integer hour, Integer minute, List<Integer> intervals,String operator,String reviewer,String isBusinessRelevent) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Date date = new Date();
        date.setHours(hour);
        date.setMinutes(minute);
        date.setSeconds(0);
        List<String> list = new ArrayList<>();
        for (Integer interval : intervals) {
            StringBuilder sb = new StringBuilder();
            String timeSpanStr = sdf.format(date) + "-" + sdf.format(new Date(date.getTime() + interval * 60 * 1000L));
            list.add(StringUtils.joinWith("\t",timeSpanStr,operator,reviewer,isBusinessRelevent));
            date = DateUtil.offsetMinute(date, interval);
        }
        return list;
    }

    public static List<String> genSteps(Integer stepNum) {
        return IntStream.range(1, stepNum+1).mapToObj(t->"步骤"+t).collect(Collectors.toList());
    }

    // 25 行
    public static void main(String[] args) {
        List<Integer> integers = Arrays.asList(2, 3);
        Random random = new Random();
//        List<Integer> boxed = random.ints(25, 2, 4).boxed().collect(Collectors.toList());
        String str = "5\n" +
                "1\n" +
                "3\n" +
                "1\n" +
                "3\n" +
                "1\n" +
                "1\n" +
                "1\n" +
                "2\n" +
                "1\n" +
                "1\n" +
                "1\n" +
                "1\n" +
                "1\n" +
                "1\n" +
                "1\n" +
                "1\n" +
                "1\n" +
                "2\n" +
                "2\n" +
                "5\n" +
                "5\n" +
                "10\n" +
                "60\n" +
                "60\n";
        List<Integer> list = Arrays.stream(str.split("\n")).map(Integer::valueOf).collect(Collectors.toList());
//        List<Integer> list = Arrays.stream(str.split("\n")).map(t->Integer.valueOf(t.trim())).collect(Collectors.toList());
//                genOperateTimeSpan(22, 00, list).forEach(System.out::println);
//        System.out.println(list.toArray().length);
////        genOperateTimeSpanAndOther(7,0, list,"邓元亚","陈烨皓","否").forEach(System.out::println);
        genSteps(list.size()).forEach(System.out::println);
//     System.out.println(list.size());
    }

}

