/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.score.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.google.common.base.Joiner;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.client.ExecuteFeignClient;
import com.skyeye.common.constans.CommonCharConstants;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.constans.SchoolConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.object.PutObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.classenum.LoginIdentity;
import com.skyeye.exception.CustomException;
import com.skyeye.rest.wall.certification.rest.ICertificationRest;
import com.skyeye.rest.wall.certification.service.ICertificationService;
import com.skyeye.school.score.classenum.NumberCodeEnum;
import com.skyeye.school.score.dao.ScoreTypeChildDao;
import com.skyeye.school.score.entity.Score;
import com.skyeye.school.score.entity.ScoreTypeChild;
import com.skyeye.school.score.service.ScoreService;
import com.skyeye.school.score.service.ScoreTypeChildService;
import com.skyeye.school.subject.service.SubjectClassesStuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

    @Autowired
    private ICertificationRest iCertificationRest;

    @Autowired
    private ICertificationService iCertificationService;

    @Autowired
    private SubjectClassesStuService subjectClassesStuService;

    @Override
    public void createPrepose(ScoreTypeChild scoreTypeChild) {
        if (StrUtil.isEmpty(scoreTypeChild.getParentId())) {
            // 新增一级数据
            scoreTypeChild.setNameLinkId(NumberCodeEnum.CUSTOM.getKey());
            scoreTypeChild.setNameLinkKey(NumberCodeEnum.class.getName());
            scoreTypeChild.setProportion(CommonNumConstants.NUM_ZERO.toString());
        } else {
            // 新增二级数据
            scoreTypeChild.setNameLinkId(StrUtil.EMPTY);
            scoreTypeChild.setNameLinkKey(StrUtil.EMPTY);
        }
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
        Map<String, String> childIdToSubClassLinkId = entity.stream().collect(Collectors.toMap(ScoreTypeChild::getId, ScoreTypeChild::getSubClassLinkId));
        scoreService.initScorePartForScoreType(ids, childIdToSubClassLinkId);
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
        List<ScoreTypeChild> list = list(queryWrapper);
        if (CollectionUtil.isEmpty(list)) {
            return;
        }
        List<String> ids = list.stream().map(ScoreTypeChild::getId).collect(Collectors.toList());
        scoreService.deleteByObjectId(ids.toArray(new String[ids.size()]));
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
    public List<ScoreTypeChild> selectIds(String subjectId, List<String> subjectClassesIdList, String testKey) {
        QueryWrapper<ScoreTypeChild> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ScoreTypeChild::getSubjectId), subjectId)
                .in(MybatisPlusUtil.toColumns(ScoreTypeChild::getSubClassLinkId), subjectClassesIdList)
                .eq(MybatisPlusUtil.toColumns(ScoreTypeChild::getNameLinkId), testKey);
        return list(queryWrapper);
    }

    @Override
    public void delete(String subjectId, String subjectClassesId, String nameLinkId) {
        ScoreTypeChild scoreTypeChild = select(subjectId, subjectClassesId, nameLinkId);
        if (ObjectUtil.isEmpty(scoreTypeChild)) {
            return;
        }
        // 删除成绩信息
        scoreService.deleteByObjectId(scoreTypeChild.getId());
        // 重新计算成绩
        scoreService.calculateScore(subjectId, subjectClassesId);
    }

    @Override
    public void deletes(String subjectId, List<String> subjectClassesIdList, String nameLinkId) {
        List<ScoreTypeChild> scoreTypeChildList = selectIds(subjectId, subjectClassesIdList, nameLinkId);
        if (CollectionUtil.isEmpty(scoreTypeChildList)) {
            return;
        }
        // 删除成绩信息
        List<String> ids = scoreTypeChildList.stream().map(ScoreTypeChild::getId).collect(Collectors.toList());
        scoreService.deleteByObjectId(ids.toArray(new String[ids.size()]));
        // 重新计算成绩
        scoreService.calculateScore(subjectId, subjectClassesIdList);
    }

    @Override
    public List<ScoreTypeChild> queryBySubjectIdAndSubjectClassId(String subjectId, String subjectClassesId) {
        QueryWrapper<ScoreTypeChild> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ScoreTypeChild::getSubjectId), subjectId)
                .eq(MybatisPlusUtil.toColumns(ScoreTypeChild::getSubClassLinkId), subjectClassesId);
        return list(queryWrapper);
    }

    @Override
    public List<ScoreTypeChild> queryBySubjectIdAndSubjectClassId(String subjectId, List<String> subjectClassesIdList) {
        QueryWrapper<ScoreTypeChild> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ScoreTypeChild::getSubjectId), subjectId)
                .in(MybatisPlusUtil.toColumns(ScoreTypeChild::getSubClassLinkId), subjectClassesIdList);
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
    public void editNames(String subjectId, List<String> subjectClassesId, String nameLinkId, String name) {
        UpdateWrapper<ScoreTypeChild> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(MybatisPlusUtil.toColumns(ScoreTypeChild::getSubjectId), subjectId)
                .in(MybatisPlusUtil.toColumns(ScoreTypeChild::getSubClassLinkId), subjectClassesId)
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
        List<String> numberCodeEnumAllKey = NumberCodeEnum.getAllKey().stream().map(String::valueOf).collect(Collectors.toList());
        queryWrapper.in(MybatisPlusUtil.toColumns(ScoreTypeChild::getNameLinkId), numberCodeEnumAllKey);
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
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(ScoreTypeChild::getCreateTime));

        List<ScoreTypeChild> scoreTypeChildList = list(queryWrapper);
        // 将id等于parentId的元素移到集合末尾
        scoreTypeChildList.sort((a, b) -> {
            if (a.getId().equals(parentId)) {
                return 1;
            } else if (b.getId().equals(parentId)) {
                return -1;
            }
            return 0;
        });
        scoreTypeChildList.forEach(scoreTypeChild -> {
            if (scoreTypeChild.getId().equals(parentId)) {
                scoreTypeChild.setName("成绩");
            }
        });
        outputObject.setCustomBeans("tableRows", JSONUtil.toList(JSONUtil.toJsonStr(scoreTypeChildList), null));

        List<String> ids = scoreTypeChildList.stream().map(ScoreTypeChild::getId).collect(Collectors.toList());

        List<Score> scoreList = null;
        String userIdentity = PutObject.getRequest().getHeader(SchoolConstants.USER_IDENTITY_KEY);
        if (StrUtil.equals(userIdentity, LoginIdentity.STUDENT.getKey())) {
            // 学生身份信息
            String userId = InputObject.getLogParamsStatic().get("id").toString();
            Map<String, Object> certification = iCertificationService.queryCertificationById(userId);
            String studentNumber = certification.get("studentNumber").toString();
            scoreList = scoreService.queryScoreList(ids, studentNumber);
        } else {
            // 教师身份信息
            scoreList = scoreService.queryScoreList(ids, StrUtil.EMPTY);
        }
        if (CollectionUtil.isEmpty(scoreList)) {
            return;
        }

        // 获取学生信息
        List<String> stuNoList = scoreList.stream().map(Score::getStuNo).distinct().collect(Collectors.toList());
        // 过滤出当前班级学生信息
        Map<String, String> stuNoReward = subjectClassesStuService.queryStuStarNumBySubClassesId(subClassLinkId, stuNoList);
        stuNoList = stuNoReward.keySet().stream().collect(Collectors.toList());
        if (CollectionUtil.isEmpty(stuNoList)) {
            return;
        }
        List<String> finalStuNoList = stuNoList;
        // 只要当前班级的学生成绩信息
        scoreList = scoreList.stream().filter(score -> finalStuNoList.contains(score.getStuNo())).collect(Collectors.toList());

        List<Map<String, Object>> userList = ExecuteFeignClient.get(() ->
                iCertificationRest.queryUserByStudentNumber(Joiner.on(CommonCharConstants.COMMA_MARK).join(finalStuNoList))).getRows();
        if (CollectionUtil.isEmpty(userList)) {
            return;
        }
        Map<String, String> userMap = userList.stream()
                .collect(Collectors.toMap(user -> user.get("studentNumber").toString(), user -> user.getOrDefault("realName", StrUtil.EMPTY).toString()));

        Map<String, List<Score>> collect = scoreList.stream().collect(Collectors.groupingBy(Score::getObjectId));
        Map<String, List<Score>> stuCollect = scoreList.stream().collect(Collectors.groupingBy(Score::getStuNo));
        Map<String, Map<String, Object>> stuScoreMap = new HashMap<>();
        for (ScoreTypeChild scoreTypeChild : scoreTypeChildList) {
            List<Score> scores = collect.get(scoreTypeChild.getId());
            if (CollectionUtil.isEmpty(scores)) {
                continue;
            }
            scores.forEach(score -> {
                if (stuScoreMap.containsKey(score.getStuNo())) {
                    Map<String, Object> map = stuScoreMap.get(score.getStuNo());
                    Map<String, Object> scoreInfo = new HashMap<>();
                    scoreInfo.put("score", score.getScore());
                    scoreInfo.put("id", score.getId());
                    map.put(scoreTypeChild.getId(), scoreInfo);
                } else {
                    Map<String, Object> map = new HashMap<>();
                    Map<String, Object> scoreInfo = new HashMap<>();
                    scoreInfo.put("score", score.getScore());
                    scoreInfo.put("id", score.getId());
                    map.put(scoreTypeChild.getId(), scoreInfo);
                    map.put("stuNo", score.getStuNo());
                    map.put("name", userMap.get(score.getStuNo()));
                    stuScoreMap.put(score.getStuNo(), map);
                }
            });
        }
        float sum = 0;
        for (Map<String, Object> student : userList) {
            String stuNo = student.get("studentNumber").toString();
            List<Score> scores = stuCollect.get(stuNo);
            if (CollectionUtil.isEmpty(scores)) {
                stuScoreMap.get(stuNo).put("avg", String.format("%.2f", sum));
                continue;
            }
            for (Score score : scores) {
                sum += Float.parseFloat(score.getScore());
            }
            stuScoreMap.get(stuNo).put("avg", String.format("%.2f", sum / scoreTypeChildList.size()));
            sum = 0;
        }
        List<Map<String, Object>> result = stuScoreMap.values().stream().collect(Collectors.toList());
        outputObject.setBeans(result);
        outputObject.settotal(result.size());
    }

    @Override
    public void changeProportion(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String subjectId = params.get("subjectId").toString();
        String subClassLinkId = params.get("subClassLinkId").toString();
        List<Map<String, Object>> proportionMapList = JSONUtil.toList(params.get("proportionList").toString(), null);
        Map<String, String> proportionMap = proportionMapList.stream()
                .collect(Collectors.toMap(item -> item.get("id").toString(), item -> item.get("proportion").toString()));

        // 查询这个课程与班级下的所有成绩类型
        List<ScoreTypeChild> scoreTypeChildrenList = queryBySubjectIdAndSubjectClassId(subjectId, subClassLinkId);
        for (ScoreTypeChild scoreTypeChild : scoreTypeChildrenList) {
            if (proportionMap.containsKey(scoreTypeChild.getId())) {
                scoreTypeChild.setProportion(proportionMap.get(scoreTypeChild.getId()));
            }
        }
        List<String> scoreTypeIds = scoreTypeChildrenList.stream().map(ScoreTypeChild::getId).collect(Collectors.toList());
        // 查询这个学生的成绩
        List<Score> scoreList = scoreService.queryScoreList(scoreTypeIds, StrUtil.EMPTY);
        // 计算成绩
        scoreService.calculateScore(scoreTypeChildrenList, scoreList);
        super.updateEntity(scoreTypeChildrenList, inputObject.getLogParams().get("id").toString());
    }

    @Override
    public void queryScoreTypeChildListByParentId(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String id = params.get("id").toString();
        String subjectId = params.get("subjectId").toString();
        String subClassLinkId = params.get("subClassLinkId").toString();
        QueryWrapper<ScoreTypeChild> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ScoreTypeChild::getSubjectId), subjectId)
                .eq(MybatisPlusUtil.toColumns(ScoreTypeChild::getSubClassLinkId), subClassLinkId)
                // 忽略其本身
                .ne(CommonConstants.ID, id);
        queryWrapper.and(wra -> {
            String parentId = MybatisPlusUtil.toColumns(ScoreTypeChild::getParentId);
            wra.eq(parentId, id)
                    .or(w -> {
                        w.eq(parentId, StrUtil.EMPTY)
                                .or().eq(parentId, null);
                    });
        });
        queryWrapper.in(MybatisPlusUtil.toColumns(ScoreTypeChild::getNameLinkId), NumberCodeEnum.getKeysButAll());
        List<ScoreTypeChild> scoreTypeChildList = list(queryWrapper);
        if (CollectionUtil.isEmpty(scoreTypeChildList)) {
            return;
        }
        List<ScoreTypeChild> beans = scoreTypeChildList.stream()
                .filter(scoreTypeChild -> !Objects.equals(NumberCodeEnum.ALL.getKey(), scoreTypeChild.getNameLinkId())).collect(Collectors.toList());
        outputObject.setBeans(beans);
        outputObject.settotal(beans.size());
    }

    @Override
    public void connectScore(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String id = params.get("id").toString();
        ScoreTypeChild parentSTC = selectById(id);
        if (ObjectUtil.isEmpty(parentSTC)) {
            throw new CustomException("该id查询不到数据");
        }
        List<String> childIdList = JSONUtil.toList(params.get("childIdList").toString(), null);
        List<ScoreTypeChild> scoreTypeChildren = super.selectByIds(childIdList.toArray(new String[]{}));
        if (CollectionUtil.isEmpty(scoreTypeChildren)) {
            throw new CustomException("该childIdList查询不到数据");
        }
        for (ScoreTypeChild scoreTypeChild : scoreTypeChildren) {
            if (StrUtil.isEmpty(scoreTypeChild.getParentId())) {
                // 关联的成绩类型的parentId为空，则表示该成绩类型还没有绑定，直接绑定
                scoreTypeChild.setParentId(id);
            } else if (StrUtil.isNotEmpty(scoreTypeChild.getParentId())) {
                // 关联的成绩类型的parentId不为空，则表示该成绩类型已经有绑定，解除绑定
                scoreTypeChild.setParentId(StrUtil.EMPTY);
            }
        }
        super.updateEntity(scoreTypeChildren, inputObject.getLogParams().get("id").toString());
        scoreService.calculateScore(parentSTC.getSubjectId(), parentSTC.getSubClassLinkId());
        outputObject.setBeans(scoreTypeChildren);
        outputObject.settotal(scoreTypeChildren.size());
    }
}
