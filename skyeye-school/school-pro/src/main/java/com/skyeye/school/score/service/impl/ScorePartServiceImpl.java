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
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.school.score.dao.ScorePartDao;
import com.skyeye.school.score.entity.ScorePart;
import com.skyeye.school.score.entity.ScoreSum;
import com.skyeye.school.score.entity.ScoreType;
import com.skyeye.school.score.entity.ScoreTypeChild;
import com.skyeye.school.score.service.ScorePartService;
import com.skyeye.school.score.service.ScoreSumService;
import com.skyeye.school.score.service.ScoreTypeChildService;
import com.skyeye.school.score.service.ScoreTypeService;
import com.skyeye.school.subject.entity.SubjectClasses;
import com.skyeye.school.subject.entity.SubjectClassesStu;
import com.skyeye.school.subject.service.SubjectClassesService;
import com.skyeye.school.subject.service.SubjectClassesStuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@SkyeyeService(name = "分成绩管理", groupName = "分成绩管理")
public class ScorePartServiceImpl extends SkyeyeBusinessServiceImpl<ScorePartDao, ScorePart> implements ScorePartService {

    @Autowired
    private ScoreTypeChildService scoreTypeChildService;

    @Autowired
    private ScoreSumService scoreSumService;

    @Autowired
    private ScorePartService scorePartService;

    @Autowired
    private ScoreTypeService scoreTypeService;

    @Autowired
    private SubjectClassesService subjectClassesService;

    @Autowired
    private SubjectClassesStuService subjectClassesStuService;

