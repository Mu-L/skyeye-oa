package com.skyeye.piecework.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.piecework.service.PieceworkSystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "计件数量或工时统计信息", tags = "计件数量或工时统计信息", modelName = "计件数量或工时统计信息")
public class PieceworkSystemController {

    @Autowired
    private PieceworkSystemService pieceworkSystemService;

    /**
     * 新增计件数量或工时统计信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writePieceworkSystem", value = "新增计件数量或工时统计信息", method = "POST", allUse = "1")
    @RequestMapping("/post/PieceworkSystemController/writePieceworkSystem")
    public void writePieceworkSystem(InputObject inputObject, OutputObject outputObject) {
        pieceworkSystemService.writePieceworkSystem(inputObject, outputObject);
    }
}
