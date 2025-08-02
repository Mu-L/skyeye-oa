package com.skyeye.school.schedules.service.impl;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.yulichang.query.MPJQueryWrapper;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.service.SchoolService;
import com.skyeye.school.building.service.FloorInfoService;
import com.skyeye.school.faculty.service.FacultyService;
import com.skyeye.school.grade.service.ClassesService;
import com.skyeye.school.major.service.MajorService;
import com.skyeye.school.schedules.dao.ScheduleDao;
import com.skyeye.school.schedules.entity.Schedule;
import com.skyeye.school.schedules.entity.ScheduleChild;
import com.skyeye.school.schedules.service.ScheduleChildService;
import com.skyeye.school.schedules.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: ScheduleChildService
 * @Description: 排课表接口层实现层
 * @author: skyeye云系列--卫志强
 * @date: 2023/8/8 14:55
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的F
 */
@Service
@SkyeyeService(name = "排课表管理", groupName = "排课表管理")
public class ScheduleServiceImpl extends SkyeyeBusinessServiceImpl<ScheduleDao, Schedule> implements ScheduleService {

    @Autowired
    private FloorInfoService floorInfoService;

    @Autowired
    private SchoolService schoolService;

    @Autowired
    private FacultyService  facultyService;

    @Autowired
    private ClassesService classesService;

    @Autowired
    private MajorService majorService;



    @Autowired
    private ScheduleChildService scheduleChildService;

    @Override
    protected void createPostpose(Schedule entity, String userId) {
        super.createPostpose(entity, userId);
        scheduleChildService.writeScheduleChildList(entity.getId(), entity.getScheduleChildList());
    }

    @Override
    protected void updatePostpose(Schedule entity, String userId) {
        super.updatePostpose(entity, userId);
        scheduleChildService.updateScheduleChildList(entity.getId(), entity.getScheduleChildList());
    }

    @Override
    protected void deletePostpose(Schedule entity) {
        super.deletePostpose(entity);
        scheduleChildService.deleteByScheduleId(entity.getId());
    }

    @Override
    public void querySchedulesList(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String teacherId = (String) params.get("teacherId");
        String classroomId = (String) params.get("classroomId");
        MPJLambdaWrapper<Schedule> queryWrapper = new MPJLambdaWrapper<>();
        // 老师
        if(StrUtil.isNotEmpty(teacherId)){
        }
        // 教室
        if(StrUtil.isNotEmpty(classroomId)){
        }
        List<Map<String, Object>> beans = skyeyeBaseMapper.selectJoinMaps(queryWrapper);
        // 设置信息
        outputObject.setBeans(beans);
    }
}
