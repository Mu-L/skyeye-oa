/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye-report
 ******************************************************************************/

package com.skyeye.page.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.page.dao.ReportPageDao;
import com.skyeye.page.entity.ReportPage;
import com.skyeye.page.service.ReportPageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * @ClassName: ReportPageServiceImpl
 * @Description: 报表页面信息服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/6/26 17:44
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye-report Inc. All rights reserved.
 * 注意：本内容具体规则请参照readme执行，地址：https://gitee.com/doc_wei01/skyeye-report/blob/master/README.md
 */
@Service
@SkyeyeService(name = "报表页面", groupName = "报表页面", tenant = TenantEnum.PLATE)
public class ReportPageServiceImpl extends SkyeyeBusinessServiceImpl<ReportPageDao, ReportPage> implements ReportPageService {

    @Override
    protected void updatePrepose(ReportPage entity) {
        ReportPage reportPage = selectById(entity.getId());
        entity.setContent(reportPage.getContent());
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void editReportPageContentById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String id = params.get("id").toString();
        String content = params.get("content").toString();

        UpdateWrapper<ReportPage> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(ReportPage::getContent), content);
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        updateWrapper.set(MybatisPlusUtil.toColumns(ReportPage::getLastUpdateId), userId);
        updateWrapper.set(MybatisPlusUtil.toColumns(ReportPage::getLastUpdateTime), DateUtil.getTimeAndToString());
        update(updateWrapper);
        refreshCache(id);
    }

    @Override
    @IgnoreTenant
    public ReportPage selectById(String id) {
        return super.selectById(id);
    }
}
