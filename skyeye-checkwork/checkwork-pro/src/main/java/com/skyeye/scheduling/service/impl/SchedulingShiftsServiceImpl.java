package com.skyeye.scheduling.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.scheduling.dao.SchedulingShiftsDao;
import com.skyeye.scheduling.entity.SchedulingShifts;
import com.skyeye.scheduling.service.SchedulingShiftsService;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;

@Service
@SkyeyeService(name = "排班班次管理", groupName = "排班班次管理")
public class SchedulingShiftsServiceImpl extends SkyeyeBusinessServiceImpl<SchedulingShiftsDao, SchedulingShifts> implements SchedulingShiftsService {

    @Override
    protected void createPrepose(SchedulingShifts entity) {
        String startTime = entity.getStartTime();
        String endTime = entity.getEndTime();

        if (StrUtil.isNotEmpty(startTime) && StrUtil.isNotEmpty(endTime)) {
            LocalTime startLocalTime = LocalTime.parse(startTime);
            LocalTime endLocalTime = LocalTime.parse(endTime);

            boolean isCrossDay = startLocalTime.isAfter(endLocalTime);

            // 如果跨日，调整逻辑
            if (isCrossDay) {
            } else {
                if (startLocalTime.isAfter(endLocalTime)) {
                    throw new CustomException("开始时间不能大于结束时间");
                }
            }
        }

        QueryWrapper<SchedulingShifts> queryWrapper = new QueryWrapper<>();
        queryWrapper.gt(MybatisPlusUtil.toColumns(SchedulingShifts::getStartTime), startTime);
        queryWrapper.lt(MybatisPlusUtil.toColumns(SchedulingShifts::getEndTime), endTime);
        List<SchedulingShifts> schedulingShifts = list(queryWrapper);

        if (CollectionUtil.isNotEmpty(schedulingShifts)) {
            throw new CustomException("班次时间冲突");
        }
    }

    @Override
    public void deleteSchedulingShifts(InputObject inputObject, OutputObject outputObject) {
        String ids = inputObject.getParams().get("ids").toString();
        QueryWrapper<SchedulingShifts> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(CommonConstants.ID, ids);
        remove(queryWrapper);
    }
}
