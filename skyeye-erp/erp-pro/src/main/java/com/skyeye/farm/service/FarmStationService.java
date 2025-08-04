package com.skyeye.farm.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.farm.entity.FarmStation;

import java.util.List;

/**
 * @ClassName: FarmStationService
 * @Description: 车间工位管理服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2022/2/4 21:14
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface FarmStationService extends SkyeyeBusinessService<FarmStation> {
    void queryFarmStationByIds(InputObject inputObject, OutputObject outputObject);

    List<FarmStation> queryFarmStationListByIds(List<String> farmStationIds);

}
