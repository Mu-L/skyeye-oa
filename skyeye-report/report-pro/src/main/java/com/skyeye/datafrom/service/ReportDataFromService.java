/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye-report
 ******************************************************************************/

package com.skyeye.datafrom.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.datafrom.entity.ReportDataFrom;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: ReportDataFromService
 * @Description: 数据来源服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2021/5/16 23:21
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye-report Inc. All rights reserved.
 * 注意：本内容具体规则请参照readme执行，地址：https://gitee.com/doc_wei01/skyeye-report/blob/master/README.md
 */
public interface ReportDataFromService extends SkyeyeBusinessService<ReportDataFrom> {

    /**
     * 根据数据来源id获取该数据来源下的所有数据并组装成map
     *
     * @param fromId      数据来源id
     * @param needGetKeys 需要获取的key
     * @param inputParams 入参
     * @return 该数据来源下的所有数据并组装成map
     */
    Map<String, Object> getReportDataFromMapByFromId(String fromId, List<String> needGetKeys, String inputParams);

    /**
     * 根据数据来源信息获取要取的数据
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    void queryReportDataFromMationById(InputObject inputObject, OutputObject outputObject);

    /**
     * 批量根据数据来源信息获取要取的数据（同页多组件合并请求）
     */
    void queryReportDataFromMationBatch(InputObject inputObject, OutputObject outputObject);

}
