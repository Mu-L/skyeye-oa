/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.schedule.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.client.ExecuteFeignClient;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.QuartzConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.CheckDayType;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.object.PutObject;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.DateAfterSpacePointTime;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ExcelUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.schedule.classenum.ScheduleImported;
import com.skyeye.eve.schedule.classenum.ScheduleRemindType;
import com.skyeye.eve.schedule.classenum.ScheduleState;
import com.skyeye.eve.schedule.dao.ScheduleDayDao;
import com.skyeye.eve.schedule.entity.ScheduleDay;
import com.skyeye.eve.rest.quartz.SysQuartzMation;
import com.skyeye.eve.rest.schedule.OtherModuleScheduleMation;
import com.skyeye.eve.service.IQuartzService;
import com.skyeye.eve.schedule.service.ScheduleDayService;
import com.skyeye.exception.CustomException;
import com.skyeye.rest.checkwork.CheckWorkService;
import com.skyeye.rest.checkwork.entity.DayWorkMationRest;
import com.skyeye.rest.checkwork.entity.UserOtherDayMationRest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import java.io.IOException;
import java.util.*;

/**
 * @ClassName: ScheduleDayServiceImpl
 * @Description: 日程管理服务层
 * @author: skyeye云系列--卫志强
 * @date: 2021/4/24 11:43
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Service
@SkyeyeService(name = "日程管理", groupName = "日程管理")
public class ScheduleDayServiceImpl extends SkyeyeBusinessServiceImpl<ScheduleDayDao, ScheduleDay> implements ScheduleDayService {

    @Autowired
    private IQuartzService iQuartzService;

    @Autowired
    private CheckWorkService checkWorkService;

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void addSchedule(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String startTime = map.get("startTime").toString();
        String name = map.get("name").toString();
        String endTime = map.get("endTime").toString();

        Map<String, Object> m = skyeyeBaseMapper.queryIsnullThistime(startTime, endTime);
        if (CollectionUtil.isNotEmpty(m)) {
            outputObject.setreturnMessage("该节假日时间段与已有的节假日时间段有冲突，请重新设置节假日");
        } else {
            ScheduleDay scheduleDay = new ScheduleDay();
            scheduleDay.setName(name);
            scheduleDay.setStartTime(startTime);
            scheduleDay.setEndTime(endTime);
            scheduleDay.setIsRemind(WhetherEnum.DISABLE_USING.getKey());
            // 是否全天
            scheduleDay.setAllDay(WhetherEnum.ENABLE_USING.getKey());
            // 日程类型
            scheduleDay.setType(CheckDayType.DAY_IS_HOLIDAY.getKey());
            // 提醒时间所属类型
            scheduleDay.setRemindType(ScheduleRemindType.NO_REMINDER.getKey());
            scheduleDay.setState(ScheduleState.NEW_SCHEDULE.getKey());
            scheduleDay.setImported(ScheduleImported.ORDINARY.getKey());
            createEntity(scheduleDay, inputObject.getLogParams().get("id").toString());
        }
    }

    @Override
    public void createPrepose(ScheduleDay entity) {
        String scheduleStartTime = entity.getStartTime();
        String remindTime = DateAfterSpacePointTime.getSpecifiedTime(entity.getRemindType(), scheduleStartTime,
                DateUtil.YYYY_MM_DD_HH_MM_SS, DateAfterSpacePointTime.AroundType.BEFORE);
        if (StrUtil.isNotBlank(remindTime)) {
            if (DateUtil.compare(remindTime, DateUtil.getTimeAndToString())) {
                // 日程提醒时间早于当前时间
                throw new CustomException("日程提醒时间不能早于当前时间");
            } else {
                if (DateUtil.compare(entity.getEndTime(), scheduleStartTime)) {
                    // 结束时间早于开始时间
                    throw new CustomException("日程结束时间不能早于开始时间");
                } else {
                    entity.setRemindTime(remindTime);
                }
            }
        }
    }

    @Override
    public void createPostpose(ScheduleDay entity, String userId) {
        if (StrUtil.isNotEmpty(entity.getRemindTime())) {
            // 定时任务
            startUpTaskQuartz(entity.getId(), entity.getName(), entity.getRemindTime());
        }
    }

    private void startUpTaskQuartz(String name, String title, String delayedTime) {
        SysQuartzMation sysQuartzMation = new SysQuartzMation();
        sysQuartzMation.setName(name);
        sysQuartzMation.setTitle(title);
        sysQuartzMation.setDelayedTime(delayedTime);
        sysQuartzMation.setGroupId(QuartzConstants.QuartzMateMationJobType.MY_SCHEDULEDAY_MATION.getTaskType());
        iQuartzService.startUpTaskQuartz(sysQuartzMation);
    }

    @Override
    @IgnoreTenant
    public List<Map<String, Object>> queryDataList(InputObject inputObject) {
        Map<String, Object> map = inputObject.getParams();
        String userId = inputObject.getLogParams().get("id").toString();
        String yearMonth = map.get("yearMonth").toString();
        String timeId = map.get("checkWorkId").toString();
        List<String> months = DateUtil.getPointMonthAfterMonthList(yearMonth, 2);
        String tenantId = tenantEnable ? TenantContext.getTenantId() : StrUtil.EMPTY;
        // 1.获取当前登录人指定月份的日程信息
        List<Map<String, Object>> beans = skyeyeBaseMapper.queryScheduleDayMationByUserId(userId, months, tenantId);
        beans.forEach(bean -> {
            bean.put("backgroundColor", CheckDayType.getColor(Integer.parseInt(bean.get("type").toString())));
        });
        DayWorkMationRest dayWorkMationParams = new DayWorkMationRest();
        dayWorkMationParams.setBeans(beans);
        dayWorkMationParams.setMonths(months);
        dayWorkMationParams.setTimeId(timeId);
        beans = ExecuteFeignClient.get(() -> checkWorkService.queryDayWorkMation(dayWorkMationParams)).getRows();
        // 2.获取用户指定班次在指定月份的其他日期信息[审核通过的](例如：请假，出差，加班等)
        UserOtherDayMationRest userOtherDayMationParams = new UserOtherDayMationRest();
        userOtherDayMationParams.setTimeId(timeId);
        userOtherDayMationParams.setUserId(userId);
        userOtherDayMationParams.setMonths(months);
        beans.addAll(ExecuteFeignClient.get(() -> checkWorkService.getUserOtherDayMation(userOtherDayMationParams)).getRows());
        return beans;
    }

    @Override
    public void queryTodayScheduleDayByUserId(InputObject inputObject, OutputObject outputObject) {
        String userId = inputObject.getLogParams().get("id").toString();
        List<ScheduleDay> scheduleDayList = getScheduleDayList(userId, DateUtil.getTimeIsYMD());
        outputObject.setBeans(scheduleDayList);
        outputObject.settotal(scheduleDayList.size());
    }

    private List<ScheduleDay> getScheduleDayList(String userId, String timeHms) {
        QueryWrapper<ScheduleDay> queryWrapper = new QueryWrapper<>();
        queryWrapper.apply("date_format(" + MybatisPlusUtil.toColumns(ScheduleDay::getStartTime) + ", '%Y-%m-%d') <= date_format({0}, '%Y-%m-%d')", timeHms)
                .apply("date_format(" + MybatisPlusUtil.toColumns(ScheduleDay::getEndTime) + ", '%Y-%m-%d') >= date_format({0}, '%Y-%m-%d')", timeHms);
        queryWrapper.ne(MybatisPlusUtil.toColumns(ScheduleDay::getType), CheckDayType.DAY_IS_HOLIDAY.getKey());
        queryWrapper.eq(MybatisPlusUtil.toColumns(ScheduleDay::getCreateId), userId);
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(ScheduleDay::getStartTime));
        List<ScheduleDay> list = list(queryWrapper);
        list.forEach(bean -> {
            bean.setImportedName(ScheduleImported.getName(bean.getImported()));
            bean.setBackgroundColor(CheckDayType.getColor(bean.getType()));
        });
        return list;
    }

    @Override
    public void queryScheduleDayByPointHms(InputObject inputObject, OutputObject outputObject) {
        String userId = inputObject.getLogParams().get("id").toString();
        String pointHms = inputObject.getParams().get("pointHms").toString();
        List<ScheduleDay> scheduleDayList = getScheduleDayList(userId, pointHms);
        outputObject.setBeans(scheduleDayList);
        outputObject.settotal(scheduleDayList.size());
    }

    /**
     * 修改日程日期信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void editScheduleDayById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        ScheduleDay scheduleDay = selectById(id);
        int remindTimeType = scheduleDay.getRemindType();
        String startTime = map.get("startTime").toString();
        String endTime = map.get("endTime").toString();
        String remindTime = DateAfterSpacePointTime.getSpecifiedTime(remindTimeType, startTime, DateUtil.YYYY_MM_DD_HH_MM_SS, DateAfterSpacePointTime.AroundType.BEFORE);
        if (StrUtil.isNotBlank(remindTime)) {
            if (DateUtil.compare(remindTime, DateUtil.getTimeAndToString())) {
                // 日程提醒时间早于当前时间,提醒时间则变为当前时间+2分钟；获取当前时间和开始时间相差几分钟
                long minute = DateUtil.getDistanceMinute(remindTime, startTime);
                if (minute >= 2) {
                    // 两分钟后
                    remindTime = DateAfterSpacePointTime.getSpecifiedTime(remindTimeType, DateUtil.getTimeAndToString(),
                            DateUtil.YYYY_MM_DD_HH_MM_SS, DateAfterSpacePointTime.AroundType.AFTER, 2);
                } else if (minute < 2 && minute > 0) {
                    // minute分钟后
                    remindTime = DateAfterSpacePointTime.getSpecifiedTime(remindTimeType, DateUtil.getTimeAndToString(),
                            DateUtil.YYYY_MM_DD_HH_MM_SS, DateAfterSpacePointTime.AroundType.AFTER, (int) minute);
                } else {
                    // 日程开始时
                    remindTime = startTime;
                }
            } else if (DateUtil.compare(endTime, startTime)) {
                // 结束时间早于开始时间
                outputObject.setreturnMessage("日程结束时间不能早于开始时间");
                return;
            }
            // 修改定时任务
            startUpTaskQuartz(id, map.get("name").toString(), remindTime);
        }
        String userId = inputObject.getLogParams().get("id").toString();
        UpdateWrapper<ScheduleDay> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(ScheduleDay::getStartTime), startTime);
        updateWrapper.set(MybatisPlusUtil.toColumns(ScheduleDay::getEndTime), endTime);
        updateWrapper.set(MybatisPlusUtil.toColumns(ScheduleDay::getRemindTime), remindTime);
        updateWrapper.set(MybatisPlusUtil.toColumns(ScheduleDay::getLastUpdateId), userId);
        updateWrapper.set(MybatisPlusUtil.toColumns(ScheduleDay::getLastUpdateTime), DateUtil.getTimeAndToString());
        update(updateWrapper);
    }

    @Override
    public ScheduleDay selectById(String id) {
        ScheduleDay scheduleDay = super.selectById(id);
        scheduleDay.setImportedName(ScheduleImported.getName(scheduleDay.getImported()));
        scheduleDay.setStateName(ScheduleState.getName(scheduleDay.getState()));
        scheduleDay.setTypeName(CheckDayType.getName(scheduleDay.getType()));
        return scheduleDay;
    }

    @Override
    public void deletePostpose(String id) {
        // 删除定时任务
        iQuartzService.stopAndDeleteTaskQuartz(id);
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        if (StrUtil.isNotEmpty(commonPageInfo.getType()) && commonPageInfo.getType().equals(CheckDayType.DAY_IS_HOLIDAY.getKey().toString())) {
        } else {
            commonPageInfo.setCreateId(inputObject.getLogParams().get("id").toString());
        }
        List<Map<String, Object>> beans = skyeyeBaseMapper.queryScheduleList(commonPageInfo);
        for (Map<String, Object> str : beans) {  //遍历数组
            boolean before = DateUtil.compare(DateUtil.getTimeAndToString(), str.get("startTime").toString());
            if (before) {  //当前时间 在 节假日开始时间 之前
                str.put("before", "1");
            } else {
                str.put("before", "2");
            }
        }
        return beans;
    }

    /**
     * 其他模块同步到日程
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void insertScheduleByOtherModule(InputObject inputObject, OutputObject outputObject) {
        OtherModuleScheduleMation scheduleMationParams = inputObject.getParams(OtherModuleScheduleMation.class);
        ScheduleDay scheduleDay = new ScheduleDay();
        scheduleDay.setName(scheduleMationParams.getTitle());
        int length = scheduleMationParams.getContent().length();
        scheduleDay.setRemark(length > 1000 ? scheduleMationParams.getContent().substring(0, 1000) : scheduleMationParams.getContent());
        scheduleDay.setStartTime(scheduleMationParams.getStartTime());
        scheduleDay.setEndTime(scheduleMationParams.getEndTime());
        scheduleDay.setIsRemind(WhetherEnum.ENABLE_USING.getKey());
        // 是否全天
        scheduleDay.setAllDay(WhetherEnum.ENABLE_USING.getKey());
        // 日程类型
        scheduleDay.setType(CheckDayType.DAY_IS_WORK.getKey());
        // 提醒时间所属类型
        scheduleDay.setRemindType(ScheduleRemindType.NO_REMINDER.getKey());
        scheduleDay.setImported(ScheduleImported.ORDINARY.getKey());
        scheduleDay.setObjectId(scheduleMationParams.getObjectId());
        scheduleDay.setObjectType(scheduleMationParams.getObjectType());
        createEntity(scheduleDay, scheduleMationParams.getUserId());
    }

    /**
     * 根据ObjectId删除日程
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void deleteScheduleMationByObjectId(InputObject inputObject, OutputObject outputObject) {
        String objectId = inputObject.getParams().get("objectId").toString();
        QueryWrapper<ScheduleDay> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ScheduleDay::getObjectId), objectId);
        remove(queryWrapper);
    }

    /**
     * 判断指定日期是否是节假日
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void judgeISHoliday(InputObject inputObject, OutputObject outputObject) {
        String day = inputObject.getParams().get("day").toString();
        Map<String, Object> result = new HashMap<>();
        result.put("result", this.judgeISHoliday(day));
        outputObject.setBean(result);
        outputObject.settotal(1);
    }

    /**
     * 判断指定日期是否是节假日
     *
     * @param day 日期，格式为yyyy-mm-dd
     * @return true：是节假日；false：不是节假日
     */
    private boolean judgeISHoliday(String day) {
        QueryWrapper<ScheduleDay> queryWrapper = new QueryWrapper<>();
        queryWrapper.apply("date_format(" + MybatisPlusUtil.toColumns(ScheduleDay::getStartTime) + ", '%Y-%m-%d') <= date_format({0}, '%Y-%m-%d')", day)
                .apply("date_format(" + MybatisPlusUtil.toColumns(ScheduleDay::getEndTime) + ", '%Y-%m-%d') >= date_format({0}, '%Y-%m-%d')", day);
        queryWrapper.eq(MybatisPlusUtil.toColumns(ScheduleDay::getType), CheckDayType.DAY_IS_HOLIDAY.getKey());
        List<ScheduleDay> scheduleDays = list(queryWrapper);
        if (CollectionUtil.isEmpty(scheduleDays)) {
            return false;
        }
        return true;
    }

    @Override
    public void deleteHolidayScheduleByThisYear(InputObject inputObject, OutputObject outputObject) {
        QueryWrapper<ScheduleDay> queryWrapper = new QueryWrapper<>();
        queryWrapper.apply("YEAR(" + MybatisPlusUtil.toColumns(ScheduleDay::getStartTime) + ") = YEAR(NOW())");
        queryWrapper.eq(MybatisPlusUtil.toColumns(ScheduleDay::getType), CheckDayType.DAY_IS_HOLIDAY.getKey());
        List<ScheduleDay> scheduleDays = list(queryWrapper);
        scheduleDays.forEach(scheduleDay -> {
            // 删除定时任务
            iQuartzService.stopAndDeleteTaskQuartz(scheduleDay.getId());
        });
        remove(queryWrapper);
    }

    @Override
    public void editScheduleStateById(String id, Integer state) {
        UpdateWrapper<ScheduleDay> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(ScheduleDay::getState), state);
        update(updateWrapper);
    }

    /**
     * 下载节假日导入模板
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void downloadScheduleTemplate(InputObject inputObject, OutputObject outputObject) {
        String[] key = new String[9];
        String[] column = new String[]{"标题", "开始日期", "结束日期"};
        String[] dataType = new String[]{"", "data", "data"};
        ExcelUtil.createWorkBook("节假日模板", "节假日", null, key, column, dataType, PutObject.getResponse());
    }

    /**
     * 导入节假日日程
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void exploreScheduleTemplate(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> user = inputObject.getLogParams();
        // 将当前上下文初始化给 CommonsMutipartResolver （多部分解析器）
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(PutObject.getRequest().getSession().getServletContext());
        // 检查form中是否有enctype="multipart/form-data"
        if (multipartResolver.isMultipart(PutObject.getRequest())) {
            // 将request变成多部分request
            MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) PutObject.getRequest();
            // 获取multiRequest 中所有的文件名
            Iterator iter = multiRequest.getFileNames();
            //循环内使用参数
            MultipartFile file;//获取到文件
            List<List<String>> list;//excel读取到的数据
            String nowTime = DateUtil.getTimeAndToString();//当前时间
            List<String> row;//获取每一行的数据
            while (iter.hasNext()) {
                // 一次遍历所有文件
                file = multiRequest.getFile(iter.next().toString());
                try {
                    list = ExcelUtil.readExcelContent(file.getInputStream());
                } catch (IOException e) {
                    throw new CustomException(e);
                }
                List<ScheduleDay> beans = new ArrayList<>();
                for (int i = 0, j = list.size(); i < j; i++) {
                    row = list.get(i);
                    if (!ToolUtil.isBlank(row.get(1).toString()) && !ToolUtil.isBlank(row.get(1).toString())) {
                        String startTime = row.get(1).toString() + " 00:00:00";//日程开始时间
                        String endTime = row.get(2).toString() + " 23:59:59";//日程结束时间
                        ScheduleDay scheduleDay = new ScheduleDay();
                        scheduleDay.setStartTime(startTime);
                        scheduleDay.setEndTime(endTime);
                        scheduleDay.setIsRemind(WhetherEnum.DISABLE_USING.getKey());
                        // 是否全天
                        scheduleDay.setAllDay(WhetherEnum.ENABLE_USING.getKey());
                        // 日程类型
                        scheduleDay.setType(CheckDayType.DAY_IS_HOLIDAY.getKey());
                        // 提醒时间所属类型
                        scheduleDay.setRemindType(ScheduleRemindType.NO_REMINDER.getKey());
                        scheduleDay.setImported(ScheduleImported.ORDINARY.getKey());
                        if (DateUtil.compare(startTime, nowTime)) {//日程开始时间早于当前时间
                            scheduleDay.setState(ScheduleState.REMINDED_SCHEDULE.getKey());//已结束
                        } else {
                            scheduleDay.setState(ScheduleState.NEW_SCHEDULE.getKey());//新日程
                        }
                        if (ToolUtil.isBlank(row.get(0).toString())) {
                            scheduleDay.setName("休息日");
                        } else {
                            scheduleDay.setName(row.get(0));
                        }
                        beans.add(scheduleDay);
                    }
                }
                createEntity(beans, user.get("id").toString());
                map.put("uploadNum", beans.size());
                outputObject.setBean(map);
            }
        }
    }

    /**
     * 添加节假日日程提醒
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void addHolidayScheduleRemind(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        ScheduleDay scheduleDay = selectById(id);
        int remindType = Integer.parseInt(map.get("remindType").toString());
        String scheduleStartTime = map.get("scheduleStartTime").toString();
        String remindTime = DateAfterSpacePointTime.getSpecifiedTime(remindType, scheduleStartTime, DateUtil.YYYY_MM_DD_HH_MM_SS, DateAfterSpacePointTime.AroundType.BEFORE);
        if (StrUtil.isNotEmpty(remindTime)) {
            if (DateUtil.compare(remindTime, DateUtil.getTimeAndToString())) {
                // 日程提醒时间早于当前时间
                outputObject.setreturnMessage("日程提醒时间不能早于当前时间");
            } else {
                if (DateUtil.compare(scheduleDay.getEndTime(), scheduleStartTime)) {
                    // 结束时间早于开始时间
                    outputObject.setreturnMessage("日程结束时间不能早于开始时间");
                } else {
                    UpdateWrapper<ScheduleDay> updateWrapper = new UpdateWrapper<>();
                    updateWrapper.eq(CommonConstants.ID, id);
                    updateWrapper.set(MybatisPlusUtil.toColumns(ScheduleDay::getRemindType), remindType);
                    updateWrapper.set(MybatisPlusUtil.toColumns(ScheduleDay::getIsRemind), WhetherEnum.ENABLE_USING.getKey());
                    updateWrapper.set(MybatisPlusUtil.toColumns(ScheduleDay::getRemindTime), remindTime);
                    update(updateWrapper);
                    // 定时任务
                    startAllUpTaskQuartz(map.get("id").toString(), scheduleDay.getName(), remindTime);
                }
            }
        } else {
            outputObject.setreturnMessage("参数错误");
        }
    }

    private void startAllUpTaskQuartz(String name, String title, String delayedTime) {
        SysQuartzMation sysQuartzMation = new SysQuartzMation();
        sysQuartzMation.setName(name);
        sysQuartzMation.setTitle(title);
        sysQuartzMation.setDelayedTime(delayedTime);
        sysQuartzMation.setGroupId(QuartzConstants.QuartzMateMationJobType.ALL_SCHEDULE_MATION.getTaskType());
        iQuartzService.startUpTaskQuartz(sysQuartzMation);
    }

    /**
     * 取消节假日日程提醒
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void deleteHolidayScheduleRemind(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        // 修改节假日信息
        UpdateWrapper<ScheduleDay> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(ScheduleDay::getRemindType), ScheduleRemindType.NO_REMINDER.getKey());
        updateWrapper.set(MybatisPlusUtil.toColumns(ScheduleDay::getIsRemind), WhetherEnum.DISABLE_USING.getKey());
        update(updateWrapper);
        // 删除定时任务
        iQuartzService.stopAndDeleteTaskQuartz(id);
    }

    /**
     * 获取所有节假日
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryHolidayScheduleListBySys(InputObject inputObject, OutputObject outputObject) {
        QueryWrapper<ScheduleDay> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ScheduleDay::getType), CheckDayType.DAY_IS_HOLIDAY.getKey());
        List<ScheduleDay> scheduleDays = list(queryWrapper);
        outputObject.setBeans(scheduleDays);
        outputObject.settotal(scheduleDays.size());
    }

    @Override
    public void queryMyAgencyList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo pageInfo = inputObject.getParams(CommonPageInfo.class);
        pageInfo.setCreateId(inputObject.getLogParams().get("id").toString());
        Page pages = PageHelper.startPage(pageInfo.getPage(), pageInfo.getLimit());
        List<Map<String, Object>> beans = skyeyeBaseMapper.queryMyAgencyList(pageInfo);
        outputObject.setBeans(beans);
        outputObject.settotal(pages.getTotal());
    }

}