    @Override
    public List<ScorePart> queryByObjectIdList(List<String> scoreTypeIdList) {
        QueryWrapper<ScorePart> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(ScorePart::getObjectId), scoreTypeIdList);
        return list(queryWrapper);
    }

    /**
     * 根据学号、任务id和修改成绩
     * 此方法可用于教师打分
     *
     * @param stuNo
     * @param workId
     * @param score
     * @param workType
     */
    @Override
    public void updateScorePartByStuNoAndWorkId(String stuNo, String workId, String score, Integer workType) {
        UpdateWrapper<ScorePart> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(MybatisPlusUtil.toColumns(ScorePart::getStuNo), stuNo)
            .eq(MybatisPlusUtil.toColumns(ScorePart::getWorkId), workId)
            .set(MybatisPlusUtil.toColumns(ScorePart::getWorkType), workType)
            .set(MybatisPlusUtil.toColumns(ScorePart::getScore), score);
        update(updateWrapper);
        ScorePart one = getOne(updateWrapper);
        updateOtherScoreSum(one);
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void updateScorePart(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String id = params.get("id").toString();
        String score = params.get("score").toString();
        if (Integer.parseInt(score) < CommonNumConstants.NUM_ZERO) {
            throw new CustomException("成绩不可为负数");
        }
        UpdateWrapper<ScorePart> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id)
            .set(MybatisPlusUtil.toColumns(ScorePart::getScore), score);
        update(updateWrapper);
        // 取出被修改的"作业信息"
        ScorePart scorePart = scorePartService.selectById(id);
        if (StrUtil.isEmpty(scorePart.getId())) {
            return;
        }
        updateOtherScoreSum(scorePart);
    }

    private void updateOtherScoreSum(ScorePart scorePart) {
        // 取出"作业成绩"信息
        ScoreTypeChild scoreTypeChild = scoreTypeChildService.queryById(scorePart.getObjectId());
        if (ObjectUtil.isEmpty(scoreTypeChild)) {
            return;
        }
        // 取出该学生的”作业成绩信息“下的所有作业信息
        List<ScorePart> scorePartList = queryByObjectIdListAndStuNo(Arrays.asList(scoreTypeChild.getId()), scorePart.getStuNo());
        int sumScore = CommonNumConstants.NUM_ZERO;
        // 计算给学生的"作业成绩"总分
        for (ScorePart part : scorePartList) {
            String flagScore = CalculationUtil.multiply(part.getScore(), part.getProportion(), CommonNumConstants.NUM_FOUR);
            sumScore = Integer.parseInt(flagScore) + sumScore;
        }
        // 更新该学生的"作业成绩"总分
        scoreSumService.updateScoreByObjectIdAndStuNo(scoreTypeChild.getId(), sumScore, scorePart.getStuNo());
        // 查询”平时成绩“的子成绩（作业成绩、测试成绩）----------二阶段
        List<ScoreTypeChild> scoreTypeChildren = scoreTypeChildService.queryListByParentIdList(Arrays.asList(scoreTypeChild.getParentId()));
        if (CollectionUtil.isEmpty(scoreTypeChildren)) {
            return;
        }
        // 获取子成绩的占比
        Map<String, String> proportionMap = scoreTypeChildren.stream()
            .collect(Collectors.toMap(ScoreTypeChild::getId, bean -> {
                return String.valueOf(bean.getProportion());
            }));
        // 取出子成绩（作业成绩、测试成绩）的id
        List<String> scoreTypeChildIdList = scoreTypeChildren.stream().map(ScoreTypeChild::getId).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(scoreTypeChildIdList)) {
            return;
        }
        // 查询子成绩（作业成绩、测试成绩）总分
        List<ScoreSum> scoreSumList = scoreSumService.queryByObjectIdListAndStuNo(scoreTypeChildIdList, scorePart.getStuNo());
        int parentPartScore = CommonNumConstants.NUM_ZERO;
        for (ScoreSum scoreSum : scoreSumList) {
            // 计算给学生的"作业成绩"总分
            String flagScore = CalculationUtil.multiply(scoreSum.getScore(), proportionMap.get(scoreSum.getObjectId()), CommonNumConstants.NUM_FOUR);
            parentPartScore = Integer.parseInt(flagScore) + parentPartScore;
        }
        // 更新该学生的”平时成绩“
        scorePartService.updateScoreByObjectIdAndStuNo(scoreTypeChild.getParentId(), parentPartScore, scorePart.getStuNo());

        // 获取”平时成绩“的父成绩id(总成绩id)---三阶段
        ScoreTypeChild parentScoreType = scoreTypeChildService.queryByTypeId(scoreTypeChild.getParentId());
        // 查询”总成绩“的子成绩（平时成绩、考试成绩）child表
        List<ScoreTypeChild> scoreTypeChildren1 = scoreTypeChildService.queryListByParentIdList(Arrays.asList(parentScoreType.getParentId()));
        if (CollectionUtil.isEmpty(scoreTypeChildren1)) {
            return;
        }
        // 收集子成绩（平时成绩、考试成绩）的id
        List<String> collectIdList = scoreTypeChildren1.stream().map(ScoreTypeChild::getScoreTypeId).collect(Collectors.toList());
        // 查询”总成绩“的子成绩（平时成绩、考试成绩） 主表
        List<ScoreType> scoreTypeList = scoreTypeService.selectByIds(collectIdList.toArray(new String[0]));
        // 收集子成绩（平时成绩、考试成绩）的占比
        Map<String, String> proportionMapParent = scoreTypeList.stream()
            .collect(Collectors.toMap(ScoreType::getId, bean -> {
                return String.valueOf(bean.getProportion());
            }));
        // 查询”总成绩“的子成绩（平时成绩、考试成绩）总分
        List<ScorePart> scorePartParent = queryByObjectIdListAndStuNo(collectIdList, scorePart.getStuNo());
        int lastScore = CommonNumConstants.NUM_ZERO;
        for (ScorePart part : scorePartParent) {
            String flagScore = CalculationUtil.multiply(part.getScore(), proportionMapParent.get(part.getObjectId()), CommonNumConstants.NUM_FOUR);
            lastScore = Integer.parseInt(flagScore + lastScore);
        }
        // 更新该学生的”总成绩“
        scoreSumService.updateScoreByObjectIdAndStuNo(parentScoreType.getParentId(), lastScore, scorePart.getStuNo());
    }

    public List<ScorePart> queryByObjectIdListAndStuNo(List<String> objectIdList, String stuNo) {
        QueryWrapper<ScorePart> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(ScorePart::getObjectId), objectIdList);
        if (StrUtil.isNotEmpty(stuNo)) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(ScorePart::getStuNo), stuNo);
        }
        return list(queryWrapper);
    }

    @Override
    public void updateScoreByObjectIdAndStuNo(String parentId, double parentPartScore, String stuNo) {
        UpdateWrapper<ScorePart> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(MybatisPlusUtil.toColumns(ScorePart::getObjectId), parentId);
        updateWrapper.eq(MybatisPlusUtil.toColumns(ScorePart::getStuNo), stuNo);
        updateWrapper.set(MybatisPlusUtil.toColumns(ScorePart::getScore), String.valueOf(parentPartScore));
        update(updateWrapper);
    }

    @Override
    public void deleteByObjectId(String objectId) {
        QueryWrapper<ScorePart> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ScorePart::getObjectId), objectId);
        remove(queryWrapper);
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void createScorePart(InputObject inputObject, OutputObject outputObject) {
        String currentUserId = inputObject.getLogParams().get("id").toString();
        ScorePart inputScorePart = inputObject.getParams(ScorePart.class);
        ScoreTypeChild scoreTypeChild = scoreTypeChildService.queryById(inputScorePart.getObjectId());
        if (ObjectUtil.isEmpty(scoreTypeChild)) {
            throw new CustomException("objectId错误，关联的成绩类型不存在。");
        }
        SubjectClasses subjectClasses = subjectClassesService.getSubjectClassesByObjectIdAndClassesId(scoreTypeChild.getSubjectId(), scoreTypeChild.getClassId());
        if (ObjectUtil.isNotEmpty(subjectClasses)) {
            List<ScorePart> scorePartList = new ArrayList<>();
            List<SubjectClassesStu> subjectClassesStuList = subjectClassesStuService.queryListBySubClassLinkId(subjectClasses.getId());
            for (SubjectClassesStu subjectClassesStu : subjectClassesStuList) {
                ScorePart scorePart = new ScorePart();
                scorePart.setWorkId(inputScorePart.getWorkId());
                scorePart.setScore(CommonNumConstants.NUM_ZERO.toString());
                scorePart.setProportion(scoreTypeChild.getProportion());
                scorePart.setStuNo(subjectClassesStu.getStuNo());
                scorePart.setObjectId(scoreTypeChild.getId());
                scorePartList.add(scorePart);
            }
            scorePartService.createEntity(scorePartList, currentUserId);
        }
    }
}
