/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.vehicle.service.impl;

import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.eve.vehicle.dao.InsuranceDao;
import com.skyeye.eve.vehicle.entity.Insurance;
import com.skyeye.eve.vehicle.entity.InsuranceCoverage;
import com.skyeye.eve.vehicle.service.InsuranceCoverageService;
import com.skyeye.eve.vehicle.service.InsuranceService;
import com.skyeye.eve.vehicle.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: InsuranceServiceImpl
 * @Description: 车辆保险服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/24 15:54
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "车辆保险管理", groupName = "车辆模块")
public class InsuranceServiceImpl extends SkyeyeBusinessServiceImpl<InsuranceDao, Insurance> implements InsuranceService {

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private InsuranceCoverageService insuranceCoverageService;

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        CommonPageInfo pageInfo = inputObject.getParams(CommonPageInfo.class);
        List<Map<String, Object>> beans = skyeyeBaseMapper.queryInsuranceList(pageInfo);
        vehicleService.setMationForMap(beans, "vehicleId", "vehicleMation");
        return beans;
    }

    @Override
    public void validatorEntity(Insurance entity) {
        super.validatorEntity(entity);
        String totalPrice = "0";
        for (InsuranceCoverage insuranceCoverage : entity.getVehicleInsuranceCoverages()) {
            totalPrice = CalculationUtil.add(totalPrice, insuranceCoverage.getPremium());
        }
        entity.setInsuranceAllPrice(totalPrice);
    }

    @Override
    public void writePostpose(Insurance entity, String userId) {
        super.writePostpose(entity, userId);
        insuranceCoverageService.saveInsuranceCoverage(entity.getId(), entity.getVehicleInsuranceCoverages(), userId);
    }

    @Override
    public Insurance getDataFromDb(String id) {
        Insurance vehicleInsurance = super.getDataFromDb(id);
        // 保险险种信息
        List<InsuranceCoverage> vehicleInsuranceCoverages = insuranceCoverageService.queryInsuranceCoverageByPId(id);
        vehicleInsurance.setVehicleInsuranceCoverages(vehicleInsuranceCoverages);
        return vehicleInsurance;
    }

    @Override
    public Insurance selectById(String id) {
        Insurance vehicleInsurance = super.selectById(id);
        // 车辆信息
        vehicleService.setDataMation(vehicleInsurance, Insurance::getVehicleId);
        // 保险险种信息
        iSysDictDataService.setDataMation(vehicleInsurance.getVehicleInsuranceCoverages(), InsuranceCoverage::getCoverageId);
        return vehicleInsurance;
    }

    @Override
    protected void deletePostpose(String id) {
        insuranceCoverageService.deleteInsuranceCoverageByPId(id);
    }

}
