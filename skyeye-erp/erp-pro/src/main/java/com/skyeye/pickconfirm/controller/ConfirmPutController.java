/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.pickconfirm.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.pickconfirm.entity.ConfirmPut;
import com.skyeye.pickconfirm.service.ConfirmPutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: ConfirmPutController
 * @Description: 物料接收单控制层
 * @author: skyeye云系列--卫志强
 * @date: 2024/6/27 10:05
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "物料接收单", tags = "物料接收单", modelName = "物料确认")
public class ConfirmPutController {

    @Autowired
    private ConfirmPutService confirmPutService;

    @ApiOperation(id = "queryConfirmPutList", value = "获取物料接收单列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ConfirmPutController/queryConfirmPutList")
    public void queryConfirmPutList(InputObject inputObject, OutputObject outputObject) {
        confirmPutService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeConfirmPut", value = "新增/编辑物料接收单", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = ConfirmPut.class)
    @RequestMapping("/post/ConfirmPutController/writeConfirmPut")
    public void writeConfirmPut(InputObject inputObject, OutputObject outputObject) {
        confirmPutService.saveOrUpdateEntity(inputObject, outputObject);
    }

}
