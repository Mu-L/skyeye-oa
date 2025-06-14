package com.skyeye.scheduling.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.scheduling.dao.SchedulingTimeWorkPeopleDao;
import com.skyeye.scheduling.entity.SchedulingTimeWorkPeople;
import com.skyeye.scheduling.service.SchedulingTimeWorkPeopleService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@SkyeyeService(name = "排班工位下员工管理", groupName = "排班工位下员工管理")
public class SchedulingTimeWorkPeopleServiceImpl extends SkyeyeBusinessServiceImpl<SchedulingTimeWorkPeopleDao, SchedulingTimeWorkPeople> implements SchedulingTimeWorkPeopleService {

    @Override
    public List<SchedulingTimeWorkPeople> queryTimeWorkByThreeId(String id, List<String> timeIds, List<String> timeWorkIds) {
        QueryWrapper<SchedulingTimeWorkPeople> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(SchedulingTimeWorkPeople::getSchedulingId), id);
        queryWrapper.in(MybatisPlusUtil.toColumns(SchedulingTimeWorkPeople::getSchedulingTimeId), timeIds);
        queryWrapper.in(MybatisPlusUtil.toColumns(SchedulingTimeWorkPeople::getSchedulingTimeWorkId), timeWorkIds);
        List<SchedulingTimeWorkPeople> timeWorkPeopleList = list(queryWrapper);
        return timeWorkPeopleList;
    }

    @Override
    public void deleteBySchedulingTimeIdsAndOthorId(List<String> schedulingTimeWorkIds) {
        QueryWrapper<SchedulingTimeWorkPeople> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(SchedulingTimeWorkPeople::getSchedulingTimeId), schedulingTimeWorkIds);
        remove(queryWrapper);
    }

    @Override
    public List<SchedulingTimeWorkPeople> queryPeopleByThreeId(String id, String schedulingId, String schedulingTimeId) {
        QueryWrapper<SchedulingTimeWorkPeople> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(SchedulingTimeWorkPeople::getSchedulingId), schedulingId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(SchedulingTimeWorkPeople::getSchedulingTimeId), schedulingTimeId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(SchedulingTimeWorkPeople::getSchedulingTimeWorkId), id);
        return list(queryWrapper);
    }

    @Override
    public void deleteBySchedulingTimeIds(List<String> deleteIdList) {
        QueryWrapper<SchedulingTimeWorkPeople> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(CommonConstants.ID, deleteIdList);
        remove(queryWrapper);
    }

    @Override
    public void deleteSchedulingTimeWorkPeopleByTimeIds(List<String> allDeleteIds) {
        QueryWrapper<SchedulingTimeWorkPeople> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(SchedulingTimeWorkPeople::getSchedulingTimeWorkId), allDeleteIds);
        remove(queryWrapper);
    }

    @Override
    public List<SchedulingTimeWorkPeople> querySchedulingByschedulingIdsAndStaffId(List<String> schedulingIds, String staffId) {
        QueryWrapper<SchedulingTimeWorkPeople> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(SchedulingTimeWorkPeople::getSchedulingId), schedulingIds);
        queryWrapper.eq(MybatisPlusUtil.toColumns(SchedulingTimeWorkPeople::getEmployeeId), staffId);
        return list(queryWrapper);
    }

    @Override
    public void deleteBySchedulingIds(List<String> ids) {
        QueryWrapper<SchedulingTimeWorkPeople> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(SchedulingTimeWorkPeople::getSchedulingId), ids);
        remove(queryWrapper);
    }
}
