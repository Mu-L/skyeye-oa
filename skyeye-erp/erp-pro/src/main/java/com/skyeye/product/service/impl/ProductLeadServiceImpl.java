package com.skyeye.product.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeFlowableServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.FlowableChildStateEnum;
import com.skyeye.common.enumeration.FlowableStateEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.service.IAuthUserService;
import com.skyeye.exception.CustomException;
import com.skyeye.product.dao.ProductLeadDao;
import com.skyeye.product.entity.ProductLead;
import com.skyeye.product.entity.ProductLeadLink;
import com.skyeye.product.entity.ProductRestitution;
import com.skyeye.product.service.ProductLeadLinkService;
import com.skyeye.product.service.ProductLeadService;
import com.skyeye.product.service.ProductRestitutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@SkyeyeService(name = "借出出库", groupName = "借出出库", manageShow = false)
public class ProductLeadServiceImpl extends SkyeyeFlowableServiceImpl<ProductLeadDao, ProductLead> implements ProductLeadService {

    @Autowired
    private IAuthUserService iAuthUserService;

    @Autowired
    private ProductLeadLinkService productLeadLinkService;

    @Autowired
    private ProductRestitutionService productRestitutionService;

    @Override
    public QueryWrapper<ProductLead> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<ProductLead> queryWrapper = super.getQueryWrapper(commonPageInfo);
        queryWrapper.eq(MybatisPlusUtil.toColumns(ProductLead::getIdKey), getServiceClassName());
        return queryWrapper;
    }

    @Override
    public void validatorEntity(ProductLead entity) {
        // 判断产品不重复
        checkOrderItem(entity.getProductLeadLinks());
        // 计算总金额
        getTotalPrice(entity);
    }

    private void getTotalPrice(ProductLead entity) {
        String totalPrice = "0";
        // 计算关联的资产总价
        for (ProductLeadLink productLeadLink : entity.getProductLeadLinks()) {
            String amountOfMoney = CalculationUtil.multiply(CommonNumConstants.NUM_TWO, String.valueOf(productLeadLink.getArticleNum()), productLeadLink.getUnitPrice());
            productLeadLink.setAllPrice(amountOfMoney);
            totalPrice = CalculationUtil.add(totalPrice, amountOfMoney);
        }
        entity.setAllPrice(totalPrice);
    }

    private void checkOrderItem(List<ProductLeadLink> productLeadLinks) {
        List<String> assetIds = productLeadLinks.stream()
            .map(bean -> String.format(Locale.ROOT, "%s-%s", bean.getParentId(), bean.getArticleId())).distinct()
            .collect(Collectors.toList());
        if (productLeadLinks.size() != assetIds.size()) {
            throw new CustomException("单据中不允许相同来源的同一产品信息");
        }
    }

    @Override
    public void createPrepose(ProductLead entity) {
        entity.setIdKey(getServiceClassName());
        super.createPrepose(entity);
    }

    @Override
    public void writeChild(ProductLead entity, String userId) {
        // 新增借出出库价格信息
        productLeadLinkService.saveLinkList(entity.getId(), entity.getProductLeadLinks());
        super.writeChild(entity, userId);
    }

    @Override
    public void submitToApprovalPostpose(String id, String processInstanceId) {
        super.submitToApprovalPostpose(id, processInstanceId);
        // 修改借出出库价格信息的状态
        productLeadLinkService.editStateByPId(id, FlowableChildStateEnum.IN_EXAMINE.getKey());
    }

    @Override
    public ProductLead getDataFromDb(String id) {
        ProductLead productLead = super.getDataFromDb(id);
        List<ProductLeadLink> assetPurchaseLinks = productLeadLinkService.selectByPId(productLead.getId());
        productLead.setProductLeadLinks(assetPurchaseLinks);
        return productLead;
    }

    @Override
    public ProductLead selectById(String id) {
        ProductLead productLead = super.selectById(id);
        // 获取借出出库产品信息
        productLead.getProductLeadLinks().forEach(bean -> {
            bean.setStateName(FlowableChildStateEnum.getStateName(bean.getState()));
        });
        List<ProductLeadLink> productLeadLinks = productLeadLinkService.selectByLeadLinkMation(id);
        productLead.setProductLeadLinks(productLeadLinks);
        productLead.setStateName(FlowableStateEnum.getStateName(productLead.getState()));
        iAuthUserService.setName(productLead, "createId", "createName");
        return productLead;
    }

    @Override
    public void revokePostpose(ProductLead entity) {
        super.revokePostpose(entity);
        productLeadLinkService.editStateByPId(entity.getId(), FlowableChildStateEnum.DRAFT.getKey());
    }

    @Override
    public void queryProductLeadToReturnGoodsById(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        ProductLead productLead = selectById(id);
        outputObject.setBean(productLead);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void insertLeadToRestitution(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        ProductRestitution restitutionMation = JSONUtil.toBean(inputObject.getParams().get("restitutionMation").toString(), ProductRestitution.class);
        ProductLead productLead = selectById(id);
        if (ObjectUtil.isNotEmpty(productLead)) {
            throw new CustomException("该数据不存在");
        }
        // 审核通过的可以转到归还入库
        if (FlowableStateEnum.PASS.getKey().equals(productLead.getState())) {
            String userId = inputObject.getLogParams().get("id").toString();
            restitutionMation.setLeadId(id);
            restitutionMation.setId(StrUtil.EMPTY);
            productRestitutionService.createEntity(restitutionMation, userId);
        } else {
            outputObject.setreturnMessage("状态错误，无法下达采购入库单.");
        }

    }
}
