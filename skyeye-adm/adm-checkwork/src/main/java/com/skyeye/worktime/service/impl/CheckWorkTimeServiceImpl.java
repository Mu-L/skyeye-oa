/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.worktime.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.client.ExecuteFeignClient;
import com.skyeye.common.constans.CommonCharConstants;
import com.skyeye.common.enumeration.DeleteFlagEnum;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.centerrest.user.SysEveUserService;
import com.skyeye.exception.CustomException;
import com.skyeye.rest.pro.rest.ISysEveUserStaffTimeRest;
import com.skyeye.worktime.classenum.CheckWorkTimeType;
import com.skyeye.worktime.dao.CheckWorkTimeDao;
import com.skyeye.worktime.entity.CheckWorkTime;
import com.skyeye.worktime.entity.CheckWorkTimePoint;
import com.skyeye.worktime.entity.CheckWorkTimeWeek;
import com.skyeye.worktime.service.CheckWorkTimePointService;
import com.skyeye.worktime.service.CheckWorkTimeService;
import com.skyeye.worktime.service.CheckWorkTimeWeekService;
import com.skyeye.worktime.util.CheckWorkTimeWeekUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: CheckWorkTimeServiceImpl
 * @Description: 考勤班次管理服务层--强隔离
 * @author: skyeye云系列--卫志强
 * @date: 2023/4/3 14:38
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "考勤班次", groupName = "考勤班次")
public class CheckWorkTimeServiceImpl extends SkyeyeBusinessServiceImpl<CheckWorkTimeDao, CheckWorkTime> implements CheckWorkTimeService {

    @Autowired
    private CheckWorkTimeWeekService checkWorkTimeWeekService;

    @Autowired
    private CheckWorkTimePointService checkWorkTimePointService;

    @Autowired
    private SysEveUserService sysEveUserService;

