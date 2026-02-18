/**
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 */

package com.skyeye.farm.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.enumeration.DeleteFlagEnum;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.constants.ApsConstants;
import com.skyeye.exception.CustomException;
import com.skyeye.farm.dao.FarmDao;
import com.skyeye.farm.entity.Farm;
import com.skyeye.farm.entity.FarmCalendar;
import com.skyeye.farm.service.FarmCalendarService;
import com.skyeye.farm.service.FarmService;
import com.skyeye.organization.service.IDepmentService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: FarmServiceImpl
 * @Description: 车间管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 22:47
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "车间管理", groupName = "车间管理", allowDynamicAttrKey = false)
public class FarmServiceImpl extends SkyeyeBusinessServiceImpl<FarmDao, Farm> implements FarmService {

    @Autowired
    private IDepmentService iDepmentService;

    @Autowired
    private FarmCalendarService farmCalendarService;

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        if (CollectionUtil.isEmpty(beans)) {
            return beans;
        }
        iDepmentService.setMationForMap(beans, "departmentId", "departmentMation");
        iAuthUserService.setMationForMap(beans, "chargePerson", "chargePersonMation");
        return beans;
    }

    @Override
    public void validatorEntity(Farm entity) {
        QueryWrapper<Farm> queryWrapper = new QueryWrapper<>();
        queryWrapper.and(wrapper ->
            wrapper.eq(MybatisPlusUtil.toColumns(Farm::getName), entity.getName())
                .or().eq(MybatisPlusUtil.toColumns(Farm::getNumber), entity.getNumber()));
        queryWrapper.eq(MybatisPlusUtil.toColumns(Farm::getDeleteFlag), DeleteFlagEnum.NOT_DELETE.getKey());
        if (StringUtils.isNotEmpty(entity.getId())) {
            queryWrapper.ne(CommonConstants.ID, entity.getId());
        }
        Farm checkFarm = getOne(queryWrapper);
        if (ObjectUtil.isNotEmpty(checkFarm)) {
            throw new CustomException("车间名称或编号已存在");
        }
        // 每日可用工时校验：若填写则必须在规范范围内，便于后续扩展校验规则
        if (entity.getDailyWorkMinutes() != null) {
            if (entity.getDailyWorkMinutes() < ApsConstants.MIN_DAILY_WORK_MINUTES
                || entity.getDailyWorkMinutes() > ApsConstants.MAX_DAILY_WORK_MINUTES) {
                throw new CustomException("每日可用工时必须在" + ApsConstants.MIN_DAILY_WORK_MINUTES + "~"
                    + ApsConstants.MAX_DAILY_WORK_MINUTES + "分钟之间(1分钟~24小时)");
            }
        }
    }

    @Override
    public void writePostpose(Farm entity, String userId) {
        super.writePostpose(entity, userId);
    }

    @Override
    public Farm selectById(String id) {
        Farm farm = super.selectById(id);
        // 设置负责人
        iAuthUserService.setDataMation(farm, Farm::getChargePerson);
        iDepmentService.setDataMation(farm, Farm::getDepartmentId);
        return farm;
    }

    @Override
    public List<Farm> selectByIds(String... ids) {
        List<Farm> farms = super.selectByIds(ids);
        // 设置负责人
        iAuthUserService.setDataMation(farms, Farm::getChargePerson);
        iDepmentService.setDataMation(farms, Farm::getDepartmentId);
        return farms;
    }

    @Override
    public List<String> queryFarmIdListByDepartmentId(String departmentId) {
        QueryWrapper<Farm> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Farm::getDepartmentId), departmentId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(Farm::getDeleteFlag), DeleteFlagEnum.NOT_DELETE.getKey());
        List<Farm> farmList = list(queryWrapper);
        List<String> idList = farmList.stream().map(Farm::getId).collect(Collectors.toList());
        return idList;
    }

    @Override
    public void queryMyChargeFarmList(InputObject inputObject, OutputObject outputObject) {
        String userId = inputObject.getLogParams().get("id").toString();
        List<Farm> farmList = queryFarmListByChargePerson(userId);
        outputObject.setBeans(farmList);
        outputObject.settotal(farmList.size());
    }

    @Override
    public List<Farm> queryFarmListByChargePerson(String userId) {
        QueryWrapper<Farm> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Farm::getChargePerson), userId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(Farm::getDeleteFlag), DeleteFlagEnum.NOT_DELETE.getKey());
        List<Farm> farmList = list(queryWrapper);
        return farmList;
    }

    @Override
    public void queryEnabledFarmList(InputObject inputObject, OutputObject outputObject) {
        List<Farm> farmList = queryEnabledFarmList();
        outputObject.setBeans(farmList);
        outputObject.settotal(farmList.size());
    }

    @Override
    public List<Farm> queryEnabledFarmList() {
        QueryWrapper<Farm> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Farm::getEnabled), EnableEnum.ENABLE_USING.getKey());
        queryWrapper.eq(MybatisPlusUtil.toColumns(Farm::getDeleteFlag), DeleteFlagEnum.NOT_DELETE.getKey());
        List<Farm> farmList = list(queryWrapper);
        return farmList;
    }

    @Override
    public List<Farm> queryFarmListByIds(List<String> farmIds) {
        if (CollectionUtil.isEmpty(farmIds)) {
            return Collections.emptyList();
        }
        QueryWrapper<Farm> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(CommonConstants.ID, farmIds);
        return list(queryWrapper) == null ? Collections.emptyList() : list(queryWrapper);
    }

    @Override
    public Map<String, Integer> getDailyWorkMinutesByDateRange(String farmId, String startDateStr, String endDateStr,
                                                               List<FarmCalendar> calendarList, int defaultMinutes) {
        Map<String, Integer> result = new HashMap<>();
        if (farmId == null || StrUtil.isEmpty(startDateStr)) {
            return result;
        }
        LocalDate start = LocalDate.parse(startDateStr, DateTimeFormatter.ISO_LOCAL_DATE);
        LocalDate end = StrUtil.isEmpty(endDateStr) ? start.plusDays(365) : LocalDate.parse(endDateStr, DateTimeFormatter.ISO_LOCAL_DATE);
        if (calendarList == null) {
            calendarList = Collections.emptyList();
        }
        for (LocalDate d = start; !d.isAfter(end); d = d.plusDays(1)) {
            String ds = d.format(DateTimeFormatter.ISO_LOCAL_DATE);
            Integer cap = farmCalendarService.resolveDailyCapFromCalendar(calendarList, ds);
            result.put(ds, cap != null ? cap : defaultMinutes);
        }
        return result;
    }

}
