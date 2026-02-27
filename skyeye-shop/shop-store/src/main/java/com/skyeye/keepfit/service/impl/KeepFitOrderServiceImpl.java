/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.keepfit.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.yulichang.toolkit.JoinWrappers;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.google.common.base.Joiner;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonCharConstants;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.erp.service.IMaterialNormsService;
import com.skyeye.erp.service.IMaterialService;
import com.skyeye.exception.CustomException;
import com.skyeye.keepfit.classenum.KeepFitOrderState;
import com.skyeye.keepfit.classenum.KeepFitOrderUserType;
import com.skyeye.keepfit.dao.KeepFitOrderDao;
import com.skyeye.keepfit.entity.KeepFitOrder;
import com.skyeye.keepfit.entity.KeepFitOrderConsume;
import com.skyeye.keepfit.service.KeepFitOrderConsumeService;
import com.skyeye.keepfit.service.KeepFitOrderService;
import com.skyeye.meal.classenum.ShopMealOrderType;
import com.skyeye.meal.entity.MealOrder;
import com.skyeye.meal.entity.MealOrderChild;
import com.skyeye.meal.entity.ShopMeal;
import com.skyeye.meal.service.MealOrderChildService;
import com.skyeye.meal.service.ShopMealService;
import com.skyeye.rest.norms.service.IMaterialNormsCodeService;
import com.skyeye.service.MemberService;
import com.skyeye.store.service.ShopStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: KeepFitOrderServiceImpl
 * @Description: 保养订单服务类
 * @author: skyeye云系列--卫志强
 * @date: 2022/2/8 15:14
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "保养订单管理", groupName = "保养订单管理")
public class KeepFitOrderServiceImpl extends SkyeyeBusinessServiceImpl<KeepFitOrderDao, KeepFitOrder> implements KeepFitOrderService {

    @Autowired
    private MealOrderChildService mealOrderChildService;

    @Autowired
    private IMaterialService iMaterialService;

    @Autowired
    private IMaterialNormsService iMaterialNormsService;

    @Autowired
    private ShopStoreService shopStoreService;

    @Autowired
    private KeepFitOrderConsumeService keepFitOrderConsumeService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private ShopMealService shopMealService;

    @Autowired
    private IMaterialNormsCodeService iMaterialNormsCodeService;

