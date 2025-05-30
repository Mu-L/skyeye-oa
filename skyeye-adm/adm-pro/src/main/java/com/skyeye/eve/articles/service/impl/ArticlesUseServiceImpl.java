/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.articles.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.base.business.service.impl.SkyeyeFlowableServiceImpl;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.FlowableChildStateEnum;
import com.skyeye.common.enumeration.FlowableStateEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.articles.dao.ArticlesUseDao;
import com.skyeye.eve.articles.entity.ArticlesUse;
import com.skyeye.eve.articles.entity.ArticlesUseLink;
import com.skyeye.eve.articles.service.ArticlesService;
import com.skyeye.eve.articles.service.ArticlesUseLinkService;
import com.skyeye.eve.articles.service.ArticlesUseService;
import com.skyeye.eve.assets.entity.AssetReturn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: ArticlesUseServiceImpl
 * @Description: 用品领用申请服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/24 9:22
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "用品领用单", groupName = "用品模块", flowable = true)
public class ArticlesUseServiceImpl extends SkyeyeFlowableServiceImpl<ArticlesUseDao, ArticlesUse> implements ArticlesUseService {

    @Autowired
    private ArticlesUseLinkService articlesUseLinkService;

    @Autowired
    private ArticlesService articlesService;

    @Value("${skyeye.tenant.enable}")
    private boolean tenantEnable;

    @Override
    protected QueryWrapper<ArticlesUse> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<ArticlesUse> queryWrapper = super.getQueryWrapper(commonPageInfo);
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        queryWrapper.eq(MybatisPlusUtil.toColumns(AssetReturn::getCreateId), userId);
        return queryWrapper;
    }

    @Override
    public void writeChild(ArticlesUse entity, String userId) {
        articlesUseLinkService.saveLinkList(entity.getId(), entity.getApplyUseLnk());
        super.writeChild(entity, userId);
    }

    @Override
    public void submitToApprovalPostpose(String id, String processInstanceId) {
        super.submitToApprovalPostpose(id, processInstanceId);
        articlesUseLinkService.editStateByPId(id, FlowableChildStateEnum.IN_EXAMINE.getKey());
    }

    @Override
    public ArticlesUse getDataFromDb(String id) {
        ArticlesUse articlesUse = super.getDataFromDb(id);
        List<ArticlesUseLink> articlesUseLink = articlesUseLinkService.selectByPId(articlesUse.getId());
        articlesUse.setApplyUseLnk(articlesUseLink);
        return articlesUse;
    }

    @Override
    public ArticlesUse selectById(String id) {
        ArticlesUse articlesUse = super.selectById(id);
        // 获取用品信息
        articlesService.setDataMation(articlesUse.getApplyUseLnk(), ArticlesUseLink::getArticleId);
        articlesUse.getApplyUseLnk().forEach(bean -> {
            bean.setStateName(FlowableChildStateEnum.getStateName(bean.getState()));
        });
        articlesUse.setStateName(FlowableStateEnum.getStateName(articlesUse.getState()));
        iAuthUserService.setName(articlesUse, "createId", "createName");
        return articlesUse;
    }

    @Override
    public void revokePostpose(ArticlesUse entity) {
        super.revokePostpose(entity);
        articlesUseLinkService.editStateByPId(entity.getId(), FlowableChildStateEnum.DRAFT.getKey());
    }

    @Override
    protected void approvalEndIsSuccess(ArticlesUse entity) {
        ArticlesUse articlesApplyUse = selectById(entity.getId());
        for (ArticlesUseLink bean : articlesApplyUse.getApplyUseLnk()) {
            // 当前用品剩余的数量
            int residualNum = bean.getArticleMation().getResidualNum();
            // 允许用户领用的数量
            int actualUseNum;
            // 用品对应的领用状态
            String applyState;
            if (residualNum >= bean.getApplyUseNum()) {
                // 当前库存充足
                applyState = FlowableChildStateEnum.ADEQUATE.getKey();
                actualUseNum = bean.getApplyUseNum();
            } else {
                // 当前库存不足
                applyState = FlowableChildStateEnum.INSUFFICIENT.getKey();
                actualUseNum = residualNum;
            }
            // 重置库存剩余数量
            residualNum = residualNum - actualUseNum;
            // 修改库存
            articlesService.editResidualNum(bean.getArticleId(), residualNum);
            // 修改用品领用状态
            articlesUseLinkService.editActualUseNumById(bean.getId(), applyState, actualUseNum);
        }
    }

    @Override
    protected void approvalEndIsFailed(ArticlesUse entity) {
        articlesUseLinkService.editStateByPId(entity.getId(), FlowableChildStateEnum.REJECT.getKey());
    }

    @Override
    @IgnoreTenant
    public void queryMyArticlesList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo pageInfo = inputObject.getParams(CommonPageInfo.class);
        pageInfo.setCreateId(inputObject.getLogParams().get("id").toString());
        Page pages = PageHelper.startPage(pageInfo.getPage(), pageInfo.getLimit());
        if (tenantEnable) {
            pageInfo.setTenantId(TenantContext.getTenantId());
        }
        List<Map<String, Object>> beans = skyeyeBaseMapper.queryMyArticlesList(pageInfo);
        iSysDictDataService.setNameForMap(beans, "typeId", "typeName");
        outputObject.setBeans(beans);
        outputObject.settotal(pages.getTotal());
    }
}
