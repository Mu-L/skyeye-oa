package com.skyeye.scheduling.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.scheduling.dao.SchedulingShiftsTimeDao;
import com.skyeye.scheduling.entity.SchedulingShiftsTime;
import com.skyeye.scheduling.entity.SchedulingShiftsTimeWork;
import com.skyeye.scheduling.service.SchedulingShiftsTimeService;
import com.skyeye.scheduling.service.SchedulingShiftsTimeWorkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@SkyeyeService(name = "排班班次时间管理", groupName = "排班班次时间管理")
public class SchedulingShiftsTimeServiceImpl extends SkyeyeBusinessServiceImpl<SchedulingShiftsTimeDao, SchedulingShiftsTime> implements SchedulingShiftsTimeService {

    @Autowired
    private SchedulingShiftsTimeWorkService schedulingShiftsTimeWorkService;

    @Override
    protected void createPostpose(List<SchedulingShiftsTime> entity, String userId) {
        for (SchedulingShiftsTime schedulingShiftsTime : entity) {
            List<SchedulingShiftsTimeWork> shiftsTimeWorkMation = schedulingShiftsTime.getShiftsTimeWorkMation();
            if (CollectionUtil.isNotEmpty(shiftsTimeWorkMation)) {
                for (SchedulingShiftsTimeWork schedulingShiftsTimeWork : shiftsTimeWorkMation) {
                    schedulingShiftsTimeWork.setShiftsTimeId(schedulingShiftsTime.getId());
                    Integer minStaff = schedulingShiftsTimeWork.getMinStaff();
                    Integer maxStaff = schedulingShiftsTimeWork.getMaxStaff();
                    if (minStaff != null && maxStaff != null && minStaff > maxStaff) {
                        throw new CustomException("最小需求人数不能大于最大需求人数");
                    }
                }
                schedulingShiftsTimeWorkService.createEntity(shiftsTimeWorkMation, userId);
            }
        }
    }

    @Override
    protected void updatePostpose(List<SchedulingShiftsTime> entity, String userId) {
        for (SchedulingShiftsTime schedulingShiftsTime : entity) {
            List<SchedulingShiftsTimeWork> shiftsTimeWorkMation = schedulingShiftsTime.getShiftsTimeWorkMation();
            if (CollectionUtil.isNotEmpty(shiftsTimeWorkMation)) {
                schedulingShiftsTimeWorkService.updateEntity(shiftsTimeWorkMation, userId);
            }
        }
    }

    @Override
    public void deleteSchedulingShiftsTimeByShiftIds(List<String> idList) {
        if (CollectionUtil.isEmpty(idList)) {
            return;
        }
        QueryWrapper<SchedulingShiftsTime> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(SchedulingShiftsTime::getShiftId), idList);
        List<SchedulingShiftsTime> list = list(queryWrapper);
        remove(queryWrapper);
        List<String> shiftsTimeIds = list.stream().map(SchedulingShiftsTime::getId).collect(Collectors.toList());
        schedulingShiftsTimeWorkService.deleteShiftsTimeWorkByShiftsTimeIds(shiftsTimeIds);
    }

    @Override
    public List<SchedulingShiftsTime> queryTimeByIdList(List<String> schedulingShiftsIds) {
        QueryWrapper<SchedulingShiftsTime> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(SchedulingShiftsTime::getShiftId), schedulingShiftsIds);
        return list(queryWrapper);
    }

    @Override
    public List<SchedulingShiftsTime> queryTimeByShiftId(String id) {
        QueryWrapper<SchedulingShiftsTime> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(SchedulingShiftsTime::getShiftId), id);
        return list(queryWrapper);
    }

    @Override
    public Map<String, List<SchedulingShiftsTime>> queryTimeByIdListMap(List<String> schedulingShiftsIdList) {
        List<SchedulingShiftsTime> list = queryTimeByIdList(schedulingShiftsIdList);
        return list.stream().collect(Collectors.groupingBy(SchedulingShiftsTime::getShiftId));
    }

    @Override
    public List<SchedulingShiftsTime> queryShiftsTimeByIdList(List<String> shiftsTimeIdList) {
        QueryWrapper<SchedulingShiftsTime> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(CommonConstants.ID, shiftsTimeIdList);
        return list(queryWrapper);
    }
}
