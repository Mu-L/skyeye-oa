/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.measurement.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.constans.SchoolConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.object.PutObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.classenum.LoginIdentity;
import com.skyeye.exception.CustomException;
import com.skyeye.rest.wall.user.service.IUserService;
import com.skyeye.school.measurement.classnum.MeasurementCorrectState;
import com.skyeye.school.measurement.classnum.MeasurementSubState;
import com.skyeye.school.measurement.dao.MeasurementSubDao;
import com.skyeye.school.measurement.entity.Measurement;
import com.skyeye.school.measurement.entity.MeasurementSub;
import com.skyeye.school.measurement.service.MeasurementService;
import com.skyeye.school.measurement.service.MeasurementSubService;
import com.skyeye.school.subject.service.SubjectClassesStuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: MeasurementSubServiceImpl
 * @Description: 测试提交服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/7/2 11:10
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "测试提交", groupName = "测试提交")
public class MeasurementSubServiceImpl extends SkyeyeBusinessServiceImpl<MeasurementSubDao, MeasurementSub> implements MeasurementSubService {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private MeasurementService measurementService;

    @Autowired
    private SubjectClassesStuService subjectClassesStuService;

    @Override
    public void validatorEntity(MeasurementSub entity) {
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        QueryWrapper<MeasurementSub> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(MeasurementSub::getMeasurementId), entity.getMeasurementId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(MeasurementSub::getCreateId), userId);
        if (StrUtil.isNotEmpty(entity.getId())) {
            queryWrapper.ne(CommonConstants.ID, entity.getId());
        }
        long count = count(queryWrapper);
        if (count != 0) {
            throw new CustomException("请勿重复提交作业");
        }
        // 校验时间
        Measurement measurement = measurementService.selectById(entity.getMeasurementId());
        String currentTime = DateUtil.getYmdTimeAndToString();
        if (DateUtil.getDistanceDay(measurement.getStartTime(), currentTime) < 0 || DateUtil.getDistanceDay(currentTime, measurement.getEndTime()) < 0) {
            // startTime > 当前时间 || 当前时间 > endTime
            throw new CustomException("不在作业的提交时间范围");
        }
        entity.setState(MeasurementCorrectState.BE_CORRECTED.getKey());
    }

    @Override
    public MeasurementSub selectById(String id) {
        MeasurementSub measurementSub = super.selectById(id);
        iUserService.setDataMation(measurementSub, MeasurementSub::getCreateId);
        return measurementSub;
    }

    @Override
    public void queryMeasurementSubListByMeasurementId(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String measurementId = map.get("measurementId").toString();
        if (StrUtil.isEmpty(measurementId)) {
            return;
        }
        String userIdentity = PutObject.getRequest().getHeader(SchoolConstants.USER_IDENTITY_KEY);
        if (StrUtil.equals(userIdentity, LoginIdentity.STUDENT.getKey())) {
            // 学生身份不允许查看所有的作业提交信息
            return;
        }
        List<MeasurementSub> measurementSubList = querySubList(measurementId);
        iUserService.setDataMation(measurementSubList, MeasurementSub::getCreateId);
        outputObject.setBeans(measurementSubList);
        outputObject.settotal(measurementSubList.size());
    }

    private List<MeasurementSub> querySubList(String measurementId) {
        QueryWrapper<MeasurementSub> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(MeasurementSub::getMeasurementId), measurementId);
        List<MeasurementSub> measurementSubList = list(queryWrapper);
        return measurementSubList;
    }

    @Override
    public void queryMeasurementNotSubListByMeasurementId(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String measurementId = map.get("measurementId").toString();
        if (StrUtil.isEmpty(measurementId)) {
            return;
        }
        String userIdentity = PutObject.getRequest().getHeader(SchoolConstants.USER_IDENTITY_KEY);
        if (StrUtil.equals(userIdentity, LoginIdentity.STUDENT.getKey())) {
            // 学生身份不允许查看所有的作业提交信息
            return;
        }
        // 查询作业信息
        Measurement measurement = measurementService.selectById(measurementId);
        // 查询所有学生信息
        List<Map<String, Object>> allUserList = subjectClassesStuService.queryClassStuIds(measurement.getSubjectClassesId());
        if (CollectionUtil.isEmpty(allUserList)) {
            return;
        }
        // 查询已提交的学生信息
        List<MeasurementSub> measurementSubList = querySubList(measurementId);
        List<String> subIdList = measurementSubList.stream().map(MeasurementSub::getCreateId).distinct().collect(Collectors.toList());
        // 过滤已提交的学生信息
        List<Map<String, Object>> notSubUserList = allUserList.stream().filter(user -> !subIdList.contains(user.get("id").toString()))
            .collect(Collectors.toList());
        outputObject.setBeans(notSubUserList);
        outputObject.settotal(notSubUserList.size());
    }

    @Override
    public void readOverMeasurementSubById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        String score = map.get("score").toString();
        String comment = map.get("comment").toString();
        UpdateWrapper<MeasurementSub> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(MeasurementSub::getScore), score);
        updateWrapper.set(MybatisPlusUtil.toColumns(MeasurementSub::getState), MeasurementCorrectState.CORRECTED.getKey());
        updateWrapper.set(MybatisPlusUtil.toColumns(MeasurementSub::getComment), comment);
        update(updateWrapper);
        refreshCache(id);
    }

    @Override
    public Map<String, Long> querySubResult(String... measurementId) {
        List<String> measurementIdList = Arrays.asList(measurementId);
        if (CollectionUtil.isEmpty(measurementIdList)) {
            return MapUtil.newHashMap();
        }
        QueryWrapper<MeasurementSub> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(MeasurementSub::getMeasurementId), measurementIdList);
        List<MeasurementSub> measurementSubList = list(queryWrapper);

        Map<String, Long> subNumMap = measurementSubList.stream().collect(
            Collectors.groupingBy(MeasurementSub::getMeasurementId, Collectors.counting()));
        // 和数据库的已提交的作业做对比
        Map<String, Long> result = new HashMap<>();
        measurementIdList.forEach(measurementTmpId -> {
            if (subNumMap.containsKey(measurementTmpId)) {
                result.put(measurementTmpId, subNumMap.get(measurementTmpId));
            } else {
                result.put(measurementTmpId, Long.valueOf(0));
            }
        });
        return result;
    }

    @Override
    public Map<String, Long> querySubCorrectResult(String... testId) {
        List<String> measurementIdList = Arrays.asList(testId);
        if (CollectionUtil.isEmpty(measurementIdList)) {
            return MapUtil.newHashMap();
        }
        QueryWrapper<MeasurementSub> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(MeasurementSub::getMeasurementId), measurementIdList);
        // 已批改
        queryWrapper.in(MybatisPlusUtil.toColumns(MeasurementSub::getState), MeasurementCorrectState.CORRECTED.getKey());
        List<MeasurementSub> measurementSubList = list(queryWrapper);

        Map<String, Long> correctNumMap = measurementSubList.stream().collect(
            Collectors.groupingBy(MeasurementSub::getMeasurementId, Collectors.counting()));
        // 和数据库的已批改的作业做对比
        Map<String, Long> result = new HashMap<>();
        measurementIdList.forEach(measurementTmpId -> {
            if (correctNumMap.containsKey(measurementTmpId)) {
                result.put(measurementTmpId, correctNumMap.get(measurementTmpId));
            } else {
                result.put(measurementTmpId, Long.valueOf(0));
            }
        });
        return result;
    }

    @Override
    public Map<String, String> querySubResult(String userId, String... measurementId) {
        List<String> measurementIdList = Arrays.asList(measurementId);
        if (CollectionUtil.isEmpty(measurementIdList)) {
            return MapUtil.newHashMap();
        }
        QueryWrapper<MeasurementSub> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(MeasurementSub::getMeasurementId), measurementIdList);
        queryWrapper.eq(MybatisPlusUtil.toColumns(MeasurementSub::getCreateId), userId);
        List<MeasurementSub> measurementSubList = list(queryWrapper);
        List<String> sqlMeasurementIdList = measurementSubList.stream().map(MeasurementSub::getMeasurementId)
            .distinct().collect(Collectors.toList());
        // 和数据库的已提交的作业做对比
        Map<String, String> result = new HashMap<>();
        measurementIdList.forEach(measurementTmpId -> {
            if (sqlMeasurementIdList.indexOf(measurementTmpId) >= 0) {
                result.put(measurementTmpId, MeasurementSubState.SUBMITTED.getKey());
            } else {
                result.put(measurementTmpId, MeasurementSubState.NOT_SUBMITTED.getKey());
            }
        });
        return result;
    }

    @Override
    public void queryMeasurementStuSubListByMeasurementId(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String measurementId = map.get("measurementId").toString();
        // 查询当前登录人的作业提交信息
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        QueryWrapper<MeasurementSub> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(MeasurementSub::getMeasurementId), measurementId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(MeasurementSub::getCreateId), userId);
        MeasurementSub measurementSub = getOne(queryWrapper, false);
        outputObject.setBean(measurementSub);
        outputObject.settotal(ObjectUtil.isEmpty(measurementSub) ? CommonNumConstants.NUM_ZERO : CommonNumConstants.NUM_ONE);
    }

    @Override
    public double queryMeasurementFinshRate(List<String> ids, Long classNum) {
        double sum = 0;
        if(CollectionUtil.isEmpty(ids) || classNum == 0){
            return sum;
        }
        for (String id : ids) {
            QueryWrapper<MeasurementSub> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq(MybatisPlusUtil.toColumns(MeasurementSub::getMeasurementId), id);
            long count = count(queryWrapper);
            if (count == 0){
                continue;
            }
            double temp = (double) count / classNum;
            sum += temp;
        }
        if(sum == 0){
            return sum;
        }
        return sum / ids.size();
    }
}