    @Override
    public QueryWrapper<KeepFitOrder> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<KeepFitOrder> queryWrapper = super.getQueryWrapper(commonPageInfo);
        if (StrUtil.equals(commonPageInfo.getType(), "Store")) {
            // 门店下的订单
            queryWrapper.eq(MybatisPlusUtil.toColumns(KeepFitOrder::getStoreId), commonPageInfo.getHolderId());
        } else if (StrUtil.equals(commonPageInfo.getType(), "All")) {
            // 所有订单
        }
        return queryWrapper;
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        shopStoreService.setMationForMap(beans, "storeId", "storeMation");
        memberService.setMationForMap(beans, "objectId", "objectMation");
        return beans;
    }

    @Override
    public void createPrepose(KeepFitOrder entity) {
        if (entity.getType() == ShopMealOrderType.MEMBER.getKey()) {
            // 会员线上下单
            if (StrUtil.isEmpty(entity.getOnlineDay())) {
                throw new CustomException("线上预约日期不能为空.");
            }
            if (StrUtil.isEmpty(entity.getOnlineTime())) {
                throw new CustomException("线上预约时间段不能为空.");
            }
        }
        if (entity.getUserType() == KeepFitOrderUserType.MEMBER.getKey()) {
            if (StrUtil.isEmpty(entity.getObjectId())) {
                throw new CustomException("会员信息不能为空.");
            }
        }
        if (entity.getUserType() == KeepFitOrderUserType.MEMBER.getKey()) {
            QueryWrapper<KeepFitOrder> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq(MybatisPlusUtil.toColumns(KeepFitOrder::getMaterialId), entity.getMaterialId());
            queryWrapper.eq(MybatisPlusUtil.toColumns(KeepFitOrder::getNormsId), entity.getNormsId());
            // 商品信息
            Map<String, Object> material = iMaterialService.queryDataMationById(entity.getMaterialId());
            if (CollectionUtil.isEmpty(material)) {
                throw new CustomException("商品信息不存在");
            }
            Integer itemCode = Integer.parseInt(material.get("itemCode").toString());
            if (itemCode == 1) {
                // 一物一码
                if (StrUtil.isEmpty(entity.getCodeNum())) {
                    throw new CustomException("请输入条形码");
                }
            }
            if (StrUtil.isNotEmpty(entity.getCodeNum())) {
                queryWrapper.eq(MybatisPlusUtil.toColumns(KeepFitOrder::getCodeNum), entity.getCodeNum());
            }
            List<Integer> stateList = Arrays.asList(KeepFitOrderState.NO_PAYING.getKey(), KeepFitOrderState.PAY.getKey());
            queryWrapper.in(MybatisPlusUtil.toColumns(KeepFitOrder::getState), stateList);
            KeepFitOrder keepFitOrder = getOne(queryWrapper, false);
            if (ObjectUtil.isNotEmpty(keepFitOrder)) {
                throw new CustomException("该商品正在保养中/未完成核销，请勿重复下单");
            }
        }
        checkNormsCodeAndOutbound(entity, true);
        entity.setState(KeepFitOrderState.NO_PAYING.getKey());
        Map<String, Object> business = BeanUtil.beanToMap(entity);
        String oddNumber = iCodeRuleService.getNextCodeByClassName(getClass().getName(), business);
        entity.setOddNumber(oddNumber);
        // 计算应付金额
        String payablePrice = keepFitOrderConsumeService.calculationTotalPrice(entity.getConsumeMationList());
        payablePrice = CalculationUtil.add(payablePrice, entity.getServicePrice(), CommonNumConstants.NUM_TWO);
        entity.setPayablePrice(payablePrice);
    }

    /**
     * 校验商品规格条形码与单据明细的参数是否匹配
     *
     * @param entity
     * @param onlyCheck 是否只进行校验，true：是；false：否
     */
    private List<String> checkNormsCodeAndOutbound(KeepFitOrder entity, Boolean onlyCheck) {
        if (CollectionUtil.isEmpty(entity.getConsumeMationList())) {
            return CollectionUtil.newArrayList();
        }
        List<String> materialIdList = entity.getConsumeMationList().stream().map(KeepFitOrderConsume::getMaterialId).distinct().collect(Collectors.toList());
        List<String> normsIdList = entity.getConsumeMationList().stream().map(KeepFitOrderConsume::getNormsId).distinct().collect(Collectors.toList());
        Map<String, Map<String, Object>> materialMap = iMaterialService.queryDataMationForMapByIds(Joiner.on(CommonCharConstants.COMMA_MARK).join(materialIdList));
        Map<String, Map<String, Object>> normsMap = iMaterialNormsService.queryDataMationForMapByIds(Joiner.on(CommonCharConstants.COMMA_MARK).join(normsIdList));
        // 所有需要进行出库的条形码编码
        List<String> allNormsCodeList = new ArrayList<>();
        int allCodeNum = checkErpOrderItemDetail(entity, materialMap, normsMap, allNormsCodeList);
        if (CollectionUtil.isNotEmpty(allNormsCodeList)) {
            allNormsCodeList = allNormsCodeList.stream().distinct().collect(Collectors.toList());
            if (allCodeNum != allNormsCodeList.size()) {
                throw new CustomException("商品明细中存在相同的条形码编号，请确认");
            }
            // 从数据库查询入库状态的条形码信息
            List<Map<String, Object>> materialNormsCodeList = iMaterialNormsCodeService.queryMaterialNormsCode(entity.getStoreId(), allNormsCodeList,
                CommonNumConstants.NUM_ONE);
            List<String> inSqlNormsCodeList = materialNormsCodeList.stream().map(bean -> bean.get("codeNum").toString()).collect(Collectors.toList());
            // 获取所有前端传递过来的条形码信息，求差集(在入参中有，但是在数据库中不包含的条形码信息)
            List<String> diffList = allNormsCodeList.stream()
                .filter(num -> !inSqlNormsCodeList.contains(num)).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(diffList)) {
                throw new CustomException(
                    String.format(Locale.ROOT, "编码【%s】不存在/未入库/已经出库，请确认", Joiner.on(CommonCharConstants.COMMA_MARK).join(diffList)));
            }
            // 判断条形码是否就在出库仓库里面
            Map<String, Map<String, Object>> materialNormsCodeMap = materialNormsCodeList.stream()
                .collect(Collectors.toMap(bean -> bean.get("codeNum").toString(), bean -> bean));
            for (KeepFitOrderConsume keepFitOrderConsume : entity.getConsumeMationList()) {
                Map<String, Object> material = materialMap.get(keepFitOrderConsume.getMaterialId());
                Integer itemCode = Integer.parseInt(material.get("itemCode").toString());
                if (itemCode == 1) {
                    // 一物一码
                    keepFitOrderConsume.getNormsCodeList().forEach(normsCode -> {
                        Map<String, Object> materialNormsCode = materialNormsCodeMap.get(normsCode);
                        if (!StrUtil.equals(materialNormsCode.get("storeId").toString(), entity.getStoreId())) {
                            throw new CustomException(
                                String.format(Locale.ROOT, "条形码【%s】不在指定门店，请确认", normsCode));
                        }
                        if (!StrUtil.equals(materialNormsCode.get("normsId").toString(), keepFitOrderConsume.getNormsId())) {
                            throw new CustomException(String.format(Locale.ROOT, "条形码【%s】与商品规格不匹配，请确认", normsCode));
                        }
                    });
                }
            }
            if (!onlyCheck) {
                // 批量修改条形码信息
                List<String> ids = materialNormsCodeList.stream().map(bean -> bean.get("id").toString()).collect(Collectors.toList());
                iMaterialNormsCodeService.editStoreMaterialNormsCodeUseState(ids, CommonNumConstants.NUM_TWO);
            }
        }
        return allNormsCodeList;
    }

    private int checkErpOrderItemDetail(KeepFitOrder entity, Map<String, Map<String, Object>> materialMap, Map<String, Map<String, Object>> normsMap, List<String> allNormsCodeList) {
        int allCodeNum = 0;
        for (KeepFitOrderConsume keepFitOrderConsume : entity.getConsumeMationList()) {
            Map<String, Object> material = materialMap.get(keepFitOrderConsume.getMaterialId());
            Map<String, Object> norms = normsMap.get(keepFitOrderConsume.getNormsId());
            if (CalculationUtil.compareTo(keepFitOrderConsume.getOperNumber(), CommonNumConstants.NUM_ZERO.toString(), CommonNumConstants.NUM_TWO, RoundingMode.UP) <= 0) {
                throw new CustomException(
                    String.format(Locale.ROOT, "耗材【%s】【%s】的数量不能为0，请确认", material.get("name").toString(), norms.get("name").toString()));
            }

            Integer itemCode = Integer.parseInt(material.get("itemCode").toString());
            if (itemCode == 1) {
                // 一物一码
                // 过滤掉空的，并且去重
                List<String> normsCodeList = Arrays.asList(keepFitOrderConsume.getCodeNum().split("\n")).stream()
                    .filter(str -> StrUtil.isNotEmpty(str)).distinct().collect(Collectors.toList());
                String codeCountStr = String.valueOf(normsCodeList.size());
                if (CalculationUtil.compareTo(keepFitOrderConsume.getOperNumber(), codeCountStr, CommonNumConstants.NUM_TWO, RoundingMode.UP) != 0) {
                    throw new CustomException(
                        String.format(Locale.ROOT, "耗材【%s】【%s】的条形码数量与明细数量不一致，请确认", material.get("name").toString(), norms.get("name").toString()));
                }
                allCodeNum += normsCodeList.size();
                keepFitOrderConsume.setNormsCodeList(normsCodeList);
                allNormsCodeList.addAll(normsCodeList);
            }
        }
        return allCodeNum;
    }

    @Override
    public void createPostpose(KeepFitOrder entity, String userId) {
        // 保存耗材信息
        keepFitOrderConsumeService.saveList(entity.getId(), entity.getConsumeMationList());
    }

    @Override
    public void deletePostpose(String id) {
        keepFitOrderConsumeService.deleteByOrderId(id);
    }

    @Override
    public KeepFitOrder getDataFromDb(String id) {
        KeepFitOrder keepFitOrder = super.getDataFromDb(id);
        keepFitOrder.setConsumeMationList(keepFitOrderConsumeService.selectByOrderId(id));
        return keepFitOrder;
    }

    @Override
    public KeepFitOrder selectById(String id) {
        KeepFitOrder keepFitOrder = super.selectById(id);

        Map<String, Object> codeNumMation = new HashMap<>();
        codeNumMation.put("name", keepFitOrder.getCodeNum());
        keepFitOrder.setCodeNumMation(codeNumMation);

        // 产品信息
        iMaterialService.setDataMation(keepFitOrder, KeepFitOrder::getMaterialId);
        iMaterialService.setDataMation(keepFitOrder.getConsumeMationList(), KeepFitOrderConsume::getMaterialId);
        // 规格信息
        iMaterialNormsService.setDataMation(keepFitOrder, KeepFitOrder::getNormsId);
        iMaterialNormsService.setDataMation(keepFitOrder.getConsumeMationList(), KeepFitOrderConsume::getNormsId);
        // 其他用户信息
        iAuthUserService.setDataMation(keepFitOrder, KeepFitOrder::getCreateId);
        iAuthUserService.setDataMation(keepFitOrder, KeepFitOrder::getComplatePayUserId);
        iAuthUserService.setDataMation(keepFitOrder, KeepFitOrder::getVerificationUserId);
        // 维修技师信息
        if (StrUtil.isNotEmpty(keepFitOrder.getServiceTechnicianId())) {
            Map<String, Map<String, Object>> staffMap = iAuthUserService.queryUserMationListByStaffIds(Arrays.asList(keepFitOrder.getServiceTechnicianId()));
            keepFitOrder.setServiceTechnicianMation(staffMap.get(keepFitOrder.getServiceTechnicianId()));
        }
        // 门店
        shopStoreService.setDataMation(keepFitOrder, KeepFitOrder::getStoreId);
        if (StrUtil.isNotEmpty(keepFitOrder.getMealOrderChildId())) {
            // 套餐订单子表信息
            MealOrderChild mealOrderChild = mealOrderChildService.selectById(keepFitOrder.getMealOrderChildId());
            if (ObjectUtil.isNotEmpty(mealOrderChild)) {
                ShopMeal shopMeal = shopMealService.selectById(mealOrderChild.getMealId());
                if (ObjectUtil.isNotEmpty(shopMeal)) {
                    mealOrderChild.setName(shopMeal.getName());
                }
                keepFitOrder.setMealOrderChildMation(mealOrderChild);
            }
        }
        // 会员信息
        memberService.setDataMation(keepFitOrder, KeepFitOrder::getObjectId);
        keepFitOrder.getConsumeMationList().forEach(item -> {
            Map<String, Object> codeNumConsumeMation = new HashMap<>();
            codeNumConsumeMation.put("name", item.getCodeNum());
            item.setCodeNumMation(codeNumConsumeMation);
        });
        return keepFitOrder;
    }

    /**
     * 支付订单完成后的回调
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void keepFitOrderNotify(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String outTradeNo = params.get("outTradeNo").toString();
        // 实际支付的订单金额:单位 分
        String totalFee = params.get("totalFee").toString();
        // 转为元
        totalFee = CalculationUtil.divide(totalFee, "100", CommonNumConstants.NUM_TWO);
        KeepFitOrder keepFitOrder = queryKeepFitOrderByOddNumber(outTradeNo);
        if (keepFitOrder.getState() == KeepFitOrderState.NO_PAYING.getKey()) {
            KeepFitOrder entity = selectById(keepFitOrder.getId());
            checkNormsCodeAndOutbound(entity, false);

            String userId = inputObject.getLogParams().get("id").toString();
            UpdateWrapper<KeepFitOrder> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq(CommonConstants.ID, keepFitOrder.getId());
            updateWrapper.set(MybatisPlusUtil.toColumns(KeepFitOrder::getState), KeepFitOrderState.PAY.getKey());
            updateWrapper.set(MybatisPlusUtil.toColumns(KeepFitOrder::getPayPrice), totalFee);
            updateWrapper.set(MybatisPlusUtil.toColumns(KeepFitOrder::getPayTime), DateUtil.getTimeAndToString());
            updateWrapper.set(MybatisPlusUtil.toColumns(KeepFitOrder::getComplatePayUserId), userId);
            update(updateWrapper);
            refreshCache(keepFitOrder.getId());
        } else {
            throw new CustomException("订单状态已改变，不允许支付.");
        }
    }

    private KeepFitOrder queryKeepFitOrderByOddNumber(String oddNumber) {
        QueryWrapper<KeepFitOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(KeepFitOrder::getOddNumber), oddNumber);
        KeepFitOrder keepFitOrder = getOne(queryWrapper, false);
        if (ObjectUtil.isEmpty(keepFitOrder)) {
            throw new CustomException("订单不存在");
        }
        return keepFitOrder;
    }

    /**
     * 单据核销
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void verificationOrder(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String id = params.get("id").toString();
        KeepFitOrder keepFitOrder = selectById(id);
        if (keepFitOrder.getState() == KeepFitOrderState.FIT_COMPLATE.getKey()) {
            String userId = inputObject.getLogParams().get("id").toString();
            UpdateWrapper<KeepFitOrder> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq(CommonConstants.ID, keepFitOrder.getId());
            updateWrapper.set(MybatisPlusUtil.toColumns(KeepFitOrder::getState), KeepFitOrderState.PAY_VERIFICATION.getKey());
            updateWrapper.set(MybatisPlusUtil.toColumns(KeepFitOrder::getVerificationUserId), userId);
            update(updateWrapper);
            refreshCache(keepFitOrder.getId());
        } else {
            throw new CustomException("订单状态已改变，不允许再次核销.");
        }
    }

    /**
     * 完成保养
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void complateKeepFitOrder(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String id = params.get("id").toString();
        String serviceTechnicianId = params.get("serviceTechnicianId").toString();
        String nextServiceMileage = params.get("nextServiceMileage").toString();
        String nextServiceTime = params.get("nextServiceTime").toString();
        KeepFitOrder keepFitOrder = selectById(id);
        if (keepFitOrder.getState() == KeepFitOrderState.PAY.getKey()) {
            UpdateWrapper<KeepFitOrder> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq(CommonConstants.ID, keepFitOrder.getId());
            updateWrapper.set(MybatisPlusUtil.toColumns(KeepFitOrder::getState), KeepFitOrderState.FIT_COMPLATE.getKey());
            updateWrapper.set(MybatisPlusUtil.toColumns(KeepFitOrder::getServiceTechnicianId), serviceTechnicianId);
            updateWrapper.set(MybatisPlusUtil.toColumns(KeepFitOrder::getNextServiceMileage), nextServiceMileage);
            updateWrapper.set(MybatisPlusUtil.toColumns(KeepFitOrder::getNextServiceTime), nextServiceTime);
            update(updateWrapper);
            refreshCache(keepFitOrder.getId());
        } else {
            throw new CustomException("订单状态已改变，不允许多次完成.");
        }
    }

    @Override
    @IgnoreTenant
    public void queryListByStoreIdsAndDate(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String storeIdColum = MybatisPlusUtil.toColumns(MealOrder::getStoreId);
        MPJLambdaWrapper<KeepFitOrder> wrapper = JoinWrappers.lambda("kfo", KeepFitOrder.class)
            .leftJoin(MealOrderChild.class, "moc", MealOrderChild::getId, KeepFitOrder::getMealOrderChildId)
            .leftJoin(MealOrder.class, "mo", MealOrder::getId, MealOrderChild::getOrderId);
        wrapper.eq("mo." + storeIdColum, params.get("mealStoreId").toString())
            .eq("kfo." + storeIdColum, params.get("keepFitStoreId").toString())
            .eq(MybatisPlusUtil.toColumns(KeepFitOrder::getOnlineDay), params.get("date").toString());
        wrapper.selectAll(KeepFitOrder.class);
        if (tenantEnable) {
            String tenantId = TenantContext.getTenantId();
            wrapper.eq("kfo." + CommonConstants.TENANT_ID_FIELD, tenantId)
                .eq("moc." + CommonConstants.TENANT_ID_FIELD, tenantId)
                .eq("mo." + CommonConstants.TENANT_ID_FIELD, tenantId);
        }
        List<KeepFitOrder> beans = skyeyeBaseMapper.selectJoinList(KeepFitOrder.class, wrapper);
        if (CollectionUtil.isEmpty(beans)) {
            return;
        }
        List<String> keepFitOrderIdList = beans.stream().map(KeepFitOrder::getId).collect(Collectors.toList());
        List<KeepFitOrderConsume> keepFitOrderConsumeList = keepFitOrderConsumeService.selectByOrderIds(keepFitOrderIdList);
        Map<String, List<KeepFitOrderConsume>> listMap = keepFitOrderConsumeList.stream().collect(Collectors.groupingBy(KeepFitOrderConsume::getOrderId));
        List<String> allStaffIdList = new ArrayList<>();
        for (KeepFitOrder bean : beans) {
            allStaffIdList.add(bean.getServiceTechnicianId());
            allStaffIdList.add(bean.getComplatePayUserId());
            allStaffIdList.add(bean.getVerificationUserId());
        }
        // 维修技师信息、核销人信息、完成支付人信息
        List<String> serviceTechnicianIdList = allStaffIdList.stream().filter(StrUtil::isNotEmpty).distinct().collect(Collectors.toList());
        Map<String, Map<String, Object>> staffMap = iAuthUserService.queryUserMationListByStaffIds(serviceTechnicianIdList);
        beans.forEach(bean -> {
            bean.setConsumeMationList(listMap.getOrDefault(bean.getId(), Collections.emptyList()));
            Map<String, Object> codeNumMation = new HashMap<>();
            codeNumMation.put("name", bean.getCodeNum());
            bean.setCodeNumMation(codeNumMation);
            bean.setServiceTechnicianMation(staffMap.getOrDefault(bean.getServiceTechnicianId(), null));
            bean.setComplatePayUserMation(staffMap.getOrDefault(bean.getComplatePayUserId(), null));
            bean.setVerificationUserMation(staffMap.getOrDefault(bean.getVerificationUserId(), null));
        });
        outputObject.setBeans(beans);
        outputObject.settotal(beans.size());
    }

}
