package com.skyeye.school.score.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
import com.skyeye.school.score.dao.ScoreTypeDao;
import com.skyeye.school.score.entity.*;
import com.skyeye.school.score.service.*;
import com.skyeye.school.subject.entity.SubjectClasses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@SkyeyeService(name = "成绩类型管理", groupName = "成绩类型管理")
public class ScoreTypeServiceImpl extends SkyeyeBusinessServiceImpl<ScoreTypeDao, ScoreType> implements ScoreTypeService {

    @Autowired
    private ScoreTypeChildService scoreTypeChildService;

    @Autowired
    private ScorePartService scorePartService;

    @Autowired
    private ScoreSumService scoreSumService;

    @Autowired
    private ScoreMaxMinService scoreMaxMinService;

    @Override
    public void createPrepose(ScoreType scoreType) {
        scoreType.setIsDefault(ObjectUtil.isEmpty(scoreType.getIsDefault()) ? IsDefaultEnum.NOT_DEFAULT.getKey() : scoreType.getIsDefault());
    }

    @Override
    public void createPostpose(ScoreType scoreType, String userId) {
        if (Objects.equals(scoreType.getIsDefault(), IsDefaultEnum.NOT_DEFAULT.getKey())) {// 新增非默认数据
            ScoreType parent = queryDefaultInfo(scoreType.getSubjectId(), scoreType.getClassId());
            ScoreTypeChild scoreTypeChild = new ScoreTypeChild();
            scoreTypeChild.setSubjectId(scoreType.getSubjectId());
            scoreTypeChild.setClassId(scoreType.getClassId());
            scoreTypeChild.setIsDefault(IsDefaultEnum.NOT_DEFAULT.getKey());
            scoreTypeChild.setProportion(CommonNumConstants.NUM_ZERO.toString());
            scoreTypeChild.setParentId(parent.getId());
            scoreTypeChild.setScoreTypeId(scoreType.getId());
            scoreTypeChildService.createEntity(scoreTypeChild, userId);
            // 检查课程下的班级是否有人
            List<ScoreSum> scoreSums = scoreSumService.queryByObjectIdList(Arrays.asList(parent.getId()));
            if (StrUtil.isEmpty(scoreSums.get(CommonNumConstants.NUM_ZERO).getStuNo())) {// 没人
                ScorePart scorePart = new ScorePart();
                scorePart.setWorkId(scoreType.getName());
                scorePart.setScore(CommonNumConstants.NUM_ZERO.toString());
                scorePart.setProportion(scoreType.getProportion());
                scorePart.setStuNo(StrUtil.EMPTY);
                scorePart.setObjectId(scoreType.getId());
                scorePartService.createEntity(scorePart, userId);
            } else {// 有人
                List<ScorePart> createScorePartList = new ArrayList<>();
                for (ScoreSum scoreSum : scoreSums) {// 课程下班级的所有人
                    ScorePart scorePart = new ScorePart();
                    scorePart.setWorkId(scoreType.getName());
                    scorePart.setScore(CommonNumConstants.NUM_ZERO.toString());
                    scorePart.setProportion(scoreType.getProportion());
                    scorePart.setStuNo(scoreSum.getStuNo());
                    scorePart.setObjectId(scoreType.getId());
                    createScorePartList.add(scorePart);
                }
                scorePartService.createEntity(createScorePartList, userId);
            }
        } else if (Objects.equals(scoreType.getIsDefault(), IsDefaultEnum.IS_DEFAULT.getKey())) {// 新增默认数据
            ScoreSum scoreSum = new ScoreSum();
            scoreSum.setScore(CommonNumConstants.NUM_ZERO.toString());
            scoreSum.setProportion("100");
            scoreSum.setObjectId(scoreType.getId());
            scoreSum.setStuNo(StrUtil.EMPTY);
            scoreSumService.createEntity(scoreSum, userId);
        }
    }

