package com.skyeye.exam.examQuRadio.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.exam.examQuRadio.entity.ExamQuRadio;
import com.skyeye.exam.examQuRadio.service.ExamQuRadioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: ExamQuRadioController
 * @Description: 单选题选项表管理控制层
 * @author: skyeye云系列--lqy
 * @date: 2024/7/16 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "单选题选项表管理", tags = "单选题选项表管理", modelName = "单选题选项表管理")
public class ExamQuRadioController {

    @Autowired
    private ExamQuRadioService examQuRadioService;

//    /**
//     * 新增或编辑单选题选项
//     *
//     * @param inputObject  入参以及用户信息等获取对象
//     * @param outputObject 出参以及提示信息的返回值对象
//     */
//    @ApiOperation(id = "writeOrUpdateQuRadio", value = "新增或编辑单选题选项", method = "POST", allUse = "2")
//    @ApiImplicitParams(classBean = ExamQuRadio.class)
//    @RequestMapping("/post/ExamQuRadioController/writeOrUpdateQuRadio")
//    public void writeOrUpdateQuRadio(InputObject inputObject, OutputObject outputObject) {
//        examQuRadioService.saveOrUpdateEntity(inputObject, outputObject);
//    }

}
