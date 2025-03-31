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
import com.skyeye.school.assignment.entity.Assignment;
import com.skyeye.school.assignment.service.AssignmentService;
import com.skyeye.school.score.classenum.NumberCodeEnum;
import com.skyeye.school.score.dao.ScoreTypeChildDao;
import com.skyeye.school.score.entity.ScorePart;
import com.skyeye.school.score.entity.ScoreSum;
import com.skyeye.school.score.entity.ScoreType;
import com.skyeye.school.score.entity.ScoreTypeChild;
import com.skyeye.school.score.service.*;
import com.skyeye.school.student.entity.Student;
import com.skyeye.school.student.service.StudentService;
import com.skyeye.school.subject.entity.SubjectClasses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
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
    private ScorePartService scorePartService;

    @Autowired
    private ScoreSumService scoreSumService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private ScoreTypeService scoreTypeService;

    @Autowired
    private ScoreMaxMinService scoreMaxMinService;

    @Autowired
    private AssignmentService assignmentService;

    @Override
    public void validatorEntity(ScoreTypeChild scoreTypeChild) {
        // 新增/编辑不操作占比和parentId，通过另外的接口修改
        scoreTypeChild.setProportion(CommonNumConstants.NUM_ZERO.toString());
        scoreTypeChild.setNumberCode(ObjectUtil.isEmpty(scoreTypeChild.getNumberCode()) ? NumberCodeEnum.CUSTOM.getKey() : scoreTypeChild.getNumberCode());
        scoreTypeChild.setIsDefault(ObjectUtil.isEmpty(scoreTypeChild.getIsDefault()) ? IsDefaultEnum.NOT_DEFAULT.getKey() : scoreTypeChild.getIsDefault());
    }

    @Override
    public List<ScoreTypeChild> queryListByParentIdList(List<String> list) {
        if (CollectionUtil.isEmpty(list)) {
            return Collections.emptyList();
        }
        QueryWrapper<ScoreTypeChild> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(ScoreTypeChild::getParentId), list);
        return list(queryWrapper);
    }

    @Override
    public ScoreTypeChild queryByTypeId(String typeId) {
        QueryWrapper<ScoreTypeChild> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ScoreTypeChild::getScoreTypeId), typeId);
        return getOne(queryWrapper);
    }

    @Override
    public ScoreTypeChild selectById(String id) {
        ScoreTypeChild bean = super.selectById(id);
        List<ScoreSum> scoreSumList = scoreSumService.queryByObjectIdList(Arrays.asList(bean.getId()));
        List<String> stuNoList = scoreSumList.stream().map(ScoreSum::getStuNo).collect(Collectors.toList());
        List<Student> studentList = studentService.queryListByStuNoList(stuNoList);
        Map<String, Map<String, Object>> stuNoStudentMap = studentList.stream()
            .collect(Collectors.toMap(Student::getNo, sco -> JSONUtil.toBean(JSONUtil.toJsonStr(sco), null)));
        List<ScorePart> scorePartList = scorePartService.queryByObjectIdList(Arrays.asList(bean.getId()), null);
        // 查询作业成绩
        if (Objects.equals(bean.getNumberCode(), NumberCodeEnum.WORK.getKey())) {
            List<String> workIdList = scorePartList.stream().map(ScorePart::getWorkId).collect(Collectors.toList());
            List<Assignment> assignments = assignmentService.selectByIds(workIdList.toArray(new String[]{}));
            Map<String, Map<String, Object>> assignmentMap = assignments.stream().collect(Collectors.toMap(Assignment::getId, assignment -> JSONUtil.toBean(JSONUtil.toJsonStr(assignment), null)));
            for (ScorePart scorePart : scorePartList) {
                scorePart.setWorkMation(assignmentMap.get(scorePart.getWorkId()));
            }
        }
        // 将成绩列表根据创建时间排序饭后根据学号分组
        Map<String, List<ScorePart>> stuPartListMap = scorePartList.stream()
            .sorted(Comparator.comparing(ScorePart::getCreateTime))
            .collect(Collectors.groupingBy(ScorePart::getStuNo));
        for (ScoreSum scoreSum : scoreSumList) {
            if (stuPartListMap.containsKey(scoreSum.getStuNo())) {
                scoreSum.setScorePartList(stuPartListMap.get(scoreSum.getStuNo()));
            }
            if (stuNoStudentMap.containsKey(scoreSum.getStuNo())) {
                scoreSum.setStuMation(stuNoStudentMap.get(scoreSum.getStuNo()));
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
        List<Integer> numberCodeList = Arrays.asList(NumberCodeEnum.WORK.getKey(), NumberCodeEnum.TEST.getKey()
            , NumberCodeEnum.INTERACTION.getKey(), NumberCodeEnum.USUAL.getKey());
        Map<String, Integer> map = new HashMap<>();
        for (int i = 0; i < nameList.size(); i++) {
            map.put(nameList.get(i), numberCodeList.get(i));
        }
        List<ScoreTypeChild> scoreTypeChildList = new ArrayList<>();
        map.forEach((name, numberCode) -> {
            ScoreTypeChild scoreTypeChild = new ScoreTypeChild();
            scoreTypeChild.setIsDefault(IsDefaultEnum.IS_DEFAULT.getKey());
            scoreTypeChild.setName(name);
            scoreTypeChild.setProportion(CommonNumConstants.NUM_ZERO.toString());
            scoreTypeChild.setSubjectId(subjectClasses.getObjectId());
            scoreTypeChild.setClassId(subjectClasses.getClassesId());
            scoreTypeChild.setNumberCode(numberCode);
            scoreTypeChildList.add(scoreTypeChild);
        });
        super.createEntity(scoreTypeChildList, subjectClasses.getCreateId());
    }

    @Override
    public void createPostpose(ScoreTypeChild entity, String userId) {
        if (StrUtil.isNotEmpty(entity.getName())) {
            ScoreType scoreType = scoreTypeService.queryDefaultInfo(entity.getSubjectId(), entity.getClassId());
            List<ScoreSum> scoreSums = scoreSumService.queryByObjectIdList(Arrays.asList(scoreType.getId()));
            List<ScoreSum> scoreSumList = new ArrayList<>();
            if (scoreSums.size() == CommonNumConstants.NUM_ZERO) {// 课程下班级没人
                ScoreSum scoreSum = new ScoreSum();
                scoreSum.setScore(CommonNumConstants.NUM_ZERO.toString());
                scoreSum.setProportion(entity.getProportion());
                scoreSum.setObjectId(entity.getId());
                scoreSum.setStuNo(StrUtil.EMPTY);
                scoreSumList.add(scoreSum);
            } else {// 有人
                for (ScoreSum scoreSum : scoreSums) {
                    ScoreSum newScoreSum = new ScoreSum();
                    newScoreSum.setScore(CommonNumConstants.NUM_ZERO.toString());
                    newScoreSum.setProportion(entity.getProportion());
                    newScoreSum.setObjectId(entity.getId());
                    newScoreSum.setStuNo(scoreSum.getStuNo());
                    scoreSumList.add(newScoreSum);
                }
            }
            scoreSumService.createEntity(scoreSumList, userId);
        }
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void boundDataOrNot(InputObject inputObject, OutputObject outputObject) {
        String currentUserId = inputObject.getLogParams().get("id").toString();
        Map<String, Object> params = inputObject.getParams();
        String parentId = params.get("parentId").toString();
        String id = params.get("id").toString();
        if (StrUtil.isEmpty(parentId)) {// 父级为空，则取消绑定
            UpdateWrapper<ScoreTypeChild> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq(CommonConstants.ID, id)
                .set(MybatisPlusUtil.toColumns(ScoreTypeChild::getParentId), StrUtil.EMPTY)
                .set(MybatisPlusUtil.toColumns(ScoreTypeChild::getProportion), CommonNumConstants.NUM_ZERO.toString());
            ScoreTypeChild one = getOne(updateWrapper);
            // 更新成绩
            if (Double.parseDouble(one.getProportion()) > CommonNumConstants.NUM_ZERO) {// 占比大于0，则更新该学生的”平时成绩“
                ScoreType scoreType = scoreTypeService.queryDefaultInfo(one.getSubjectId(), one.getClassId());
                List<ScoreSum> scoreList = scoreSumService.queryByObjectIdList(Arrays.asList(scoreType.getId()));
                if (CollectionUtil.isEmpty(scoreList) || (StrUtil.isEmpty(scoreList.get(CommonNumConstants.NUM_ZERO).getStuNo()))) {// 课程下该班级没有学生
                    update(updateWrapper);
                    return;
                }
                List<ScorePart> updateScorePartList = new ArrayList<>();
                List<ScoreSum> updateScoreSumList = new ArrayList<>();
                List<ScoreSum> scoreSums = scoreSumService.queryByObjectIdList(Arrays.asList(one.getId()));
                Map<String, String> map = new HashMap<>();
                for (ScoreSum scoreSum : scoreSums) {
                    String subtractNum = CalculationUtil.multiply(scoreSum.getScore(), CalculationUtil.divide(scoreSum.getProportion(), "100"), CommonNumConstants.NUM_TWO);
                    map.put(scoreSum.getStuNo(), subtractNum);
                }
                List<ScorePart> scoreParts = scorePartService.queryByObjectIdList(Arrays.asList(one.getParentId()), null);
                for (ScorePart scorePart : scoreParts) {
                    String newScore = CalculationUtil.subtract(scorePart.getScore(), map.get(scorePart.getStuNo()), CommonNumConstants.NUM_TWO);
                    scorePart.setScore(newScore);
                    updateScorePartList.add(scorePart);
                }
                List<ScoreSum> scoreTypeSums = scoreSumService.queryByObjectIdList(Arrays.asList(scoreType.getId()));
                for (ScoreSum scoreSum : scoreTypeSums) {
                    String newScore = CalculationUtil.subtract(map.get(scoreSum.getStuNo()), CalculationUtil.divide(scoreSum.getProportion(), "100"), CommonNumConstants.NUM_TWO);
                    scoreSum.setScore(newScore);
                    updateScoreSumList.add(scoreSum);
                }
                List<ScoreSum> sortByScoreList = scoreTypeSums.stream().sorted(Comparator.comparing(ScoreSum::getScore)).collect(Collectors.toList());
                scoreMaxMinService.updateScoreById(sortByScoreList.get(CommonNumConstants.NUM_ZERO).getObjectId(),
                    sortByScoreList.get(sortByScoreList.size()).getScore(), sortByScoreList.get(CommonNumConstants.NUM_ZERO).getScore(), currentUserId);
                scoreSumService.updateEntity(updateScoreSumList, currentUserId);
                scorePartService.updateEntity(updateScorePartList, currentUserId);
                update(updateWrapper);
            }
            update(updateWrapper);
            return;
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
        Map<String, Object> params = inputObject.getParams();
        String id = params.get("id").toString();
        String proportion = params.get("proportion").toString();
        ScoreTypeChild scoreTypeChild = queryById(id);
        if (ObjectUtil.isEmpty(scoreTypeChild)) {
            throw new CustomException("成绩类型子表信息不存在");
        }
        UpdateWrapper<ScoreTypeChild> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id)
            .set(MybatisPlusUtil.toColumns(ScoreTypeChild::getProportion), proportion);
        if (StrUtil.isNotEmpty(scoreTypeChild.getParentId())) { // 绑定了scoreType表
            ScoreType scoreType = scoreTypeService.queryDefaultInfo(scoreTypeChild.getSubjectId(), scoreTypeChild.getClassId());
            List<ScoreSum> scoreList = scoreSumService.queryByObjectIdList(Arrays.asList(scoreType.getId()));
            if (CollectionUtil.isEmpty(scoreList) || (StrUtil.isEmpty(scoreList.get(CommonNumConstants.NUM_ZERO).getStuNo()))) {// 课程下该班级没有学生
                update(updateWrapper);
                return;
            }
            List<ScorePart> updateScorePartList = new ArrayList<>();
            List<ScoreSum> updateScoreSumList = new ArrayList<>();
            ScoreTypeChild flagScoreChild = queryByTypeId(scoreTypeChild.getParentId());
            List<ScoreTypeChild> flagScoreTypeChildList = queryListByParentIdList(Arrays.asList(flagScoreChild.getParentId()));
            // 取出期末成绩、期中成绩等信息的主键id
            List<String> flagScoreTypeTypeIdList = flagScoreTypeChildList.stream().map(ScoreTypeChild::getScoreTypeId).collect(Collectors.toList());
            // 取出作业成绩、测试成绩的成绩的信息
            List<ScoreTypeChild> scoreTypeChildList = queryListByParentIdList(flagScoreTypeTypeIdList);
            // 取出作业成绩、测试成绩的主键id
            List<String> scoreTypeChildIdList = scoreTypeChildList.stream().map(ScoreTypeChild::getId).collect(Collectors.toList());
            // 更新被修改占比的所有学生的作业成绩总分
            List<ScoreSum> scoreChildSums = scoreSumService.queryByObjectIdList(scoreTypeChildIdList);
            for (ScoreSum scoreChildSum : scoreChildSums) {// 修改占比
                if (scoreChildSum.getObjectId().equals(scoreTypeChild.getId())) {
                    scoreChildSum.setProportion(proportion);
                    updateScoreSumList.add(scoreChildSum);// 放进更新列表中
                }
            }
            // 计算作业成绩绑定的平时成绩
            List<String> idListByParentId = scoreTypeChildList.stream().filter(scoreTypeChild1 -> scoreTypeChild1.getParentId().equals(scoreTypeChild.getParentId()))
                .map(ScoreTypeChild::getId).collect(Collectors.toList());
            List<ScoreSum> collect = scoreChildSums.stream().filter(scoreSum -> idListByParentId.contains(scoreSum.getObjectId())).collect(Collectors.toList());
            Map<String, List<ScoreSum>> map = collect.stream().collect(Collectors.groupingBy(ScoreSum::getStuNo));
            Map<String, String> mapStuNoScore = scoreSumService.getStuNoScoreSumMap(map);
            List<ScorePart> flagScoreParts = scorePartService.queryByObjectIdList(flagScoreTypeTypeIdList, null);
            for (ScorePart flagScorePart : flagScoreParts) {
                if (flagScorePart.getObjectId().equals(scoreTypeChild.getParentId())) {// 是平时成绩，修改分数
                    flagScorePart.setScore(mapStuNoScore.get(flagScorePart.getStuNo()));
                    updateScorePartList.add(flagScorePart);
                }
            }
            Map<String, List<ScorePart>> collect1 = flagScoreParts.stream().collect(Collectors.groupingBy(ScorePart::getStuNo));
            Map<String, String> mapStuNoScore1 = scorePartService.getStuNoScorePartMap(collect1);
            List<ScoreSum> scoreSums = scoreSumService.queryByObjectIdList(Arrays.asList(flagScoreChild.getParentId()));
            for (ScoreSum scoreSum : scoreSums) {
                scoreSum.setScore(mapStuNoScore1.get(scoreSum.getStuNo()));
                updateScoreSumList.add(scoreSum);
            }
            List<ScoreSum> sortByScoreList = scoreSums.stream().sorted(Comparator.comparing(ScoreSum::getScore)).collect(Collectors.toList());
            scoreMaxMinService.updateScoreById(sortByScoreList.get(CommonNumConstants.NUM_ZERO).getObjectId(),
                sortByScoreList.get(sortByScoreList.size()).getScore(), sortByScoreList.get(CommonNumConstants.NUM_ZERO).getScore(), currentUserId);
            scorePartService.updateEntity(updateScorePartList, currentUserId);
            scoreSumService.updateEntity(updateScoreSumList, currentUserId);
        }
        update(updateWrapper);
        outputObject.setBean(scoreTypeChild);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
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

    @Override
    public ScoreTypeChild selectBySubjectIdClassIdAndNumberCode(String subjectId, String classesId, Integer numberCode) {
        QueryWrapper<ScoreTypeChild> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ScoreTypeChild::getSubjectId), subjectId)
            .eq(MybatisPlusUtil.toColumns(ScoreTypeChild::getClassId), classesId)
            .eq(MybatisPlusUtil.toColumns(ScoreTypeChild::getNumberCode), numberCode);
        return getOne(queryWrapper);
    }
}
