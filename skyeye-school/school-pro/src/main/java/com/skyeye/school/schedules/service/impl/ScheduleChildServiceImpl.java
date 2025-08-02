package com.skyeye.school.schedules.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.school.schedules.dao.ScheduleChildDao;
import com.skyeye.school.schedules.entity.Schedule;
import com.skyeye.school.schedules.entity.ScheduleChild;
import com.skyeye.school.schedules.service.ScheduleChildService;
import com.skyeye.school.schedules.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: ScheduleChildServiceImpl
 * @Description: 排课表子表接口层实现曾
 * @author: skyeye云系列--卫志强
 * @date: 2023/8/8 14:55
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的F
 */
@Service
@SkyeyeService(name = "排课表子表管理", groupName = "排课表管理")
public class ScheduleChildServiceImpl extends SkyeyeBusinessServiceImpl<ScheduleChildDao, ScheduleChild> implements ScheduleChildService {

    @Autowired
    private ScheduleService scheduleService;


    @Override
    public void deleteByScheduleId(String id) {
        QueryWrapper<ScheduleChild> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ScheduleChild::getParentId), id);
        remove(queryWrapper);
    }

    @Override
    public void writeScheduleChildList(String parentId, List<ScheduleChild> scheduleChildList) {
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        // 查出这个学校-学期的所有课表
        Schedule schedule = scheduleService.selectById(parentId);
        MPJLambdaWrapper<ScheduleChild> mpjLambdaWrapper = new MPJLambdaWrapper<>();
        mpjLambdaWrapper.innerJoin(Schedule.class, Schedule::getId, ScheduleChild::getParentId);
        mpjLambdaWrapper.eq(MybatisPlusUtil.toColumns(Schedule::getSchoolId), schedule.getSchoolId());
        mpjLambdaWrapper.eq(MybatisPlusUtil.toColumns(Schedule::getSemesterId), schedule.getSemesterId());
        List<ScheduleChild> list = skyeyeBaseMapper.selectJoinList(ScheduleChild.class, mpjLambdaWrapper);

        scheduleChildList.forEach(scheduleChild -> scheduleChild.setParentId(parentId));
        list.addAll(scheduleChildList);
        // 校验
         validateScheduleConflicts( list);
        // 过滤出id为空的数据
        List<ScheduleChild> collect = list.stream().filter(course -> StrUtil.isEmpty(course.getId())).collect(Collectors.toList());
        createEntity(collect, userId);
    }

    @Override
    public void updateScheduleChildList(String parentId, List<ScheduleChild> scheduleChildList) {
        String userId = InputObject.getLogParamsStatic().get("id").toString();

        // 查出这个学校-学期的所有课表（不包括当前正在编辑的课表）
        Schedule schedule = scheduleService.selectById(parentId);
        MPJLambdaWrapper<ScheduleChild> mpjLambdaWrapper = new MPJLambdaWrapper<>();
        mpjLambdaWrapper.innerJoin(Schedule.class, Schedule::getId, ScheduleChild::getParentId);
        mpjLambdaWrapper.eq(MybatisPlusUtil.toColumns(Schedule::getSchoolId), schedule.getSchoolId());
        mpjLambdaWrapper.eq(MybatisPlusUtil.toColumns(Schedule::getSemesterId), schedule.getSemesterId());
        // 排除当前正在编辑的课表，避免与自己冲突
        mpjLambdaWrapper.ne(MybatisPlusUtil.toColumns(ScheduleChild::getParentId), parentId);
        List<ScheduleChild> existingList = skyeyeBaseMapper.selectJoinList(ScheduleChild.class, mpjLambdaWrapper);

        // 设置父ID
        scheduleChildList.forEach(scheduleChild -> scheduleChild.setParentId(parentId));

        // 合并现有课程和当前编辑的课程
        List<ScheduleChild> allScheduleList = new ArrayList<>(existingList);
        allScheduleList.addAll(scheduleChildList);

        // 校验冲突
        validateScheduleConflicts(allScheduleList);

        // 分离新增和更新的数据
        List<ScheduleChild> toCreateList = scheduleChildList.stream()
                .filter(course -> StrUtil.isEmpty(course.getId()))
                .collect(Collectors.toList());

        List<ScheduleChild> toUpdateList = scheduleChildList.stream()
                .filter(course -> StrUtil.isNotEmpty(course.getId()))
                .collect(Collectors.toList());

        // 批量创建新增的数据
        if (!toCreateList.isEmpty()) {
            createEntity(toCreateList, userId);
        }

        // 批量更新已有的数据
        if (!toUpdateList.isEmpty()) {
            updateEntity(toUpdateList, userId);
        }
    }


    /**
     * 校验排课冲突
     */
    private void validateScheduleConflicts(List<ScheduleChild> allScheduleList) {
        // 按星期分组校验
        Map<Integer, List<ScheduleChild>> weekMap = allScheduleList.stream()
                .collect(Collectors.groupingBy(ScheduleChild::getWeekDay));

        for (Map.Entry<Integer, List<ScheduleChild>> entry : weekMap.entrySet()) {
            // 一天的所有课程
            List<ScheduleChild> oneDayScheduleChildList = entry.getValue();

            // 对这一天的所有课程进行冲突检测
            for (int i = 0; i < oneDayScheduleChildList.size(); i++) {
                ScheduleChild courseA = oneDayScheduleChildList.get(i);
                for (int j = i + 1; j < oneDayScheduleChildList.size(); j++) {
                    ScheduleChild courseB = oneDayScheduleChildList.get(j);

                    // 检查时间是否交叉
                    if (isTimeOverlap(courseA, courseB)) {
                        // 检查教室冲突
                        if (courseA.getClassroomId().equals(courseB.getClassroomId())) {
                            throw new CustomException(String.format(
                                    "教室冲突：星期%d，%d-%d周，第%d-%d节，教室[%s]被重复使用",
                                    courseA.getWeekDay(),
                                    Math.max(courseA.getStartWeek(), courseB.getStartWeek()),
                                    Math.min(courseA.getEndWeek(), courseB.getEndWeek()),
                                    Math.max(courseA.getStartNum(), courseB.getStartNum()),
                                    Math.min(courseA.getEndNum(), courseB.getEndNum()),
                                    courseA.getClassroomId()
                            ));
                        }

                        // 检查教师冲突
                        if (courseA.getTeacherId().equals(courseB.getTeacherId())) {
                            throw new CustomException(String.format(
                                    "教师冲突：星期%d，%d-%d周，第%d-%d节，教师[%s]时间冲突",
                                    courseA.getWeekDay(),
                                    Math.max(courseA.getStartWeek(), courseB.getStartWeek()),
                                    Math.min(courseA.getEndWeek(), courseB.getEndWeek()),
                                    Math.max(courseA.getStartNum(), courseB.getStartNum()),
                                    Math.min(courseA.getEndNum(), courseB.getEndNum()),
                                    courseA.getTeacherId()
                            ));
                        }
                    }
                }
            }
        }
    }

    /**
     * 判断两个课程的时间是否交叉
     */
    private boolean isTimeOverlap(ScheduleChild a, ScheduleChild b) {
        // 周次交叉检测
        boolean weekOverlap = a.getStartWeek() <= b.getEndWeek() && b.getStartWeek() <= a.getEndWeek();
        // 节数交叉检测
        boolean numOverlap = a.getStartNum() <= b.getEndNum() && b.getStartNum() <= a.getEndNum();

        // 只有周次和节数都交叉才算时间冲突
        return weekOverlap && numOverlap;
    }
}
