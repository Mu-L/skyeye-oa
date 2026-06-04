/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.doc.browse.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.doc.browse.entity.DocumentBrowseHistory;

/**
 * 文档浏览历史服务
 */
public interface DocumentBrowseHistoryService extends SkyeyeBusinessService<DocumentBrowseHistory> {

    void recordDocumentBrowseHistory(InputObject inputObject, OutputObject outputObject);

    void queryMyDocumentBrowseHistoryList(InputObject inputObject, OutputObject outputObject);

    void deleteMyDocumentBrowseHistoryById(InputObject inputObject, OutputObject outputObject);

    void clearMyDocumentBrowseHistory(InputObject inputObject, OutputObject outputObject);

}
