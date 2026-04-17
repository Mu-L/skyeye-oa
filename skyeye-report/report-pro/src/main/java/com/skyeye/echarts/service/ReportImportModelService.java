/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye-report
 ******************************************************************************/

package com.skyeye.echarts.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.echarts.entity.ImportModel;

/**
 * @ClassName: ReportImportModelService
 * @Description: Echarts模型管理服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2021/5/16 23:21
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye-report Inc. All rights reserved.
 * 注意：本内容具体规则请参照readme执行，地址：https://gitee.com/doc_wei01/skyeye-report/blob/master/README.md
 */
public interface ReportImportModelService extends SkyeyeBusinessService<ImportModel> {

    void queryAllMaxVersionReportModel(InputObject inputObject, OutputObject outputObject);

    void queryReportModelVersionList(InputObject inputObject, OutputObject outputObject);

    void enableReportModelVersion(InputObject inputObject, OutputObject outputObject);

    void disableReportModelVersion(InputObject inputObject, OutputObject outputObject);

    void queryReportModelVersionById(InputObject inputObject, OutputObject outputObject);
}
