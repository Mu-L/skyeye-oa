package com.skyeye.school.interaction.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.rest.wall.user.service.IUserService;
import com.skyeye.school.announcement.entity.Announcement;
import com.skyeye.school.interaction.dao.QuestionCategoriesDao;
import com.skyeye.school.interaction.entity.QuestionCategories;
import com.skyeye.school.interaction.service.QuestionCategoriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName: questionCategoriesServiceImpl
 * @Description: 互动答题题目类别管理服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/7/17 10:46
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "互动答题题目类别管理", groupName = "互动答题题目类别管理")
public class QuestionCategoriesServiceImpl extends SkyeyeBusinessServiceImpl<QuestionCategoriesDao, QuestionCategories> implements QuestionCategoriesService {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private QuestionCategoriesService questionCategoriesService;

    @Override
    public void queryQuestionCategoriesAllList(InputObject inputObject, OutputObject outputObject) {
        List<QuestionCategories> questionCategories = queryAllData();
        if (CollectionUtil.isEmpty(questionCategories)) {
            iUserService.setDataMation(questionCategories, QuestionCategories::getCreateId);
        } else {
            iAuthUserService.setDataMation(questionCategories, QuestionCategories::getCreateId);
        }
        outputObject.setBeans(questionCategories);
        outputObject.settotal(questionCategories.size());
    }

    @Override public void validatorEntity(QuestionCategories questionCategories){
        super.validatorEntity(questionCategories);
        String id = questionCategories.getId();
        String categoriesName = questionCategories.getCategoriesName();
        QueryWrapper<QuestionCategories> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(QuestionCategories::getCategoriesName), categoriesName);
        List<QuestionCategories> questionCategoriesList = list(queryWrapper);
        if(StrUtil.isEmpty(id)){
            if(!CollectionUtil.isEmpty(questionCategoriesList)){
                throw new CustomException("题目类别名称已存在");
            }
        } else {
            QuestionCategories questionCategories1 = questionCategoriesService.selectById(id);
            if (ObjectUtil.isNull(questionCategories1.getId())) {
                throw new CustomException("该类别id不存在");
            }
            if (!questionCategoriesList.get(0).getId().equals(id)||CollectionUtil.isEmpty(questionCategoriesList)){
                throw new CustomException("题目类别名称已存在");
           }
        }
    }
}
