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
import com.skyeye.common.enumeration.IsDefaultEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.school.score.dao.ScoreTypeChildDao;
import com.skyeye.school.score.entity.ScorePart;
import com.skyeye.school.score.entity.ScoreSum;
import com.skyeye.school.score.entity.ScoreTypeChild;
import com.skyeye.school.score.entity.ScoreTypeChildList;
import com.skyeye.school.score.service.ScorePartService;
import com.skyeye.school.score.service.ScoreSumService;
import com.skyeye.school.score.service.ScoreTypeChildService;
import com.skyeye.school.subject.entity.SubjectClasses;
import com.skyeye.school.subject.entity.SubjectClassesStu;
import com.skyeye.school.subject.service.SubjectClassesService;
import com.skyeye.school.subject.service.SubjectClassesStuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@SkyeyeService(name = "成绩类型子表管理", groupName = "成绩类型子表管理")
public class ScoreTypeChildServiceImpl extends SkyeyeBusinessServiceImpl<ScoreTypeChildDao, ScoreTypeChild> implements ScoreTypeChildService {

    @Autowired
    private ScorePartService scorePartService;

    @Autowired
    private ScoreSumService scoreSumService;

    @Autowired
    private ScoreTypeChildService scoreTypeChildServicec;

    @Autowired
    private SubjectClassesService subjectClassesService;

    @Autowired
    private SubjectClassesStuService subjectClassesStuService;

    @Override
    public void validatorEntity(ScoreTypeChild scoreTypeChild) {
        if (StrUtil.isNotEmpty(scoreTypeChild.getProportion())) {
            double proportion = Double.parseDouble(scoreTypeChild.getProportion());
            if (proportion < 0 || proportion > 100) {
                throw new CustomException("占比必须为0-100");
            }
        }
    }

    @Override
    public void createPostpose(ScoreTypeChild scoreTypeChild, String userId) {
        if (StrUtil.isEmpty(scoreTypeChild.getScoreTypeId())) {
            // 新增作业成绩、测试成绩等时，给总成表新增空白数据
            SubjectClasses subjectClasses = subjectClassesService.getSubjectClassesByObjectIdAndClassesId(scoreTypeChild.getSubjectId(), scoreTypeChild.getClassId());
            if (ObjectUtil.isNotEmpty(subjectClasses)) {
                List<ScoreSum> scorePartList = new ArrayList<>();
                List<SubjectClassesStu> subjectClassesStuList = subjectClassesStuService.queryListBySubClassLinkId(subjectClasses.getId());
                for (SubjectClassesStu subjectClassesStu : subjectClassesStuList) {
                    ScoreSum scoreSum = new ScoreSum();
                    scoreSum.setProportion(scoreTypeChild.getProportion());
                    scoreSum.setStuNo(subjectClassesStu.getStuNo());
                    scoreSum.setObjectId(scoreTypeChild.getId());
                    scorePartList.add(scoreSum);
                }
                scoreSumService.createEntity(scorePartList, userId);
            }
        }
    }

    @Override
    public void updatePostpose(ScoreTypeChild entity, String userId) {
        if (StrUtil.isNotEmpty(entity.getProportion())) {
            // 修改作业成绩、测试成绩等时(修改占比)，给总成表修改数据(占比)
            scoreSumService.updateProportionByObjectId(entity.getId(), entity.getProportion());
        }
    }

