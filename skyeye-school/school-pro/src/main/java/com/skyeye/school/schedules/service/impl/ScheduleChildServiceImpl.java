package com.skyeye.school.schedules.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.yulichang.toolkit.JoinWrappers;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.jedis.util.RedisLock;
import com.skyeye.school.building.service.FloorInfoService;
import com.skyeye.school.schedules.dao.ScheduleChildDao;
import com.skyeye.school.schedules.entity.Schedule;
import com.skyeye.school.schedules.entity.ScheduleChild;
import com.skyeye.school.schedules.service.ScheduleChildService;
import com.skyeye.school.subject.service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private FloorInfoService floorInfoService;

    @Autowired
    private SubjectService subjectService;

    private static final String LOCK_KEY = "schedule:";

    @Override
    protected void validatorEntity(List<ScheduleChild> entity) {
        super.validatorEntity(entity);
        for (ScheduleChild scheduleChild : entity) {
            if (scheduleChild.getStartWeek() < 0 || scheduleChild.getEndWeek() < 0 || scheduleChild.getStartNum() < 0 || scheduleChild.getEndNum() < 0 || scheduleChild.getWeekDay() < 0) {
                throw new CustomException("请输入正确的周次和节次");
            }
            if (scheduleChild.getStartWeek() > scheduleChild.getEndWeek() || scheduleChild.getStartNum() > scheduleChild.getEndNum()) {
                throw new CustomException("请输入正确的周次和节次");
            }
            if (scheduleChild.getWeekDay() > 7) {
                throw new CustomException("请输入正确的星期几");
            }
            boolean compare = DateUtil.compare(scheduleChild.getStartTime(), scheduleChild.getEndTime());
            if (!compare) {
                throw new CustomException("请输入正确的开始时间和结束时间");
            }
            if (scheduleChild.getCredits() < 0 || scheduleChild.getStudentHour() < 0) {
                throw new CustomException("请输入正确的学分和学时");
            }
        }
    }

    @Override
    public void deleteByScheduleId(String id) {
        QueryWrapper<ScheduleChild> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ScheduleChild::getParentId), id);
        remove(queryWrapper);
    }

    @Override
    @IgnoreTenant
    public void writeScheduleChildList(Schedule entity, String userId) {
        List<ScheduleChild> scheduleChildList = entity.getScheduleChildList();
        String lockKey = tenantEnable ? LOCK_KEY + entity.getSchoolId() + ":" + entity.getSemesterId() + ":" + TenantContext.getTenantId()
                : LOCK_KEY + entity.getSchoolId() + ":" + entity.getSemesterId();
        RedisLock lock = new RedisLock(lockKey);
        try {
            if (!lock.lock()) {
                //  加锁失败
                return;
            }
            // 查出这个学校-学期的所有课表
            MPJLambdaWrapper<ScheduleChild> mpjLambdaWrapper = JoinWrappers.lambda("sc", ScheduleChild.class);
            mpjLambdaWrapper.innerJoin(Schedule.class, "s", Schedule::getId, ScheduleChild::getParentId);
            mpjLambdaWrapper.eq(MybatisPlusUtil.toColumns(Schedule::getSchoolId), entity.getSchoolId());
            mpjLambdaWrapper.eq(MybatisPlusUtil.toColumns(Schedule::getSemesterId), entity.getSemesterId());
            if (tenantEnable) {
                mpjLambdaWrapper.eq("sc." + CommonConstants.TENANT_ID_FIELD, TenantContext.getTenantId());
                mpjLambdaWrapper.eq("s." + CommonConstants.TENANT_ID_FIELD, TenantContext.getTenantId());
            }
            List<ScheduleChild> list = skyeyeBaseMapper.selectJoinList(ScheduleChild.class, mpjLambdaWrapper);

            scheduleChildList.forEach(scheduleChild -> {
                scheduleChild.setParentId(entity.getId());
                scheduleChild.setId(StrUtil.EMPTY);
            });
            list.addAll(scheduleChildList);
            // 校验
            validateScheduleConflicts(list);
            // 过滤出id为空的数据
            createEntity(scheduleChildList, userId);
        } catch (Exception e) {
            throw new CustomException(e);
        } finally {
            // 释放锁
            lock.unlock();
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
                                    "教室冲突：星期%d，%d-%d周，第%d-%d节，教室被重复使用",
                                    courseA.getWeekDay(),
                                    Math.max(courseA.getStartWeek(), courseB.getStartWeek()),
                                    Math.min(courseA.getEndWeek(), courseB.getEndWeek()),
                                    Math.max(courseA.getStartNum(), courseB.getStartNum()),
                                    Math.min(courseA.getEndNum(), courseB.getEndNum())
                            ));
                        }

                        // 检查教师冲突
                        if (courseA.getTeacherId().equals(courseB.getTeacherId())) {
                            throw new CustomException(String.format(
                                    "教师冲突：星期%d，%d-%d周，第%d-%d节，教师时间冲突",
                                    courseA.getWeekDay(),
                                    Math.max(courseA.getStartWeek(), courseB.getStartWeek()),
                                    Math.min(courseA.getEndWeek(), courseB.getEndWeek()),
                                    Math.max(courseA.getStartNum(), courseB.getStartNum()),
                                    Math.min(courseA.getEndNum(), courseB.getEndNum())
                            ));
                        }

                        // 检查班级冲突
                        if (courseA.getParentId().equals(courseB.getParentId())) {
                            throw new CustomException(String.format(
                                    "班级冲突：星期%d，%d-%d周，第%d-%d节，班级时间冲突",
                                    courseA.getWeekDay(),
                                    Math.max(courseA.getStartWeek(), courseB.getStartWeek()),
                                    Math.min(courseA.getEndWeek(), courseB.getEndWeek()),
                                    Math.max(courseA.getStartNum(), courseB.getStartNum()),
                                    Math.min(courseA.getEndNum(), courseB.getEndNum())
                            ));
                        }

                        // 同一时间片不能被两个不同的课程占用（即使教室、教师、班级都不同）
                        throw new CustomException(String.format(
                                "时间冲突：星期%d，%d-%d周，第%d-%d节，该时间段已被占用",
                                courseA.getWeekDay(),
                                Math.max(courseA.getStartWeek(), courseB.getStartWeek()),
                                Math.min(courseA.getEndWeek(), courseB.getEndWeek()),
                                Math.max(courseA.getStartNum(), courseB.getStartNum()),
                                Math.min(courseA.getEndNum(), courseB.getEndNum())
                        ));
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

    /**
     * 根据学期ID和周次查询排课表
     *
     * @param userId     教师ID
     * @param semesterId 学期ID
     * @param week       周次
     * @return 排课表
     */
    @Override
    @IgnoreTenant
    public List<ScheduleChild> queryMyScheduleBySemesterIdAndWeek(String userId, String semesterId, Integer week) {
        MPJLambdaWrapper<ScheduleChild> mpjLambdaWrapper = JoinWrappers.lambda("sc", ScheduleChild.class)
                .innerJoin(Schedule.class, "s", Schedule::getId, ScheduleChild::getParentId);
        if (StrUtil.isNotEmpty(semesterId)) {
            mpjLambdaWrapper.eq(MybatisPlusUtil.toColumns(Schedule::getSemesterId), semesterId);
        }
        if (week != null) {
            // 大于
            mpjLambdaWrapper.ge(MybatisPlusUtil.toColumns(ScheduleChild::getStartWeek), week);
            // 小于
            mpjLambdaWrapper.le(MybatisPlusUtil.toColumns(ScheduleChild::getEndWeek), week);
        }
        if (tenantEnable) {
            String tenantId = TenantContext.getTenantId();
            mpjLambdaWrapper.eq("s." + CommonConstants.TENANT_ID_FIELD, tenantId);
            mpjLambdaWrapper.eq("sc." + CommonConstants.TENANT_ID_FIELD, tenantId);
        }
        List<ScheduleChild> beans = skyeyeBaseMapper.selectJoinList(ScheduleChild.class, mpjLambdaWrapper);
        // 设置信息
        floorInfoService.setDataMation(beans, ScheduleChild::getClassroomId);
        subjectService.setDataMation(beans, ScheduleChild::getCourseId);
        iAuthUserService.setDataMation(beans, ScheduleChild::getTeacherId);
        return beans;

    }

    @Override
    public List<ScheduleChild> queryScheduleChildListByScheduleId(String id) {
        QueryWrapper<ScheduleChild> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ScheduleChild::getParentId), id);
        List<ScheduleChild> beans = list(queryWrapper);
        floorInfoService.setDataMation(beans, ScheduleChild::getClassroomId);
        subjectService.setDataMation(beans, ScheduleChild::getCourseId);
        iAuthUserService.setDataMation(beans, ScheduleChild::getTeacherId);
        return beans;
    }
}