    @Override
    public void updatePostpose(ScoreType scoreType, String userId) {
        QueryWrapper<ScoreType> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(CommonConstants.ID, scoreType.getId());
        ScoreType oldScoreType = getOne(queryWrapper);
        if (!oldScoreType.getProportion().equals(scoreType.getProportion())) {// 更改占比，更新成绩
            List<ScoreSum> scoreSums = scoreSumService.queryByObjectIdList(Arrays.asList(scoreType.getId()));
            if (StrUtil.isEmpty(scoreSums.get(CommonNumConstants.NUM_ZERO).getStuNo())) {// 没人
                return;
            }
            List<ScorePart> updateScorePartList = new ArrayList<>();
            List<ScoreSum> updateScoreSumList = new ArrayList<>();
            QueryWrapper<ScoreType> wrapper = new QueryWrapper<>();
            wrapper.eq(MybatisPlusUtil.toColumns(ScoreType::getClassId), scoreType.getClassId())
                .eq(MybatisPlusUtil.toColumns(ScoreType::getSubjectId), scoreType.getSubjectId());
            List<ScoreType> allScoreTypeList = list(wrapper);
            List<String> notDefaultScoreTypeIdList = allScoreTypeList.stream()
                .filter(scoreType1 -> Objects.equals(scoreType1.getIsDefault(), IsDefaultEnum.NOT_DEFAULT.getKey()))
                .map(ScoreType::getId).collect(Collectors.toList());
            List<ScorePart> scoreParts = scorePartService.queryByObjectIdList(notDefaultScoreTypeIdList, null);
            for (ScorePart scorePart : scoreParts) {
                if (scorePart.getObjectId().equals(scoreType.getId())) {
                    scorePart.setProportion(scoreType.getProportion());
                    updateScorePartList.add(scorePart);
                }
            }
            Map<String, List<ScorePart>> collect = scoreParts.stream().collect(Collectors.groupingBy(ScorePart::getStuNo));
            Map<String, String> map = new HashMap<>();
            collect.forEach((stuNo, scorePartList) -> {
                final double[] newSum = {CommonNumConstants.NUM_ZERO};
                for (ScorePart scorePart : scorePartList) {
                    String flagSum = CalculationUtil.multiply(scorePart.getScore(), CalculationUtil.divide(scorePart.getProportion(), "100"), CommonNumConstants.NUM_TWO);
                    newSum[CommonNumConstants.NUM_ZERO] = newSum[CommonNumConstants.NUM_ZERO] + Double.parseDouble(flagSum);
                }
                map.put(stuNo, String.valueOf(newSum[CommonNumConstants.NUM_ZERO]));
            });
            List<String> defaultScoreTypeIdList = allScoreTypeList.stream()
                .filter(scoreType1 -> Objects.equals(scoreType1.getIsDefault(), IsDefaultEnum.IS_DEFAULT.getKey()))
                .map(ScoreType::getId).collect(Collectors.toList());
            List<ScoreSum> scoreTypeSums = scoreSumService.queryByObjectIdList(defaultScoreTypeIdList);
            for (ScoreSum scoreTypeSum : scoreTypeSums) {
                String newScore = CalculationUtil.multiply(map.get(scoreTypeSum.getStuNo()), CalculationUtil.divide(scoreTypeSum.getProportion(), "100"), CommonNumConstants.NUM_TWO);
                scoreTypeSum.setScore(newScore);
                updateScoreSumList.add(scoreTypeSum);
            }
            scorePartService.updateEntity(updateScorePartList, userId);
            scoreSumService.updateEntity(updateScoreSumList, userId);
        }
    }

