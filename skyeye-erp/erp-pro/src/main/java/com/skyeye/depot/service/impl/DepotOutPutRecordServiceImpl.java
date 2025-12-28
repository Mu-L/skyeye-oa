package com.skyeye.depot.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.business.service.SkyeyeErpOrderItemService;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.constants.ErpConstants;
import com.skyeye.depot.classenum.DepotOutFromType;
import com.skyeye.depot.classenum.DepotOutPutStateEnum;
import com.skyeye.depot.dao.DepotOutPutRecordDao;
import com.skyeye.depot.entity.DepotOut;
import com.skyeye.depot.entity.DepotOutPutRecord;
import com.skyeye.depot.entity.DepotPut;
import com.skyeye.depot.service.DepotOutPutRecordService;
import com.skyeye.entity.ErpOrderItem;
import com.skyeye.exception.CustomException;
import com.skyeye.material.service.MaterialNormsService;
import com.skyeye.material.service.MaterialService;
import com.skyeye.product.entity.ProductLead;
import com.skyeye.product.entity.ProductLeadOutStock;
import com.skyeye.product.entity.ProductReturn;
import com.skyeye.product.entity.ProductReturnInStock;
import com.skyeye.product.service.ProductLeadOutStockService;
import com.skyeye.product.service.ProductLeadService;
import com.skyeye.product.service.ProductReturnInStockService;
import com.skyeye.product.service.ProductReturnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: DepotOutPutRecordServiceImpl
 * @Description: 仓库出入库记录管理值服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/7/6 10:12
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "仓库出入库记录管理", groupName = "仓库出入库记录管理")
public class DepotOutPutRecordServiceImpl extends SkyeyeBusinessServiceImpl<DepotOutPutRecordDao, DepotOutPutRecord> implements DepotOutPutRecordService {


    @Autowired
    private ProductLeadOutStockService productLeadOutStockService;

    @Autowired
    private ProductLeadService productLeadService;

    @Autowired
    private ProductReturnInStockService productReturnInStockService;

    @Autowired
    private ProductReturnService productReturnService;

    @Autowired
    private MaterialService materialService;

    @Autowired
    private MaterialNormsService iMaterialNormsService;

    @Autowired
    private SkyeyeErpOrderItemService skyeyeErpOrderItemService;

