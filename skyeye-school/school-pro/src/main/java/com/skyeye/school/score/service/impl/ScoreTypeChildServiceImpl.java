/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.score.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.school.score.classenum.NumberCodeEnum;
import com.skyeye.school.score.dao.ScoreTypeChildDao;
import com.skyeye.school.score.entity.Score;
import com.skyeye.school.score.entity.ScoreTypeChild;
import com.skyeye.school.score.service.ScoreService;
import com.skyeye.school.score.service.ScoreTypeChildService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: ScoreTypeChildServiceImpl
 * @Description: 成绩类型子表管理服务层
 * @author: skyeye云系列--卫志强
 * @date: 2023/8/29 10:53
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "成绩类型子表管理", groupName = "成绩类型子表管理")
public class ScoreTypeChildServiceImpl extends SkyeyeBusinessServiceImpl<ScoreTypeChildDao, ScoreTypeChild> implements ScoreTypeChildService {

    @Autowired
    private ScoreService scoreService;

    @Override
    public void validatorEntity(ScoreTypeChild scoreTypeChild) {
        // 新增/编辑不操作占比和parentId，通过另外的接口修改
        scoreTypeChild.setProportion(CommonNumConstants.NUM_ZERO.toString());
    }

    @Override
    public void createPostpose(ScoreTypeChild entity, String userId) {
        // 初始化成绩
        scoreService.initScorePartForScoreType(entity.getId(), entity.getSubClassLinkId());
    }

    @Override
    public void updatePostpose(ScoreTypeChild entity, String userId) {
        ScoreTypeChild scoreTypeChild = selectById(entity.getId());
        if (!StrUtil.equals(entity.getProportion(), scoreTypeChild.getProportion())) {
            // 重新占比，重新计算成绩
            scoreService.calculateScore(scoreTypeChild.getSubjectId(), scoreTypeChild.getSubClassLinkId());
        }
    }

    @Override
    public void createPostpose(List<ScoreTypeChild> entity, String userId) {
        if (CollectionUtil.isEmpty(entity)) {
            return;
        }
        // 初始化成绩
        List<String> ids = entity.stream().map(ScoreTypeChild::getId).collect(Collectors.toList());
        String subClassLinkId = entity.get(CommonNumConstants.NUM_ZERO).getSubClassLinkId();
        scoreService.initScorePartForScoreType(ids, subClassLinkId);
    }

    @Override
    public void initScoreTypeChild(String subjectId, String subjectClassId) {
        List<ScoreTypeChild> scoreTypeChildList = NumberCodeEnum.getScoreTypeList(subjectId, subjectClassId);
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        createEntity(scoreTypeChildList, userId);
    }

