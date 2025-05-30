package com.skyeye.scheduling.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.scheduling.dao.SchedulingShiftsTimeWorkDao;
import com.skyeye.scheduling.entity.SchedulingShiftsTimeWork;
import com.skyeye.scheduling.service.SchedulingShiftsTimeWorkService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@SkyeyeService(name = "排班班次时间下工位管理", groupName = "排班班次时间下工位管理")
public class SchedulingShiftsTimeWorkServiceImpl extends SkyeyeBusinessServiceImpl<SchedulingShiftsTimeWorkDao, SchedulingShiftsTimeWork> implements SchedulingShiftsTimeWorkService {

    @Override
    public List<SchedulingShiftsTimeWork> queryShiftsTimeWorkByShiftsTimeIds(List<String> shiftsTimeIds) {
        QueryWrapper<SchedulingShiftsTimeWork> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(SchedulingShiftsTimeWork::getShiftsTimeId), shiftsTimeIds);
        return list(queryWrapper);
    }

    @Override
    public void deleteShiftsTimeWorkByShiftsTimeIds(List<String> shiftsTimeIds) {
        if (CollectionUtil.isEmpty(shiftsTimeIds)) {
            return;
        }
        QueryWrapper<SchedulingShiftsTimeWork> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(SchedulingShiftsTimeWork::getShiftsTimeId), shiftsTimeIds);
        remove(queryWrapper);
    }
}
