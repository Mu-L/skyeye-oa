/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.request.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.yulichang.toolkit.JoinWrappers;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.DeleteFlagEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.service.ITenantService;
import com.skyeye.exception.CustomException;
import com.skyeye.request.classenum.InquiryQuoteSourceEnum;
import com.skyeye.request.classenum.PurchaseRequestInquiryState;
import com.skyeye.request.dao.PurchaseRequestInquiryChildDao;
import com.skyeye.request.entity.PurchaseRequest;
import com.skyeye.request.entity.PurchaseRequestInquiryChild;
import com.skyeye.request.service.PurchaseRequestInquiryChildService;
import com.skyeye.request.service.PurchaseRequestService;
import com.skyeye.supplier.entity.Supplier;
import com.skyeye.supplier.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: PurchaseRequestInquiryChildServiceImpl
 * @Description: 采购申请询价明细服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/5/22 11:32
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "采购申请询价明细", groupName = "采购申请")
public class PurchaseRequestInquiryChildServiceImpl extends SkyeyeBusinessServiceImpl<PurchaseRequestInquiryChildDao, PurchaseRequestInquiryChild> implements PurchaseRequestInquiryChildService {

    @Autowired
    private PurchaseRequestService purchaseRequestService;

    @Autowired
    private ITenantService iTenantService;

    @Autowired
    private SupplierService supplierService;

    @Override
    public void saveList(String parentId, List<PurchaseRequestInquiryChild> beans) {
        deleteByParentId(parentId, InquiryQuoteSourceEnum.BACKEND.getKey());
        if (CollectionUtil.isNotEmpty(beans)) {
            for (PurchaseRequestInquiryChild purchaseRequestInquiryChild : beans) {
                purchaseRequestInquiryChild.setParentId(parentId);
                // 后端询价报价保存，统一标记为后端添加
                purchaseRequestInquiryChild.setQuoteSource(InquiryQuoteSourceEnum.BACKEND.getKey());
                // 计算子单据总价：单价 * 数量
                BigDecimal itemAllPrice = new BigDecimal(purchaseRequestInquiryChild.getUnitPrice());
                itemAllPrice = itemAllPrice.multiply(new BigDecimal(purchaseRequestInquiryChild.getOperNumber()));
                purchaseRequestInquiryChild.setAllPrice(itemAllPrice.toString());

                // 计算子单据价税合计：含税单价 * 数量
                BigDecimal taxUnitPrice = new BigDecimal(purchaseRequestInquiryChild.getTaxUnitPrice());
                taxUnitPrice = taxUnitPrice.multiply(new BigDecimal(purchaseRequestInquiryChild.getOperNumber()));
                purchaseRequestInquiryChild.setTaxLastMoney(taxUnitPrice.toString());

            }
            createEntity(beans, StrUtil.EMPTY);
        }
    }