    @Override
    public void deleteBySubjectIdAndSubjectClassId(String subjectId, String subjectClassesId) {
        QueryWrapper<ScoreTypeChild> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ScoreTypeChild::getSubjectId), subjectId)
            .eq(MybatisPlusUtil.toColumns(ScoreTypeChild::getSubClassLinkId), subjectClassesId);
        remove(queryWrapper);
    }

    @Override
    public void deletePostpose(ScoreTypeChild entity) {
        // 删除成绩信息
        scoreService.deleteByObjectId(entity.getId());
        // 重新计算成绩
        scoreService.calculateScore(entity.getSubjectId(), entity.getSubClassLinkId());
    }

    @Override
    public ScoreTypeChild select(String subjectId, String subjectClassesId, String nameLinkId) {
        QueryWrapper<ScoreTypeChild> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ScoreTypeChild::getSubjectId), subjectId)
            .eq(MybatisPlusUtil.toColumns(ScoreTypeChild::getSubClassLinkId), subjectClassesId)
            .eq(MybatisPlusUtil.toColumns(ScoreTypeChild::getNameLinkId), nameLinkId);
        return getOne(queryWrapper);
    }

    @Override
    public void delete(String subjectId, String subjectClassesId, String nameLinkId) {
        ScoreTypeChild scoreTypeChild = select(subjectId, subjectClassesId, nameLinkId);
        if (ObjectUtil.isEmpty(scoreTypeChild)) {
            return;
        }
        // 删除成绩信息
        scoreService.deleteByObjectId(scoreTypeChild.getId());
    }

    @Override
    public List<ScoreTypeChild> queryBySubjectIdAndSubjectClassId(String subjectId, String subjectClassesId) {
        QueryWrapper<ScoreTypeChild> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ScoreTypeChild::getSubjectId), subjectId)
            .eq(MybatisPlusUtil.toColumns(ScoreTypeChild::getSubClassLinkId), subjectClassesId);
        return list(queryWrapper);
    }

    @Override
    public void editName(String subjectId, String subjectClassesId, String nameLinkId, String name) {
        UpdateWrapper<ScoreTypeChild> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(MybatisPlusUtil.toColumns(ScoreTypeChild::getSubjectId), subjectId)
            .eq(MybatisPlusUtil.toColumns(ScoreTypeChild::getSubClassLinkId), subjectClassesId)
            .eq(MybatisPlusUtil.toColumns(ScoreTypeChild::getNameLinkId), nameLinkId);
        updateWrapper.set(MybatisPlusUtil.toColumns(ScoreTypeChild::getName), name);
        update(updateWrapper);
    }

    @Override
    public void queryScoreTypeChildFirstList(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String subjectId = params.get("subjectId").toString();
        String subClassLinkId = params.get("subClassLinkId").toString();
        QueryWrapper<ScoreTypeChild> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ScoreTypeChild::getSubjectId), subjectId)
            .eq(MybatisPlusUtil.toColumns(ScoreTypeChild::getSubClassLinkId), subClassLinkId);
        String parentIdKey = MybatisPlusUtil.toColumns(ScoreTypeChild::getParentId);
        queryWrapper.and(wra -> {
            wra.isNull(parentIdKey).or().eq(parentIdKey, StrUtil.EMPTY);
        });
        List<ScoreTypeChild> scoreTypeChildList = list(queryWrapper);
        outputObject.setBeans(scoreTypeChildList);
        outputObject.settotal(scoreTypeChildList.size());
    }

    @Override
    public void queryScoreTypeChildSecondList(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String subjectId = params.get("subjectId").toString();
        String subClassLinkId = params.get("subClassLinkId").toString();
        String parentId = params.get("parentId").toString();
        QueryWrapper<ScoreTypeChild> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ScoreTypeChild::getSubjectId), subjectId)
            .eq(MybatisPlusUtil.toColumns(ScoreTypeChild::getSubClassLinkId), subClassLinkId);
        queryWrapper.and(wra -> {
            wra.eq(MybatisPlusUtil.toColumns(ScoreTypeChild::getParentId), parentId)
                .or().eq(CommonConstants.ID, parentId);
        });

        List<ScoreTypeChild> scoreTypeChildList = list(queryWrapper);
        List<String> ids = scoreTypeChildList.stream().map(ScoreTypeChild::getId).collect(Collectors.toList());
        List<Score> scoreList = scoreService.queryScoreList(ids, StrUtil.EMPTY);
        if (CollectionUtil.isEmpty(scoreList)) {
            return;
        }
        Map<String, List<Score>> collect = scoreList.stream().collect(Collectors.groupingBy(Score::getObjectId));

        Map<String, Map<String, Object>> stuScoreMap = new HashMap<>();
        for (ScoreTypeChild scoreTypeChild : scoreTypeChildList) {
            List<Score> scores = collect.get(scoreTypeChild.getId());
            if (CollectionUtil.isEmpty(scores)) {
                continue;
            }
            scores.forEach(score -> {
                if (stuScoreMap.containsKey(score.getStuNo())) {
                    Map<String, Object> map = stuScoreMap.get(score.getStuNo());
                    map.put(scoreTypeChild.getId(), score.getScore());
                } else {
                    Map<String, Object> map = new HashMap<>();
                    map.put(scoreTypeChild.getId(), score.getScore());
                    map.put("stuNo", score.getStuNo());
                    stuScoreMap.put(score.getStuNo(), map);
                }
            });
        }
        List<Map<String, Object>> result = stuScoreMap.values().stream().collect(Collectors.toList());
        outputObject.setBeans(result);
        outputObject.settotal(result.size());
    }

}
