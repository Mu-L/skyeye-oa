/**
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 */

package com.skyeye.farm.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.constants.ApsConstants;
import com.skyeye.exception.CustomException;
import com.skyeye.farm.classenum.FarmCalendarConfigTypeEnum;
import com.skyeye.farm.dao.FarmCalendarDao;
import com.skyeye.farm.entity.FarmCalendar;
import com.skyeye.farm.service.FarmCalendarService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: FarmCalendarServiceImpl
 * @Description: 车间产能日历服务实现
 * @author: skyeye云系列--卫志强
 * @date: 2026/2/14
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "车间产能日历", groupName = "车间管理")
public class FarmCalendarServiceImpl extends SkyeyeBusinessServiceImpl<FarmCalendarDao, FarmCalendar> implements FarmCalendarService {

    @Override
    public QueryWrapper<FarmCalendar> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<FarmCalendar> queryWrapper = super.getQueryWrapper(commonPageInfo);
        queryWrapper.eq(MybatisPlusUtil.toColumns(FarmCalendar::getFarmId), commonPageInfo.getObjectId());
        return queryWrapper;
    }

    @Override
    public void validatorEntity(FarmCalendar entity) {
        if (entity.getDailyWorkMinutes() < ApsConstants.MIN_DAILY_WORK_MINUTES
            || entity.getDailyWorkMinutes() > ApsConstants.MAX_DAILY_WORK_MINUTES) {
            throw new CustomException("当日可用工时必须在" + ApsConstants.MIN_DAILY_WORK_MINUTES + "~"
                + ApsConstants.MAX_DAILY_WORK_MINUTES + "分钟之间(1分钟~24小时)");
        }
        FarmCalendarConfigTypeEnum configType = FarmCalendarConfigTypeEnum.parse(entity.getConfigType());
        if (configType == null) {
            String options = String.join("、", Arrays.stream(FarmCalendarConfigTypeEnum.values())
                .map(FarmCalendarConfigTypeEnum::getValue).toArray(String[]::new));
            throw new CustomException("配置类型无效，请选择：" + options);
        }
        if (configType == FarmCalendarConfigTypeEnum.DATE) {
            if (StrUtil.isEmpty(entity.getWorkDate())) {
                throw new CustomException("按日期配置时，工作日期(yyyy-MM-dd)不能为空");
            }
        } else if (configType == FarmCalendarConfigTypeEnum.WEEKDAY) {
            if (StrUtil.isEmpty(entity.getDayOfWeek())) {
                throw new CustomException("按星期配置时，星期几不能为空，可多选如1,2,3,4,5(周一至周五)");
            }
            Set<Integer> days = parseDayOfWeekSet(entity.getDayOfWeek());
            if (days.isEmpty()) {
                throw new CustomException("按星期配置时，星期几必须为1-7(周一至周日)，可多选逗号分隔");
            }
        } else if (configType == FarmCalendarConfigTypeEnum.PERIOD) {
            if (StrUtil.isEmpty(entity.getWorkDateStart()) || StrUtil.isEmpty(entity.getWorkDateEnd())) {
                throw new CustomException("按日期区间配置时，开始日期和结束日期(yyyy-MM-dd)不能为空");
            }
            if (entity.getWorkDateStart().compareTo(entity.getWorkDateEnd()) > 0) {
                throw new CustomException("按日期区间配置时，开始日期不能大于结束日期");
            }
        }
    }

    @Override
    public Integer resolveDailyCapFromCalendar(List<FarmCalendar> allList, String dateStr) {
        if (CollectionUtil.isEmpty(allList)) {
            return null;
        }
        int currentDay = DateUtil.getWeek(dateStr);
        for (FarmCalendar pc : allList) {
            if (FarmCalendarConfigTypeEnum.DATE.getKey().equals(pc.getConfigType())) {
                if (dateStr.equals(pc.getWorkDate())) {
                    return pc.getDailyWorkMinutes();
                }
            } else if (FarmCalendarConfigTypeEnum.PERIOD.getKey().equals(pc.getConfigType())) {
                if (StrUtil.isNotEmpty(pc.getWorkDateStart()) && StrUtil.isNotEmpty(pc.getWorkDateEnd())
                    && dateStr.compareTo(pc.getWorkDateStart()) >= 0 && dateStr.compareTo(pc.getWorkDateEnd()) <= 0) {
                    return pc.getDailyWorkMinutes();
                }
            } else if (FarmCalendarConfigTypeEnum.WEEKDAY.getKey().equals(pc.getConfigType())) {
                if (parseDayOfWeekSet(pc.getDayOfWeek()).contains(currentDay)) {
                    return pc.getDailyWorkMinutes();
                }
            }
        }
        return null;
    }

    /**
     * 解析 dayOfWeek 字符串为数字集合，如 "1,2,3,4,5" -> {1,2,3,4,5}，仅保留1-7
     */
    private Set<Integer> parseDayOfWeekSet(String dayOfWeekStr) {
        if (StrUtil.isEmpty(dayOfWeekStr)) {
            return Collections.emptySet();
        }
        return Arrays.stream(dayOfWeekStr.split("[,，\\s]+"))
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .map(s -> {
                try {
                    return Integer.parseInt(s);
                } catch (NumberFormatException e) {
                    return -1;
                }
            })
            .filter(d -> d >= 1 && d <= 7)
            .collect(Collectors.toSet());
    }

    @Override
    public List<FarmCalendar> listByFarmId(String farmId) {
        if (StrUtil.isEmpty(farmId)) {
            return Collections.emptyList();
        }
        return listByFarmIds(Collections.singletonList(farmId)).getOrDefault(farmId, Collections.emptyList());
    }

    @Override
    public Map<String, List<FarmCalendar>> listByFarmIds(List<String> farmIds) {
        if (CollectionUtil.isEmpty(farmIds)) {
            return Collections.emptyMap();
        }
        QueryWrapper<FarmCalendar> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(FarmCalendar::getFarmId), farmIds);
        queryWrapper.eq(MybatisPlusUtil.toColumns(FarmCalendar::getEnabled), EnableEnum.ENABLE_USING.getKey());
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(FarmCalendar::getFarmId));
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(FarmCalendar::getPriority));
        List<FarmCalendar> allList = list(queryWrapper);
        return allList.stream().collect(Collectors.groupingBy(FarmCalendar::getFarmId));
    }
}