    @Override
    public List<DepotOutPutRecord> selectByNormCodes(List<String> codeList) {
        QueryWrapper<DepotOutPutRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DepotOutPutRecord::getState), DepotOutPutStateEnum.NOT_RETURN.getKey());
        queryWrapper.in(MybatisPlusUtil.toColumns(DepotOutPutRecord::getNormsCode), codeList);
        return list(queryWrapper);
    }

    @Override
    public List<DepotOutPutRecord> queryRecordListByHolderIdAndMN(String holderId, List<String> materialIdList, List<String> normsIdList) {
        QueryWrapper<DepotOutPutRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DepotOutPutRecord::getObjectId), holderId);
        queryWrapper.ne(MybatisPlusUtil.toColumns(DepotOutPutRecord::getState), DepotOutPutStateEnum.RETURNED.getKey());
        queryWrapper.in(MybatisPlusUtil.toColumns(DepotOutPutRecord::getMaterialId), materialIdList);
        queryWrapper.in(MybatisPlusUtil.toColumns(DepotOutPutRecord::getNormsId), normsIdList);
        List<DepotOutPutRecord> list = list(queryWrapper);
        return list;
    }

    @Override
    public void writeOutPutRecord(Object o, Integer fromTypeId) {
        if (fromTypeId == DepotOutFromType.LOANOUT.getKey()) {
            // 新增
            DepotOut entity = (DepotOut) o;
            // 借出单
            // 1. 查询借出出库单
            ProductLeadOutStock productLeadOutStock = productLeadOutStockService.selectById(entity.getFromId());
            // 2. 查询借出申请
            ProductLead productLead = StrUtil.isNotEmpty(productLeadOutStock.getFromId()) ? productLeadService.selectById(productLeadOutStock.getFromId()) : new ProductLead();
            // 获取子单数据
            List<ErpOrderItem> erpOrderItemList = entity.getErpOrderItemList();
            List<DepotOutPutRecord> depotOutPutRecordList = new ArrayList<>();
            for (ErpOrderItem erpOrderItem : erpOrderItemList) {
                if (StrUtil.isNotEmpty(erpOrderItem.getNormsCode())) {
                    // 解析编码
                    List<String> normsCode = Arrays.asList(erpOrderItem.getNormsCode().split("\n")).stream()
                        .filter(str -> StrUtil.isNotEmpty(str)).distinct().collect(Collectors.toList());
                    for (String code : normsCode) {
                        DepotOutPutRecord depotOutPutRecord = new DepotOutPutRecord();
                        depotOutPutRecord.setOutCount(CommonNumConstants.NUM_ONE.toString());

                        depotOutPutRecord.setObjectId(entity.getHolderId());
                        depotOutPutRecord.setObjectKey(entity.getHolderKey());
                        depotOutPutRecord.setBorrowId(ObjectUtil.isNotEmpty(productLead) ? productLead.getId() : StrUtil.EMPTY);
                        depotOutPutRecord.setBorrowTime(ObjectUtil.isNotEmpty(productLead) ? productLead.getOperTime() : entity.getOperTime());
                        depotOutPutRecord.setOutDepotTime(DateUtil.getPointTime(DateUtil.YYYY_MM_DD));

                        depotOutPutRecord.setMaterialId(erpOrderItem.getMaterialId());
                        depotOutPutRecord.setNormsId(erpOrderItem.getNormsId());

                        depotOutPutRecord.setNormsCode(code);
                        depotOutPutRecordList.add(depotOutPutRecord);
                    }
                } else {
                    DepotOutPutRecord depotOutPutRecord = new DepotOutPutRecord();

                    depotOutPutRecord.setObjectId(entity.getHolderId());
                    depotOutPutRecord.setObjectKey(entity.getHolderKey());
                    depotOutPutRecord.setBorrowId(ObjectUtil.isNotEmpty(productLead) ? productLead.getId() : StrUtil.EMPTY);
                    depotOutPutRecord.setBorrowTime(ObjectUtil.isNotEmpty(productLead) ? productLead.getOperTime() : entity.getOperTime());
                    depotOutPutRecord.setOutDepotTime(DateUtil.getPointTime(DateUtil.YYYY_MM_DD));

                    depotOutPutRecord.setOutCount(erpOrderItem.getOperNumber());
                    depotOutPutRecord.setMaterialId(erpOrderItem.getMaterialId());
                    depotOutPutRecord.setNormsId(erpOrderItem.getNormsId());

                    depotOutPutRecord.setNormsCode(StrUtil.EMPTY);
                    depotOutPutRecordList.add(depotOutPutRecord);
                }
            }
            createEntity(depotOutPutRecordList, null);
        } else {
            // 编辑
            DepotPut entity = (DepotPut) o;
            // 归还入库单
            // 1.查询归还入库单
            ProductReturnInStock productReturnInStock = productReturnInStockService.selectById(entity.getFromId());
            // 2. 查询归还申请
            ProductReturn productReturn = StrUtil.isNotEmpty(productReturnInStock.getFromId()) ? productReturnService.selectById(productReturnInStock.getFromId()) : new ProductReturn();
            // 获取子单数据
            List<ErpOrderItem> erpOrderItemList = entity.getErpOrderItemList();
            List<String> normsIdList = erpOrderItemList.stream().map(ErpOrderItem::getNormsId).distinct().collect(Collectors.toList());
            List<String> materialIdList = erpOrderItemList.stream().map(ErpOrderItem::getMaterialId).distinct().collect(Collectors.toList());
            // 获取所有该客户/供应商的 未全部归还的记录
            List<DepotOutPutRecord> outPutRecordList = queryRecordListByHolderIdAndMN(entity.getHolderId(), materialIdList, normsIdList);

            // 过滤出编码为空的数据
            List<DepotOutPutRecord> outPutRecords = outPutRecordList.stream().filter(outPutRecord -> StrUtil.isEmpty(outPutRecord.getNormsCode())).collect(Collectors.toList());
            // 过滤出编码不为空的数据
            List<DepotOutPutRecord> outPutCodeRecords = outPutRecordList.stream().filter(outPutRecord -> StrUtil.isNotEmpty(outPutRecord.getNormsCode())).collect(Collectors.toList());
            // 根据编码分组
            Map<String, List<DepotOutPutRecord>> outPutCodeRecordMap = outPutCodeRecords.stream().collect(Collectors.groupingBy(DepotOutPutRecord::getNormsCode));
            Map<String, Map<String, List<DepotOutPutRecord>>> outPutRecordMap = outPutRecords.stream().collect(Collectors.groupingBy(DepotOutPutRecord::getMaterialId, Collectors.groupingBy(DepotOutPutRecord::getNormsId)));
            List<DepotOutPutRecord> depotOutPutRecordArrayList = new ArrayList<>();
            for (ErpOrderItem erpOrderItem : erpOrderItemList) {
                if (StrUtil.isNotEmpty(erpOrderItem.getNormsCode())) {
                    List<String> normsCode = Arrays.asList(erpOrderItem.getNormsCode().split("\n")).stream()
                        .filter(str -> StrUtil.isNotEmpty(str)).distinct().collect(Collectors.toList());
                    for (String code : normsCode) {
                        DepotOutPutRecord depotOutPutRecord = outPutCodeRecordMap.get(code).get(CommonNumConstants.NUM_ZERO);
                        depotOutPutRecord.setPutCount(CommonNumConstants.NUM_ONE.toString());
                        depotOutPutRecord.setState(DepotOutPutStateEnum.RETURNED.getKey());
                        depotOutPutRecord.setPutDepotTime(DateUtil.getPointTime(DateUtil.YYYY_MM_DD));
                        depotOutPutRecord.setRepayId(ObjectUtil.isNotEmpty(productReturn) ? productReturn.getId() : StrUtil.EMPTY);
                        depotOutPutRecord.setRepayTime(ObjectUtil.isNotEmpty(productReturn) ? productReturn.getOperTime() : entity.getOperTime());
                        depotOutPutRecordArrayList.add(depotOutPutRecord);
                    }
                } else {
                    DepotOutPutRecord depotOutPutRecord = outPutRecordMap.get(erpOrderItem.getMaterialId()).get(erpOrderItem.getNormsId()).get(CommonNumConstants.NUM_ZERO);
                    String operNumber = StrUtil.isEmpty(erpOrderItem.getOperNumber())
                        ? CommonNumConstants.NUM_ZERO.toString()
                        : erpOrderItem.getOperNumber();
                    String putCount = StrUtil.isEmpty(depotOutPutRecord.getPutCount())
                        ? CommonNumConstants.NUM_ZERO.toString()
                        : depotOutPutRecord.getPutCount();
                    String newPutCount = CalculationUtil.add(operNumber, putCount, ErpConstants.NUM_AFTER_DOT, RoundingMode.UP);
                    depotOutPutRecord.setPutCount(newPutCount);
                    String outCount = StrUtil.isEmpty(depotOutPutRecord.getOutCount())
                        ? CommonNumConstants.NUM_ZERO.toString()
                        : depotOutPutRecord.getOutCount();
                    if (CalculationUtil.compareTo(newPutCount, outCount, ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) == 0) {
                        depotOutPutRecord.setState(DepotOutPutStateEnum.RETURNED.getKey());
                    } else if (CalculationUtil.compareTo(newPutCount, CommonNumConstants.NUM_ZERO.toString(), ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) > 0
                        && CalculationUtil.compareTo(newPutCount, outCount, ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) < 0) {
                        depotOutPutRecord.setState(DepotOutPutStateEnum.PART_RETURN.getKey());
                    }
                    depotOutPutRecord.setPutDepotTime(DateUtil.getPointTime(DateUtil.YYYY_MM_DD));
                    depotOutPutRecord.setRepayId(ObjectUtil.isNotEmpty(productReturn) ? productReturn.getId() : StrUtil.EMPTY);
                    depotOutPutRecord.setRepayTime(ObjectUtil.isNotEmpty(productReturn) ? productReturn.getOperTime() : entity.getOperTime());
                    depotOutPutRecordArrayList.add(depotOutPutRecord);
                }
            }
            updateEntity(depotOutPutRecordArrayList, null);
        }
    }

    @Override
    public void queryOutPutRecordDetailList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        if (StrUtil.isEmpty(commonPageInfo.getObjectId())) {
            throw new CustomException("客户/供应商id不能为空");
        }
        if (StrUtil.isEmpty(commonPageInfo.getFirstTypeId())) {
            throw new CustomException("商品id不能为空");
        }
        if (StrUtil.isEmpty(commonPageInfo.getSecondTypeId())) {
            throw new CustomException("规格id不能为空");
        }
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        QueryWrapper<DepotOutPutRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DepotOutPutRecord::getObjectId), commonPageInfo.getObjectId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(DepotOutPutRecord::getMaterialId), commonPageInfo.getFirstTypeId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(DepotOutPutRecord::getNormsId), commonPageInfo.getSecondTypeId());
        List<DepotOutPutRecord> bean = list(queryWrapper);
        materialService.setDataMation(bean, DepotOutPutRecord::getMaterialId);
        iMaterialNormsService.setDataMation(bean, DepotOutPutRecord::getNormsId);

        outputObject.settotal(page.getTotal());
        outputObject.setBeans(bean);
    }

    @Override
    public void queryHolderOutPutNormsList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        if (StrUtil.isEmpty(commonPageInfo.getHolderId())) {
            throw new CustomException("holderId不能为空");
        }
        if (StrUtil.isEmpty(commonPageInfo.getHolderKey())) {
            throw new CustomException("holderKey不能为空");
        }
        if (StrUtil.isEmpty(commonPageInfo.getType())) {
            throw new CustomException("type不能为空");
        }
        List<ErpOrderItem> beans = skyeyeErpOrderItemService.queryHolderOutPutNormsList(commonPageInfo.getHolderKey(), commonPageInfo.getType(), commonPageInfo.getHolderId(), commonPageInfo.getKeyword());
        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, List<ErpOrderItem>> groupByMaterialId = beans.stream()
            .collect(Collectors.groupingBy(ErpOrderItem::getMaterialId));
        for (Map.Entry<String, List<ErpOrderItem>> entry : groupByMaterialId.entrySet()) {
            Map<String, Object> map = new HashMap<>();
            map.put("normsId", entry.getValue().get(CommonNumConstants.NUM_ZERO).getNormsId());
            map.put("materialId", entry.getKey());
            String totalNum = entry.getValue().stream()
                .map(item -> StrUtil.isEmpty(item.getOperNumber()) ? CommonNumConstants.NUM_ZERO.toString() : item.getOperNumber())
                .reduce(CommonNumConstants.NUM_ZERO.toString(),
                    (a, b) -> CalculationUtil.add(a, b, ErpConstants.NUM_AFTER_DOT, RoundingMode.UP));
            double totalPrice = entry.getValue().stream().mapToDouble(item -> Double.parseDouble(item.getAllPrice())).sum();
            map.put("totalNum", totalNum);
            map.put("totalPrice", totalPrice);
            result.add(map);
        }
        // 设置商品信息
        materialService.setMationForMap(result, "materialId", "materialMation");
        iMaterialNormsService.setMationForMap(result, "normsId", "normsMation");
        outputObject.settotal(page.getTotal());
        outputObject.setBeans(result);
    }

    @Override
    public void checkOutPutRecord(List<ErpOrderItem> erpOrderItemList, String holderId) {
        List<String> materialIds = erpOrderItemList.stream().map(ErpOrderItem::getMaterialId).distinct().collect(Collectors.toList());
        List<String> normIds = erpOrderItemList.stream().map(ErpOrderItem::getNormsId).distinct().collect(Collectors.toList());
        List<DepotOutPutRecord> depotOutPutRecords = queryRecordListByHolderIdAndMN(holderId, materialIds, normIds);
        // 过滤出编码不为空的的出库记录
        List<DepotOutPutRecord> outPutCodeRecords = depotOutPutRecords.stream().filter(v -> StrUtil.isNotEmpty(v.getNormsCode())).collect(Collectors.toList());
        // 根据编码分组
        Map<String, List<DepotOutPutRecord>> outPutCodeMap = outPutCodeRecords.stream().collect(Collectors.groupingBy(DepotOutPutRecord::getNormsCode));
        // 过滤出编码为空的的出库记录
        List<DepotOutPutRecord> outPutRecords = depotOutPutRecords.stream().filter(v -> StrUtil.isEmpty(v.getNormsCode())).collect(Collectors.toList());
        Map<String, Map<String, List<DepotOutPutRecord>>> outPutRecordMap = outPutRecords.stream().collect(Collectors.groupingBy(DepotOutPutRecord::getMaterialId, Collectors.groupingBy(DepotOutPutRecord::getNormsId)));

        for (ErpOrderItem erpOrderItem : erpOrderItemList) {
            List<String> normsCode = Arrays.asList(erpOrderItem.getNormsCode().split("\n")).stream()
                .filter(str -> StrUtil.isNotEmpty(str)).distinct().collect(Collectors.toList());

            if (CollectionUtils.isNotEmpty(normsCode)) {
                // 一物一码
                for (String code : normsCode) {
                    List<DepotOutPutRecord> list = outPutCodeMap.getOrDefault(code, new ArrayList<>());
                    if (CollectionUtils.isEmpty(list)) {
                        throw new CustomException("商品编码为" + code + "的出库记录不存在");
                    }
                }
            } else {
                Map<String, List<DepotOutPutRecord>> normIdMap = outPutRecordMap.getOrDefault(erpOrderItem.getMaterialId(), new HashMap<>());
                if (CollectionUtils.isEmpty(normIdMap)) {
                    throw new CustomException("该商品的出库记录不存在");
                }
                List<DepotOutPutRecord> list = normIdMap.getOrDefault(erpOrderItem.getNormsId(), new ArrayList<>());
                if (CollectionUtils.isEmpty(list)) {
                    throw new CustomException("商品规格为的出库记录不存在");
                }
                DepotOutPutRecord depotOutPutRecord = list.get(CommonNumConstants.NUM_ZERO);
                // 带归还入库单数量
                String outCount = StrUtil.isEmpty(depotOutPutRecord.getOutCount())
                    ? CommonNumConstants.NUM_ZERO.toString()
                    : depotOutPutRecord.getOutCount();
                String putCount = StrUtil.isEmpty(depotOutPutRecord.getPutCount())
                    ? CommonNumConstants.NUM_ZERO.toString()
                    : depotOutPutRecord.getPutCount();
                String surplusCount = CalculationUtil.subtract(outCount, putCount, ErpConstants.NUM_AFTER_DOT, RoundingMode.UP);
                String operNumber = StrUtil.isEmpty(erpOrderItem.getOperNumber())
                    ? CommonNumConstants.NUM_ZERO.toString()
                    : erpOrderItem.getOperNumber();
                if (CalculationUtil.compareTo(operNumber, surplusCount, ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) > 0) {
                    throw new CustomException("归还数量过多");
                }
            }
        }
    }
}
