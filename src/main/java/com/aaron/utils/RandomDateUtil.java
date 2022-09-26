package com.aaron.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class RandomDateUtil {
    public static void main(String[] args) throws Exception {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        List<Date> dates = randomDate("2021-6-21 00:00:00", "2021-07-01 23:59:59", 100);
        dates.forEach(t -> System.out.println(sdf.format(t)));

    }


    /**
     * 生成size数量的随机时间，位于[start,end)范围内 时间倒序排列
     * @param start 开始时间
     * @param end 结束时间
     * @param size 生成时间个数
     * @return List<Date>
     * @throws Exception
     */
    public static List<Date> randomDate(String start, String end, int size) throws Exception {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date startTime = sdf.parse(start);
        Date endTime = sdf.parse(end);

        Random random = new Random();
        List<Date> dates = random.longs(size, startTime.getTime(), endTime.getTime()).mapToObj(t -> new Date(t)).collect(Collectors.toList());

        dates.sort((t1,t2)->{
            return t2.compareTo(t1);
        });

        return dates;
    }


}
