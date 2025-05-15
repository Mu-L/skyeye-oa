/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.print.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.print.entity.PrintTemplate;

/**
 * @ClassName: PrintTemplateService
 * @Description: 打印模板服务接口
 * @author: skyeye云系列--卫志强
 * @date: 2025/5/15 8:33
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface PrintTemplateService extends SkyeyeBusinessService<PrintTemplate> {

    void queryPrintTemplateListByPageId(InputObject inputObject, OutputObject outputObject);

    void queryPreviewPrintTemplateById(InputObject inputObject, OutputObject outputObject);

    void generatePdfPrintTemplateById(InputObject inputObject, OutputObject outputObject);

    void copyPrintTemplateById(InputObject inputObject, OutputObject outputObject);

    void editConfigContentById(InputObject inputObject, OutputObject outputObject);
}
