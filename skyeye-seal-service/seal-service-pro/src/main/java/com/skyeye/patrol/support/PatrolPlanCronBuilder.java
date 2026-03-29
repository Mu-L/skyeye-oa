/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.patrol.support;

import cn.hutool.core.util.StrUtil;
import com.skyeye.patrol.classenum.PatrolPlanFrequency;
import com.skyeye.patrol.entity.PatrolPlan;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 将巡检计划频次转换为 XXL-JOB / Quartz 风格 CRON（6 域：秒 分 时 日 月 周）。
 * 周几约定与计划实体一致：1=周一 … 7=周日。
 *
 * @author skyeye云系列--卫志强
 * @date 2026/03/28
 */
public final class PatrolPlanCronBuilder {

    private static final String DEFAULT_PATROL_TIME = "09:00";
    /**
     * 下标 1=周一 … 7=周日 → Quartz 周域英文缩写
     */
    private static final String[] ISO_DOW_TO_QUARTZ = {"", "MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN"};

    private PatrolPlanCronBuilder() {
    }

    /**
     * @return 可注册的 CRON；无法构建时返回 null
     */
    public static String buildScheduleConf(PatrolPlan plan) {
        if (plan == null || plan.getFrequency() == null) {
            return null;
        }
        Integer freq = plan.getFrequency();
        LocalTime t = parsePatrolTime(plan.getPatrolTime());
        int second = 0;
        int minute = t.getMinute();
        int hour = t.getHour();

        if (PatrolPlanFrequency.CUSTOM.getKey().equals(freq)) {
            String cron = StrUtil.trimToNull(plan.getCustomCron());
            return cron;
        }
        if (PatrolPlanFrequency.DAILY.getKey().equals(freq)) {
            return String.format("%d %d %d * * ?", second, minute, hour);
        }
        if (PatrolPlanFrequency.WEEKLY.getKey().equals(freq)) {
            List<String> quartzDows = parseWeekDaysToQuartz(plan.getWeekDays());
            if (quartzDows.isEmpty()) {
                return null;
            }
            return String.format("%d %d %d ? * %s", second, minute, hour, String.join(",", quartzDows));
        }
        if (PatrolPlanFrequency.MONTHLY.getKey().equals(freq)) {
            String dom = parseMonthDaysDom(plan.getMonthDays());
            if (StrUtil.isBlank(dom)) {
                return null;
            }
            return String.format("%d %d %d %s * ?", second, minute, hour, dom);
        }
        return null;
    }

    private static LocalTime parsePatrolTime(String patrolTime) {
        String s = StrUtil.isBlank(patrolTime) ? DEFAULT_PATROL_TIME : patrolTime.trim();
        try {
            if (s.length() == 5 && s.charAt(2) == ':') {
                return LocalTime.parse(s, DateTimeFormatter.ofPattern("HH:mm"));
            }
            return LocalTime.parse(s, DateTimeFormatter.ofPattern("HH:mm:ss"));
        } catch (Exception e1) {
            try {
                return LocalTime.parse(s, DateTimeFormatter.ofPattern("HH:mm"));
            } catch (Exception e2) {
                return LocalTime.parse(DEFAULT_PATROL_TIME, DateTimeFormatter.ofPattern("HH:mm"));
            }
        }
    }

    private static List<String> parseWeekDaysToQuartz(String weekDays) {
        List<String> out = new ArrayList<>();
        if (StrUtil.isBlank(weekDays)) {
            return out;
        }
        for (String p : weekDays.split(",")) {
            String t = p.trim();
            if (StrUtil.isEmpty(t)) {
                continue;
            }
            try {
                int iso = Integer.parseInt(t);
                if (iso >= 1 && iso <= 7) {
                    out.add(ISO_DOW_TO_QUARTZ[iso]);
                }
            } catch (NumberFormatException ignored) {
                // skip
            }
        }
        return out.stream().distinct().collect(Collectors.toList());
    }

    private static String parseMonthDaysDom(String monthDays) {
        if (StrUtil.isBlank(monthDays)) {
            return null;
        }
        List<String> parts = new ArrayList<>();
        for (String p : monthDays.split(",")) {
            String t = p.trim();
            if (StrUtil.isEmpty(t)) {
                continue;
            }
            try {
                int d = Integer.parseInt(t);
                if (d >= 1 && d <= 31) {
                    parts.add(String.valueOf(d));
                }
            } catch (NumberFormatException ignored) {
                // skip
            }
        }
        if (parts.isEmpty()) {
            return null;
        }
        return String.join(",", parts.stream().distinct().collect(Collectors.toList()));
    }
}
