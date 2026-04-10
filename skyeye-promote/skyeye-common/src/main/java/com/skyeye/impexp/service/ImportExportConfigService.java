/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.impexp.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.impexp.entity.ImportExportConfig;

public interface ImportExportConfigService extends SkyeyeBusinessService<ImportExportConfig> {

    void queryImportExportConfigList(InputObject inputObject, OutputObject outputObject);

    void queryImportExportConfigForUse(InputObject inputObject, OutputObject outputObject);

    /**
     * 导入导出场景获取最终列清单（已应用配置与默认回落规则）
     */
    void queryImportExportColumnsForUse(InputObject inputObject, OutputObject outputObject);

    void queryImportExportFieldOptions(InputObject inputObject, OutputObject outputObject);

    /**
     * 按当前配置的 import_config 列下载导入模板（仅表头，与 ScheduleDay 下载模板方式一致）
     */
    void downloadImportTemplate(InputObject inputObject, OutputObject outputObject);

    /**
     * 按当前配置的 export_config 列下载导出空表（仅表头，业务数据导出由各业务后续对接）
     */
    void downloadExportTemplate(InputObject inputObject, OutputObject outputObject);

    /**
     * 按配置导出数据（不接收rows，后端按filters查询数据）
     */
    void exportByConfig(InputObject inputObject, OutputObject outputObject);
}

