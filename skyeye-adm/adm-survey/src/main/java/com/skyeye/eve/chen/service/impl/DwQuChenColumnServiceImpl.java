package com.skyeye.eve.chen.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.chen.dao.DwQuChenColumnDao;
import com.skyeye.eve.chen.entity.DwQuChenColumn;
import com.skyeye.eve.chen.entity.DwQuChenRow;
import com.skyeye.eve.chen.service.DwQuChenColumnService;
import com.skyeye.eve.chen.service.DwQuChenRowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: DwAnCompChenRadioServiceImpl
 * @Description: 矩陈题列选项服务层
 * @author: skyeye云系列--卫志强
 * @date: 2021/5/16 23:20
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye-report Inc. All rights reserved.
 * 注意：本内容具体规则请参照readme执行，地址：https://gitee.com/doc_wei01/skyeye-report/blob/master/README.md
 */
@Service
@SkyeyeService(name = "矩陈题-列选项管理", groupName = "矩陈题-列选项管理", manageShow = false)
public class DwQuChenColumnServiceImpl extends SkyeyeBusinessServiceImpl<DwQuChenColumnDao, DwQuChenColumn> implements DwQuChenColumnService {

    @Autowired
    private DwQuChenRowService dwQuChenRowService;

    @Override
    protected QueryWrapper<DwQuChenColumn> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<DwQuChenColumn> queryWrapper = super.getQueryWrapper(commonPageInfo);
        if (StrUtil.isNotEmpty(commonPageInfo.getHolderId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(DwQuChenColumn::getQuId), commonPageInfo.getHolderId());
            dwQuChenRowService.QueryExamQuChenRowList(commonPageInfo.getHolderId());
        }
        return queryWrapper;
    }

    @Override
    public void saveList(List<DwQuChenColumn> column, List<DwQuChenRow> row, String quId, String userId) {
        List<DwQuChenColumn> quColumn = new ArrayList<>();
        List<DwQuChenColumn> editquColumn = new ArrayList<>();
        for (int i = 0; i < column.size(); i++) {
            DwQuChenColumn object = column.get(i);
            DwQuChenColumn bean = new DwQuChenColumn();
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

        List<DwQuChenRow> quRow = new ArrayList<>();
        List<DwQuChenRow> editquRow = new ArrayList<>();
        for (int i = 0; i < row.size(); i++) {
            DwQuChenRow object = row.get(i);
            DwQuChenRow bean = new DwQuChenRow();
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
            dwQuChenRowService.saveRowEntity(quRow, userId);
        }
        if (!editquRow.isEmpty()) {
            dwQuChenRowService.updateRowEntity(editquRow, userId);
        }
    }

    @Override
    protected void deletePreExecution(DwQuChenColumn entity) {
        String createId = entity.getCreateId();
        String quId = entity.getQuId();
        Integer queryvisibility = dwQuChenRowService.QueryvisibilityInRow(quId, createId);
//        Integer visibility = entity.getVisibility();
//        if (visibility.equals(CommonNumConstants.NUM_ONE) && queryvisibility.equals(CommonNumConstants.NUM_ONE)) {
//            throw new CustomException("该选项已显示，请先隐藏再删除");
//        }
    }

    @Override
    public void changeVisibility(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        String quId = map.get("quId").toString();
        String createId = map.get("createId").toString();
        dwQuChenRowService.changeVisibility(quId, createId);
        UpdateWrapper<DwQuChenColumn> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(DwQuChenColumn::getVisibility), CommonNumConstants.NUM_ZERO);
        update(updateWrapper);
    }

    @Override
    public void removeByQuId(String quId) {
        dwQuChenRowService.removeByQuId(quId);
        UpdateWrapper<DwQuChenColumn> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(MybatisPlusUtil.toColumns(DwQuChenColumn::getQuId), quId);
        remove(updateWrapper);
    }

    @Override
    public List<DwQuChenColumn> selectQuChenColumn(String copyFromId) {
        QueryWrapper<DwQuChenColumn> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwQuChenColumn::getQuId), copyFromId);
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(DwQuChenColumn::getOrderById));
        return list(queryWrapper);
    }

    @Override
    public Map<String, List<DwQuChenColumn>> selectByBelongId(List<String> id) {
        if (CollectionUtil.isEmpty(id)) {
            return new HashMap<>();
        }
        QueryWrapper<DwQuChenColumn> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(DwQuChenColumn::getQuId), id);
        List<DwQuChenColumn> list = list(queryWrapper);
        Map<String, List<DwQuChenColumn>> result = list.stream().collect(Collectors.groupingBy(DwQuChenColumn::getQuId));
        return result;
    }

}
