/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.echarts.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.echarts.dao.ReportModelDao;
import com.skyeye.echarts.entity.ReportModel;
import com.skyeye.echarts.service.ReportModelAttrService;
import com.skyeye.echarts.service.ReportModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName: ReportModelServiceImpl
 * @Description: 模型版本服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/6/20 15:01
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "模型版本", groupName = "模型版本", manageShow = false)
public class ReportModelServiceImpl extends SkyeyeBusinessServiceImpl<ReportModelDao, ReportModel> implements ReportModelService {

    @Autowired
    private ReportModelAttrService reportModelAttrService;

    @Override
    protected void createPrepose(ReportModel entity) {
        Integer softwareVersion = queryNewMaxVersionByImportModelId(entity.getImportModelId());
        entity.setSoftwareVersion(softwareVersion);
    }

    @Override
    protected void writePostpose(ReportModel entity, String userId) {
        super.writePostpose(entity, userId);
        if (entity.getEnabled().equals(EnableEnum.ENABLE_USING.getKey())) {
            // 如果将当前数据修改为启动数据，则需要修改之前的数据为禁用
            UpdateWrapper<ReportModel> updateWrapper = new UpdateWrapper<>();
            updateWrapper.ne(CommonConstants.ID, entity.getId());
            updateWrapper.eq(MybatisPlusUtil.toColumns(ReportModel::getImportModelId), entity.getImportModelId());
            updateWrapper.set(MybatisPlusUtil.toColumns(ReportModel::getEnabled), EnableEnum.DISABLE_USING.getKey());
            update(updateWrapper);
        }
        reportModelAttrService.saveList(entity.getId(), entity.getReportModelAttrList());
    }

    /**
     * 根据 ImportModel 主键获取下一个版本号（当前最大 softwareVersion + 1）
     *
     * @param importModelId 导入模型主档 id
     * @return 新版本号
     */
    @Override
    public Integer queryNewMaxVersionByImportModelId(String importModelId) {
        Integer version = 1;
        QueryWrapper<ReportModel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ReportModel::getImportModelId), importModelId);
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(ReportModel::getSoftwareVersion));
        List<ReportModel> list = list(queryWrapper);
        if (CollectionUtil.isNotEmpty(list)) {
            version = list.get(0).getSoftwareVersion() + 1;
        }
        return version;
    }

    @Override
    public ReportModel getDataFromDb(String id) {
        ReportModel reportModel = super.getDataFromDb(id);
        reportModel.setReportModelAttrList(reportModelAttrService.queryReportModelAttrMapByModelId(reportModel.getId()));
        return reportModel;
    }

    @Override
    protected void deletePostpose(String id) {
        reportModelAttrService.deleteByReportModelId(id);
    }

    @Override
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public void deleteByImportModelId(String importModelId) {
        QueryWrapper<ReportModel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ReportModel::getImportModelId), importModelId);
        List<ReportModel> reportModelList = list(queryWrapper);
        if (CollectionUtil.isEmpty(reportModelList)) {
            return;
        }
        List<String> reportModelIds = reportModelList.stream().map(ReportModel::getId).collect(Collectors.toList());
        reportModelAttrService.deleteByReportModelIds(reportModelIds);
        remove(queryWrapper);
    }

    @Override
    public List<ReportModel> queryAllMaxVersionReportModel() {
        QueryWrapper<ReportModel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ReportModel::getEnabled), EnableEnum.ENABLE_USING.getKey());
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(ReportModel::getSoftwareVersion));
        List<ReportModel> list = list(queryWrapper);
        return list;
    }
}
