package com.skyeye.school.score.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.enumeration.IsDefaultEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.school.score.dao.ScoreTypeDao;
import com.skyeye.school.score.entity.ScorePart;
import com.skyeye.school.score.entity.ScoreSum;
import com.skyeye.school.score.entity.ScoreType;
import com.skyeye.school.score.entity.ScoreTypeChild;
import com.skyeye.school.score.service.ScorePartService;
import com.skyeye.school.score.service.ScoreSumService;
import com.skyeye.school.score.service.ScoreTypeChildService;
import com.skyeye.school.score.service.ScoreTypeService;
import com.skyeye.school.subject.entity.SubjectClasses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
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

    @Override
    public void validatorEntity(ScoreType scoreType) {
        if (StrUtil.isNotEmpty(scoreType.getProportion())) {
            float proportion = Float.parseFloat(scoreType.getProportion());
            if (proportion <= 0 || proportion > 100) {
                throw new CustomException("占比必须为1-100");
            }
            QueryWrapper<ScoreType> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq(MybatisPlusUtil.toColumns(ScoreType::getClassId), scoreType.getClassId())
                .eq(MybatisPlusUtil.toColumns(ScoreType::getSubjectId), scoreType.getSubjectId())
                .eq(MybatisPlusUtil.toColumns(ScoreType::getIsDefault), IsDefaultEnum.NOT_DEFAULT.getKey());
            List<ScoreType> scoreTypeList = list(queryWrapper);
            double sumProportion = scoreTypeList.stream().map(ScoreType::getProportion).mapToDouble(Double::parseDouble).sum();
            if (sumProportion + proportion > 100 || proportion + sumProportion < 0 ){
                throw new CustomException("占比总和非法");
            }
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
        List<String> scoreTypeIdList = sameTableChildDateList.stream().map(ScoreType::getId).collect(Collectors.toList());
        List<ScorePart> scorePartList = scorePartService.queryByObjectIdList(scoreTypeIdList);
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
        List<ScoreTypeChild> scoreTypeChildList = scoreTypeChildService.queryListBySubjectIdAndClassId(bean.getId(), bean.getClassId());
        bean.setDifferentTableChildDateList(scoreTypeChildList);
        outputObject.setBean(bean);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }


    @Override
    public void deletePreExecution(ScoreType scoreType){
        if (ObjectUtil.equals(scoreType.getIsDefault(), IsDefaultEnum.IS_DEFAULT.getKey())){
            throw new RuntimeException("默认数据不可删除");
        }
    }

    @Override
    public void deletePostpose(String id) {
        scoreTypeChildService.deleteByTypeId(id);
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
        scoreTypeChildService.createDeFaultInfo(userId);
    }
}
