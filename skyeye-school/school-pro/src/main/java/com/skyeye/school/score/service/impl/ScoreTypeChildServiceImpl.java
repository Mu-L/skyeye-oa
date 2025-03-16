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
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

@Service
@SkyeyeService(name = "成绩类型子表管理", groupName = "成绩类型子表管理")
public class ScoreTypeChildServiceImpl extends SkyeyeBusinessServiceImpl<ScoreTypeChildDao, ScoreTypeChild> implements ScoreTypeChildService {

    @Autowired
    private ScorePartService scorePartService;

    @Autowired
    private ScoreSumService scoreSumService;

    @Override
    public void validatorEntity(ScoreTypeChild scoreTypeChild) {
        if (StrUtil.isNotEmpty(scoreTypeChild.getProportion())) {
            float proportion = Float.parseFloat(scoreTypeChild.getProportion());
            if (proportion <= 0 || proportion > 100) {
                throw new CustomException("占比必须为1-100");
            }
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
        Map<String, List<ScorePart>> mapListPart = scorePartList.stream().collect(Collectors.groupingBy(ScorePart::getStuNo));
        for (ScoreSum scoreSum : scoreSumList) {
            if (mapListPart.containsKey(scoreSum.getStuNo())) {
                scoreSum.setScorePartList(mapListPart.get(scoreSum.getStuNo()));
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
    public void createDeFaultInfo(String userId) {
        List<String> nameList = Arrays.asList(
            "作业成绩", "测试成绩", "互动课件成绩",
            "资料成绩", "互动答题成绩", "话题成绩",
            "考勤成绩", "表现成绩");
        List<ScoreTypeChild> scoreTypeChildList = new ArrayList<>();
        for (String s : nameList) {
            ScoreTypeChild scoreTypeChild = new ScoreTypeChild();
            scoreTypeChild.setIsDefault(IsDefaultEnum.IS_DEFAULT.getKey());
            scoreTypeChild.setName(s);
            scoreTypeChild.setProportion("12.5");
            scoreTypeChildList.add(scoreTypeChild);
        }
        super.createEntity(scoreTypeChildList, userId);
    }

    @Override
    public void getQueryWrapper(InputObject inputObject, QueryWrapper<ScoreTypeChild> wrapper) {
        Map<String, Object> params = inputObject.getParams();
        String subjectId = params.get("subjectId").toString();
        String classId = params.get("classId").toString();
        wrapper.eq(MybatisPlusUtil.toColumns(ScoreTypeChild::getSubjectId), subjectId)
            .eq(MybatisPlusUtil.toColumns(ScoreTypeChild::getClassId), classId);
    }

    @Override
    public void boundData(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String parentId = params.get("parentId").toString();
        List<String> childIdList = Arrays.asList(params.get("childIdList").toString().split(","));

        List<ScoreTypeChild> scoreTypeChildren = queryListByParentIdList(Arrays.asList(parentId));
        scoreTypeChildren.forEach(scoreTypeChild -> {
            childIdList.remove(scoreTypeChild.getId());
        });
        // 存在未绑定的子成绩
        if (CollectionUtil.isNotEmpty(childIdList)) {
            QueryWrapper<ScoreTypeChild> queryWrapper = new QueryWrapper<>();
            queryWrapper.in(CommonConstants.ID, childIdList);
            List<ScoreTypeChild> list = list(queryWrapper);
            // 判断是否已经绑定
            boolean b = list.stream().anyMatch(scoreTypeChild -> {
                return StrUtil.isNotEmpty(scoreTypeChild.getParentId());
            });
            if (b) {
                throw new CustomException("不可重复绑定");
            }
            for (ScoreTypeChild scoreTypeChild : list) {
                scoreTypeChild.setParentId(parentId);
            }
            super.updateEntity(list, inputObject.getLogParams().get("id").toString());
        }
    }

    @Override
    public void changeProportion(InputObject inputObject, OutputObject outputObject) {
        ScoreTypeChildList params = inputObject.getParams(ScoreTypeChildList.class);
        List<ScoreTypeChild> scoreTypeChildList = params.getScoreTypeChildList();
        if (CollectionUtil.isEmpty(scoreTypeChildList)){
            return;
        }
        List<String> proportionList = scoreTypeChildList.stream().map(ScoreTypeChild::getProportion).collect(Collectors.toList());
        // 使用stream流计算占比总和是否小于100大于0
        double sum = proportionList.stream().mapToDouble(Double::parseDouble).sum();
        if (sum <= CommonNumConstants.NUM_ZERO || sum >= 100) {
            throw new CustomException("占比总和需要大于0小于100");
        }
        super.updateEntity(scoreTypeChildList, inputObject.getLogParams().get("id").toString());
    }

    @Override
    public List<ScoreTypeChild> queryListBySubjectIdAndClassId(String subjectId, String classId) {
        QueryWrapper<ScoreTypeChild> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ScoreTypeChild::getSubjectId), subjectId)
            .eq(MybatisPlusUtil.toColumns(ScoreTypeChild::getClassId), classId);
        return list(queryWrapper);
    }

    @Override
    public void deleteByTypeId(String typeId) {
        QueryWrapper<ScoreTypeChild> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ScoreTypeChild::getScoreTypeId), typeId);
        remove(queryWrapper);
        // 接触绑定(将parentId置空)
        updateParentId(typeId);
    }

    private void updateParentId(String parentId){
        UpdateWrapper<ScoreTypeChild> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set(MybatisPlusUtil.toColumns(ScoreTypeChild::getParentId), StrUtil.EMPTY)
            .eq(MybatisPlusUtil.toColumns(ScoreTypeChild::getParentId), parentId);
        update(updateWrapper);
    }
}
