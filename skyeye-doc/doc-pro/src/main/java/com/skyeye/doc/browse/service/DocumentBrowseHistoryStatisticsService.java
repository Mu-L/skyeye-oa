/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.doc.browse.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

/**
 * 文档浏览历史统计分析服务
 */
public interface DocumentBrowseHistoryStatisticsService {

    void queryBrowseHistoryTotal(InputObject inputObject, OutputObject outputObject);

    void queryBrowseViewCountTotal(InputObject inputObject, OutputObject outputObject);

    void queryBrowseMemberTotal(InputObject inputObject, OutputObject outputObject);

    void queryBrowseDocumentTotal(InputObject inputObject, OutputObject outputObject);

    void queryBrowseRevisitTotal(InputObject inputObject, OutputObject outputObject);

    void queryBrowseHistoryStatsByCreateTime(InputObject inputObject, OutputObject outputObject);

    void queryBrowseHistoryStatsByLastViewTime(InputObject inputObject, OutputObject outputObject);

    void queryBrowseStatsByDocument(InputObject inputObject, OutputObject outputObject);

    void queryBrowseStatsByCity(InputObject inputObject, OutputObject outputObject);

    void queryBrowseStatsByMember(InputObject inputObject, OutputObject outputObject);

}
