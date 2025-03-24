/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.subject.service.impl;

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
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.classenum.LoginIdentity;
import com.skyeye.exception.CustomException;
import com.skyeye.school.subject.dao.SubjectClassesTopDao;
import com.skyeye.school.subject.entity.SubjectClassesTop;
import com.skyeye.school.subject.service.SubjectClassesTopService;
import com.skyeye.school.subject.service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: SubjectClassesTopServiceImpl
 * @Description: 学生科目置顶表服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/7/23 20:22
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "学生科目置顶", groupName = "科目管理")
public class SubjectClassesTopServiceImpl extends SkyeyeBusinessServiceImpl<SubjectClassesTopDao, SubjectClassesTop> implements SubjectClassesTopService {

    @Autowired
    private SubjectService subjectService;

    @Override
    public List<Map<String, Object>> queryDataList(InputObject inputObject) {
        String userId = inputObject.getLogParams().get("id").toString();
        // 获取当前用户的置顶科目id列表
        QueryWrapper<SubjectClassesTop> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(SubjectClassesTop::getCreateId), userId);
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(SubjectClassesTop::getCreateTime));
        List<SubjectClassesTop> subjectClassesTopList = list(queryWrapper);
        if (CollectionUtil.isEmpty(subjectClassesTopList)) {
            return CollectionUtil.newArrayList();
        }

        // 根据id列表获取科目信息
        List<String> subjectIdList = subjectClassesTopList.stream().map(SubjectClassesTop::getSubjectId).collect(Collectors.toList());
        List<Map<String, Object>> subjectList = new ArrayList<>(subjectService.selectValIsMapByIds(subjectIdList).values());
        String userIdentity = PutObject.getRequest().getHeader(SchoolConstants.USER_IDENTITY_KEY);
        if (StrUtil.equals(userIdentity, LoginIdentity.STUDENT.getKey())) {
            // 学生
            Map<String, String> subjectLinkMap = subjectClassesTopList.stream().collect(Collectors.toMap(SubjectClassesTop::getSubjectId, SubjectClassesTop::getSubClassLinkId));
            for (Map<String, Object> subject : subjectList) {
                String subjectId = subject.get("id").toString();
                String subClassLinkId = subjectLinkMap.get(subjectId);
                subject.put("subClassLinkId", subClassLinkId);
            }
        }
        return subjectList;
    }

    @Override
    public void validatorEntity(SubjectClassesTop entity) {
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        // 校验当前用户是否已经置顶过该科目
        QueryWrapper<SubjectClassesTop> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(SubjectClassesTop::getCreateId), userId);
        String userIdentity = PutObject.getRequest().getHeader(SchoolConstants.USER_IDENTITY_KEY);
        if (StrUtil.equals(userIdentity, LoginIdentity.TEACHER.getKey())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(SubjectClassesTop::getSubjectId), entity.getSubjectId());
        } else {
            if (StrUtil.isEmpty(entity.getSubClassLinkId())) {
                throw new CustomException("请传入课程与班级的关联id！");
            }
            queryWrapper.eq(MybatisPlusUtil.toColumns(SubjectClassesTop::getSubClassLinkId), entity.getSubClassLinkId());
        }
        SubjectClassesTop subjectClassesTop = getOne(queryWrapper, false);
        if (ObjectUtil.isNotEmpty(subjectClassesTop)) {
            throw new CustomException("当前用户已经置顶过该科目，请勿重复置顶！");
        }
    }

    @Override
    public void deleteSubjectClassesTop(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String userId = inputObject.getLogParams().get("id").toString();
        QueryWrapper<SubjectClassesTop> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(SubjectClassesTop::getCreateId), userId);
        String userIdentity = PutObject.getRequest().getHeader(SchoolConstants.USER_IDENTITY_KEY);
        if (StrUtil.equals(userIdentity, LoginIdentity.TEACHER.getKey())) {
            String subjectId = params.get("subjectId").toString();
            queryWrapper.eq(MybatisPlusUtil.toColumns(SubjectClassesTop::getSubjectId), subjectId);
        } else {
            String subClassLinkId = params.get("subClassLinkId").toString();
            queryWrapper.eq(MybatisPlusUtil.toColumns(SubjectClassesTop::getSubClassLinkId), subClassLinkId);
        }
        remove(queryWrapper);
    }

    @Override
    public void deleteSubjectClassesTopBySubClassLinkId(String subClassLinkId) {
        QueryWrapper<SubjectClassesTop> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(SubjectClassesTop::getSubClassLinkId), subClassLinkId);
        remove(queryWrapper);
    }

    @Override
    public void deleteSubjectClassesTopBySubjectId(String subjectId) {
        QueryWrapper<SubjectClassesTop> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(SubjectClassesTop::getSubjectId), subjectId);
        remove(queryWrapper);
    }
}
