package com.skyeye.exam.examquchencolumn.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exam.examquchckbox.entity.ExamQuCheckbox;
import com.skyeye.exam.examquchencolumn.dao.ExamQuChenColumnDao;
import com.skyeye.exam.examquchencolumn.entity.ExamQuChenColumn;
import com.skyeye.exam.examquchencolumn.service.ExamQuChenColumnService;
import com.skyeye.exam.examquchenrow.entity.ExamQuChenRow;
import com.skyeye.exam.examquchenrow.service.ExamQuChenRowService;
import com.skyeye.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@SkyeyeService(name = "矩陈题-列选项管理", groupName = "矩陈题-列选项管理")
public class ExamQuChenColumnServiceImpl extends SkyeyeBusinessServiceImpl<ExamQuChenColumnDao, ExamQuChenColumn> implements ExamQuChenColumnService {

    @Autowired
    private ExamQuChenRowService examQuChenRowService;

    @Override
    protected QueryWrapper<ExamQuChenColumn> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<ExamQuChenColumn> queryWrapper = super.getQueryWrapper(commonPageInfo);
        if (StrUtil.isNotEmpty(commonPageInfo.getHolderId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(ExamQuChenColumn::getQuId), commonPageInfo.getHolderId());
            examQuChenRowService.QueryExamQuChenRowList(commonPageInfo.getHolderId());
        }
        return queryWrapper;
    }

    @Override
    public void saveList(List<ExamQuChenColumn> column, List<ExamQuChenRow> row, String quId, String userId) {
        List<ExamQuChenColumn> quColumn = new ArrayList<>();
        List<ExamQuChenColumn> editquColumn = new ArrayList<>();
        for (int i = 0; i < column.size(); i++) {
            ExamQuChenColumn object = column.get(i);
            ExamQuChenColumn bean = new ExamQuChenColumn();
            bean.setOrderById(object.getOrderById());
            bean.setOptionName(object.getOptionName());
            if (ToolUtil.isBlank(object.getOptionId())) {
                bean.setQuId(quId);
                bean.setVisibility(1);
                bean.setId(ToolUtil.getSurFaceId());
                bean.setCreateId(userId);
                bean.setCreateTime(DateUtil.getTimeAndToString());
                quColumn.add(bean);
            } else {
                bean.setId(object.getOptionId());
                editquColumn.add(bean);
            }
        }
        if (!quColumn.isEmpty()) {
            createEntity(quColumn, userId);

        }
        if (!editquColumn.isEmpty()) {
            updateEntity(editquColumn, userId);
        }

        List<ExamQuChenRow> quRow = new ArrayList<>();
        List<ExamQuChenRow> editquRow = new ArrayList<>();
        for (int i = 0; i < row.size(); i++) {
            ExamQuChenRow object = row.get(i);
            ExamQuChenRow bean = new ExamQuChenRow();
            bean.setOrderById(object.getOrderById());
            bean.setOptionName(object.getOptionName());
            if (ToolUtil.isBlank(object.getOptionId())) {
                bean.setQuId(quId);
                bean.setVisibility(1);
                bean.setId(ToolUtil.getSurFaceId());
                bean.setCreateId(userId);
                bean.setCreateTime(DateUtil.getTimeAndToString());
                quRow.add(bean);
            } else {
                bean.setId(object.getOptionId());
                editquRow.add(bean);
            }
        }
        if (!quRow.isEmpty()) {
            examQuChenRowService.saveRowEntity(quRow, userId);
        }
        if (!editquRow.isEmpty()) {
            examQuChenRowService.updateRowEntity(editquRow, userId);
        }
    }

    @Override
    protected void deletePreExecution(ExamQuChenColumn entity) {
        String createId = entity.getCreateId();
        String quId = entity.getQuId();
        Integer queryvisibility = examQuChenRowService.QueryvisibilityInRow(quId, createId);
        Integer visibility = entity.getVisibility();
        if (visibility.equals(CommonNumConstants.NUM_ONE) && queryvisibility.equals(CommonNumConstants.NUM_ONE)) {
            throw new CustomException("该选项已显示，请先隐藏再删除");
        }
    }

    @Override
    public void changeVisibility(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        String quId = map.get("quId").toString();
        String createId = map.get("createId").toString();
        examQuChenRowService.changeVisibility(quId, createId);
        UpdateWrapper<ExamQuChenColumn> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(MybatisPlusUtil.toColumns(ExamQuChenColumn::getId), id);
        updateWrapper.set(MybatisPlusUtil.toColumns(ExamQuChenColumn::getVisibility), CommonNumConstants.NUM_ZERO);
        update(updateWrapper);
    }

    @Override
    public void removeByQuId(String quId) {
        examQuChenRowService.removeByQuId(quId);
        UpdateWrapper<ExamQuChenColumn> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(MybatisPlusUtil.toColumns(ExamQuChenColumn::getQuId), quId);
        remove(updateWrapper);
    }

    @Override
    public List<ExamQuChenColumn> selectQuChenColumn(String copyFromId) {
        QueryWrapper<ExamQuChenColumn> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamQuChenColumn::getQuId), copyFromId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamQuChenColumn::getVisibility), CommonNumConstants.NUM_ONE);
        return list(queryWrapper);
    }

    @Override
    public Map<String, List<Map<String, Object>>> selectByBelongId(String id) {
        QueryWrapper<ExamQuChenColumn> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamQuChenColumn::getBelongId), id);
        List<ExamQuChenColumn> list = list(queryWrapper);
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
