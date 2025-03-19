package com.skyeye.exam.examquchenrow.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exam.examquchencolumn.entity.ExamQuChenColumn;
import com.skyeye.exam.examquchenrow.dao.ExamQuChenRowDao;
import com.skyeye.exam.examquchenrow.entity.ExamQuChenRow;
import com.skyeye.exam.examquchenrow.service.ExamQuChenRowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(ExamQuChenRow::getOrderBy));
        return list(queryWrapper);
    }

    @Override
    public Map<String, List<Map<String, Object>>> selectByBelongId(String id) {
        if (StrUtil.isEmpty(id)) {
            return new HashMap<>();
        }
        QueryWrapper<ExamQuChenRow> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamQuChenRow::getBelongId),id);
        List<ExamQuChenRow> list = list(queryWrapper);
        Map<String, List<Map<String, Object>>> result = new HashMap<>();
        list.forEach(item->{
            String quId = item.getQuId();
            if(result.containsKey(quId)){
                result.get(quId).add(JSONUtil.toBean(JSONUtil.toJsonStr(item), null));
            }else {
                List<Map<String, Object>> tmp = new ArrayList<>();
                tmp.add(JSONUtil.toBean(JSONUtil.toJsonStr(item), null));
                result.put(quId,tmp);
            }
        });
        return result;
    }
}
