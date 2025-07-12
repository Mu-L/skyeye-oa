/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.farm.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.farm.entity.FarmStaff;

import java.util.List;

/**
 * @ClassName: FarmStaffService
 * @Description: 车间与员工的关系服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2022/2/4 21:14
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface FarmStaffService extends SkyeyeBusinessService<FarmStaff> {

    void insertFarmStaff(InputObject inputObject, OutputObject outputObject);

    void queryStaffBelongFarmList(InputObject inputObject, OutputObject outputObject);

    void deleteFarmStaffByStaffId(InputObject inputObject, OutputObject outputObject);

    void queryAllFarmStaffList(InputObject inputObject, OutputObject outputObject);

    List<FarmStaff> queryFarmsStaffByStaffId(String staffId);

}
