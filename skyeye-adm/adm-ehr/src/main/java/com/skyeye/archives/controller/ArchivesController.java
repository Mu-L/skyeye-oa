/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.archives.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.archives.entity.Archives;
import com.skyeye.archives.service.ArchivesService;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: ArchivesController
 * @Description: 员工档案管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2022/5/10 16:34
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "员工档案", tags = "员工档案", modelName = "员工档案")
public class ArchivesController {

    @Autowired
    private ArchivesService archivesService;

    @ApiOperation(id = "querySysStaffArchivesList", value = "查询档案信息列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ArchivesController/querySysStaffArchivesList")
    public void querySysStaffArchivesList(InputObject inputObject, OutputObject outputObject) {
        archivesService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeSysStaffArchives", value = "新增/编辑员工档案信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = Archives.class)
    @RequestMapping("/post/ArchivesController/writeSysStaffArchives")
    public void writeSysStaffArchives(InputObject inputObject, OutputObject outputObject) {
        archivesService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteSysStaffArchivesById", value = "删除员工档案信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ArchivesController/deleteSysStaffArchivesById")
    public void deleteSysStaffArchivesById(InputObject inputObject, OutputObject outputObject) {
        archivesService.deleteById(inputObject, outputObject);
    }

}
