package com.skyeye.school.measurement.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.SchoolConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.object.PutObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.classenum.LoginIdentity;
import com.skyeye.exception.CustomException;
import com.skyeye.school.chapter.service.ChapterService;
import com.skyeye.school.measurement.classnum.MeasurementTimeState;
import com.skyeye.school.measurement.dao.MeasurementDao;
import com.skyeye.school.measurement.entity.Measurement;
import com.skyeye.school.measurement.service.MeasurementService;
import com.skyeye.school.measurement.service.MeasurementSubService;
import com.skyeye.school.subject.service.SubjectClassesStuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: MeasurementServiceImpl
 * @Description: 测试管理服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/7/2 10:46
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "测试管理", groupName = "测试管理")
public class MeasurementServiceImpl extends SkyeyeBusinessServiceImpl<MeasurementDao, Measurement> implements MeasurementService {

    @Autowired
    private MeasurementSubService measurementSubService;

    @Autowired
    private SubjectClassesStuService subjectClassesStuService;

    @Autowired
    private ChapterService chapterService;

    @Override
    public void validatorEntity(Measurement entity) {
        if (DateUtil.getDistanceDay(entity.getStartTime(), entity.getEndTime()) < 0) {
            // endTime < startTime
            throw new CustomException("结束时间不能早于开始时间");
        }
    }

    @Override
    public Measurement selectById(String id) {
        Measurement measurement = super.selectById(id);
        chapterService.setDataMation(measurement, Measurement::getChapterId);
        if (ObjectUtil.isNotEmpty(measurement.getChapterMation())) {
            measurement.getChapterMation().setName(String.format(Locale.ROOT, "第 %s 章 %s", measurement.getChapterMation().getSection(), measurement.getChapterMation().getName()));
        }
        iAuthUserService.setDataMation(measurement, Measurement::getCreateId);
        return measurement;
    }

    @Override
    public void queryMeasurementListBySubjectClassesId(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String subjectClassesId = map.get("subjectClassesId").toString();
        QueryWrapper<Measurement> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Measurement::getSubjectClassesId), subjectClassesId);
        List<Measurement> measurementList = list(queryWrapper);
        if (CollectionUtil.isEmpty(measurementList)) {
            return;
        }
        setTimeState(measurementList);

        String userIdentity = PutObject.getRequest().getHeader(SchoolConstants.USER_IDENTITY_KEY);
        String userId = inputObject.getLogParams().get("id").toString();
        List<String> measurementIdList = measurementList.stream().map(Measurement::getId).collect(Collectors.toList());
        if (StrUtil.equals(userIdentity, LoginIdentity.TEACHER.getKey())) {
            // 教师身份信息
            // 设置需要提交测试的人数
            Long allStuNum = subjectClassesStuService.queruClassStuNum(subjectClassesId);
            // 设置总人数/已经提交作业/未提交作业的学生人数
            Map<String, Long> userSubMap = measurementSubService.querySubResult(measurementIdList.toArray(new String[]{}));
            measurementList.forEach(measurement -> {
                Long subNum = userSubMap.get(measurement.getId());
                measurement.setNeedNum(allStuNum);
                measurement.setSubNum(subNum);
                measurement.setNoSubNum(allStuNum - subNum);
            });
            // 设置已经批改/未批改的作业的人数
            Map<String, Long> correctSubMap = measurementSubService.querySubCorrectResult(measurementIdList.toArray(new String[]{}));
            measurementList.forEach(measurement -> {
                Long correctNum = correctSubMap.get(measurement.getId());
                measurement.setCorrectNum(correctNum);
                measurement.setNoCorrectNum(measurement.getSubNum() - correctNum);
            });
        } else {
            // 学生身份信息
            // 设置当前学生提交作业的状态
            Map<String, String> userSubMap = measurementSubService.querySubResult(userId, measurementIdList.toArray(new String[]{}));
            measurementList.forEach(measurement -> {
                measurement.setSubState(userSubMap.get(measurement.getId()));
            });
        }

        chapterService.setDataMation(measurementList, Measurement::getChapterId);
        measurementList.forEach(measurement -> {
            if (ObjectUtil.isNotEmpty(measurement.getChapterMation())) {
                measurement.getChapterMation().setName(String.format(Locale.ROOT, "第 %s 章 %s", measurement.getChapterMation().getSection(), measurement.getChapterMation().getName()));
            }
        });
        outputObject.setBeans(measurementList);
        outputObject.settotal(measurementList.size());
    }

    public void setTimeState(List<Measurement> measurementList) {
        for (Measurement measurement : measurementList) {
            String currentTime = DateUtil.getYmdTimeAndToString();
            if (DateUtil.getDistanceDay(measurement.getStartTime(), currentTime) >= 0 && DateUtil.getDistanceDay(currentTime, measurement.getEndTime()) >= 0) {
                // startTime <= 当前时间 <= endTime
                measurement.setTimeState(MeasurementTimeState.IN_PROGRESS.getKey());
            } else {
                measurement.setTimeState(MeasurementTimeState.EXPIRED.getKey());
            }
        }
    }

    @Override
    public Map<String, Double> queryTestByChapterId(Long classNum, String... ids) {
        Map<String, Double> map = new HashMap<>();
        double sumSize = 0;
        double finishRate = 0;
        map.put("activeNum", sumSize);
        map.put("finishRate", finishRate);
        for (String id: ids){
            QueryWrapper<Measurement> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq(MybatisPlusUtil.toColumns(Measurement::getChapterId), id);
            List<Measurement> list = list(queryWrapper);
            if (CollectionUtil.isEmpty(list)) {
                continue;
            }
            sumSize += list.size();
            List<String> mIds = list.stream().map(Measurement::getId).collect(Collectors.toList());
            double rate = measurementSubService.queryMeasurementFinshRate(mIds, classNum);
            finishRate = finishRate + rate;
        }
        if(finishRate == 0 && ids.length > 1){
            finishRate = finishRate / ids.length;
        }
        map.put("finishRate", finishRate);
        return map;
    }

    // 根据科目班级id获取测试的数量
    @Override
    public Long queryClassMeasurementNum(String id, String stuId, String chapterId) {
        QueryWrapper<Measurement> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Measurement::getSubjectClassesId), id);
        if (StrUtil.isNotEmpty(chapterId)) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(Measurement::getChapterId), chapterId);
        }
        if(StrUtil.isNotEmpty(stuId)){
            queryWrapper.eq(MybatisPlusUtil.toColumns(Measurement::getCreateId), stuId);
        }
        return count(queryWrapper);
    }

    @Override
    public List<String> queryMeasurementIdsBySubjectClassId(String id) {
        QueryWrapper<Measurement> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Measurement::getSubjectClassesId), id);
        List<Measurement> list = list(queryWrapper);
        return list.stream().map(Measurement::getId).collect(Collectors.toList());
    }

    @Override
    public Long queryStuMeasurementNum(String id, String stuId, String chapterId) {
        QueryWrapper<Measurement> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Measurement::getSubjectClassesId), id);
        if (StrUtil.isNotEmpty(chapterId)) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(Measurement::getChapterId), chapterId);
        }
        queryWrapper.eq(MybatisPlusUtil.toColumns(Measurement::getCreateId), stuId);
        return count(queryWrapper);
    }
}
