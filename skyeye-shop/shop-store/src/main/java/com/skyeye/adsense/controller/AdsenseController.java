/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.adsense.controller;

import com.skyeye.adsense.entity.Adsense;
import com.skyeye.adsense.service.AdsenseService;
import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: AdsenseController
 * @Description: 广告位管理控制类
 * @author: skyeye云系列--卫志强
 * @date: 2022/2/4 10:06
 *
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "广告位管理", tags = "广告位管理", modelName = "广告位管理")
public class AdsenseController {

    @Autowired
    private AdsenseService adsenseService;

    @ApiOperation(id = "queryAdsenseList", value = "分页查询广告位管理信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/DeliveryController/queryAdsenseList")
    public void queryAdsenseList(InputObject inputObject, OutputObject outputObject) {
        adsenseService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeAdsense", value = "新增/编辑广告位管理信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = Adsense.class)
    @RequestMapping("/post/AdsenseController/writeAdsense")
    public void writeAdsense(InputObject inputObject, OutputObject outputObject) {
        adsenseService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteAdsenseByIds", value = "批量删除广告位管理信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "ids", name = "ids", value = "主键id列表，多个id用逗号分隔", required = "required")})
    @RequestMapping("/post/AdsenseController/deleteAdsenseByIds")
    public void deleteAdsenseByIds(InputObject inputObject, OutputObject outputObject) {
        adsenseService.deleteByIds(inputObject, outputObject);
    }

    @ApiOperation(id = "queryAdsense", value = "获取已启用广告位管理信息", method = "POST", allUse = "0")
    @RequestMapping("/post/AdsenseController/queryAdsense")
    public void queryAdsense(InputObject inputObject, OutputObject outputObject) {
        adsenseService.queryList(inputObject, outputObject);
    }

    @ApiOperation(id = "queryAdsenseById", value = "根据id获取广告位", method = "POST", allUse = "2")
    @ApiImplicitParams({@ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/AdsenseController/queryAdsenseById")
    public void queryAdsenseById(InputObject inputObject, OutputObject outputObject) {
        adsenseService.selectById(inputObject, outputObject);
    }
}
