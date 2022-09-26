package com.aaron.utils;


import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializeWriter;
import lombok.Data;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * 推送主线告警工具类
 *
 * @author liurong
 * @version 1.0
 * @date 2022/8/19 16:18
 */
public class WarnPusher {

//    public static final String HOST = "http://localhost:8081";
    public static final String HOST = "http://172.31.47.174:8081";

    public static final String WARN_URI = "/imp/equipmentWarn/pushWarn";

    public static void pushWarn(WarnParam warnParam, Integer num, Integer intervalSecond) {
        if (null == num || num < 0) {
            num = 1;
        }
        if (null == intervalSecond || intervalSecond < 0) {
            intervalSecond = 0;
        }
        System.out.println("开始推送，设备编号：" + warnParam.getDeviceCode() + "，告警类型：" + warnParam.getWarnType() + "，推送次数：" + num + "，间隔时间：" + intervalSecond);
//        Date date = new Date(System.currentTimeMillis() - num * intervalSecond * 1000L);
        for (int i = 0; i < num; i++) {
            Date date = new Date();
            warnParam.setDeviceReportTime(date);
            warnParam.setPlatformReportTime(date);
            warnParam.setNextReportTime(new Date(date.getTime() + 60 * 60 * 1000L));
            SerializeConfig config = new SerializeConfig();
            // 转换为驼峰
            config.propertyNamingStrategy = PropertyNamingStrategy.SnakeCase;
            String body = JSON.toJSONString(warnParam, config);
            HttpUtil.post(HOST + WARN_URI, body);
            HttpRequest httpRequest = HttpRequest.post(HOST + WARN_URI).body(body);
            System.out.println("推送：" + body);
            HttpResponse httpResponse = httpRequest.execute();
            int status = httpResponse.getStatus();
            String responseBody = httpResponse.body();
            if (i == num - 1) {
                continue;
            }
            try {
                Thread.sleep(intervalSecond * 1000);
            } catch (InterruptedException ignored) {
            }
//            date = new Date(date.getTime() + intervalSecond * 1000L);
        }
    }

    public static void pushWarnToDap(DapWarnParam warnParam, Integer num, Integer intervalSecond) {

    }

    public static void main(String[] args) {
        List<String> pics = Arrays.asList(
                "group1_M00/1F/18/rB8v4mMDNJyAVBpuAA9RjLMhmi0659.jpg",
                "group1_M00/20/91/rB8v4mMgIgGATJRpAAA1XvdgRw4150.jpg",
                "group1_M00/20/92/rB8v4mMgMFWAB_GiAAA1XvdgRw4638.jpg",
                "group1_M00/06/E5/rB8v4mMizwWAbubrAADiotYOv88470.jpg"
        );
        List<String> warnCodes = Arrays.asList("T37W02","T37W13","T13W01","T37W36");
//        List<String> deviceCodes = Arrays.asList("jwjw01","jwjw02","blhy01","blhy02","rsy01","rsy02");
//        List<String> deviceCodes = Arrays.asList("smel01","smel02","smel03","smel04");
//        List<String> deviceCodes = Arrays.asList("rsy02");
        Map<String, List<String>> supplierDeviceMap = MapUtil.builder("gyszx", Arrays.asList("smel01", "smel02", "smel03", "smel04"))
                .build();
        Map<String, String> deviceCodeSupplierMap = new HashMap<>();
        supplierDeviceMap.forEach((k, v) -> v.forEach(deviceCode -> deviceCodeSupplierMap.put(deviceCode, k)));
        List<String> deviceCodes = new ArrayList<>(deviceCodeSupplierMap.keySet());

        //
        Supplier<List<String>> onePicSupplier = () -> Arrays.asList(pics.get(RandomUtil.randomInt(0, pics.size())));
        Supplier<List<String>> manyPicSupplier = () -> {
            List<String> list = new ArrayList<>();
            int num = RandomUtil.randomInt(0, 5);
            list.add(onePicSupplier.get().get(0));
            return list;
        };
        List<WarnParam> warnParams = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            String deviceCode = deviceCodes.get(RandomUtil.randomInt(0, deviceCodes.size()));
            warnParams.add(new WarnParam(
                    deviceCodeSupplierMap.get(deviceCode),
                    deviceCode,
                    warnCodes.get(RandomUtil.randomInt(0, warnCodes.size())),
                    "",
//                    Arrays.asList(),
                    manyPicSupplier.get()
            ));
        }
        ExecutorService executor = Executors.newFixedThreadPool(2);
        for (WarnParam warnParam : warnParams) {
            int num = RandomUtil.randomInt(1, 3);
            int intervalSeconds = RandomUtil.randomInt(2, 10);
            // 推送告警
            executor.submit(() -> {
                pushWarn(warnParam, num, intervalSeconds);
            });
        }
        try {
            // 等待所有任务执行完毕
            executor.shutdown();
            // 阻塞当前线程，直到所有任务执行完毕（超时或者中断）
            if (!executor.awaitTermination(60 * 60, TimeUnit.SECONDS)) {
                // 超时的时候停止所有任务
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            // 异常的时候停止所有任务
            executor.shutdownNow();
        }
        System.out.println("推送完成");
    }
}


@Data
class WarnParam {
    private String firmCode;
    private String deviceCode;
    private String warnType;
    private String warnDescription;
    private List<String> pics;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date deviceReportTime;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date platformReportTime;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date nextReportTime;

    public WarnParam(String firmCode, String deviceCode, String warnType, String warnDescription, List<String> pics) {
        this.firmCode = firmCode;
        this.deviceCode = deviceCode;
        this.warnType = warnType;
        this.warnDescription = warnDescription;
        this.pics = pics;
    }
}

@Data
class PushParam {
    private WarnParam warnParam;
    private Integer num;
    private Integer intervalSecond;

    public PushParam(WarnParam warnParam, Integer num, Integer intervalSecond) {
        this.warnParam = warnParam;
        this.num = num;
        this.intervalSecond = intervalSecond;
    }
}


@Data
class DapWarnParam {

    @JSONField(name = "firm_code")
    private String firmCode;

    @JSONField(name = "device_code")
    private String deviceCode;

    @JSONField(name = "device_report_time", format = "yyyy-MM-dd HH:mm:ss",serializeUsing = DateCustomSerializer.class)
    private Date deviceReportTime;

    @JSONField(name = "platform_report_time",format = "yyyy-MM-dd HH:mm:ss",serializeUsing = DateCustomSerializer.class)
    private Date platformReportTime;

    @JSONField(name = "warn_description")
    private String warnDescription;

    @JSONField(name = "warn_type")
    private String warnType;

}

interface Warn {
}

class DateCustomSerializer implements ObjectSerializer {
    @Override
    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) throws IOException {
        SerializeWriter out = serializer.out;
        if (object == null) {
            out.writeString(DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
            return;
        }
        out.writeString(DateUtil.format((Date) object, "yyyy-MM-dd HH:mm:ss"));
    }
}

