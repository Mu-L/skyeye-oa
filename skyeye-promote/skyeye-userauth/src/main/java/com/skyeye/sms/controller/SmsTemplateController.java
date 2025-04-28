/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.sms.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.sms.entity.SmsTemplate;
import com.skyeye.sms.service.SmsTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: SmsTemplateController
 * @Description: 短信模板控制层
 * @author: skyeye云系列--卫志强
 * @date: 2024/8/31 0:47
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@RestController
@Api(value = "短信模板", tags = "短信模板", modelName = "短信模板")
public class SmsTemplateController {

    @Autowired
    private SmsTemplateService smsTemplateService;

    @ApiOperation(id = "querySmsTemplatelList", value = "获取短信模板列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/SmsTemplatelController/querySmsTemplatelList")
    public void querySmsTemplatelList(InputObject inputObject, OutputObject outputObject) {
        smsTemplateService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeSmsTemplatel", value = "新增/编辑短信模板", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = SmsTemplate.class)
    @RequestMapping("/post/SmsTemplatelController/writeSmsTemplatel")
    public void writeSmsTemplatel(InputObject inputObject, OutputObject outputObject) {
        smsTemplateService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteSmsTemplatelById", value = "删除短信模板", method = "DELETE", allUse = "1")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/SmsTemplatelController/deleteSmsTemplatelById")
    public void deleteSmsTemplatelById(InputObject inputObject, OutputObject outputObject) {
        smsTemplateService.deleteById(inputObject, outputObject);
    }

}
