/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.entity.sticky.StickyNotes;
import com.skyeye.eve.service.StickyNotesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: StickyNotesController
 * @Description: 便签模块控制层
 * @author: skyeye云系列--卫志强
 * @date: 2024/8/20 22:00
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "便签模块", tags = "便签模块", modelName = "便签模块")
public class StickyNotesController {

    @Autowired
    private StickyNotesService stickyNotesService;

    @ApiOperation(id = "writeStickyNotes", value = "新增/编辑便签", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = StickyNotes.class)
    @RequestMapping("/post/StickyNotesController/writeStickyNotes")
    public void writeStickyNotes(InputObject inputObject, OutputObject outputObject) {
        stickyNotesService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "queryStickyNotesList", value = "查询便签", method = "GET", allUse = "2")
    @RequestMapping("/post/StickyNotesController/queryStickyNotesList")
    public void queryStickyNotesList(InputObject inputObject, OutputObject outputObject) {
        stickyNotesService.queryStickyNotesList(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteStickyNotesById", value = "删除便签", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/StickyNotesController/deleteStickyNotesById")
    public void deleteStickyNotesById(InputObject inputObject, OutputObject outputObject) {
        stickyNotesService.deleteById(inputObject, outputObject);
    }

}
