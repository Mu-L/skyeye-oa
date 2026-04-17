/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.echarts.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.echarts.entity.ReportModelAttr;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: ReportModelAttrService
 * @Description: Echarts报表模型属性服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2021/6/20 15:22
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface ReportModelAttrService extends SkyeyeBusinessService<ReportModelAttr> {

    void saveList(String reportModelId, List<ReportModelAttr> beans);

    Map<String, List<ReportModelAttr>> queryReportModelAttrMapByModelIds(List<String> reportModelIds);

    List<ReportModelAttr> queryReportModelAttrMapByModelId(String reportModelId);

    void deleteByReportModelId(String reportModelId);

    void deleteByReportModelIds(List<String> reportModelIds);
}
