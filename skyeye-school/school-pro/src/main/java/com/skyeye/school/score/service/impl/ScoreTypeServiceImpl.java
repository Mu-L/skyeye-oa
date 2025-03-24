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
import com.skyeye.school.score.service.ScorePartService;
import com.skyeye.school.score.service.ScoreSumService;
import com.skyeye.school.score.service.ScoreTypeChildService;
import com.skyeye.school.score.service.ScoreTypeService;
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
    private ScoreTypeService scoreTypeService;

    @Autowired
    private ScorePartService scorePartService;

    @Autowired
    private ScoreSumService scoreSumService;

//    @Override
//    public void validatorEntity(ScoreType scoreType) {
//        if (StrUtil.isNotEmpty(scoreType.getProportion())) {
//            double proportion = Double.parseDouble(scoreType.getProportion());
//            if (proportion < 0 || proportion > 100) {
//                throw new CustomException("占比必须为1-100");
//            }
//            QueryWrapper<ScoreType> queryWrapper = new QueryWrapper<>();
//            queryWrapper.eq(MybatisPlusUtil.toColumns(ScoreType::getClassId), scoreType.getClassId())
//                .eq(MybatisPlusUtil.toColumns(ScoreType::getSubjectId), scoreType.getSubjectId())
//                .eq(MybatisPlusUtil.toColumns(ScoreType::getIsDefault), IsDefaultEnum.NOT_DEFAULT.getKey());
//            List<ScoreType> scoreTypeList = list(queryWrapper);
//            if (CollectionUtil.isNotEmpty(scoreTypeList)) {
//                double sumProportion = scoreTypeList.stream().map(ScoreType::getProportion).mapToDouble(Double::parseDouble).sum();
//                if (sumProportion + proportion > 100 || proportion + sumProportion < 0) {
//                    throw new CustomException("占比总和非法");
//                }
//            }
//        }
//    }

    @Override
    public void createPrepose(ScoreType scoreType) {
        scoreType.setIsDefault(ObjectUtil.isEmpty(scoreType.getIsDefault()) ? IsDefaultEnum.NOT_DEFAULT.getKey() : scoreType.getIsDefault());
    }

    @Override
    public void createPostpose(ScoreType scoreType, String userId) {
        if (Objects.equals(scoreType.getIsDefault(), IsDefaultEnum.NOT_DEFAULT.getKey())) {// 新增非默认数据
            QueryWrapper<ScoreType> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq(MybatisPlusUtil.toColumns(ScoreType::getClassId), scoreType.getClassId())
                .eq(MybatisPlusUtil.toColumns(ScoreType::getSubjectId), scoreType.getSubjectId())
                .eq(MybatisPlusUtil.toColumns(ScoreType::getIsDefault), IsDefaultEnum.IS_DEFAULT.getKey());
            ScoreType parent = getOne(queryWrapper);
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
                for (ScoreSum scoreSum : scoreSums) {// 课程下班级的所有人
                    ScorePart scorePart = new ScorePart();
                    scorePart.setWorkId(scoreType.getName());
                    scorePart.setScore(CommonNumConstants.NUM_ZERO.toString());
                    scorePart.setProportion(scoreType.getProportion());
                    scorePart.setStuNo(scoreSum.getStuNo());
                    scorePart.setObjectId(scoreType.getId());
                    scorePartService.createEntity(scorePart, userId);
                }
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
        ScoreType oldScoreType = selectById(scoreType.getId());
        if (!oldScoreType.getProportion().equals(scoreType.getProportion())) {
            ScoreTypeChild scoreTypeChild = scoreTypeChildService.queryByTypeId(scoreType.getId());
            scoreTypeChild.setProportion(scoreType.getProportion());
            scoreTypeChildService.updateEntity(scoreTypeChild, userId);
            List<ScorePart> scoreParts = scorePartService.queryByObjectIdList(Arrays.asList(scoreType.getId()), null);
            scoreParts.forEach(scorePart -> {
                scorePart.setScore(CalculationUtil.multiply(scorePart.getScore(), CalculationUtil.divide(scoreType.getProportion(), "100"), CommonNumConstants.NUM_THREE));
            });
            scorePartService.updateEntity(scoreParts, userId);

            QueryWrapper<ScoreType> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq(MybatisPlusUtil.toColumns(ScoreType::getClassId), scoreType.getClassId())
                .eq(MybatisPlusUtil.toColumns(ScoreType::getSubjectId), scoreType.getSubjectId())
                .eq(MybatisPlusUtil.toColumns(ScoreType::getIsDefault), IsDefaultEnum.NOT_DEFAULT.getKey())
                .select(CommonConstants.ID);
            List<ScoreType> list = list(queryWrapper);
            if (CollectionUtil.isEmpty(list)) {
                return;
            }
            List<String> idList = list.stream().map(ScoreType::getId).collect(Collectors.toList());
            List<ScorePart> scoreParts1 = scorePartService.queryByObjectIdList(idList, null);
            Map<String, List<ScorePart>> mapList = scoreParts1.stream().collect(Collectors.groupingBy(ScorePart::getStuNo));
            ScoreType zong = queryDefaultInfo(scoreType.getClassId(), scoreType.getSubjectId());
            mapList.forEach((stuNo, ScorePartList) -> {
                final double[] sum = {CommonNumConstants.NUM_ZERO};
                for (ScorePart scorePart : ScorePartList) {
                    String flagSum = CalculationUtil.multiply(scorePart.getScore(), scorePart.getProportion(), CommonNumConstants.NUM_FOUR);
                    sum[CommonNumConstants.NUM_ZERO] = sum[CommonNumConstants.NUM_ZERO] + Double.parseDouble(flagSum);
                }
                // 更新总分
                scoreSumService.updateScoreByObjectIdAndStuNo(zong.getId(), sum[CommonNumConstants.NUM_ZERO], stuNo);
            });
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
        List<ScoreSum> scoreSumList = scoreSumService.queryByObjectIdList(scoreTypeIdList);
        for (ScoreSum scoreSum : scoreSumList) {
            if (mapByStuNo.containsKey(scoreSum.getStuNo())) {
                scoreSum.setScorePartList(mapByStuNo.get(scoreSum.getStuNo()));
            }
        }
        bean.setSameTableChildDateList(JSONUtil.toList(JSONUtil.toJsonStr(sameTableChildDateList), null));
        outputObject.setBean(bean);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void queryDifferentTableDateList(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        QueryWrapper<ScoreType> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ScoreType::getClassId), params.get("classId").toString())
            .eq(MybatisPlusUtil.toColumns(ScoreType::getIsDefault), IsDefaultEnum.IS_DEFAULT.getKey())
            .eq(MybatisPlusUtil.toColumns(ScoreType::getSubjectId), params.get("subjectId").toString());
        ScoreType bean = getOne(queryWrapper);
        List<ScoreTypeChild> scoreTypeChildList = scoreTypeChildService.queryListBySubjectIdAndClassId(bean.getSubjectId(), bean.getClassId());
        List<ScoreTypeChild> deffrentTableDataList = scoreTypeChildList.stream()
            .filter(scoreTypeChild -> StrUtil.isNotEmpty(scoreTypeChild.getScoreTypeId())).collect(Collectors.toList());
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
    public void writeScoreTypeList(InputObject inputObject, OutputObject outputObject) {
        ScoreTypeList params = inputObject.getParams(ScoreTypeList.class);
        List<ScoreType> scoreTypeChildList = params.getScoreTypeList();
        scoreTypeChildList.forEach(scoreTypeChild -> {// 若执行新增操作，则将占比设置为0
            if (StrUtil.isEmpty(scoreTypeChild.getProportion())) {
                scoreTypeChild.setProportion("0");
            }
        });
        // 校验每一个占比都要合法
        boolean b = scoreTypeChildList.stream().map(ScoreType::getProportion)
            .anyMatch(s -> Double.parseDouble(s) > 100 || Double.parseDouble(s) < 0);
        if (b) {
            throw new CustomException("存在非法的占比");
        }
        // 验证占比总和
        double sumProportion = scoreTypeChildList.stream().map(ScoreType::getProportion).filter(StrUtil::isNotEmpty)
            .mapToDouble(Double::parseDouble).sum();
        if (sumProportion > 100 || sumProportion < 0) {
            throw new CustomException("占比总和非法");
        }

        List<ScoreType> updataScoreList = new ArrayList<>();
        List<ScoreType> addScoreTypeList = new ArrayList<>();
        scoreTypeChildList.forEach(scoreTypeChild -> {
            if (ObjectUtil.isNotEmpty(scoreTypeChild.getId())) {
                updataScoreList.add(scoreTypeChild);
            } else {
                addScoreTypeList.add(scoreTypeChild);
            }
        });
        String currentUserId = inputObject.getLogParams().get("id").toString();
        if (CollectionUtil.isNotEmpty(updataScoreList)) {
            scoreTypeService.updateEntity(updataScoreList, currentUserId);
        }
        if (CollectionUtil.isNotEmpty(addScoreTypeList)) {
            scoreTypeService.createEntity(addScoreTypeList, currentUserId);
        }
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
            .eq(MybatisPlusUtil.toColumns(ScoreType::getClassId), classId);
        return getOne(queryWrapper);
    }
}
