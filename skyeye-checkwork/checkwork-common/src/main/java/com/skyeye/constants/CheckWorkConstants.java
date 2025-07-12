/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.constants;

import com.skyeye.common.enumeration.CheckDayType;
import com.skyeye.common.util.ToolUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: CheckWorkConstants
 * @Description: 考勤相关的常量类
 * @author: skyeye云系列--卫志强
 * @date: 2021/4/24 11:20
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
public class CheckWorkConstants {

    /**
     * 构造上班日的对象数据
     *
     * @param day 指定日期
     * @return
     */
    public static Map<String, Object> structureWorkMation(String day) {
        Map<String, Object> mation = new HashMap<>();
        mation.put("title", "班");
        mation.put("start", day + " 00:00:00");
        mation.put("end", day + " 23:59:59");
        mation.put("backgroundColor", "");
        mation.put("allDay", "1");
        mation.put("showBg", "2");
        mation.put("editable", false);
        mation.put("type", CheckDayType.DAY_IS_WORKING.getKey());
        mation.put("className", CheckDayType.DAY_IS_WORKING.getClassName());
        return mation;
    }

    /**
     * 构造排版班次上班日的对象数据
     *
     * @param day 指定日期
     * @return
     */
    public static Map<String, Object> structureScheduleWorkMation(String day) {
        Map<String, Object> mation = structureWorkMation(day);
        mation.put("isSchedulingWorkDay", true);
        return mation;
    }

    /**
     * 构造星期天休息日的对象数据
     *
     * @param day   指定日期
     * @param title 标题
     * @return
     */
    public static Map<String, Object> structureRestMation(String day, String title) {
        Map<String, Object> mation = new HashMap<>();
        mation.put("title", ToolUtil.isBlank(title) ? "休" : title);
        mation.put("start", day + " 00:00:00");
        mation.put("end", day + " 23:59:59");
        mation.put("backgroundColor", "#54FF9F");
        mation.put("allDay", "1");
        mation.put("showBg", "2");
        mation.put("editable", false);
        mation.put("type", CheckDayType.DAY_IS_HOLIDAY.getKey());
        mation.put("className", CheckDayType.DAY_IS_HOLIDAY.getClassName());
        return mation;
    }

}
