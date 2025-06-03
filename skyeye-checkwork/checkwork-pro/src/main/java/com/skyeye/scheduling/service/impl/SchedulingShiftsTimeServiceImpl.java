package com.skyeye.scheduling.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
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
import java.util.Set;
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
        List<String> shiftsTimeIds = entity.stream().map(SchedulingShiftsTime::getId).collect(Collectors.toList());
        List<SchedulingShiftsTimeWork> schedulingShiftsTimeWorks = schedulingShiftsTimeWorkService.queryShiftsTimeWorkByShiftsTimeIds(shiftsTimeIds);
        // 时间段id对应的工位信息
        Map<String, List<SchedulingShiftsTimeWork>> shiftsTimeMapWork = schedulingShiftsTimeWorks.stream().collect(Collectors.groupingBy(SchedulingShiftsTimeWork::getShiftsTimeId));
        for (SchedulingShiftsTime schedulingShiftsTime : entity) {
            // 每个时间段下数据库中所有工位信息
            List<SchedulingShiftsTimeWork> shiftsTimeWorks = shiftsTimeMapWork.get(schedulingShiftsTime.getId());
            // 每个时间段下参数传过来的工位信息
            List<SchedulingShiftsTimeWork> shiftsTimeWorkMation = schedulingShiftsTime.getShiftsTimeWorkMation();
            // 提取 shiftsTimeWorkMation 中的所有 id
            Set<String> mationIds = shiftsTimeWorkMation.stream().map(SchedulingShiftsTimeWork::getId).collect(Collectors.toSet());
            // 过滤 shiftsTimeWorks 中 id 不在 mationIds 中的 SchedulingShiftsTimeWork 对象
            List<SchedulingShiftsTimeWork> missingShiftsTimeWorks = shiftsTimeWorks.stream().filter(work -> !mationIds.contains(work.getId()))
                .collect(Collectors.toList());
            // 获取所有不在参数在数据库中的时间段工位id
            List<String> NotShiftsTimeWorkIds = missingShiftsTimeWorks.stream().map(SchedulingShiftsTimeWork::getId).collect(Collectors.toList());
            // 删除多余时间段下工位信息
            schedulingShiftsTimeWorkService.deleteShiftsTimeWorkByShiftsTimeIds(NotShiftsTimeWorkIds);
            // 找出 id 为空的 SchedulingShiftsTimeWork 对象
            List<SchedulingShiftsTimeWork> emptyIdShiftsTimeWorks = shiftsTimeWorkMation.stream().filter(shift -> StrUtil.isEmpty(shift.getId()))
                .collect(Collectors.toList());
            schedulingShiftsTimeWorkService.createEntity(emptyIdShiftsTimeWorks, userId);
            // 找出 id 不为空的 SchedulingShiftsTimeWork 对象
            List<SchedulingShiftsTimeWork> IdShiftsTimeWorks = shiftsTimeWorkMation.stream().filter(shift -> StrUtil.isNotEmpty(shift.getId()))
                .collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(IdShiftsTimeWorks)) {
                schedulingShiftsTimeWorkService.updateEntity(IdShiftsTimeWorks, userId);
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

    @Override
    public List<SchedulingShiftsTime> queryShiftsTimeById(String id) {
        QueryWrapper<SchedulingShiftsTime> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(SchedulingShiftsTime::getShiftId), id);
        return list(queryWrapper);
    }
}
