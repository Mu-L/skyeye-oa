/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.afterseal.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.skyeye.accessory.classenum.UserStockPutOutType;
import com.skyeye.accessory.entity.ServiceUserStock;
import com.skyeye.accessory.service.ServiceUserStockService;
import com.skyeye.afterseal.classenum.AfterSealState;
import com.skyeye.afterseal.dao.SealFaultDao;
import com.skyeye.afterseal.entity.AfterSeal;
import com.skyeye.afterseal.entity.SealFault;
import com.skyeye.afterseal.entity.SealFaultUseMaterial;
import com.skyeye.afterseal.service.SealFaultService;
import com.skyeye.afterseal.service.SealFaultUseMaterialService;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.erp.service.IMaterialNormsService;
import com.skyeye.erp.service.IMaterialService;
import com.skyeye.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: SealFaultServiceImpl
 * @Description: 售后服务故障信息服务层
 * @author: skyeye云系列--卫志强
 * @date: 2023/8/12 17:39
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Service
@SkyeyeService(name = "售后服务故障信息", groupName = "售后工单", teamAuth = true)
public class SealFaultServiceImpl extends SkyeyeBusinessServiceImpl<SealFaultDao, SealFault> implements SealFaultService {

    @Autowired
    private SealFaultUseMaterialService sealFaultUseMaterialService;

    @Autowired
    private IMaterialNormsService iMaterialNormsService;

    @Autowired
    private IMaterialService iMaterialService;

    @Autowired
    private ServiceUserStockService serviceUserStockService;