    @Autowired
    private ISysEveUserStaffTimeRest iSysEveUserStaffTimeRest;

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        setStaffCountForList(beans);
        return beans;
    }

    @Override
    protected void validatorEntity(CheckWorkTime entity) {
        super.validatorEntity(entity);
        if (!DateUtil.compareTimeHMS(entity.getStartTime() + ":00", entity.getEndTime() + ":00")) {
            throw new CustomException("工作开始时间必须早于结束时间。");
        }
        boolean hasRestStart = StrUtil.isNotBlank(entity.getRestStartTime());
        boolean hasRestEnd = StrUtil.isNotBlank(entity.getRestEndTime());
        if (hasRestStart != hasRestEnd) {
            throw new CustomException("作息开始时间和结束时间需同时填写或同时为空。");
        }
        if (hasRestStart) {
            if (!DateUtil.compareTimeHMS(entity.getRestStartTime() + ":00", entity.getRestEndTime() + ":00")) {
                throw new CustomException("作息开始时间必须早于结束时间。");
            }
            if (!DateUtil.compareTimeHMS(entity.getStartTime() + ":00", entity.getRestStartTime() + ":00")
                || !DateUtil.compareTimeHMS(entity.getRestEndTime() + ":00", entity.getEndTime() + ":00")) {
                throw new CustomException("作息时间必须在工作时间范围内。");
            }
        }
        if (CollectionUtil.isEmpty(entity.getCheckWorkTimeWeekList())) {
            throw new CustomException("请配置工作日。");
        }
    }

    @Override
    protected void writePostpose(CheckWorkTime entity, String userId) {
        super.writePostpose(entity, userId);
        checkWorkTimeWeekService.saveCheckWorkTimeWeekList(entity.getId(), entity.getCheckWorkTimeWeekList(), userId);
    }

    @Override
    protected void deletePreExecution(String id) {
        if (getStaffCountByTimeId(id) > 0) {
            throw new CustomException("该考勤班次已被员工使用，无法删除。");
        }
        checkWorkTimePointService.deleteByTimeId(id);
    }

    @Override
    public CheckWorkTime getDataFromDb(String id) {
        CheckWorkTime checkWorkTime = super.getDataFromDb(id);
        checkWorkTime.setCheckWorkTimeWeekList(checkWorkTimeWeekService.selectByTimeId(checkWorkTime.getId()));
        checkWorkTime.setCheckWorkTimePointList(checkWorkTimePointService.selectByTimeId(checkWorkTime.getId()));
        return checkWorkTime;
    }

    @Override
    public CheckWorkTime selectById(String id) {
        CheckWorkTime checkWorkTime = super.selectById(id);
        checkWorkTime.setTypeName(CheckWorkTimeType.getShowName(checkWorkTime.getType()));
        checkWorkTime.setStaffCount(getStaffCountByTimeId(id));
        return checkWorkTime;
    }

    @Override
    protected List<CheckWorkTime> getDataFromDb(List<String> idList) {
        List<CheckWorkTime> checkWorkTimeList = super.getDataFromDb(idList);
        Map<String, List<CheckWorkTimeWeek>> weekMap = checkWorkTimeWeekService.selectByTimeId(idList);
        Map<String, List<CheckWorkTimePoint>> pointMap = checkWorkTimePointService.selectByTimeId(idList);
        checkWorkTimeList.forEach(checkWorkTime -> {
            checkWorkTime.setCheckWorkTimeWeekList(weekMap.get(checkWorkTime.getId()));
            List<CheckWorkTimePoint> pointList = pointMap.get(checkWorkTime.getId());
            checkWorkTime.setCheckWorkTimePointList(CollectionUtil.isNotEmpty(pointList) ? pointList : new ArrayList<>());
        });
        return checkWorkTimeList;
    }

    @Override
    public List<CheckWorkTime> selectByIds(String... ids) {
        List<CheckWorkTime> checkWorkTimes = super.selectByIds(ids);
        checkWorkTimes.forEach(checkWorkTime -> {
            checkWorkTime.setTypeName(CheckWorkTimeType.getShowName(checkWorkTime.getType()));
        });
        return checkWorkTimes;
    }

    @Override
    public void queryEnableCheckWorkTimeList(InputObject inputObject, OutputObject outputObject) {
        QueryWrapper<CheckWorkTime> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(CheckWorkTime::getEnabled), EnableEnum.ENABLE_USING.getKey());
        queryWrapper.eq(MybatisPlusUtil.toColumns(CheckWorkTime::getDeleteFlag), DeleteFlagEnum.NOT_DELETE.getKey());
        List<CheckWorkTime> checkWorkTimeList = list(queryWrapper);
        outputObject.setBeans(checkWorkTimeList);
        outputObject.settotal(checkWorkTimeList.size());
    }

    @Override
    public void queryCheckWorkTimeListByLoginUser(InputObject inputObject, OutputObject outputObject) {
        String staffId = inputObject.getLogParams().get("staffId").toString();
        // 获取员工绑定的考勤班次信息
        List<Map<String, Object>> workTime = ExecuteFeignClient.get(() -> sysEveUserService.queryStaffCheckWorkTimeRelationNameByStaffId(staffId)).getRows();
        List<String> timeIds = workTime.stream().map(bean -> bean.get("timeId").toString()).collect(Collectors.toList());

        List<CheckWorkTime> checkWorkTimes = selectByIds(timeIds.toArray(new String[]{}));
        checkWorkTimes = checkWorkTimes.stream()
            .filter(item -> EnableEnum.ENABLE_USING.getKey().equals(item.getEnabled()))
            .collect(Collectors.toList());
        outputObject.setBeans(checkWorkTimes);
        outputObject.settotal(checkWorkTimes.size());
    }

    @Override
    public void getAllCheckWorkTime(InputObject inputObject, OutputObject outputObject) {
        String pointMonthDate = inputObject.getParams().get("pointMonthDate").toString();
        List<CheckWorkTime> beans = this.getAllCheckWorkTime(pointMonthDate);
        outputObject.setBeans(beans);
        outputObject.settotal(beans.size());
    }

    /**
     * 根据指定年月获取所有的考勤班次的信息以及工作日信息等
     *
     * @param pointMonthDate 指定年月，格式为yyyy-MM
     * @return
     */
    @Override
    public List<CheckWorkTime> getAllCheckWorkTime(String pointMonthDate) {
        List<CheckWorkTime> checkWorkTimes = queryAllData().stream()
            .filter(item -> EnableEnum.ENABLE_USING.getKey().equals(item.getEnabled()))
            .collect(Collectors.toList());
        List<String> lastMonthDays = DateUtil.getMonthFullDay(Integer.parseInt(pointMonthDate.split("-")[0]), Integer.parseInt(pointMonthDate.split("-")[1]));
        for (CheckWorkTime bean : checkWorkTimes) {
            List<String> workDays = new ArrayList<>();
            for (String day : lastMonthDays) {
                if (CheckWorkTimeWeekUtil.isWorkDay(day, bean.getCheckWorkTimeWeekList())) {
                    workDays.add(day);
                }
            }
            bean.setWorkDays(workDays);
        }
        return checkWorkTimes;
    }

    @Override
    public void setOnlineCheckWorkTime(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String timeId = params.get("id").toString();
        String userId = inputObject.getLogParams().get("id").toString();
        List<CheckWorkTimePoint> pointList = JSONUtil.toList(params.get("checkWorkTimePointList").toString(), CheckWorkTimePoint.class);
        checkWorkTimePointService.saveCheckWorkTimePointList(timeId, pointList, userId);
        refreshCache(timeId);
    }

    @Override
    public void copyCheckWorkTime(InputObject inputObject, OutputObject outputObject) {
        String sourceId = inputObject.getParams().get("id").toString();
        String userId = inputObject.getLogParams().get("id").toString();
        CheckWorkTime source = selectById(sourceId);

        CheckWorkTime copy = new CheckWorkTime();
        copy.setName(buildCopyName(source.getName()));
        copy.setRemark(source.getRemark());
        copy.setStartTime(source.getStartTime());
        copy.setEndTime(source.getEndTime());
        copy.setRestStartTime(source.getRestStartTime());
        copy.setRestEndTime(source.getRestEndTime());
        copy.setType(source.getType());
        copy.setEnabled(source.getEnabled());
        copy.setOnlineClockEnabled(source.getOnlineClockEnabled());
        copy.setWebClockEnabled(source.getWebClockEnabled());
        copy.setCheckWorkTimeWeekList(cloneWeekList(source.getCheckWorkTimeWeekList()));
        createEntity(copy, userId);

        if (CollectionUtil.isNotEmpty(source.getCheckWorkTimePointList())) {
            List<CheckWorkTimePoint> pointList = source.getCheckWorkTimePointList().stream()
                .map(this::clonePoint)
                .collect(Collectors.toList());
            checkWorkTimePointService.saveCheckWorkTimePointList(copy.getId(), pointList, userId);
            refreshCache(copy.getId());
        }
        copy.setStaffCount(0);
        outputObject.setBean(copy);
    }

    private static final String COPY_NAME_SUFFIX = "-副本";

    /**
     * 批量填充班次使用人数
     */
    private void setStaffCountForList(List<Map<String, Object>> beans) {
        if (CollectionUtil.isEmpty(beans)) {
            return;
        }
        List<String> timeIds = beans.stream()
            .map(bean -> bean.get("id").toString()).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(timeIds)) {
            beans.forEach(bean -> bean.put("staffCount", 0));
            return;
        }
        Map<String, Integer> staffCountMap = queryStaffCountMap(timeIds);
        for (Map<String, Object> bean : beans) {
            bean.put("staffCount", staffCountMap.getOrDefault(bean.get("id").toString(), 0));
        }
    }

    private int getStaffCountByTimeId(String timeId) {
        return queryStaffCountMap(java.util.Collections.singletonList(timeId)).getOrDefault(timeId, 0);
    }

    private Map<String, Integer> queryStaffCountMap(List<String> timeIds) {
        List<Map<String, Object>> countRows = ExecuteFeignClient.get(() ->
            iSysEveUserStaffTimeRest.countSysEveUserStaffTimeByTimeIds(String.join(CommonCharConstants.COMMA_MARK, timeIds))).getRows();
        if (CollectionUtil.isEmpty(countRows)) {
            return new HashMap<>();
        }
        Map<String, Integer> staffCountMap = new HashMap<>();
        for (Map<String, Object> row : countRows) {
            if (row.get("timeId") == null) {
                continue;
            }
            Object staffCount = row.get("staffCount");
            int count = 0;
            if (staffCount instanceof Number) {
                count = ((Number) staffCount).intValue();
            } else if (staffCount != null) {
                count = Integer.parseInt(staffCount.toString());
            }
            staffCountMap.put(row.get("timeId").toString(), count);
        }
        return staffCountMap;
    }

    private String buildCopyName(String name) {
        String sourceName = StrUtil.blankToDefault(name, "班次");
        if (sourceName.endsWith(COPY_NAME_SUFFIX)) {
            return sourceName.length() > 100 ? sourceName.substring(0, 100) : sourceName;
        }
        if (sourceName.length() + COPY_NAME_SUFFIX.length() > 100) {
            return sourceName.substring(0, 100 - COPY_NAME_SUFFIX.length()) + COPY_NAME_SUFFIX;
        }
        return sourceName + COPY_NAME_SUFFIX;
    }

    private List<CheckWorkTimeWeek> cloneWeekList(List<CheckWorkTimeWeek> weekList) {
        if (CollectionUtil.isEmpty(weekList)) {
            return new ArrayList<>();
        }
        return weekList.stream().map(week -> {
            CheckWorkTimeWeek item = new CheckWorkTimeWeek();
            item.setWeekNumber(week.getWeekNumber());
            item.setType(week.getType());
            return item;
        }).collect(Collectors.toList());
    }

    private CheckWorkTimePoint clonePoint(CheckWorkTimePoint source) {
        CheckWorkTimePoint point = new CheckWorkTimePoint();
        point.setName(source.getName());
        point.setLongitude(source.getLongitude());
        point.setLatitude(source.getLatitude());
        point.setAbsoluteAddress(source.getAbsoluteAddress());
        point.setProvinceId(source.getProvinceId());
        point.setCityId(source.getCityId());
        point.setAreaId(source.getAreaId());
        point.setTownshipId(source.getTownshipId());
        point.setRadius(source.getRadius());
        point.setOrderBy(source.getOrderBy());
        return point;
    }

}
