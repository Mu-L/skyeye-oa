/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.machin.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.business.service.impl.SkyeyeErpOrderServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.FlowableStateEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.constants.ErpConstants;
import com.skyeye.depot.classenum.DepotPutFromType;
import com.skyeye.depot.classenum.DepotPutOutType;
import com.skyeye.depot.classenum.DepotPutState;
import com.skyeye.depot.entity.DepotPut;
import com.skyeye.depot.service.DepotPutService;
import com.skyeye.exception.CustomException;
import com.skyeye.farm.service.FarmService;
import com.skyeye.machin.classenum.MachinPutFromType;
import com.skyeye.machin.dao.MachinPutDao;
import com.skyeye.machin.entity.MachinPut;
import com.skyeye.machin.service.MachinPutService;
import com.skyeye.machinprocedure.service.MachinProcedureFarmService;
import com.skyeye.material.classenum.MaterialInOrderType;
import com.skyeye.organization.service.IDepmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: MachinPutServiceImpl
 * @Description: 加工入库单服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/7/6 22:02
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "加工入库单", groupName = "加工单管理", flowable = true)
public class MachinPutServiceImpl extends SkyeyeErpOrderServiceImpl<MachinPutDao, MachinPut> implements MachinPutService {

    @Autowired
    private IDepmentService iDepmentService;

    @Autowired
    private DepotPutService depotPutService;

    @Autowired
    private MachinProcedureFarmService machinProcedureFarmService;

    @Autowired
    private FarmService farmService;

    @Override
    public QueryWrapper<MachinPut> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<MachinPut> queryWrapper = super.getQueryWrapper(commonPageInfo);
        queryWrapper.eq(MybatisPlusUtil.toColumns(MachinPut::getFarmId), commonPageInfo.getObjectId());
        return queryWrapper;
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        // 部门
        iDepmentService.setMationForMap(beans, "departmentId", "departmentMation");
        // 业务员
        iAuthUserService.setMationForMap(beans, "salesman", "salesmanMation");

        machinProcedureFarmService.setOrderMationByFromId(beans, "fromId", "fromMation");

        farmService.setMationForMap(beans, "farmId", "farmMation");
        return beans;
    }

    @Override
    public void validatorEntity(MachinPut entity) {
        entity.setOtherState(DepotPutState.NEED_PUT.getKey());
    }

    @Override
    public void createPrepose(MachinPut entity) {
        super.createPrepose(entity);
        if (StrUtil.isEmpty(entity.getFarmId())) {
            throw new CustomException("请选择加工车间");
        }
        entity.setFromTypeId(MachinPutFromType.FARM_TASK.getKey());
        entity.setType(DepotPutOutType.PUT.getKey());
        entity.getErpOrderItemList().forEach(erpOrderItem -> {
            erpOrderItem.setMType(MaterialInOrderType.GENERAL.getKey());
        });
    }

    @Override
    public void updatePrepose(MachinPut entity) {
        super.updatePrepose(entity);
        // 保证下面的参数不会因为编辑而改变
        MachinPut oldMachinPut = selectById(entity.getId());
        entity.setFarmId(oldMachinPut.getFarmId());
    }

    @Override
    public MachinPut selectById(String id) {
        MachinPut machinPut = super.selectById(id);
        iDepmentService.setDataMation(machinPut, MachinPut::getDepartmentId);
        return machinPut;
    }

    @Override
    public void queryMachinPutTransById(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        MachinPut machinPut = selectById(id);
        // 该加工入库单下的已经下达仓库入库单(审核通过)的数量
        Map<String, String> depotNumMap = depotPutService.calcMaterialNormsNumByFromId(machinPut.getId());
        // 设置未下达商品数量-----加工入库单数量 - 已入库数量
        super.setOrCheckOperNumber(machinPut.getErpOrderItemList(), true, depotNumMap);
        // 过滤掉数量为0的商品信息
        machinPut.setErpOrderItemList(machinPut.getErpOrderItemList().stream()
            .filter(erpOrderItem -> CalculationUtil.compareTo(erpOrderItem.getOperNumber(), CommonNumConstants.NUM_ZERO.toString(), ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) > 0)
            .collect(Collectors.toList()));
        outputObject.setBean(machinPut);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void insertMachinPutToTurnDepot(InputObject inputObject, OutputObject outputObject) {
        DepotPut depotPut = inputObject.getParams(DepotPut.class);
        // 获取加工入库单状态
        MachinPut machinPut = selectById(depotPut.getId());
        if (ObjectUtil.isEmpty(machinPut)) {
            throw new CustomException("该数据不存在.");
        }
        // 审核通过的可以转到仓库入库单
        if (FlowableStateEnum.PASS.getKey().equals(machinPut.getState())) {
            String userId = inputObject.getLogParams().get("id").toString();
            depotPut.setFromId(depotPut.getId());
            depotPut.setFromTypeId(DepotPutFromType.MACHIN_PUT.getKey());
            depotPut.setId(StrUtil.EMPTY);
            depotPutService.createEntity(depotPut, userId);
        } else {
            outputObject.setreturnMessage("状态错误，无法下达仓库入库单.");
        }
    }

    @Override
    public List<MachinPut> queryMachinPutByMachinProcedureFarmId(String machinProcedureFarmId) {
        QueryWrapper<MachinPut> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(MachinPut::getFromId), machinProcedureFarmId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(MachinPut::getFromTypeId), MachinPutFromType.FARM_TASK.getKey());
        return this.list(queryWrapper);
    }
}
