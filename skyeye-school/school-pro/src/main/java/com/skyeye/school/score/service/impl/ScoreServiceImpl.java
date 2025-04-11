/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.score.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNodeConfig;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.school.score.dao.ScoreDao;
import com.skyeye.school.score.entity.Score;
import com.skyeye.school.score.entity.ScoreTypeChild;
import com.skyeye.school.score.service.ScoreService;
import com.skyeye.school.score.service.ScoreTypeChildService;
import com.skyeye.school.subject.service.SubjectClassesStuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: ScoreServiceImpl
 * @Description: 成绩管理服务层
 * @author: skyeye云系列--卫志强
 * @date: 2023/8/29 10:53
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "成绩管理", groupName = "成绩管理")
public class ScoreServiceImpl extends SkyeyeBusinessServiceImpl<ScoreDao, Score> implements ScoreService {

    @Autowired
    private SubjectClassesStuService subjectClassesStuService;

    @Autowired
    private ScoreTypeChildService scoreTypeChildService;

    @Override
    public void deleteByObjectId(String... objectId) {
        List<String> idList = Arrays.asList(objectId).stream()
            .filter(id -> StrUtil.isNotEmpty(id)).distinct().collect(Collectors.toList());
        if (CollectionUtil.isEmpty(idList)) {
            return;
        }
        QueryWrapper<Score> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(Score::getObjectId), idList);
        remove(queryWrapper);
    }

    @Override
    public void initScorePartForScoreType(List<String> scoreTypeIds, String subClassLinkId) {
        List<Map<String, Object>> students = subjectClassesStuService.queryClassStuIds(subClassLinkId);
        if (CollectionUtil.isEmpty(students)) {
            return;
        }
        List<Score> scoreSumList = new ArrayList<>();
        scoreTypeIds.forEach(scoreTypeId -> {
            for (Map<String, Object> student : students) {
                Score newScoreSum = new Score();
                newScoreSum.setScore(CommonNumConstants.NUM_ZERO.toString());
                newScoreSum.setObjectId(scoreTypeId);
                newScoreSum.setStuNo(student.get("studentNumber").toString());
                scoreSumList.add(newScoreSum);
            }
        });
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        createEntity(scoreSumList, userId);
    }

    @Override
    public void initScorePartForScoreType(String scoreTypeId, String subClassLinkId) {
        List<Map<String, Object>> students = subjectClassesStuService.queryClassStuIds(subClassLinkId);
        if (CollectionUtil.isEmpty(students)) {
            return;
        }
        List<Score> scoreSumList = new ArrayList<>();
        for (Map<String, Object> student : students) {
            Score newScoreSum = new Score();
            newScoreSum.setScore(CommonNumConstants.NUM_ZERO.toString());
            newScoreSum.setObjectId(scoreTypeId);
            newScoreSum.setStuNo(student.get("studentNumber").toString());
            scoreSumList.add(newScoreSum);
        }
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        createEntity(scoreSumList, userId);
    }

    @Override
    public void initScorePartForStudent(List<String> scoreTypeIds, String studentNumber) {
        List<Score> scoreSumList = new ArrayList<>();
        scoreTypeIds.forEach(scoreTypeId -> {
            Score newScoreSum = new Score();
            newScoreSum.setScore(CommonNumConstants.NUM_ZERO.toString());
            newScoreSum.setObjectId(scoreTypeId);
            newScoreSum.setStuNo(studentNumber);
            scoreSumList.add(newScoreSum);
        });
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        createEntity(scoreSumList, userId);
    }

    @Override
    public void deleteScoreByStudentNumber(String studentNumber) {
        QueryWrapper<Score> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Score::getStuNo), studentNumber);
        remove(queryWrapper);
    }

    @Override
    public List<Score> queryScoreList(List<String> scoreTypeIds, String studentNumber) {
        QueryWrapper<Score> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(Score::getObjectId), scoreTypeIds);
        if (StrUtil.isNotEmpty(studentNumber)) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(Score::getStuNo), studentNumber);
        }
        return list(queryWrapper);
    }

    @Override
    public void updateStudentScore(String subjectId, String subClassLinkId, String studentNumber, String nameLinkId, String score) {
        // 查询这个课程与班级下的所有成绩类型
        List<ScoreTypeChild> scoreTypeChildrenList = scoreTypeChildService.queryBySubjectIdAndSubjectClassId(subjectId, subClassLinkId);
        List<String> scoreTypeIds = scoreTypeChildrenList.stream().map(ScoreTypeChild::getId).collect(Collectors.toList());
        // 查询这个学生的成绩
        List<Score> scoreList = queryScoreList(scoreTypeIds, studentNumber);
        // 更新学生的成绩
        // 1. 先查询出 scoreTypeChildId
        ScoreTypeChild scoreTypeChild = scoreTypeChildrenList.stream()
            .filter(item -> item.getNameLinkId().equals(nameLinkId)).findFirst().orElse(null);
        if (ObjectUtil.isNotEmpty(scoreTypeChild)) {
            return;
        }
        scoreList.forEach(item -> {
            if (item.getObjectId().equals(scoreTypeChild.getId())) {
                item.setScore(score);
            }
        });

        calculateScore(scoreTypeChildrenList, scoreList);
    }

    @Override
    public void calculateScore(List<ScoreTypeChild> scoreTypeChildrenList, List<Score> scoreList) {
        if (CollectionUtil.isEmpty(scoreTypeChildrenList) || CollectionUtil.isEmpty(scoreList)) {
            return;
        }

        // 将成绩类型转换为树形结构
        TreeNodeConfig treeNodeConfig = new TreeNodeConfig();
        treeNodeConfig.setIdKey("id");
        treeNodeConfig.setParentIdKey("parentId");
        treeNodeConfig.setChildrenKey("children");
        treeNodeConfig.setWeightKey("proportion");
        List<Tree<String>> scoreTypeTree = TreeUtil.build(scoreTypeChildrenList, StrUtil.EMPTY, treeNodeConfig,
            (treeNode, tree) -> {
                tree.setId(treeNode.getId());
                tree.setParentId(treeNode.getParentId());
                tree.setWeight(StrUtil.isEmpty(treeNode.getProportion()) ?
                    CommonNumConstants.NUM_ZERO.toString() : treeNode.getProportion());
                tree.putExtra("scoreTypeChild", treeNode);
            });

        // 为每个成绩类型节点计算成绩
        for (Tree<String> rootNode : scoreTypeTree) {
            calculateNodeScore(rootNode, scoreList);
        }

        // 更新成绩到数据库
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        updateEntity(scoreList, userId);
    }

    @Override
    public void calculateScore(String subjectId, String subClassLinkId) {
        // 查询这个课程与班级下的所有成绩类型
        List<ScoreTypeChild> scoreTypeChildrenList = scoreTypeChildService.queryBySubjectIdAndSubjectClassId(subjectId, subClassLinkId);
        List<String> scoreTypeIds = scoreTypeChildrenList.stream().map(ScoreTypeChild::getId).collect(Collectors.toList());
        // 查询这个学生的成绩
        List<Score> scoreList = queryScoreList(scoreTypeIds, StrUtil.EMPTY);
        // 计算成绩
        calculateScore(scoreTypeChildrenList, scoreList);
    }

    @Override
    public void updateScore(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String subjectId = params.get("subjectId").toString();
        String subClassLinkId = params.get("subClassLinkId").toString();
        List<Map<String, Object>> scoreMapList = JSONUtil.toList(params.get("scoreList").toString(), null);
        Map<String, String> scoreMap = scoreMapList.stream()
            .collect(Collectors.toMap(item -> item.get("id").toString(), item -> item.get("score").toString()));

        // 查询这个课程与班级下的所有成绩类型
        List<ScoreTypeChild> scoreTypeChildrenList = scoreTypeChildService.queryBySubjectIdAndSubjectClassId(subjectId, subClassLinkId);
        List<String> scoreTypeIds = scoreTypeChildrenList.stream().map(ScoreTypeChild::getId).collect(Collectors.toList());
        // 查询这个学生的成绩
        List<Score> scoreList = queryScoreList(scoreTypeIds, StrUtil.EMPTY);
        // 更新学生的成绩
        scoreList.forEach(item -> {
            if (scoreMap.containsKey(item.getId())) {
                item.setScore(scoreMap.get(item.getId()));
            }
        });
        // 计算成绩
        calculateScore(scoreTypeChildrenList, scoreList);
    }

    /**
     * 递归计算节点成绩
     *
     * @param node      当前节点
     * @param scoreList 成绩列表
     * @return 该节点的计算结果
     */
    private double calculateNodeScore(Tree<String> node, List<Score> scoreList) {
        List<Tree<String>> children = node.getChildren();
        String nodeId = node.getId();

        // 如果是叶子节点，直接返回该节点的分数
        if (CollectionUtil.isEmpty(children)) {
            Score score = scoreList.stream()
                .filter(s -> s.getObjectId().equals(nodeId))
                .findFirst()
                .orElse(null);
            return score == null ? 0.0 : Double.parseDouble(score.getScore());
        }

        // 计算子节点的加权平均分
        double totalScore = 0.0;
        double totalWeight = 0.0;

        for (Tree<String> child : children) {
            double childScore = calculateNodeScore(child, scoreList);
            double weight = Double.parseDouble(child.getWeight().toString());
            totalScore += childScore * weight / 100.0; // 将百分比转换为小数
            totalWeight += weight;
        }

        // 如果权重总和不为0，计算加权平均分
        double finalScore = totalWeight > 0 ? totalScore : 0.0;

        // 更新当前节点的分数
        Score currentScore = scoreList.stream()
            .filter(s -> s.getObjectId().equals(nodeId))
            .findFirst()
            .orElse(null);

        if (currentScore != null) {
            currentScore.setScore(String.format("%.2f", finalScore));
        }

        return finalScore;
    }

}
