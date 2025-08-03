package com.skyeye.school.schedules.service.impl;

import cn.hutool.core.util.StrUtil;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.service.SchoolService;
import com.skyeye.rest.wall.certification.service.ICertificationService;
import com.skyeye.school.building.service.FloorInfoService;
import com.skyeye.school.faculty.service.FacultyService;
import com.skyeye.school.grade.service.ClassesService;
import com.skyeye.school.major.service.MajorService;
import com.skyeye.school.schedules.dao.ScheduleDao;
import com.skyeye.school.schedules.entity.Schedule;
import com.skyeye.school.schedules.entity.ScheduleChild;
import com.skyeye.school.schedules.service.ScheduleChildService;
import com.skyeye.school.schedules.service.ScheduleService;
import com.skyeye.school.student.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    private FacultyService facultyService;

    @Autowired
    private ClassesService classesService;

    @Autowired
    private MajorService majorService;

    @Autowired
    private ICertificationService iCertificationService;

    @Autowired
    private ScheduleChildService scheduleChildService;

    @Autowired
    private StudentService studentService;

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
        if (StrUtil.isNotEmpty(teacherId)) {
        }
        // 教室
        if (StrUtil.isNotEmpty(classroomId)) {
        }
        List<Map<String, Object>> beans = skyeyeBaseMapper.selectJoinMaps(queryWrapper);
        // 设置信息
        outputObject.setBeans(beans);
    }

    @Override
    public void queryMySchedulesList(InputObject inputObject, OutputObject outputObject) {
        // 判断入参
        Map<String, Object> params = inputObject.getParams();
        String semesterId = params.get("semesterId").toString();
        Integer week = Integer.parseInt(params.get("week").toString());
        if (week < CommonNumConstants.NUM_ZERO || week > 20) {
            throw new RuntimeException("周数错误:" + week);
        }
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        // 教师身份信息
        List<ScheduleChild> scheduleChildren = scheduleChildService.queryMyScheduleBySemesterIdAndWeek(userId,semesterId, week);
        // 使用steam流根据起始时间、结束时间排序，然后按照星期分组
        Map<Integer, List<ScheduleChild>> scheduleChildMap = scheduleChildren.stream().sorted(Comparator.comparing(ScheduleChild::getStartTime)
                .thenComparing(ScheduleChild::getEndTime)).collect(Collectors.groupingBy(ScheduleChild::getWeekDay));
        outputObject.setBean(scheduleChildMap);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }
}
