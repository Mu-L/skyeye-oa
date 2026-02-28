/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.echarts.service.impl;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.client.ExecuteFeignClient;
import com.skyeye.common.constans.FileConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.object.PutObject;
import com.skyeye.common.util.FileUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.echarts.classenum.ReportModelState;
import com.skyeye.echarts.dao.ReportImportHistoryDao;
import com.skyeye.echarts.entity.ImportHistory;
import com.skyeye.echarts.entity.ImportModel;
import com.skyeye.echarts.entity.ReportModel;
import com.skyeye.echarts.entity.ReportModelAttr;
import com.skyeye.echarts.service.ReportImportHistoryService;
import com.skyeye.echarts.service.ReportImportModelService;
import com.skyeye.echarts.service.ReportModelAttrService;
import com.skyeye.echarts.service.ReportModelService;
import com.skyeye.eve.centerrest.common.CommonService;
import com.skyeye.exception.CustomException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: ReportImportHistoryServiceImpl
 * @Description: Echarts导入历史服务层
 * @author: skyeye云系列--卫志强
 * @date: 2021/6/20 14:05
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "Echarts导入历史", groupName = "Echarts导入历史")
public class ReportImportHistoryServiceImpl extends SkyeyeBusinessServiceImpl<ReportImportHistoryDao, ImportHistory> implements ReportImportHistoryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReportImportHistoryServiceImpl.class);

    @Autowired
    private ReportModelService reportModelService;

    @Autowired
    private ReportImportModelService reportImportModelService;

    @Autowired
    private ReportModelAttrService reportModelAttrService;

    @Autowired
    private CommonService commonService;

    @Override
    public QueryWrapper<ImportHistory> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<ImportHistory> queryWrapper = super.getQueryWrapper(commonPageInfo);
        queryWrapper.eq(MybatisPlusUtil.toColumns(ImportHistory::getModelCode), commonPageInfo.getObjectId());
        return queryWrapper;
    }

    /**
     * 插入模型上传导入历史
     *
     * @param name      文件名称
     * @param size      文件大小
     * @param modelCode 模版编码
     * @param userId    用户id
     * @return 模型上传导入历史对象
     */
    private String insertReportImportHistory(String name, Long size, String modelCode, String userId) {
        ImportHistory importHistory = new ImportHistory();
        importHistory.setName(name);
        importHistory.setSize(String.valueOf(size));
        importHistory.setModelCode(modelCode);
        return createEntity(importHistory, userId);
    }

    /**
     * 模型上传导入
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public void importReportImportModel(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String modelId = map.get("modelId").toString();
        String userId = inputObject.getLogParams().get("id").toString();
        // 将当前上下文初始化给 CommonsMutipartResolver （多部分解析器）
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(PutObject.getRequest().getSession().getServletContext());
        // 检查form中是否有enctype="multipart/form-data"
        if (multipartResolver.isMultipart(PutObject.getRequest())) {
            // 将request变成多部分request
            MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) PutObject.getRequest();
            // 获取multiRequest 中所有的文件名
            Iterator iter = multiRequest.getFileNames();
            while (iter.hasNext()) {
                MultipartFile file = multiRequest.getFile(iter.next().toString());
                // 读取基本信息
                String reportModelId = saveModelMation(file, modelId, userId);
                // 模型属性信息
                saveModelAttrMation(file, reportModelId);
            }
        }
    }

    /**
     * 解析并保存模型信息
     *
     * @param file    文件
     * @param modelId 模型id
     * @param userId  用户id
     * @return 模型id
     */
    private String saveModelMation(MultipartFile file, String modelId, String userId) {
        ImportParams reportModelParams = new ImportParams();
        reportModelParams.setStartSheetIndex(0);
        List<ReportModel> reportModelList;
        try {
            reportModelList = ExcelImportUtil.importExcel(file.getInputStream(), ReportModel.class, reportModelParams);
        } catch (Exception ee) {
            throw new CustomException(ee);
        }
        if (CollectionUtil.isNotEmpty(reportModelList)) {
            ReportModel reportModel = reportModelList.get(0);
            Map<String, Object> filePath = ExecuteFeignClient.get(() ->
                commonService.queryFilePathByFileType(FileConstants.FileUploadPath.REPORT_IMPORT_HISTORY.getType()[0])).getBean();
            String savePath = filePath.get("savePath").toString();
            String visitPath = filePath.get("visitPath").toString();
            // 设置logo图片
            String newFileName = System.currentTimeMillis() + ".png";
            // 保存
            ToolUtil.NIOCopyFile(reportModel.getLogoPath(), savePath + "/" + newFileName);
            FileUtil.deleteFile(reportModel.getLogoPath());
            // 设置可访问路径
            reportModel.setLogoPath(visitPath + newFileName);
            ImportModel importModel = reportImportModelService.selectById(modelId);
            reportModel.setModelCode(importModel.getModelCode());
            // 插入模型上传导入历史
            String historyId = insertReportImportHistory(file.getOriginalFilename(), file.getSize(), importModel.getModelCode(), userId);
            reportModel.setHistoryId(historyId);

            Integer softwareVersion = reportModelService.queryNewMaxVersionByModelCode(importModel.getModelCode());
            reportModel.setSoftwareVersion(softwareVersion);
            reportModel.setState(ReportModelState.NORMAL.getKey());
            return reportModelService.createEntity(reportModel, userId);
        }
        return StrUtil.EMPTY;
    }

    /**
     * 解析并保存模型属性信息
     *
     * @param file          文件
     * @param reportModelId 模型id
     */
    private void saveModelAttrMation(MultipartFile file, String reportModelId) {
        ImportParams reportModelAttrParams = new ImportParams();
        reportModelAttrParams.setStartSheetIndex(1);
        List<ReportModelAttr> reportModelAttrList;
        try {
            reportModelAttrList = ExcelImportUtil.importExcel(file.getInputStream(), ReportModelAttr.class, reportModelAttrParams);
        } catch (Exception ee) {
            throw new CustomException(ee);
        }
        reportModelAttrList.forEach(bean -> {
            bean.setReportModelId(reportModelId);
        });
        reportModelAttrService.createEntity(reportModelAttrList, StrUtil.EMPTY);
    }

    @Override
    public void queryAllMaxVersionReportModel(InputObject inputObject, OutputObject outputObject) {
        List<ReportModel> reportModelList = reportModelService.queryAllMaxVersionReportModel();
        Map<String, ReportModel> reportModelMap = reportModelList.stream().collect(Collectors.toMap(ReportModel::getModelCode, item -> item));
        Map<String, String> reportModelIdMap = reportModelList.stream().collect(Collectors.toMap(ReportModel::getModelCode, ReportModel::getId));

        List<ImportModel> models = reportImportModelService.queryImportModelList(new ArrayList<>(reportModelIdMap.keySet()));
        Map<String, List<ReportModelAttr>> modelAttrsMap = reportModelAttrService.queryReportModelAttrMapByModelIds(reportModelIdMap.values().stream()
            .collect(Collectors.toList()));
        models.forEach(model -> {
            try {
                model.setReportModel(reportModelMap.get(model.getModelCode()));
                String reportModelId = reportModelIdMap.get(model.getModelCode());
                List<ReportModelAttr> attrs = modelAttrsMap.get(reportModelId);
                Map<String, ReportModelAttr> attrsMap = attrs.stream().collect(Collectors.toMap(ReportModelAttr::getAttrCode, item -> item));
                model.setAttr(attrsMap);
            } catch (Exception ee) {
                LOGGER.warn("queryAllMaxVersionReportModel -> reportModelAttrDao.getReportModelAttrToEditorByModelId failed.", ee);
            }
        });
        outputObject.setBeans(models);
        outputObject.settotal(models.size());
    }

}
