/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.request.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.enumeration.FlowableStateEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.MapUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.contract.classenum.SupplierContractFromType;
import com.skyeye.contract.entity.SupplierContract;
import com.skyeye.contract.service.SupplierContractService;
import com.skyeye.exception.CustomException;
import com.skyeye.material.entity.MaterialNorms;
import com.skyeye.material.service.MaterialService;
import com.skyeye.request.classenum.PurchaseRequestChildInquiry;
import com.skyeye.request.classenum.PurchaseRequestInquiryState;
import com.skyeye.request.classenum.PurchaseRequestStateEnum;
import com.skyeye.request.dao.PurchaseRequestDao;
import com.skyeye.request.entity.PurchaseRequest;
import com.skyeye.request.entity.PurchaseRequestChild;
import com.skyeye.request.entity.PurchaseRequestFixedChild;
import com.skyeye.request.entity.PurchaseRequestInquiryChild;
import com.skyeye.request.service.PurchaseRequestChildService;
import com.skyeye.request.service.PurchaseRequestFixedChildService;
import com.skyeye.request.service.PurchaseRequestInquiryChildService;
import com.skyeye.request.service.PurchaseRequestService;
import com.skyeye.rest.project.service.IProProjectService;
import com.skyeye.supplier.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: PurchaseRequestServiceImpl
 * @Description: 采购申请服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/5/22 11:05
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "采购申请", groupName = "采购申请", flowable = true)
public class PurchaseRequestServiceImpl extends SkyeyeBusinessServiceImpl<PurchaseRequestDao, PurchaseRequest> implements PurchaseRequestService {

    @Autowired
    private PurchaseRequestChildService purchaseRequestChildService;

    @Autowired
    private PurchaseRequestInquiryChildService purchaseRequestInquiryChildService;

    @Autowired
    private PurchaseRequestFixedChildService purchaseRequestFixedChildService;

    @Autowired
    private SupplierContractService supplierContractService;

    @Autowired
    private MaterialService materialService;

    @Autowired
    private SupplierService supplierService;