    @Override
    public void deleteByParentId(String parentId, String quoteSource) {
        QueryWrapper<PurchaseRequestInquiryChild> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(PurchaseRequestInquiryChild::getParentId), parentId);
        if (StrUtil.isNotEmpty(quoteSource)) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(PurchaseRequestInquiryChild::getQuoteSource), quoteSource);
        }
        remove(queryWrapper);
    }

    @Override
    public List<PurchaseRequestInquiryChild> selectByParentId(String parentId) {
        QueryWrapper<PurchaseRequestInquiryChild> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(PurchaseRequestInquiryChild::getParentId), parentId);
        List<PurchaseRequestInquiryChild> list = list(queryWrapper);
        return list;
    }

    @Override
    @IgnoreTenant
    public void queryPurchaseRequestInquiryChildList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);

        // 从登录用户信息中获取营业执照注册号
        String socialCreditCode = InputObject.getLogParamsStatic().getOrDefault("socialCreditCode", StrUtil.EMPTY).toString();
        if (StrUtil.isEmpty(socialCreditCode)) {
            throw new CustomException("未获取到企业营业执照注册号，请先登录企业账户");
        }

        // 主表：询价明细，关联采购申请、供应商
        MPJLambdaWrapper<PurchaseRequestInquiryChild> wrapper = JoinWrappers.lambda("ic", PurchaseRequestInquiryChild.class)
            .innerJoin(PurchaseRequest.class, "pr", PurchaseRequest::getId, PurchaseRequestInquiryChild::getParentId)
            .innerJoin(Supplier.class, "s", Supplier::getId, PurchaseRequest::getId);

        // 根据营业执照注册号匹配供应商
        wrapper.eq("s." + MybatisPlusUtil.toColumns(Supplier::getSocialCreditCode), socialCreditCode);
        wrapper.eq("s." + MybatisPlusUtil.toColumns(Supplier::getDeleteFlag), DeleteFlagEnum.NOT_DELETE.getKey());

        // 供应商报价
        wrapper.like("ic." + MybatisPlusUtil.toColumns(PurchaseRequestInquiryChild::getQuoteSource), InquiryQuoteSourceEnum.SUPPLIER.getKey());

        // 关键词查询：采购申请单据编号
        if (StrUtil.isNotEmpty(commonPageInfo.getKeyword())) {
            wrapper.like("pr." + MybatisPlusUtil.toColumns(PurchaseRequest::getOddNumber), commonPageInfo.getKeyword());
        }

        Page pages = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        List<Map<String, Object>> resultList = baseMapper.selectJoinMaps(wrapper);

        iTenantService.setMationForMap(resultList, "tenantId", "tenantMation");
        purchaseRequestService.setRequestMationByFromId(resultList, "parentId", "parentMation");

        outputObject.setBeans(resultList);
        outputObject.settotal(pages.getTotal());
    }

    @Override
    @IgnoreTenant
    public void saveOrUpdateEntity(InputObject inputObject, OutputObject outputObject) {
        super.saveOrUpdateEntity(inputObject, outputObject);
    }

    @Override
    @IgnoreTenant
    public void deleteById(InputObject inputObject, OutputObject outputObject) {
        super.deleteById(inputObject, outputObject);
    }

    @Override
    @IgnoreTenant
    public void selectById(InputObject inputObject, OutputObject outputObject) {
        super.selectById(inputObject, outputObject);
    }

    @Override
    protected void validatorEntity(PurchaseRequestInquiryChild entity) {
        if (StrUtil.isEmpty(entity.getParentId())) {
            throw new CustomException("采购申请id不能为空");
        }
        checkInquiryStateAllowed(entity);
        calcPriceFields(entity);
    }

    @Override
    protected void createPrepose(PurchaseRequestInquiryChild entity) {
        entity.setQuoteSource(InquiryQuoteSourceEnum.SUPPLIER.getKey());
    }

    @Override
    protected void deletePreExecution(PurchaseRequestInquiryChild entity) {
        checkInquiryStateAllowed(entity);
    }

    /**
     * 校验采购申请询价状态：仅待询价、询价中允许改/删
     */
    private void checkInquiryStateAllowed(PurchaseRequestInquiryChild entity) {
        PurchaseRequest parent = purchaseRequestService.queryByIdAndNoIsolation(entity.getParentId());
        if (parent == null) {
            throw new CustomException("关联的采购申请不存在");
        }
        // 从登录用户信息中获取营业执照注册号
        String socialCreditCode = InputObject.getLogParamsStatic().getOrDefault("socialCreditCode", StrUtil.EMPTY).toString();
        if (StrUtil.isEmpty(socialCreditCode)) {
            throw new CustomException("未获取到企业营业执照注册号，请先登录企业账户");
        }
        Supplier supplier = supplierService.queryBySocialCreditCodeAndPointTenant(socialCreditCode, entity.getTenantId());
        if (supplier == null) {
            throw new CustomException("该企业未关联供应商，您无法进行报价，请联系企业管理员将您添加到供应商。");
        }
        entity.setSupplierId(supplier.getId());
        entity.setTenantId(entity.getTenantId());
        Integer inquiryState = parent.getInquiryState();
        if (!PurchaseRequestInquiryState.WAIT_INQUIRY.getKey().equals(inquiryState)
            && !PurchaseRequestInquiryState.INQUIRYING.getKey().equals(inquiryState)) {
            throw new CustomException("仅待询价、询价中状态的采购申请允许新增/修改/删除报价信息");
        }
    }

    @Override
    @IgnoreTenant
    public void queryEnterpriseQuoteByItemAndNorms(InputObject inputObject, OutputObject outputObject) {
        String parentId = inputObject.getParams().getOrDefault("parentId", StrUtil.EMPTY).toString();
        String materialId = inputObject.getParams().getOrDefault("materialId", StrUtil.EMPTY).toString();
        String normsId = inputObject.getParams().getOrDefault("normsId", StrUtil.EMPTY).toString();

        // 校验采购申请存在
        PurchaseRequest parent = purchaseRequestService.queryByIdAndNoIsolation(parentId);
        if (parent == null) {
            throw new CustomException("采购申请不存在");
        }

        // 获取当前企业用户对应的供应商
        String socialCreditCode = InputObject.getLogParamsStatic().getOrDefault("socialCreditCode", StrUtil.EMPTY).toString();
        if (StrUtil.isEmpty(socialCreditCode)) {
            throw new CustomException("未获取到企业营业执照注册号，请先登录企业账户");
        }
        String tenantId = parent.getTenantId();
        Supplier supplier = supplierService.queryBySocialCreditCodeAndPointTenant(socialCreditCode, tenantId);
        if (supplier == null) {
            return;
        }

        // 查询当前企业针对该单据下该商品该规格的报价信息
        QueryWrapper<PurchaseRequestInquiryChild> wrapper = new QueryWrapper<>();
        wrapper.eq(MybatisPlusUtil.toColumns(PurchaseRequestInquiryChild::getParentId), parentId)
            .eq(MybatisPlusUtil.toColumns(PurchaseRequestInquiryChild::getMaterialId), materialId)
            .eq(MybatisPlusUtil.toColumns(PurchaseRequestInquiryChild::getNormsId), normsId)
            .eq(MybatisPlusUtil.toColumns(PurchaseRequestInquiryChild::getSupplierId), supplier.getId())
            .eq(MybatisPlusUtil.toColumns(PurchaseRequestInquiryChild::getQuoteSource), InquiryQuoteSourceEnum.SUPPLIER.getKey());
        PurchaseRequestInquiryChild quote = getOne(wrapper, false);
        outputObject.setBean(quote);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    /**
     * 计算单价、含税单价对应的总价和价税合计
     */
    private void calcPriceFields(PurchaseRequestInquiryChild entity) {
        if (entity.getOperNumber() == null || StrUtil.isEmpty(entity.getUnitPrice()) || StrUtil.isEmpty(entity.getTaxUnitPrice())) {
            return;
        }
        // 计算子单据总价：单价 * 数量
        BigDecimal itemAllPrice = new BigDecimal(entity.getUnitPrice());
        itemAllPrice = itemAllPrice.multiply(new BigDecimal(entity.getOperNumber()));
        entity.setAllPrice(itemAllPrice.toString());

        // 计算子单据价税合计：含税单价 * 数量
        BigDecimal taxUnitPrice = new BigDecimal(entity.getTaxUnitPrice());
        taxUnitPrice = taxUnitPrice.multiply(new BigDecimal(entity.getOperNumber()));
        entity.setTaxLastMoney(taxUnitPrice.toString());
    }
}
