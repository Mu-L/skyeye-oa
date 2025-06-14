package com.skyeye.scheduling.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.scheduling.dao.SchedulingTimeDao;
import com.skyeye.scheduling.entity.SchedulingTime;
import com.skyeye.scheduling.entity.SchedulingTimeWork;
import com.skyeye.scheduling.service.SchedulingTimeService;
import com.skyeye.scheduling.service.SchedulingTimeWorkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@SkyeyeService(name = "排班时间段管理", groupName = "排班时间段管理")
public class SchedulingTimeServiceImpl extends SkyeyeBusinessServiceImpl<SchedulingTimeDao, SchedulingTime> implements SchedulingTimeService {

    @Autowired
    private SchedulingTimeWorkService schedulingTimeWorkService;

    @Override
    protected void createPrepose(List<SchedulingTime> entity) {
        for (SchedulingTime schedulingTime : entity) {
            String startTime = schedulingTime.getStartTime();
            String endTime = schedulingTime.getEndTime();
            boolean compareTimeHMS = DateUtil.compareTimeHMS(startTime, endTime);
            if (!compareTimeHMS) {
                throw new RuntimeException("班次开始时间不能大于结束时间");
            }
            Integer minStaff = schedulingTime.getMinStaff();
            Integer maxStaff = schedulingTime.getMaxStaff();
            if (minStaff > maxStaff) {
                throw new RuntimeException("最小需求人数不能大于最大需求人数");
            }
        }
    }

    @Override
    protected void createPostpose(List<SchedulingTime> entity, String userId) {
        if (CollectionUtil.isEmpty(entity)) {
            return;
        }
        for (SchedulingTime schedulingTime : entity) {
            List<SchedulingTimeWork> schedulingTimeWorkMation = schedulingTime.getSchedulingTimeWorkMation();
            for (SchedulingTimeWork schedulingTimeWork : schedulingTimeWorkMation) {
                schedulingTimeWork.setSchedulingId(schedulingTime.getSchedulingId());
                schedulingTimeWork.setSchedulingTimeId(schedulingTime.getId());
            }
        }
        // 获取每个排班时间段下多个排班工位
        List<List<SchedulingTimeWork>> SchedulingTimeWorkList = entity.stream().map(SchedulingTime::getSchedulingTimeWorkMation).collect(Collectors.toList());
        List<SchedulingTimeWork> flattenedList = SchedulingTimeWorkList.stream().flatMap(List::stream).collect(Collectors.toList());
        schedulingTimeWorkService.createEntity(flattenedList, userId);
    }

    @Override
    protected void updatePostpose(List<SchedulingTime> entity, String userId) {
        // 1. 获取所有工位信息（扁平化）
        List<SchedulingTimeWork> schedulingTimeWorks = entity.stream()
            .flatMap(schedulingTime -> schedulingTime.getSchedulingTimeWorkMation().stream())
            .collect(Collectors.toList());

        // 2. 按ID分类
        // 2.1 获取所有非空ID的工位信息
        List<SchedulingTimeWork> nonEmptyIdWorks = schedulingTimeWorks.stream()
            .filter(work -> work.getId() != null && !work.getId().isEmpty())
            .collect(Collectors.toList());
        // 2.2 获取所有空ID的工位信息
        List<SchedulingTimeWork> emptyIdWorks = schedulingTimeWorks.stream()
            .filter(work -> work.getId() == null || work.getId().isEmpty())
            .collect(Collectors.toList());

        // 3. 收集所有需要删除的工位ID（批量处理）
        List<String> allDeleteIds = new ArrayList<>();
        for (SchedulingTime schedulingTime : entity) {
            List<SchedulingTimeWork> works = schedulingTime.getSchedulingTimeWorkMation();
            String schedulingId = schedulingTime.getSchedulingId();
            String timeId = schedulingTime.getId();

            // 查询数据库中的工位信息
            List<SchedulingTimeWork> dbWorks = schedulingTimeWorkService.querySchedulingTimeWorkBySchedulingIdAndId(schedulingId, timeId);
            List<String> dbWorkIds = dbWorks.stream().map(SchedulingTimeWork::getId).collect(Collectors.toList());

            // 入参工位ID（非空）
            List<String> inputWorkIds = works.stream()
                .map(SchedulingTimeWork::getId)
                .filter(id -> id != null && !id.isEmpty())
                .collect(Collectors.toList());
            // 找出需要删除的工位ID（数据库有但入参没有）
            List<String> deleteIds = dbWorkIds.stream()
                .filter(id -> !inputWorkIds.contains(id))
                .collect(Collectors.toList());

            allDeleteIds.addAll(deleteIds);
        }
        if (CollectionUtil.isNotEmpty(allDeleteIds)) {
            schedulingTimeWorkService.deleteBySchedulingTimeWorkIds(allDeleteIds);
        }

        // 5. 处理新增和更新
        if (CollectionUtil.isNotEmpty(emptyIdWorks)) {
            schedulingTimeWorkService.createEntity(emptyIdWorks, userId);
        }
        if (CollectionUtil.isNotEmpty(nonEmptyIdWorks)) {
            schedulingTimeWorkService.updateEntity(nonEmptyIdWorks, userId);
        }
    }

    @Override
    public List<SchedulingTime> querySchedulingTimeBySchedulingId(String id) {
        QueryWrapper<SchedulingTime> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(SchedulingTime::getSchedulingId), id);
        List<SchedulingTime> schedulingTimeList = list(queryWrapper);
        List<String> collect = schedulingTimeList.stream().map(SchedulingTime::getId).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(collect)) {
            List<SchedulingTimeWork> schedulingTimeWorks = schedulingTimeWorkService.querySchedulingTimeByTimeIdAndId(id, collect);
            Map<String, List<SchedulingTimeWork>> stringListMap =
                schedulingTimeWorks.stream().collect(Collectors.groupingBy(SchedulingTimeWork::getSchedulingTimeId));
            for (SchedulingTime schedulingTime : schedulingTimeList) {
                schedulingTime.setSchedulingTimeWorkMation(stringListMap.get(schedulingTime.getId()));
            }
        }
        return schedulingTimeList;
    }

    @Override
    public void deleteBySchedulingTimeIds(List<String> deleteSchedulingTimeIds) {
        schedulingTimeWorkService.deleteBySchedulingTimeIds(deleteSchedulingTimeIds);
        QueryWrapper<SchedulingTime> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(CommonConstants.ID, deleteSchedulingTimeIds);
        remove(queryWrapper);
    }

    @Override
    public List<SchedulingTime> querySchedulingTimeByTimeIds(List<String> timeIds) {
        if (CollectionUtil.isEmpty(timeIds)) {
            return Collections.emptyList();
        }
        QueryWrapper<SchedulingTime> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(CommonConstants.ID, timeIds);
        return list(queryWrapper);
    }

    @Override
    public void deleteBySchedulingIds(List<String> ids) {
        schedulingTimeWorkService.deleteBySchedulingIds(ids);
        QueryWrapper<SchedulingTime> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(SchedulingTime::getSchedulingId), ids);
        remove(queryWrapper);
    }

}