    @Override
    public List<ScoreTypeChild> queryListByParentIdList(List<String> list) {
        QueryWrapper<ScoreTypeChild> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(ScoreTypeChild::getParentId), list);
        return list(queryWrapper);
    }

    @Override
    public ScoreTypeChild queryByTypeId(String typeId) {
        QueryWrapper<ScoreTypeChild> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ScoreTypeChild::getId), typeId);
        return getOne(queryWrapper);
    }

    @Override
    public ScoreTypeChild selectById(String id) {
        ScoreTypeChild bean = super.selectById(id);
        List<ScoreSum> scoreSumList = scoreSumService.queryByObjectIdList(Arrays.asList(bean.getId()));
        List<ScorePart> scorePartList = scorePartService.queryByObjectIdList(Arrays.asList(bean.getId()));
        // 将成绩列表根据创建时间排序饭后根据学号分组
        Map<String, List<ScorePart>> stuPartListMap = scorePartList.stream()
            .sorted(Comparator.comparing(ScorePart::getCreateTime))
            .collect(Collectors.groupingBy(ScorePart::getStuNo));
        for (ScoreSum scoreSum : scoreSumList) {
            if (stuPartListMap.containsKey(scoreSum.getStuNo())) {
                scoreSum.setScorePartList(stuPartListMap.get(scoreSum.getStuNo()));
            }
        }
        bean.setScoreSumList(scoreSumList);
        return bean;
    }

    @Override
    public void deletePreExecution(ScoreTypeChild scoreTypeChild) {
        if (Objects.equals(scoreTypeChild.getIsDefault(), IsDefaultEnum.IS_DEFAULT.getKey())) {
            throw new CustomException("默认数据不可删除");
        }
    }

    @Override
    public void deletePostpose(String id) {
        // 删除成绩
        scorePartService.deleteByObjectId(id);
        scoreSumService.deleteByObjectId(id);
    }

    @Override
    public void createDeFaultInfo(SubjectClasses subjectClasses) {
        List<String> nameList = Arrays.asList("作业成绩", "测试成绩", "互动答题成绩", "平时成绩");
        List<ScoreTypeChild> scoreTypeChildList = new ArrayList<>();
        for (String s : nameList) {
            ScoreTypeChild scoreTypeChild = new ScoreTypeChild();
            scoreTypeChild.setIsDefault(IsDefaultEnum.IS_DEFAULT.getKey());
            scoreTypeChild.setName(s);
            scoreTypeChild.setProportion(CommonNumConstants.NUM_ZERO.toString());
            scoreTypeChild.setSubjectId(subjectClasses.getObjectId());
            scoreTypeChild.setClassId(subjectClasses.getClassesId());
            scoreTypeChildList.add(scoreTypeChild);
        }
        super.createEntity(scoreTypeChildList, subjectClasses.getCreateId());
    }

    @Override
    public void getQueryWrapper(InputObject inputObject, QueryWrapper<ScoreTypeChild> wrapper) {
        Map<String, Object> params = inputObject.getParams();
        String subjectId = params.get("subjectId").toString();
        String classId = params.get("classId").toString();
        wrapper.eq(MybatisPlusUtil.toColumns(ScoreTypeChild::getSubjectId), subjectId)
            .eq(MybatisPlusUtil.toColumns(ScoreTypeChild::getClassId), classId)
            .and(w -> {
                w.eq(MybatisPlusUtil.toColumns(ScoreTypeChild::getScoreTypeId), StrUtil.EMPTY)
                    .or().isNull(MybatisPlusUtil.toColumns(ScoreTypeChild::getScoreTypeId));
            });
    }

    @Override
    public void boundDataOrNot(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String parentId = params.get("parentId").toString();
        String id = params.get("id").toString();
        if (StrUtil.isEmpty(parentId)) {// 父级为空，则取消绑定
            UpdateWrapper<ScoreTypeChild> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq(CommonConstants.ID, id)
                .set(MybatisPlusUtil.toColumns(ScoreTypeChild::getParentId), StrUtil.EMPTY)
                .set(MybatisPlusUtil.toColumns(ScoreTypeChild::getProportion), CommonNumConstants.NUM_ZERO.toString());
            ScoreTypeChild one = getOne(updateWrapper);
            update(updateWrapper);
            // 更新成绩
            if (Double.parseDouble(one.getProportion()) > CommonNumConstants.NUM_ZERO) {// 占比大于0，则更新该学生的”平时成绩“
                // 统计绑定平时成绩的子成绩列表
                QueryWrapper<ScoreTypeChild> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq(MybatisPlusUtil.toColumns(ScoreTypeChild::getParentId), one.getParentId());
                List<ScoreTypeChild> list = list(queryWrapper);
                // 取出主键id
                List<String> chiuldIdlist = list.stream().map(ScoreTypeChild::getId).collect(Collectors.toList());
                // 求出实验成绩、期末成绩的总分
                List<ScoreSum> scorePartList = scoreSumService.queryByObjectIdList(chiuldIdlist);
                // 取出该科目下的班级的总成绩
                String sumScoreId = queryByTypeId(one.getParentId()).getParentId();
                // 根据学号分组
                Map<String, List<ScoreSum>> stunoScorePartListMap = scorePartList.stream().collect(Collectors.groupingBy(ScoreSum::getStuNo));
                stunoScorePartListMap.forEach((stuNo, scoreSumList) -> {
                    // 重新计算每一个学生的总成绩，并更新总成绩
                    final double[] sum = {CommonNumConstants.NUM_ZERO};
                    for (ScoreSum scoreSum : scoreSumList) {
                        String flagSum = CalculationUtil.multiply(scoreSum.getScore(), one.getProportion(), CommonNumConstants.NUM_FOUR);
                        sum[CommonNumConstants.NUM_ZERO] = sum[CommonNumConstants.NUM_ZERO] + Double.parseDouble(flagSum);
                    }
                    scoreSumService.updateScoreByObjectIdAndStuNo(sumScoreId, sum[CommonNumConstants.NUM_ZERO], stuNo);
                });
            }
        }
        // 绑定操作
        UpdateWrapper<ScoreTypeChild> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        ScoreTypeChild one = getOne(updateWrapper);
        if (ObjectUtil.isNotEmpty(one)) {
            if (StrUtil.isNotEmpty(one.getParentId())) {
                throw new CustomException("不可重复绑定");
            }
            updateWrapper.set(MybatisPlusUtil.toColumns(ScoreTypeChild::getParentId), parentId);
            update(updateWrapper);
        } else {
            throw new CustomException("成绩类型不存在");
        }
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void changeProportion(InputObject inputObject, OutputObject outputObject) {
        String currentUserId = inputObject.getLogParams().get("id").toString();
        ScoreTypeChildList params = inputObject.getParams(ScoreTypeChildList.class);
        List<ScoreTypeChild> scoreTypeChildList = params.getScoreTypeChildList();
        if (CollectionUtil.isEmpty(scoreTypeChildList)) {
            return;
        }
        List<String> proportionList = scoreTypeChildList.stream().map(ScoreTypeChild::getProportion).collect(Collectors.toList());
        // 使用stream流计算占比总和是否小于100大于0
        double sum = proportionList.stream().mapToDouble(Double::parseDouble).sum();
        if (sum <= CommonNumConstants.NUM_ZERO || sum >= 100) {
            throw new CustomException("占比总和需要大于0小于100");
        }
        // 修改占比
        super.updateEntity(scoreTypeChildList, inputObject.getLogParams().get("id").toString());
        Map<String, String> idProportionMap = scoreTypeChildList.stream().collect(Collectors.toMap(ScoreTypeChild::getId, ScoreTypeChild::getProportion));
        //重新计算平时成绩、期末成绩
        List<String> chiuldSumIdList = scoreTypeChildList.stream().map(ScoreTypeChild::getId).collect(Collectors.toList());
        List<ScoreSum> oldChildSumList = scoreSumService.queryByObjectIdList(chiuldSumIdList);
        for (ScoreSum scoreSum : oldChildSumList) {
            // 设置新的占比
            scoreSum.setProportion(idProportionMap.get(scoreSum.getObjectId()));
            // 计算新的成绩
            scoreSum.setScore(CalculationUtil.multiply(scoreSum.getScore(), scoreSum.getProportion(), CommonNumConstants.NUM_FOUR));
            scoreSumService.updateEntity(scoreSum, currentUserId);
        }
        // 取出同表父级数据
        ScoreTypeChild sameTableParen = scoreTypeChildServicec.queryByTypeId(scoreTypeChildList.get(CommonNumConstants.NUM_ZERO).getParentId());
        if (ObjectUtil.isNotEmpty(sameTableParen)) {// 绑定了平时成绩
            List<ScoreSum> newChildSumList = scoreSumService.queryByObjectIdList(chiuldSumIdList);
            Map<String, List<ScoreSum>> stuNoSumListMap = newChildSumList.stream().collect(Collectors.groupingBy(ScoreSum::getStuNo));
            stuNoSumListMap.forEach((stuNo, scoreSumList) -> {
                final double[] newSum = {CommonNumConstants.NUM_ZERO};
                for (ScoreSum scoreSum : scoreSumList) {
                    String flagSum = CalculationUtil.multiply(scoreSum.getScore(), scoreSum.getProportion(), CommonNumConstants.NUM_FOUR);
                    newSum[CommonNumConstants.NUM_ZERO] = newSum[CommonNumConstants.NUM_ZERO] + Double.parseDouble(flagSum);
                }
                // 更新平时成绩
                scorePartService.updateScoreByObjectIdAndStuNo(sameTableParen.getParentId(), newSum[CommonNumConstants.NUM_ZERO], stuNo);
            });
            // 开始操作总成绩
            List<ScoreTypeChild> sameTableDateList = scoreTypeChildServicec.queryListByParentIdList(Arrays.asList(sameTableParen.getParentId()));
            // 取出平时成绩、期末成绩的主键id
            List<String> sameTableScoreTypeIdList = sameTableDateList.stream().map(ScoreTypeChild::getScoreTypeId).collect(Collectors.toList());
            // 取出所有学生的成绩
            List<ScorePart> scoreParts = scorePartService.queryByObjectIdList(sameTableScoreTypeIdList);
            // 根据学号分组
            Map<String, List<ScorePart>> stuNoScorePartListMap = scoreParts.stream().collect(Collectors.groupingBy(ScorePart::getStuNo));
            // 循环分组重新计算所有学生的总成绩
            stuNoScorePartListMap.forEach((stuNo, scorePartList) -> {
                final double[] newSum = {CommonNumConstants.NUM_ZERO};
                for (ScorePart scorePart : scorePartList) {
                    String flagSum = CalculationUtil.multiply(scorePart.getScore(), scorePart.getProportion(), CommonNumConstants.NUM_FOUR);
                    newSum[CommonNumConstants.NUM_ZERO] = newSum[CommonNumConstants.NUM_ZERO] + Double.parseDouble(flagSum);
                }
                scoreSumService.updateScoreByObjectIdAndStuNo(sameTableParen.getParentId(), newSum[CommonNumConstants.NUM_ZERO], stuNo);
            });
        }
    }

    @Override
    public List<ScoreTypeChild> queryListBySubjectIdAndClassId(String subjectId, String classId) {
        QueryWrapper<ScoreTypeChild> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ScoreTypeChild::getSubjectId), subjectId)
            .eq(MybatisPlusUtil.toColumns(ScoreTypeChild::getClassId), classId);
        return list(queryWrapper);
    }

    @Override
    public String deleteByTypeId(String typeId) {
        QueryWrapper<ScoreTypeChild> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ScoreTypeChild::getScoreTypeId), typeId);
        ScoreTypeChild one = getOne(queryWrapper);
        remove(queryWrapper);
        // 解除绑定,将parentId置空以及将占比改为0
        UpdateWrapper<ScoreTypeChild> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set(MybatisPlusUtil.toColumns(ScoreTypeChild::getParentId), StrUtil.EMPTY)
            .set(MybatisPlusUtil.toColumns(ScoreTypeChild::getProportion), CommonNumConstants.NUM_ZERO.toString())
            .eq(MybatisPlusUtil.toColumns(ScoreTypeChild::getParentId), typeId);
        update(updateWrapper);
        return one.getParentId();
    }

    @Override
    public ScoreTypeChild queryById(String id) {
        QueryWrapper<ScoreTypeChild> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(CommonConstants.ID, id);
        return getOne(queryWrapper);
    }
}
