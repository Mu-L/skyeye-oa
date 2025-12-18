/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.contract.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.google.common.base.Joiner;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonCharConstants;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.FlowableStateEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.contract.classenum.CrmContractAuthEnum;
import com.skyeye.contract.classenum.CrmContractChildStateEnum;
import com.skyeye.contract.classenum.CrmContractStateEnum;
import com.skyeye.contract.dao.CrmContractDao;
import com.skyeye.contract.entity.CrmContract;
import com.skyeye.contract.entity.CrmContractChild;
import com.skyeye.contract.service.CrmContractChildService;
import com.skyeye.contract.service.CrmContractService;
import com.skyeye.erp.service.IMaterialNormsService;
import com.skyeye.erp.service.IMaterialService;
import com.skyeye.eve.contacts.service.IContactsService;
import com.skyeye.exception.CustomException;
import com.skyeye.organization.service.IDepmentService;
import com.skyeye.rest.project.service.IProProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: CrmContractServiceImpl
 * @Description: 客户合同管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/4/5 13:05
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Service
@SkyeyeService(name = "合同管理", groupName = "合同管理", flowable = true, teamAuth = true)
public class CrmContractServiceImpl extends SkyeyeBusinessServiceImpl<CrmContractDao, CrmContract> implements CrmContractService {

    @Autowired
    private IDepmentService iDepmentService;

    @Autowired
    private IContactsService iContactsService;

    @Autowired
    private CrmContractChildService crmContractChildService;

    @Autowired
    private IMaterialService iMaterialService;

    @Autowired
    private IMaterialNormsService iMaterialNormsService;

    @Autowired
    private IProProjectService iProProjectService;

    @Override
    public Class getAuthEnumClass() {
        return CrmContractAuthEnum.class;
    }

    @Override
    public List<String> getAuthPermissionKeyList() {
        return Arrays.asList(CrmContractAuthEnum.ADD.getKey(), CrmContractAuthEnum.EDIT.getKey(), CrmContractAuthEnum.DELETE.getKey(),
            CrmContractAuthEnum.REVOKE.getKey(), CrmContractAuthEnum.INVALID.getKey(), CrmContractAuthEnum.SUBMIT_TO_APPROVAL.getKey(), CrmContractAuthEnum.LIST.getKey(),
            CrmContractAuthEnum.PERFORM.getKey(), CrmContractAuthEnum.CLOSE.getKey(), CrmContractAuthEnum.LAY_ASIDE.getKey(), CrmContractAuthEnum.RECOVERY.getKey());
    }

