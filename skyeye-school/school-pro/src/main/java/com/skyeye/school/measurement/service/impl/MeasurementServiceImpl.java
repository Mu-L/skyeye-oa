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

    @Override
    public List<Measurement> queryListByObjectIdAndClassesId(String objectId, String subjectClassesId) {
        QueryWrapper<Measurement> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Measurement::getSubjectClassesId), objectId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(Measurement::getSubjectClassesId), subjectClassesId);
        List<Measurement> list = list(queryWrapper);
        return CollectionUtil.isEmpty(list) ? new ArrayList<>() : list;
    }
}
