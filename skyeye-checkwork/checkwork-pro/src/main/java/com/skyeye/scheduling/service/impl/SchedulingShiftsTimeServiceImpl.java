package com.skyeye.scheduling.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.scheduling.dao.SchedulingShiftsTimeDao;
import com.skyeye.scheduling.entity.SchedulingShiftsTime;
import com.skyeye.scheduling.service.SchedulingShiftsTimeService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@SkyeyeService(name = "排班班次时间管理", groupName = "排班班次时间管理")
public class SchedulingShiftsTimeServiceImpl extends SkyeyeBusinessServiceImpl<SchedulingShiftsTimeDao, SchedulingShiftsTime> implements SchedulingShiftsTimeService {

    @Override
    public void deleteSchedulingShiftsTimeByShiftIds(List<String> idList) {
        QueryWrapper<SchedulingShiftsTime> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(SchedulingShiftsTime::getShiftId), idList);
        remove(queryWrapper);
    }

    @Override
    public Map<String, List<SchedulingShiftsTime>> queryTimeByIdList(List<String> schedulingShiftsIds) {
        QueryWrapper<SchedulingShiftsTime> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(SchedulingShiftsTime::getShiftId), schedulingShiftsIds);
        List<SchedulingShiftsTime> list = list(queryWrapper);
        Map<String, List<SchedulingShiftsTime>> stringListMap =
            list.stream().collect(Collectors.groupingBy(SchedulingShiftsTime::getShiftId));
        return stringListMap;
    }

    @Override
    public List<SchedulingShiftsTime> queryTimeByShiftId(String id) {
        QueryWrapper<SchedulingShiftsTime> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(SchedulingShiftsTime::getShiftId), id);
        return list(queryWrapper);
    }
}