    @Override
    public QueryWrapper<CrmContract> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<CrmContract> queryWrapper = super.getQueryWrapper(commonPageInfo);
        if (StrUtil.isNotEmpty(commonPageInfo.getObjectId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(CrmContract::getObjectId), commonPageInfo.getObjectId());
        }
        if (StrUtil.isNotEmpty(commonPageInfo.getFromId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(CrmContract::getFromId), commonPageInfo.getFromId());
        }
        if (StrUtil.isNotEmpty(commonPageInfo.getCustomParamsMapStr("projectId"))) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(CrmContract::getProjectId), commonPageInfo.getCustomParamsMapStr("projectId"));
        }
        return queryWrapper;
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        iProProjectService.setMationForMap(beans, "projectId", "projectMation");
        return beans;
    }

    @Override
    public void validatorEntity(CrmContract entity) {
        super.validatorEntity(entity);
        checkMaterialNorms(entity, false);
    }

    @Override
    public void createPrepose(CrmContract entity) {
        getTotalPrice(entity);
        if (CollectionUtil.isNotEmpty(entity.getCrmContractChildList())) {
            entity.setChildState(CrmContractChildStateEnum.PENDING_ORDER.getKey());
        }
        super.createPrepose(entity);
    }

    @Override
    public void updatePrepose(CrmContract entity) {
        getTotalPrice(entity);
        if (CollectionUtil.isNotEmpty(entity.getCrmContractChildList())) {
            entity.setChildState(CrmContractChildStateEnum.PENDING_ORDER.getKey());
        }
    }

    private void checkMaterialNorms(CrmContract entity, boolean setData) {
        if (StrUtil.isEmpty(entity.getFromId())) {
            return;
        }
        if (CollectionUtil.isEmpty(entity.getCrmContractChildList())) {
            throw new CustomException("合同下无商品信息，请确认.");
        }
        // TODO 后续关联单据来源查询判断
    }

    private void getTotalPrice(CrmContract entity) {
        // 计算关联的产品总价
        String totalPrice = crmContractChildService.calcOrderAllTotalPrice(entity.getCrmContractChildList());
        entity.setMaterialTotalPrice(totalPrice);
    }

    @Override
    public void writePostpose(CrmContract entity, String userId) {
        crmContractChildService.saveList(entity.getId(), entity.getCrmContractChildList());
        super.writePostpose(entity, userId);
    }

    @Override
    public void deletePostpose(String id) {
        crmContractChildService.deleteByParentId(id);
    }

    @Override
    public CrmContract getDataFromDb(String id) {
        CrmContract crmContract = super.getDataFromDb(id);
        // 设置合同商品信息
        List<CrmContractChild> crmContractChildList = crmContractChildService.selectByParentId(crmContract.getId());
        crmContract.setCrmContractChildList(crmContractChildList);
        return crmContract;
    }

    @Override
    public CrmContract selectById(String id) {
        CrmContract crmContract = super.selectById(id);
        Map<String, Object> department = iDepmentService.queryDataMationById(crmContract.getDepartmentId());
        crmContract.setDepartmentMation(department);
        // 联系人信息
        iContactsService.setDataMation(crmContract, CrmContract::getContacts);
        // 设置关联人员
        crmContract.setRelationUserMation(iAuthUserService.queryDataMationByIds(Joiner.on(CommonCharConstants.COMMA_MARK).join(crmContract.getRelationUserId())));
        // 设置合同商品详情信息
        iMaterialService.setDataMation(crmContract.getCrmContractChildList(), CrmContractChild::getMaterialId);
        iMaterialNormsService.setDataMation(crmContract.getCrmContractChildList(), CrmContractChild::getNormsId);
        // 关联项目
        iProProjectService.setDataMation(crmContract, CrmContract::getProjectId);
        return crmContract;
    }

    @Override
    public List<CrmContract> getDataFromDb(List<String> idList) {
        List<CrmContract> crmContractList = super.getDataFromDb(idList);
        // 设置合同商品信息
        Map<String, List<CrmContractChild>> childMap = crmContractChildService.selectByParentId(idList);
        crmContractList.forEach(crmContract -> {
            crmContract.setCrmContractChildList(childMap.get(crmContract.getId()));
        });
        return crmContractList;
    }

    @Override
    public List<CrmContract> selectByIds(String... ids) {
        List<CrmContract> crmContractList = super.selectByIds(ids);
        iDepmentService.setDataMation(crmContractList, CrmContract::getDepartmentId);
        // 联系人信息
        iContactsService.setDataMation(crmContractList, CrmContract::getContacts);
        // 设置关联人员
        List<String> relationUserIds = crmContractList.stream()
            .filter(bean -> CollectionUtil.isNotEmpty(bean.getRelationUserId()))
            .flatMap(norms -> norms.getRelationUserId().stream()).distinct().collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(relationUserIds)) {
            Map<String, Map<String, Object>> userMap = iAuthUserService.queryDataMationForMapByIds(Joiner.on(CommonCharConstants.COMMA_MARK).join(relationUserIds));
            crmContractList.forEach(crmContract -> {
                if (CollectionUtil.isEmpty(crmContract.getRelationUserId())) {
                    return;
                }
                List<Map<String, Object>> userMation = new ArrayList<>();
                crmContract.getRelationUserId().forEach(operatorId -> {
                    if (!userMap.containsKey(operatorId)) {
                        return;
                    }
                    userMation.add(userMap.get(operatorId));
                });
                crmContract.setRelationUserMation(userMation);
            });
        }

        // 设置合同商品详情信息
        List<String> materialIds = crmContractList.stream()
            .filter(bean -> CollectionUtil.isNotEmpty(bean.getCrmContractChildList()))
            .flatMap(farm -> farm.getCrmContractChildList().stream().map(CrmContractChild::getMaterialId)).distinct().collect(Collectors.toList());
        List<String> normsIds = crmContractList.stream()
            .filter(bean -> CollectionUtil.isNotEmpty(bean.getCrmContractChildList()))
            .flatMap(farm -> farm.getCrmContractChildList().stream().map(CrmContractChild::getNormsId)).distinct().collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(materialIds)) {
            Map<String, Map<String, Object>> materialMap = iMaterialService.queryDataMationForMapByIds(
                Joiner.on(CommonCharConstants.COMMA_MARK).join(materialIds));
            Map<String, Map<String, Object>> normsMap = iMaterialNormsService.queryDataMationForMapByIds(
                Joiner.on(CommonCharConstants.COMMA_MARK).join(normsIds));
            crmContractList.forEach(crmContract -> {
                if (CollectionUtil.isNotEmpty(crmContract.getCrmContractChildList())) {
                    // 合同商品明细不为空
                    crmContract.getCrmContractChildList().forEach(crmContractChild -> {
                        crmContractChild.setMaterialMation(materialMap.get(crmContractChild.getMaterialId()));
                        crmContractChild.setNormsMation(normsMap.get(crmContractChild.getNormsId()));
                    });
                }
            });
        }

        return crmContractList;
    }

    /**
     * 根据客户id获取合同列表用于下拉框选择
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryCrmContractListByObjectId(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String objectId = map.get("objectId").toString();
        if (StrUtil.isEmpty(objectId)) {
            return;
        }
        QueryWrapper<CrmContract> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(CrmContract::getObjectId), objectId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(CrmContract::getState), CrmContractStateEnum.EXECUTING.getKey());
        List<CrmContract> crmContractList = list(queryWrapper);
        crmContractList.forEach(crmContract -> {
            crmContract.setName(crmContract.getTitle());
        });
        outputObject.setBeans(crmContractList);
        outputObject.settotal(crmContractList.size());
    }

    /**
     * 合同执行
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void performCrmContract(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        CrmContract crmContract = selectById(id);
        String userId = inputObject.getLogParams().get(CommonConstants.ID).toString();
        checkAuthPermission(crmContract, userId, CommonNumConstants.NUM_SEVEN);
        if (crmContract.getState().equals(FlowableStateEnum.PASS.getKey())) {
            // 审核通过可以执行
            UpdateWrapper<CrmContract> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq(CommonConstants.ID, id);
            updateWrapper.set(MybatisPlusUtil.toColumns(CrmContract::getState), CrmContractStateEnum.EXECUTING.getKey());
            update(updateWrapper);
            refreshCache(id);
        } else {
            outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
        }
    }

    /**
     * 合同关闭
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void closeCrmContract(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        CrmContract crmContract = selectById(id);
        String userId = inputObject.getLogParams().get(CommonConstants.ID).toString();
        checkAuthPermission(crmContract, userId, CommonNumConstants.NUM_EIGHT);
        if (crmContract.getState().equals(CrmContractStateEnum.EXECUTING.getKey())) {
            // 执行中可以关闭
            UpdateWrapper<CrmContract> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq(CommonConstants.ID, id);
            updateWrapper.set(MybatisPlusUtil.toColumns(CrmContract::getState), CrmContractStateEnum.CLOSE.getKey());
            update(updateWrapper);
            refreshCache(id);
        } else {
            outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
        }
    }

    /**
     * 合同搁置
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void shelveCrmContract(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        CrmContract crmContract = selectById(id);
        String userId = inputObject.getLogParams().get(CommonConstants.ID).toString();
        checkAuthPermission(crmContract, userId, CommonNumConstants.NUM_NINE);
        if (crmContract.getState().equals(CrmContractStateEnum.EXECUTING.getKey())) {
            // 执行中可以搁置
            UpdateWrapper<CrmContract> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq(CommonConstants.ID, id);
            updateWrapper.set(MybatisPlusUtil.toColumns(CrmContract::getState), CrmContractStateEnum.LAY_ASIDE.getKey());
            update(updateWrapper);
            refreshCache(id);
        } else {
            outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
        }
    }

    /**
     * 合同恢复
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void recoveryCrmContract(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        CrmContract crmContract = selectById(id);
        String userId = inputObject.getLogParams().get(CommonConstants.ID).toString();
        checkAuthPermission(crmContract, userId, CommonNumConstants.NUM_TEN);
        if (crmContract.getState().equals(CrmContractStateEnum.LAY_ASIDE.getKey())) {
            // 搁置中可以恢复
            UpdateWrapper<CrmContract> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq(CommonConstants.ID, id);
            updateWrapper.set(MybatisPlusUtil.toColumns(CrmContract::getState), CrmContractStateEnum.EXECUTING.getKey());
            update(updateWrapper);
            refreshCache(id);
        } else {
            outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
        }
    }

    /**
     * 根据所属第三方业务数据id查询合同信息
     *
     * @param objectId 所属第三方业务数据id
     * @return
     */
    @Override
    public List<CrmContract> queryCrmContractListByObjectId(String objectId) {
        QueryWrapper<CrmContract> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(CrmContract::getObjectId), objectId);
        return list(queryWrapper);
    }

    @Override
    public void updatePaymentPrice(String id, String paymentPrice) {
        CrmContract crmContract = selectById(id);
        if (StrUtil.equals(crmContract.getState(), CrmContractStateEnum.EXECUTING.getKey())) {
            // 只有执行中的合同才可以进行回款
            paymentPrice = CalculationUtil.add(CommonNumConstants.NUM_TWO,
                StrUtil.isEmpty(crmContract.getPaymentPrice()) ? "0" : crmContract.getPaymentPrice(),
                paymentPrice);
            UpdateWrapper<CrmContract> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq(CommonConstants.ID, id);
            updateWrapper.set(MybatisPlusUtil.toColumns(CrmContract::getPaymentPrice), paymentPrice);
            update(updateWrapper);
            refreshCache(id);
        } else {
            throw new CustomException("只有执行中的合同才可以进行回款操作。");
        }
    }

    @Override
    public void updateInvoicePrice(String id, String invoicePrice) {
        CrmContract crmContract = selectById(id);
        invoicePrice = CalculationUtil.add(CommonNumConstants.NUM_TWO,
            StrUtil.isEmpty(crmContract.getInvoicePrice()) ? "0" : crmContract.getInvoicePrice(),
            invoicePrice);
        UpdateWrapper<CrmContract> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(CrmContract::getInvoicePrice), invoicePrice);
        update(updateWrapper);
        refreshCache(id);
    }

    @Override
    public void approvalEndIsSuccess(CrmContract entity) {
        entity = selectById(entity.getId());
        checkMaterialNorms(entity, true);
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void editChildState(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String id = params.get("id").toString();
        String childState = params.get("childState").toString();
        UpdateWrapper<CrmContract> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(CrmContract::getChildState), childState);
        update(updateWrapper);
        refreshCache(id);
    }

}
