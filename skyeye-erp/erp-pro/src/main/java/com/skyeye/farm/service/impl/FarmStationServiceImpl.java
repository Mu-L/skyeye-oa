package com.skyeye.farm.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.farm.dao.FarmStationDao;
import com.skyeye.farm.entity.FarmStation;
import com.skyeye.farm.service.FarmStationService;
import com.skyeye.rest.checkwork.scheduling.ISchedulingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ClassName: FarmStationServiceImpl
 * @Description: 车车间工位管理服务层
 * @author: skyeye云系列--卫志强
 * @date: 2022/2/4 21:14
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "车间工位管理", groupName = "车间工位管理")
public class FarmStationServiceImpl extends SkyeyeBusinessServiceImpl<FarmStationDao, FarmStation> implements FarmStationService {

    @Autowired
    private ISchedulingService iSchedulingService;

    @Override
    public void getQueryWrapper(InputObject inputObject, QueryWrapper<FarmStation> wrapper) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        if (StrUtil.isNotEmpty(commonPageInfo.getObjectId())){
            wrapper.eq(MybatisPlusUtil.toColumns(FarmStation::getWorkProcedureId), commonPageInfo.getObjectId());
        }
    }

    @Override
    protected void deletePreExecution(String id) {
        iSchedulingService.deleteSchedulingByWorkId(id);
    }
}