    @Autowired
    private IProProjectService iProProjectService;

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        iProProjectService.setMationForMap(beans, "projectId", "projectMation");
        return beans;
    }

    @Override
    public void createPrepose(PurchaseRequest entity) {
        super.createPrepose(entity);
        // 设置询价类型
        Integer inquiryState = PurchaseRequestInquiryState.NOT_INQUIRY.getKey();
        for (PurchaseRequestChild purchaseRequestChild : entity.getPurchaseRequestChildList()) {
            if (purchaseRequestChild.getNeedInquiry() == PurchaseRequestChildInquiry.INQUIRY.getKey()) {
                inquiryState = PurchaseRequestInquiryState.WAIT_INQUIRY.getKey();
            }
        }
        entity.setInquiryState(inquiryState);
        // 设置商品为使用中
        entity.getPurchaseRequestChildList().forEach(purchaseRequestChild -> {
            materialService.setUsed(purchaseRequestChild.getMaterialId());
        });
        getTotalPrice(entity);
    }

    @Override
    public void updatePrepose(PurchaseRequest entity) {
        super.updatePrepose(entity);
        // 设置询价类型
        Integer inquiryState = PurchaseRequestInquiryState.NOT_INQUIRY.getKey();
        for (PurchaseRequestChild purchaseRequestChild : entity.getPurchaseRequestChildList()) {
            if (purchaseRequestChild.getNeedInquiry() == PurchaseRequestChildInquiry.INQUIRY.getKey()) {
                inquiryState = PurchaseRequestInquiryState.WAIT_INQUIRY.getKey();
            }
        }
        entity.setInquiryState(inquiryState);
        getTotalPrice(entity);
    }

    private void getTotalPrice(PurchaseRequest entity) {
        // 计算关联的产品总价
        String totalPrice = purchaseRequestChildService.calcOrderAllTotalPrice(entity.getPurchaseRequestChildList());
        entity.setTotalPrice(totalPrice);
    }

    @Override
    public void writePostpose(PurchaseRequest entity, String userId) {
        purchaseRequestChildService.saveList(entity.getId(), entity.getPurchaseRequestChildList());
        super.writePostpose(entity, userId);
    }

    @Override
    public void deletePostpose(String id) {
        purchaseRequestChildService.deleteByParentId(id);
        purchaseRequestInquiryChildService.deleteByParentId(id);
    }

    @Override
    public PurchaseRequest getDataFromDb(String id) {
        PurchaseRequest purchaseRequest = super.getDataFromDb(id);
        // 商品明细信息
        List<PurchaseRequestChild> purchaseRequestChildList = purchaseRequestChildService.selectByParentId(purchaseRequest.getId());
        purchaseRequest.setPurchaseRequestChildList(purchaseRequestChildList);

        // 询价信息
        List<PurchaseRequestInquiryChild> purchaseRequestInquiryChildList = purchaseRequestInquiryChildService.selectByParentId(purchaseRequest.getId());
        purchaseRequest.setPurchaseRequestInquiryChildList(purchaseRequestInquiryChildList);

        // 定价信息
        List<PurchaseRequestFixedChild> purchaseRequestFixedChildList = purchaseRequestFixedChildService.selectByParentId(purchaseRequest.getId());
        purchaseRequest.setPurchaseRequestFixedChildList(purchaseRequestFixedChildList);
        return purchaseRequest;
    }

    @Override
    public PurchaseRequest selectById(String id) {
        PurchaseRequest purchaseRequest = super.selectById(id);
        // 设置子单据的产品信息
        materialService.setDataMation(purchaseRequest.getPurchaseRequestChildList(), PurchaseRequestChild::getMaterialId);
        purchaseRequest.getPurchaseRequestChildList().forEach(purchaseRequestChild -> {
            MaterialNorms norms = purchaseRequestChild.getMaterialMation().getMaterialNorms()
                .stream().filter(bean -> StrUtil.equals(purchaseRequestChild.getNormsId(), bean.getId())).findFirst().orElse(null);
            purchaseRequestChild.setNormsMation(norms);
            // 设置子单据的询价状态信息
            Map<String, Object> needInquiryMation = new HashMap<>();
            needInquiryMation.put("name", PurchaseRequestChildInquiry.getName(purchaseRequestChild.getNeedInquiry()));
            purchaseRequestChild.setNeedInquiryMation(needInquiryMation);
        });

        if (purchaseRequest.getInquiryState() != PurchaseRequestInquiryState.NOT_INQUIRY.getKey()) {
            // 需要询价的申请单
            if (purchaseRequest.getInquiryState() == PurchaseRequestInquiryState.INQUIRYING.getKey()) {
                // 询价中
                // 采购申请明细定价信息
                List<PurchaseRequestFixedChild> purchaseRequestFixedChildList = JSONUtil.toList(
                    JSONUtil.toJsonStr(purchaseRequest.getPurchaseRequestChildList()), PurchaseRequestFixedChild.class
                );
                purchaseRequest.setPurchaseRequestFixedChildList(purchaseRequestFixedChildList);
            }
        }
        if (CollectionUtil.isNotEmpty(purchaseRequest.getPurchaseRequestFixedChildList())) {
            // 设置定价明细信息
            materialService.setDataMation(purchaseRequest.getPurchaseRequestFixedChildList(), PurchaseRequestFixedChild::getMaterialId);
            purchaseRequest.getPurchaseRequestFixedChildList().forEach(purchaseRequestFixedChild -> {
                MaterialNorms norms = purchaseRequestFixedChild.getMaterialMation().getMaterialNorms()
                    .stream().filter(bean -> StrUtil.equals(purchaseRequestFixedChild.getNormsId(), bean.getId())).findFirst().orElse(null);
                purchaseRequestFixedChild.setNormsMation(norms);
            });
        }

        // 定价供应商
        supplierService.setDataMation(purchaseRequest.getPurchaseRequestFixedChildList(), PurchaseRequestFixedChild::getLastSupplierId);

        // 设置询价明细的产品信息
        materialService.setDataMation(purchaseRequest.getPurchaseRequestInquiryChildList(), PurchaseRequestInquiryChild::getMaterialId);
        purchaseRequest.getPurchaseRequestInquiryChildList().forEach(purchaseRequestInquiryChild -> {
            MaterialNorms norms = purchaseRequestInquiryChild.getMaterialMation().getMaterialNorms()
                .stream().filter(bean -> StrUtil.equals(purchaseRequestInquiryChild.getNormsId(), bean.getId())).findFirst().orElse(null);
            purchaseRequestInquiryChild.setNormsMation(norms);
        });
        supplierService.setDataMation(purchaseRequest.getPurchaseRequestInquiryChildList(), PurchaseRequestInquiryChild::getSupplierId);

        // 设置子单据开票类型
        iSysDictDataService.setDataMation(purchaseRequest.getPurchaseRequestInquiryChildList(), PurchaseRequestInquiryChild::getTypeId);
        // 设置项目信息
        iProProjectService.setDataMation(purchaseRequest, PurchaseRequest::getProjectId);
        // 设置定价人员信息
        iAuthUserService.setDataMation(purchaseRequest, PurchaseRequest::getFixedPriceUserId);
        return purchaseRequest;
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void inquiryPurchaseRequest(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String id = params.get("id").toString();
        PurchaseRequest purchaseRequest = selectById(id);
        if (!purchaseRequest.getState().equals(FlowableStateEnum.PASS.getKey())) {
            throw new CustomException("只有审批通过的申请单可以进行询价.");
        }
        if (PurchaseRequestInquiryState.NOT_INQUIRY.getKey() == purchaseRequest.getInquiryState()) {
            // 无需询价
            throw new CustomException("该申请单无需进行询价.");
        }
        List<String> oldNormsId = purchaseRequest.getPurchaseRequestChildList().stream()
            .map(PurchaseRequestChild::getNormsId).collect(Collectors.toList());

        List<PurchaseRequestInquiryChild> purchaseRequestInquiryChildList = JSONUtil.toList(
            params.get("purchaseRequestInquiryChildList").toString(), PurchaseRequestInquiryChild.class
        );
        purchaseRequestInquiryChildList.forEach(purchaseRequestInquiryChild -> {
            if (!oldNormsId.contains(purchaseRequestInquiryChild.getNormsId())) {
                throw new CustomException("存在申请单中不包含的商品规格信息，请确认.");
            }
        });
        purchaseRequestInquiryChildService.saveList(id, purchaseRequestInquiryChildList);
        // 设置为询价中
        editInquiryStateById(id, PurchaseRequestInquiryState.INQUIRYING.getKey(), null);
    }

    private void editInquiryStateById(String id, Integer inquiryState, String fixedPriceUserId) {
        UpdateWrapper<PurchaseRequest> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(PurchaseRequest::getInquiryState), inquiryState);
        if (StrUtil.isNotEmpty(fixedPriceUserId)) {
            updateWrapper.set(MybatisPlusUtil.toColumns(PurchaseRequest::getFixedPriceUserId), fixedPriceUserId);
        }
        update(updateWrapper);
        refreshCache(id);
    }

    @Override
    public void approvalEndIsSuccess(PurchaseRequest entity) {
        // 审批通过的且无需询价的需要将采购申请明细转到定价信息表中
        if (PurchaseRequestInquiryState.NOT_INQUIRY.getKey() == entity.getInquiryState()) {
            PurchaseRequest purchaseRequest = selectById(entity.getId());
            List<PurchaseRequestFixedChild> purchaseRequestFixedChildList = JSONUtil.toList(
                JSONUtil.toJsonStr(purchaseRequest.getPurchaseRequestChildList()), PurchaseRequestFixedChild.class
            );
            // 保存定价信息
            purchaseRequestFixedChildService.saveList(entity.getId(), purchaseRequestFixedChildList);
        }
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void fixedPricePurchaseRequest(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String id = params.get("id").toString();
        PurchaseRequest purchaseRequest = selectById(id);
        if (!purchaseRequest.getState().equals(FlowableStateEnum.PASS.getKey())) {
            throw new CustomException("只有审批通过的申请单可以进行定价.");
        }
        if (PurchaseRequestInquiryState.NOT_INQUIRY.getKey() == purchaseRequest.getInquiryState()) {
            // 无需定价
            throw new CustomException("该申请单无需进行定价.");
        }
        // 采购申请明细定价信息
        List<PurchaseRequestFixedChild> purchaseRequestFixedChildList = JSONUtil.toList(
            params.get("purchaseRequestFixedChildList").toString(), PurchaseRequestFixedChild.class
        );
        // 保存定价信息
        purchaseRequestFixedChildService.saveList(id, purchaseRequestFixedChildList);
        // 设置为询价完毕
        String fixedPriceUserId = params.get("fixedPriceUserId").toString();
        editInquiryStateById(id, PurchaseRequestInquiryState.COMPLATE_INQUIRY.getKey(), fixedPriceUserId);
    }

    @Override
    public void queryPurchaseRequestTransferContract(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String id = params.get("id").toString();
        PurchaseRequest purchaseRequest = getPurchaseRequest(id);
        // 根据供应商进行分组生成合同
        Map<String, List<PurchaseRequestFixedChild>> requestChildMap = purchaseRequest.getPurchaseRequestFixedChildList().stream()
            .collect(Collectors.groupingBy(bean -> {
                return StrUtil.isEmpty(bean.getLastSupplierId()) ? "-" : bean.getLastSupplierId();
            }));

        outputObject.setBean(requestChildMap);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    private PurchaseRequest getPurchaseRequest(String id) {
        PurchaseRequest purchaseRequest = selectById(id);
        if (purchaseRequest.getState().equals(PurchaseRequestStateEnum.PROCUREMENT_COMPLETED.getKey())) {
            throw new CustomException("该申请单已完成采购合同申请，无法再次进行合同");
        }
        return getPurchaseRequest(purchaseRequest);
    }

    private PurchaseRequest getPurchaseRequest(PurchaseRequest purchaseRequest) {
        // 获取已经签订合同的商品信息
        Map<String, Integer> executeNum = supplierContractService.calcMaterialNormsNumByFromId(purchaseRequest.getId());
        if (CollectionUtil.isEmpty(purchaseRequest.getPurchaseRequestFixedChildList())) {
            purchaseRequest.setPurchaseRequestFixedChildList(CollectionUtil.newArrayList());
            return purchaseRequest;
        }
        purchaseRequest.getPurchaseRequestFixedChildList().forEach(purchaseRequestFixedChild -> {
            Integer surplusNum = purchaseRequestFixedChild.getOperNumber()
                - (executeNum.containsKey(purchaseRequestFixedChild.getNormsId()) ? executeNum.get(purchaseRequestFixedChild.getNormsId()) : 0);
            // 设置未签合同的商品数量
            purchaseRequestFixedChild.setOperNumber(surplusNum);
        });
        // 过滤掉申请数量为0的进行生成合同
        purchaseRequest.setPurchaseRequestFixedChildList(purchaseRequest.getPurchaseRequestFixedChildList().stream()
            .filter(purchaseRequestChild -> purchaseRequestChild.getOperNumber() > 0).collect(Collectors.toList()));
        return purchaseRequest;
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void purchaseRequestToContract(InputObject inputObject, OutputObject outputObject) {
        SupplierContract supplierContract = inputObject.getParams(SupplierContract.class);
        supplierContract.setFromId(supplierContract.getId());
        supplierContract.setFromTypeId(SupplierContractFromType.PURCHASE_REQUEST.getKey());
        supplierContract.setId(null);
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        // 保存供应商合同
        supplierContractService.createEntity(supplierContract, userId, false);
    }

    @Override
    public void setRequestMationByFromId(List<Map<String, Object>> beans, String idKey, String mationKey) {
        if (CollectionUtil.isEmpty(beans)) {
            return;
        }
        List<String> ids = beans.stream().filter(bean -> !MapUtil.checkKeyIsNull(bean, idKey))
            .map(bean -> bean.get(idKey).toString()).distinct().collect(Collectors.toList());
        if (CollectionUtils.isEmpty(ids)) {
            return;
        }
        QueryWrapper<PurchaseRequest> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(CommonConstants.ID, ids);
        List<PurchaseRequest> purchaseRequestList = list(queryWrapper);
        Map<String, PurchaseRequest> purchaseRequestMap = purchaseRequestList.stream()
            .collect(Collectors.toMap(PurchaseRequest::getId, bean -> bean));
        for (Map<String, Object> bean : beans) {
            if (!MapUtil.checkKeyIsNull(bean, idKey)) {
                PurchaseRequest entity = purchaseRequestMap.get(bean.get(idKey).toString());
                if (ObjectUtil.isEmpty(entity)) {
                    continue;
                }
                bean.put(mationKey, entity);
            }
        }
    }
}
