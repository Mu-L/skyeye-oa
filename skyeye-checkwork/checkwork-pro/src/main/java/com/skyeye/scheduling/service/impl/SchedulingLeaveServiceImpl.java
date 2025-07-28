package com.skyeye.scheduling.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.scheduling.classenum.ScheduleLeaveType;
import com.skyeye.scheduling.dao.SchedulingLeaveDao;
import com.skyeye.scheduling.entity.SchedulingLeave;
import com.skyeye.scheduling.service.SchedulingLeaveService;
import com.skyeye.scheduling.service.SchedulingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@SkyeyeService(name = "排班请假管理", groupName = "排班请假管理")
public class SchedulingLeaveServiceImpl extends SkyeyeBusinessServiceImpl<SchedulingLeaveDao, SchedulingLeave> implements SchedulingLeaveService {

    @Autowired
    private SchedulingService schedulingService;

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
        // 状态
        String state = commonPageInfo.getState();
        // 车间id
        String holderId = commonPageInfo.getHolderId();
        QueryWrapper<SchedulingLeave> queryWrapper = new QueryWrapper<>();
        if (StrUtil.isNotEmpty(state)) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(SchedulingLeave::getStatus), state);
        }
        if (StrUtil.isNotEmpty(holderId)) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(SchedulingLeave::getFarmId), holderId);
        }
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(SchedulingLeave::getCreateTime));
        List<SchedulingLeave> schedulingLeaves = list(queryWrapper);
        iAuthUserService.setName(schedulingLeaves, "createId", "createName");
        iAuthUserService.setName(schedulingLeaves, "lastUpdateId", "lastUpdateName");
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
        super.updateEntity(schedulingLeave, inputObject.getLogParams().get("id").toString());
    }

    @Override
    public Map<String, List<SchedulingLeave>> queryLeaveByEmployeeIds(List<String> id, String startTime, String endTime) {
        if (CollectionUtil.isEmpty(id)) {
            return new HashMap<>();
        }
        QueryWrapper<SchedulingLeave> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(SchedulingLeave::getEmployeeId), id);
        queryWrapper.eq(MybatisPlusUtil.toColumns(SchedulingLeave::getStatus), ScheduleLeaveType.APPROVED.getKey());
        queryWrapper.le(MybatisPlusUtil.toColumns(SchedulingLeave::getStartTime), endTime);
        queryWrapper.ge(MybatisPlusUtil.toColumns(SchedulingLeave::getEndTime), startTime);
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(SchedulingLeave::getCreateTime));
        List<SchedulingLeave> leaveList = list(queryWrapper);

        // 按员工ID分组
        return leaveList.stream()
            .collect(Collectors.groupingBy(SchedulingLeave::getEmployeeId));
    }

    @Override
    public List<SchedulingLeave> querySchedulingLeaveByEmployeeId(String employeeId) {
        QueryWrapper<SchedulingLeave> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(SchedulingLeave::getEmployeeId), employeeId);
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(SchedulingLeave::getCreateTime));
        return list(queryWrapper);
    }
}
