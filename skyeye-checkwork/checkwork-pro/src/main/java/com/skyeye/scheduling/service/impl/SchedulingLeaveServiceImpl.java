package com.skyeye.scheduling.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.scheduling.classenum.ScheduleLeaveType;
import com.skyeye.scheduling.dao.SchedulingLeaveDao;
import com.skyeye.scheduling.entity.SchedulingLeave;
import com.skyeye.scheduling.service.SchedulingLeaveService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@SkyeyeService(name = "排班请假管理", groupName = "排班请假管理")
public class SchedulingLeaveServiceImpl extends SkyeyeBusinessServiceImpl<SchedulingLeaveDao, SchedulingLeave> implements SchedulingLeaveService {

    @Override
    protected void createPrepose(SchedulingLeave entity) {
        String startTime = entity.getStartTime();
        String endTime = entity.getEndTime();
        boolean compare = DateUtil.compare(startTime, endTime);
        if (!compare) {
            throw new RuntimeException("开始时间不能大于结束时间");
        }
        entity.setStatus(ScheduleLeaveType.APPLIED.getKey());
    }

    @Override
    protected void updatePrepose(SchedulingLeave entity) {
        SchedulingLeave schedulingLeave = selectById(entity.getId());
        if (schedulingLeave.getStatus().equals(ScheduleLeaveType.APPROVED.getKey())) {
            throw new RuntimeException("该请假记录已审核，无法修改");
        }
    }

    @Override
    public void querySchedulingLeaveList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        String state = commonPageInfo.getState();
        QueryWrapper<SchedulingLeave> queryWrapper = new QueryWrapper<>();
        if (StrUtil.isNotEmpty(state)) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(SchedulingLeave::getStatus), state);
        }
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(SchedulingLeave::getCreateTime));
        List<SchedulingLeave> schedulingLeaves = list(queryWrapper);
        outputObject.settotal(page.getTotal());
        outputObject.setBeans(schedulingLeaves);
    }

    @Override
    public void updateSchedulingLeave(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        String status = map.get("status").toString();
        SchedulingLeave schedulingLeave = selectById(id);
        schedulingLeave.setStatus(Integer.valueOf(status));
        super.updateEntity(schedulingLeave, schedulingLeave.getCreateId());
    }

    @Override
    public Map<String, List<SchedulingLeave>> queryStateIsSuccessLeaveDayByUserId(String startTime, String endTime, List<Map<String, Object>> staffListWithoutUserId) {
        List<String> allIds = staffListWithoutUserId.stream().map(map -> map.get("id").toString()).collect(Collectors.toList());
        List<SchedulingLeave> schedulingLeaveList = queryAllLeaveListByStaffId(allIds, startTime, endTime);
        Map<String, List<SchedulingLeave>> schedulingLeaveMap = schedulingLeaveList.stream()
            .collect(Collectors.groupingBy(SchedulingLeave::getEmployeeId));
        // 每个员工id对应员工请假数据
        return schedulingLeaveMap;
    }

    private List<SchedulingLeave> queryAllLeaveListByStaffId(List<String> allIds, String startTime, String endTime) {
        if (CollectionUtil.isEmpty(allIds)) {
            return new ArrayList<>();
        }
        QueryWrapper<SchedulingLeave> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(SchedulingLeave::getEmployeeId), allIds);
        queryWrapper.eq(MybatisPlusUtil.toColumns(SchedulingLeave::getStatus), ScheduleLeaveType.APPROVED.getKey());
        queryWrapper.le(MybatisPlusUtil.toColumns(SchedulingLeave::getStartTime), startTime);
        queryWrapper.ge(MybatisPlusUtil.toColumns(SchedulingLeave::getEndTime), endTime);
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(SchedulingLeave::getCreateTime));
        return list(queryWrapper);
    }
}
