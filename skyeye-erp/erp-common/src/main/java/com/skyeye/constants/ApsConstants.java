/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.constants;

/**
 * APS排产相关常量，便于后续扩展复杂场景时统一调整。
 * 扩展方向：按日/按星期产能、设备级产能、节假日日历等。
 */
public class ApsConstants {

    /**
     * 车间每日可用工时默认值(分钟)，8小时
     */
    public static final int DEFAULT_DAILY_WORK_MINUTES = 480;

    /**
     * 每日可用工时最小值(分钟)，1分钟
     */
    public static final int MIN_DAILY_WORK_MINUTES = 1;

    /**
     * 每日可用工时最大值(分钟)，24小时
     */
    public static final int MAX_DAILY_WORK_MINUTES = 1440;

    /**
     * 标准工时计算：定额能力单位(件/小时)对应的分钟数
     */
    public static final int MINUTES_PER_HOUR = 60;

    /**
     * 标准工时小数位数
     */
    public static final int STANDARD_TIME_DECIMAL_SCALE = 4;

}