    @Override
    protected List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        List<Map<String, Object>> beans = skyeyeBaseMapper.querySealFaultList(commonPageInfo);
        return beans;
    }

    @Override
    public void createPrepose(SealFault entity) {
        check(entity);
        getAllPrice(entity);
        Map<String, Object> business = BeanUtil.beanToMap(entity);
        String oddNumber = iCodeRuleService.getNextCodeByClassName(getServiceClassName(), business);
        entity.setOddNumber(oddNumber);
    }

    @Override
    public void updatePrepose(SealFault entity) {
        check(entity);
        getAllPrice(entity);
        // 回退数量
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        revertNum(entity.getId(), userId);
    }

    @Override
    protected void deletePreExecution(SealFault entity) {
        // 回退数量
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        revertNum(entity.getId(), userId);
    }

    @Override
    public void writePostpose(SealFault entity, String userId) {
        if (CollectionUtil.isNotEmpty(entity.getSealFaultUseMaterialList())) {
            sealFaultUseMaterialService.saveLinkList(entity.getId(), entity.getSealFaultUseMaterialList());
            // 出库
            for (SealFaultUseMaterial item : entity.getSealFaultUseMaterialList()) {
                serviceUserStockService.editMaterialNormsUserStock(userId, item.getMaterialId(), item.getNormsId(),
                    item.getOperNumber(), UserStockPutOutType.OUT.getKey());
            }
        }
        super.writePostpose(entity, userId);
    }

    private static void check(SealFault entity) {
        if (CollectionUtil.isEmpty(entity.getSealFaultUseMaterialList())) {
            return;
        }
        // 校验
        List<SealFaultUseMaterial> sealFaultUseMaterialList = entity.getSealFaultUseMaterialList();
        if (CollectionUtil.isNotEmpty(sealFaultUseMaterialList)) {
            List<String> normsIds = sealFaultUseMaterialList.stream().map(SealFaultUseMaterial::getNormsId).distinct().collect(Collectors.toList());
            if (sealFaultUseMaterialList.size() != normsIds.size()) {
                throw new CustomException("单据中不允许存在重复的产品规格信息");
            }
        }
    }

    private void revertNum(String id, String userId) {
        SealFault sealFault = selectById(id);
        if (CollectionUtil.isEmpty(sealFault.getSealFaultUseMaterialList())) {
            return;
        }
        sealFault.getSealFaultUseMaterialList().forEach(sealFaultUseMaterial -> {
            serviceUserStockService.editMaterialNormsUserStock(userId, sealFaultUseMaterial.getMaterialId(), sealFaultUseMaterial.getNormsId(),
                sealFaultUseMaterial.getOperNumber(), UserStockPutOutType.PUT.getKey());
        });
    }

    private void getAllPrice(SealFault entity) {
        String materialCost = sealFaultUseMaterialService.calcOrderAllTotalPrice(entity.getSealFaultUseMaterialList());
        entity.setMaterialCost(materialCost);
        String allPrice = CalculationUtil.add(CommonNumConstants.NUM_TWO, materialCost, entity.getCoverCost(), entity.getOtherCost());
        entity.setAllPrice(allPrice);
    }

    @Override
    public SealFault getDataFromDb(String id) {
        SealFault sealFault = super.getDataFromDb(id);

        List<SealFaultUseMaterial> sealFaultUseMaterialList = sealFaultUseMaterialService.selectByPId(sealFault.getId());
        sealFault.setSealFaultUseMaterialList(sealFaultUseMaterialList);

        return sealFault;
    }

    @Override
    public SealFault selectById(String id) {
        SealFault sealFault = super.selectById(id);
        if (CollectionUtil.isNotEmpty(sealFault.getSealFaultUseMaterialList())) {
            // 产品信息
            iMaterialService.setDataMation(sealFault.getSealFaultUseMaterialList(), SealFaultUseMaterial::getMaterialId);
            // 规格信息
            iMaterialNormsService.setDataMation(sealFault.getSealFaultUseMaterialList(), SealFaultUseMaterial::getNormsId);
            List<String> normsIds = sealFault.getSealFaultUseMaterialList().stream().map(SealFaultUseMaterial::getNormsId).collect(Collectors.toList());
            // 获取我的库存信息
            Map<String, ServiceUserStock> serviceUserStockMap = serviceUserStockService.queryUserStock(sealFault.getCreateId(), normsIds);
            sealFault.getSealFaultUseMaterialList().forEach(sealFaultUseMaterial -> {
                sealFaultUseMaterial.setServiceUserStock(serviceUserStockMap.get(sealFaultUseMaterial.getNormsId()));
            });
        }

        return sealFault;
    }

    @Override
    public Double getAllFinishedServiceTime(String startTime, String endTime) {
        // 查询已完成的售后工单，并统计总耗时
        MPJLambdaWrapper<SealFault> wrapper = new MPJLambdaWrapper<SealFault>()
            .innerJoin(AfterSeal.class, AfterSeal::getId, SealFault::getObjectId);
        wrapper.eq(MybatisPlusUtil.toColumns(AfterSeal::getState), AfterSealState.COMPLATE.getKey());
        if (StrUtil.isNotEmpty(startTime) && StrUtil.isNotEmpty(endTime)) {
            wrapper.applyFunc("date_format(%s, '%%Y-%%m-%%d') <= date_format({0}, '%%Y-%%m-%%d')", arg -> arg.accept(SealFault::getCreateTime), endTime)
                .applyFunc("date_format(%s, '%%Y-%%m-%%d') >= date_format({0}, '%%Y-%%m-%%d')", arg -> arg.accept(SealFault::getCreateTime), startTime);
        }
        List<SealFault> sealFaultUseMaterials = this.baseMapper.selectJoinList(SealFault.class, wrapper);
        if (CollectionUtil.isNotEmpty(sealFaultUseMaterials)) {
            return sealFaultUseMaterials.stream().mapToDouble(SealFault::getDoubleComWorkTime).sum();
        }
        return 0.0;
    }

    @Override
    public Map<String, Double> getAllFinishedServiceTime(List<String> userIds, String startTime, String endTime) {
        // 查询已完成的售后工单，并统计总耗时
        MPJLambdaWrapper<SealFault> wrapper = new MPJLambdaWrapper<SealFault>()
            .innerJoin(AfterSeal.class, AfterSeal::getId, SealFault::getObjectId);
        wrapper.eq(AfterSeal::getState, AfterSealState.COMPLATE.getKey());
        if (StrUtil.isNotEmpty(startTime) && StrUtil.isNotEmpty(endTime)) {
            wrapper.applyFunc("date_format(%s, '%%Y-%%m-%%d') <= date_format({0}, '%%Y-%%m-%%d')", arg -> arg.accept(SealFault::getCreateTime), endTime)
                .applyFunc("date_format(%s, '%%Y-%%m-%%d') >= date_format({0}, '%%Y-%%m-%%d')", arg -> arg.accept(SealFault::getCreateTime), startTime);
        }
        if (CollectionUtil.isNotEmpty(userIds)) {
            wrapper.in(SealFault::getCreateId, userIds);
        }
        List<SealFault> sealFaultUseMaterials = this.baseMapper.selectJoinList(SealFault.class, wrapper);
        if (CollectionUtil.isNotEmpty(sealFaultUseMaterials)) {
            return sealFaultUseMaterials.stream().collect(Collectors.groupingBy(SealFault::getCreateId,
                Collectors.summingDouble(SealFault::getDoubleComWorkTime)));
        }
        return new HashMap<>();
    }
}
