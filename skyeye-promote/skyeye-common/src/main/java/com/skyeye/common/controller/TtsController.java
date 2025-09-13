/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.common.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.service.TtsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: TtsController
 * @Description: 文字转语音控制器
 * @author: skyeye云系列--卫志强
 * @date: 2024/01/01 00:00
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "文字转语音接口", tags = "文字转语音", modelName = "基础模块")
public class TtsController {

    @Autowired
    private TtsService ttsService;

    @ApiOperation(id = "textToSpeech", value = "文字转语音", method = "POST", allUse = "0")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "text", name = "text", value = "要转换的文本内容", required = "required"),
        @ApiImplicitParam(id = "format", name = "format", value = "音频格式(wav/mp3/aiff)", defaultValue = "wav")
    })
    @RequestMapping("/post/TtsController/textToSpeech")
    public void textToSpeech(InputObject inputObject, OutputObject outputObject) {
        ttsService.textToSpeech(inputObject, outputObject);
    }

}
