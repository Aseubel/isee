package com.aseubel.isee.common.constant;

import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

/**
 * @author Aseubel
 * @date 2025/4/10 下午10:39
 */
public class Constant {
    // 导出文件的时间，从现在开始往前推 x 个时间单位
    public static final long FILE_HISTORY_TIME = 1;
    // 导出文件的时间单位
    public static final TemporalUnit FILE_HISTORY_TIME_UNIT = ChronoUnit.DAYS;
    // 线程池分批查询的时间跨度
    public static final long THREAD_POOL_TIME_SPAN = 1;
    // 线程池分批查询的时间跨度单位
    public static final TemporalUnit THREAD_POOL_TIME_UNIT = ChronoUnit.HOURS;

    public static final String ENDPOINT = "oss-cn-guangzhou.aliyuncs.com";

    public static final String BUCKET_NAME = "autogo";  // 填写Bucket名称，例如examplebucket。

    public static final String REGION = "cn-guangzhou"; // 填写Bucket所在地域。以华东1（杭州）为例，Region填写为cn-hangzhou。

    public static final long FILE_PART_SIZE = 1 * 1024 * 1024L;

    public static final String APP = "analysis";
}
