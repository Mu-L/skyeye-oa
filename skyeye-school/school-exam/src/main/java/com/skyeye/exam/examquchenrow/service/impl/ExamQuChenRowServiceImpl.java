package com.skyeye.exam.examquchenrow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exam.examquchenrow.dao.ExamQuChenRowDao;
import com.skyeye.exam.examquchenrow.entity.ExamQuChenRow;
import com.skyeye.exam.examquchenrow.service.ExamQuChenRowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@SkyeyeService(name = "矩陈题-行选项管理", groupName = "矩陈题-行选项管理")
public class ExamQuChenRowServiceImpl extends SkyeyeBusinessServiceImpl<ExamQuChenRowDao, ExamQuChenRow> implements ExamQuChenRowService {

    @Autowired
    private ExamQuChenRowService examQuChenRowService;

    @Override
    public void saveRowEntity(List<ExamQuChenRow> quRow, String userId) {
        createEntity(quRow, userId);
    }

    @Override
    public void updateRowEntity(List<ExamQuChenRow> editquRow, String userId) {
        updateEntity(editquRow, userId);
    }

    @Override
    public QueryWrapper<ExamQuChenRow> QueryExamQuChenRowList(String quId) {
        QueryWrapper<ExamQuChenRow> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamQuChenRow::getQuId), quId);
        return queryWrapper;
    }

    @Override
    public int QueryvisibilityInRow(String quId, String createId) {
        QueryWrapper<ExamQuChenRow> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamQuChenRow::getQuId), quId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamQuChenRow::getCreateId), createId);
        ExamQuChenRow one = examQuChenRowService.getOne(queryWrapper);
        Integer visibility = one.getVisibility();
        return visibility;
    }

    @Override
    public void changeVisibility(String quId, String createId) {
        UpdateWrapper<ExamQuChenRow> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(MybatisPlusUtil.toColumns(ExamQuChenRow::getQuId), quId);
        updateWrapper.eq(MybatisPlusUtil.toColumns(ExamQuChenRow::getCreateId), createId);
        updateWrapper.set(MybatisPlusUtil.toColumns(ExamQuChenRow::getVisibility), CommonNumConstants.NUM_ZERO);
        update(updateWrapper);
    }

    @Override
    public void removeByQuId(String quId) {
        UpdateWrapper<ExamQuChenRow> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(MybatisPlusUtil.toColumns(ExamQuChenRow::getQuId), quId);
        remove(updateWrapper);
    }

    @Override
    public List<ExamQuChenRow> selectQuChenRow(String copyFromId) {
        QueryWrapper<ExamQuChenRow> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamQuChenRow::getQuId), copyFromId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamQuChenRow::getVisibility), CommonNumConstants.NUM_ONE);
        return list(queryWrapper);
    }
}