    @Override
    public void querySameTableDateList(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        // 查出总成绩，一个班级中的一个班级只有一个总成绩数据
        QueryWrapper<ScoreType> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ScoreType::getClassId), params.get("classId").toString())
            .eq(MybatisPlusUtil.toColumns(ScoreType::getSubjectId), params.get("subjectId").toString());
        List<ScoreType> list = list(queryWrapper);
        if (CollectionUtil.isEmpty(list)) {
            return;
        }
        Map<Integer, List<ScoreType>> mapScoreTypeList = list.stream().collect(Collectors.groupingBy(ScoreType::getIsDefault));
        ScoreType bean = mapScoreTypeList.get(IsDefaultEnum.IS_DEFAULT.getKey()).get(CommonNumConstants.NUM_ZERO);
        List<ScoreType> sameTableChildDateList = mapScoreTypeList.get(IsDefaultEnum.NOT_DEFAULT.getKey());
        if (CollectionUtil.isEmpty(sameTableChildDateList)) {
            return;
        }
        List<String> scoreTypeIdList = sameTableChildDateList.stream().map(ScoreType::getId).collect(Collectors.toList());
        List<ScorePart> scorePartList = scorePartService.queryByObjectIdList(scoreTypeIdList, null);
        Map<String, List<ScorePart>> mapByStuNo = scorePartList.stream().collect(Collectors.groupingBy(ScorePart::getStuNo));
        List<ScoreSum> scoreSumList = scoreSumService.queryByObjectIdList(Arrays.asList(bean.getId()));
        for (ScoreSum scoreSum : scoreSumList) {
            if (mapByStuNo.containsKey(scoreSum.getStuNo())) {
                scoreSum.setScorePartList(mapByStuNo.get(scoreSum.getStuNo()));
            }
        }
        bean.setSameTableChildDateList(sameTableChildDateList);
        if (StrUtil.isNotEmpty(bean.getMaxMinId())){
            ScoreMaxMin scoreMaxMin = scoreMaxMinService.selectById(bean.getMaxMinId());
            bean.setScoreMaxMin(scoreMaxMin);
        }
        bean.setScoreSumAndChildList(scoreSumList);
        outputObject.setBean(bean);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void queryDifferentTableDateList(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String classId = params.get("classId").toString();
        String subjectId = params.get("subjectId").toString();
        ScoreType bean = queryDefaultInfo(subjectId, classId);
        List<ScoreTypeChild> scoreTypeChildList = scoreTypeChildService.queryListBySubjectIdAndClassId(bean.getSubjectId(), bean.getClassId());
        List<ScoreTypeChild> deffrentTableDataList = scoreTypeChildList.stream()
            .filter(scoreTypeChild -> StrUtil.isEmpty(scoreTypeChild.getScoreTypeId())).collect(Collectors.toList());
        bean.setDifferentTableChildDateList(deffrentTableDataList);
        outputObject.setBean(bean);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }


    @Override
    public void deletePreExecution(ScoreType scoreType) {
        if (ObjectUtil.equals(scoreType.getIsDefault(), IsDefaultEnum.IS_DEFAULT.getKey())) {
            throw new RuntimeException("默认数据不可删除");
        }
    }

    @Override
    public void deletePostpose(String id) {
        // 删除成绩子表信息，并返回其parentId(该科目下的班级的总成绩的主键id)
        String parentId = scoreTypeChildService.deleteByTypeId(id);
        // 删除分成绩和总成绩
        scorePartService.deleteByObjectId(id);
        // 查询总成绩下的平时成绩，期末成绩等
        List<ScoreTypeChild> scoreTypeChildList = scoreTypeChildService.queryListByParentIdList(Arrays.asList(parentId));
        if (CollectionUtil.isNotEmpty(scoreTypeChildList)) {
            // 获取平时成绩，期末成绩的主键id
            List<String> scoreTypeIdList = scoreTypeChildList.stream().map(ScoreTypeChild::getScoreTypeId).collect(Collectors.toList());
            // 查询平时成绩，期末成绩下的分成绩
            List<ScorePart> scoreParts = scorePartService.queryByObjectIdList(scoreTypeIdList, null);
            // 根据学号分组
            Map<String, List<ScorePart>> mapScorePart = scoreParts.stream().collect(Collectors.groupingBy(ScorePart::getStuNo));
            // 重新计算每一个学生的总成绩，并更新总成绩
            mapScorePart.forEach((stuNo, ScorePartList) -> {
                final double[] sum = {CommonNumConstants.NUM_ZERO};
                for (ScorePart scorePart : ScorePartList) {
                    String flagSum = CalculationUtil.multiply(scorePart.getScore(), scorePart.getProportion(), CommonNumConstants.NUM_FOUR);
                    sum[CommonNumConstants.NUM_ZERO] = sum[CommonNumConstants.NUM_ZERO] + Double.parseDouble(flagSum);
                }
                // 更新总分
                scoreSumService.updateScoreByObjectIdAndStuNo(parentId, sum[CommonNumConstants.NUM_ZERO], stuNo);
            });
        }
    }

    @Override
    public void createDeFaultInfo(SubjectClasses subjectClasses, String userId) {
        ScoreType scoreType = new ScoreType();
        scoreType.setIsDefault(IsDefaultEnum.IS_DEFAULT.getKey());
        scoreType.setName("总成绩");
        scoreType.setSubjectId(subjectClasses.getObjectId());
        scoreType.setClassId(subjectClasses.getClassesId());
        scoreType.setProportion("100");
        super.createEntity(scoreType, userId);
        scoreTypeChildService.createDeFaultInfo(subjectClasses);
    }

    @Override
    public List<ScoreType> queryList(String subjectId, String classId) {
        QueryWrapper<ScoreType> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ScoreType::getSubjectId), subjectId)
            .eq(MybatisPlusUtil.toColumns(ScoreType::getClassId), classId);
        return list(queryWrapper);
    }

    @Override
    public ScoreType queryDefaultInfo(String subjectId, String classId) {
        QueryWrapper<ScoreType> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ScoreType::getSubjectId), subjectId)
            .eq(MybatisPlusUtil.toColumns(ScoreType::getClassId), classId)
            .eq(MybatisPlusUtil.toColumns(ScoreType::getIsDefault), IsDefaultEnum.IS_DEFAULT.getKey());
        return getOne(queryWrapper);
    }

    @Override
    public List<ScoreType> queryNotDefaultInfo(String subjectId, String classId) {
        QueryWrapper<ScoreType> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ScoreType::getSubjectId), subjectId)
            .eq(MybatisPlusUtil.toColumns(ScoreType::getClassId), classId)
            .eq(MybatisPlusUtil.toColumns(ScoreType::getIsDefault), IsDefaultEnum.NOT_DEFAULT.getKey());
        return list(queryWrapper);
    }

    @Override
    public void queryBySubjectIdAndClassesId(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String subjectId = params.get("subjectId").toString();
        String classesId = params.get("classesId").toString();
        QueryWrapper<ScoreType> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ScoreType::getSubjectId), subjectId);
        if (StrUtil.isEmpty(classesId)) {
            List<ScoreType> list = list(queryWrapper);
        } else {
            queryWrapper.eq(MybatisPlusUtil.toColumns(ScoreType::getClassId),classesId);
            List<ScoreType> list = list(queryWrapper);
            Map<Integer, List<ScoreType>> defaultMap = list.stream().collect(Collectors.groupingBy(ScoreType::getIsDefault));
            List<String> defaultIdList = defaultMap.get(IsDefaultEnum.IS_DEFAULT.getKey()).stream().map(ScoreType::getId).collect(Collectors.toList());
            List<ScoreSum> scoreSums = scoreSumService.queryByObjectIdList(defaultIdList);
            List<String> notDefaultIdList = defaultMap.get(IsDefaultEnum.NOT_DEFAULT.getKey()).stream().map(ScoreType::getId).collect(Collectors.toList());
            List<ScorePart> scoreParts = scorePartService.queryByObjectIdList(notDefaultIdList, null);
            Map<String, List<ScorePart>> stuNoPart = scoreParts.stream().collect(Collectors.groupingBy(ScorePart::getStuNo));
            for (ScoreSum scoreSum : scoreSums) {
                scoreSum.setScorePartList(stuNoPart.get(scoreSum.getStuNo()));
            }
//            for (ScoreType scoreType : list) {
//            }

        }
    }
}
