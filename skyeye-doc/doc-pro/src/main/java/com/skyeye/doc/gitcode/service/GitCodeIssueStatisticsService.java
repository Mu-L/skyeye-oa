/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.doc.gitcode.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

/**
 * 问答社区（GitCode Issue）统计分析服务
 */
public interface GitCodeIssueStatisticsService {

    void queryIssueTotal(InputObject inputObject, OutputObject outputObject);

    void queryIssueCommentTotal(InputObject inputObject, OutputObject outputObject);

    void queryIssueBugTotal(InputObject inputObject, OutputObject outputObject);

    void queryIssueRequirementTotal(InputObject inputObject, OutputObject outputObject);

    void queryIssueBugCompletedTotal(InputObject inputObject, OutputObject outputObject);

    void queryIssueRequirementCompletedTotal(InputObject inputObject, OutputObject outputObject);

    void queryIssueStatsByCreateTime(InputObject inputObject, OutputObject outputObject);

    void queryIssueCommentStatsByCreateTime(InputObject inputObject, OutputObject outputObject);

    void queryIssueStatsByVersion(InputObject inputObject, OutputObject outputObject);

    void queryIssueStatsByRecordType(InputObject inputObject, OutputObject outputObject);

}
