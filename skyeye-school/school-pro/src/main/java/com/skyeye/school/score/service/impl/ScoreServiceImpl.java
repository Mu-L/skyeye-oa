package com.skyeye.school.score.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.school.score.dao.ScoreDao;
import com.skyeye.school.score.entity.Score;
import com.skyeye.school.score.service.ScoreService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service
@SkyeyeService(name = "成绩管理", groupName = "成绩管理")
public class ScoreServiceImpl extends SkyeyeBusinessServiceImpl<ScoreDao, Score> implements ScoreService {

    @Override
    public void queryMyScoreListByNo(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String no = map.get("no").toString();
        QueryWrapper<Score> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("no", no);
        List<Score> list = list(queryWrapper);
        if (CollectionUtil.isEmpty(list)) {
            throw new CustomException("暂无次学号学生的成绩");
        }
        outputObject.setBeans(list);
        outputObject.settotal(list.size());
    }

    @Override
    public void queryScoreList(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String no = params.get("no").toString();
        String classId = params.get("classId").toString();
        String subjectId = params.get("subjectId").toString();
        String semesterId = params.get("semesterId").toString();
        String objectId = params.get("objectId").toString();
        String objectKey = params.get("objectKey").toString();
        QueryWrapper<Score> queryWrapper = new QueryWrapper<>();
        if (StrUtil.isNotEmpty(no)) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(Score::getNo), no);
        }
        if (StrUtil.isNotEmpty(classId)) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(Score::getClassId), classId);
        }
        if (StrUtil.isNotEmpty(subjectId)) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(Score::getSubjectId), subjectId);
        }
        if (StrUtil.isNotEmpty(semesterId)) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(Score::getSemesterId), semesterId);
        }
        if (StrUtil.isNotEmpty(objectId)) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(Score::getObjectId), objectId);
        }
        if (StrUtil.isNotEmpty(objectKey)) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(Score::getObjectKey), objectKey);
        }
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(Score::getCreateTime));
        List<Score> list = list(queryWrapper);
        outputObject.setBeans(list);
        outputObject.settotal(list.size());
    }
}
