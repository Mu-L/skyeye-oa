package com.skyeye.school.lesson.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.school.lesson.dao.LessonReviewTypeDao;
import com.skyeye.school.lesson.entity.LessonReviewModel;
import com.skyeye.school.lesson.entity.LessonReviewType;
import com.skyeye.school.lesson.entity.LessonReviewTypeChild;
import com.skyeye.school.lesson.service.LessonReviewModelService;
import com.skyeye.school.lesson.service.LessonReviewTypeChildService;
import com.skyeye.school.lesson.service.LessonReviewTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@SkyeyeService(name = "质评角色听课量管理", groupName = "质评角色听课量管理")
public class LessonReviewTypeServiceImpl extends SkyeyeBusinessServiceImpl<LessonReviewTypeDao, LessonReviewType> implements LessonReviewTypeService {

    @Autowired
    private LessonReviewModelService lessonReviewModelService;

    @Autowired
    private LessonReviewTypeChildService lessonReviewTypeChildService;

    @Override
    protected void validatorEntity(LessonReviewType entity) {
        super.validatorEntity(entity);
        NonClassHourOrId(entity);
    }

    @Override
    protected void validatorEntity(List<LessonReviewType> entity) {
        super.validatorEntity(entity);
        entity.forEach(
                lessonReviewType -> NonClassHourOrId(lessonReviewType)
        );
    }

    private void NonClassHourOrId(LessonReviewType entity) {
        //判断子表的学时不为空
        List<LessonReviewTypeChild> allChildrenEntity = new ArrayList<>();
        List<LessonReviewType> children = entity.getTypeChildrenMation();
        if (CollectionUtil.isNotEmpty(children)) {
            allChildrenEntity.addAll(JSONUtil.toList(JSONUtil.toJsonStr(children), LessonReviewTypeChild.class));
        }
        for (LessonReviewTypeChild child : allChildrenEntity) {
            Integer classHour = child.getClassHour();
            if (classHour == null || classHour < CommonNumConstants.NUM_ZERO) {
                throw new RuntimeException("学时不能为空");
            }
        }
        String modelId = entity.getModelId();
        LessonReviewModel lessonReviewModel = lessonReviewModelService.queryByModelId(modelId);
        if (ObjectUtil.isEmpty(lessonReviewModel)) {
            throw new CustomException("该模型不存在");
        }
        if (StrUtil.isNotEmpty(entity.getParentId())) {
            QueryWrapper<LessonReviewType> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq(CommonConstants.ID, entity.getParentId());
            LessonReviewType lessonReviewType = getOne(queryWrapper);
            if (ObjectUtil.isEmpty(lessonReviewType)) {
                throw new CustomException("该父级角色不存在");
            }
        }
    }

    @Override
    protected void createPostpose(List<LessonReviewType> entity, String userId) {
        List<LessonReviewType> reviewTypeList = new ArrayList<>();
        for (LessonReviewType reviewType : entity) {
            List<LessonReviewType> typeChildrenMation = reviewType.getTypeChildrenMation();
            typeChildrenMation.forEach(
                    type -> {
                        type.setParentId(reviewType.getId());
                        type.setModelId(reviewType.getModelId());
                    });
            reviewTypeList.addAll(typeChildrenMation);
        }
        List<LessonReviewTypeChild> lessonReviewTypeChildren = JSONUtil.toList(JSONUtil.toJsonStr(reviewTypeList), LessonReviewTypeChild.class);
        lessonReviewTypeChildService.createEntity(lessonReviewTypeChildren, userId);
    }

    @Override
    protected void updatePostpose(List<LessonReviewType> entityList, String userId) {
        List<String> parentIds = new ArrayList<>();
        List<LessonReviewTypeChild> allChildrenEntities = new ArrayList<>();
        for (LessonReviewType entity : entityList) {
            String parentId = entity.getId();
            parentIds.add(parentId);

            List<LessonReviewType> children = entity.getTypeChildrenMation();
            if (CollectionUtil.isNotEmpty(children)) {
                children.forEach(child -> {
                    child.setParentId(parentId);
                    child.setModelId(entity.getModelId());
                });
                allChildrenEntities.addAll(JSONUtil.toList(JSONUtil.toJsonStr(children), LessonReviewTypeChild.class));
            }
        }
        if (CollectionUtil.isNotEmpty(parentIds)) {
            lessonReviewTypeChildService.deleteReviewTypeChildByParentIdList(parentIds);
        }
        if (CollectionUtil.isNotEmpty(allChildrenEntities)) {
            lessonReviewTypeChildService.createEntity(allChildrenEntities, userId);
        }
    }

    @Override
    public void deleteTypeByModelId(String id) {
        QueryWrapper<LessonReviewType> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(LessonReviewType::getModelId), id);
        remove(queryWrapper);
    }

    @Override
    public List<LessonReviewType> queryTypeByModelId(String id) {
        QueryWrapper<LessonReviewType> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(LessonReviewType::getModelId), id);
        List<LessonReviewType> lessonReviewTypeList = list(queryWrapper);
        List<LessonReviewType> NoessonReviewTypes = new ArrayList<>();
        if (ObjectUtil.isNotEmpty(lessonReviewTypeList)) {
            NoessonReviewTypes = lessonReviewTypeList.stream().filter(
                    lessonReviewType -> StrUtil.isEmpty(lessonReviewType.getParentId())
            ).collect(Collectors.toList());
            List<LessonReviewType> YeslessonReviewTypes = lessonReviewTypeList.stream().filter(
                    lessonReviewType -> StrUtil.isNotEmpty(lessonReviewType.getParentId())
            ).collect(Collectors.toList());
            Map<String, List<LessonReviewType>> collected =
                    YeslessonReviewTypes.stream().collect(Collectors.groupingBy(LessonReviewType::getParentId));
            NoessonReviewTypes.forEach(
                    lessonReviewType -> lessonReviewType.setTypeChildrenMation(collected.get(lessonReviewType.getId()))
            );
        }
        return NoessonReviewTypes;
    }
}
