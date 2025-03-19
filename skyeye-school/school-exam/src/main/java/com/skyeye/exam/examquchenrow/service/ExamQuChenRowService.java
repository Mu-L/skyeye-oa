package com.skyeye.exam.examquchenrow.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.exam.examquchenrow.entity.ExamQuChenRow;

import java.util.List;
import java.util.Map;

public interface ExamQuChenRowService extends SkyeyeBusinessService<ExamQuChenRow> {

    void saveRowEntity(List<ExamQuChenRow> quRow, String userId);

    void updateRowEntity(List<ExamQuChenRow> editquRow, String userId);

    QueryWrapper<ExamQuChenRow> QueryExamQuChenRowList(String quId);

    void changeVisibility(String quId, String createId);

    void removeByQuId(String quId);

    List<ExamQuChenRow> selectQuChenRow(String copyFromId);

    Map<String, List<Map<String, Object>>> selectByBelongId(String id);
}
