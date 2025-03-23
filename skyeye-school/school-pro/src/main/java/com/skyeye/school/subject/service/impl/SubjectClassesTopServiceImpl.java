/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.subject.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.school.subject.dao.SubjectClassesTopDao;
import com.skyeye.school.subject.entity.SubjectClassesTop;
import com.skyeye.school.subject.service.SubjectClassesService;
import com.skyeye.school.subject.service.SubjectClassesTopService;
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
    private SubjectClassesService subjectClassesService;

    @Override
    public List<Map<String, Object>> queryDataList(InputObject inputObject) {// todo-wst
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
        List<String> subClassLinkIdList = subjectClassesTopList.stream().map(SubjectClassesTop::getSubClassLinkId).collect(Collectors.toList());
        List<Map<String, Object>> subjectClassesList = new ArrayList<>(subjectClassesService.selectValIsMapByIds(subClassLinkIdList).values());
        return subjectClassesList;
    }

    @Override
    public void validatorEntity(SubjectClassesTop entity) {
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        // 校验当前用户是否已经置顶过该科目
        QueryWrapper<SubjectClassesTop> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(SubjectClassesTop::getCreateId), userId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(SubjectClassesTop::getSubClassLinkId), entity.getSubClassLinkId());
        SubjectClassesTop subjectClassesTop = getOne(queryWrapper, false);
        if (ObjectUtil.isNotEmpty(subjectClassesTop)) {
            throw new CustomException("当前用户已经置顶过该科目，请勿重复置顶！");
        }
    }

    @Override
    public void deleteSubjectClassesTop(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String userId = inputObject.getLogParams().get("id").toString();
        String subClassLinkId = params.get("subClassLinkId").toString();
        QueryWrapper<SubjectClassesTop> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(SubjectClassesTop::getCreateId), userId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(SubjectClassesTop::getSubClassLinkId), subClassLinkId);
        remove(queryWrapper);
    }

    @Override
    public void deleteSubjectClassesTopBySubClassLinkId(String subClassLinkId) {
        QueryWrapper<SubjectClassesTop> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(SubjectClassesTop::getSubClassLinkId), subClassLinkId);
        remove(queryWrapper);
    }
}
