package com.skyeye.eve.mailInviteInbox.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.mailInviteInbox.entity.DwMailInviteInbox;
import com.skyeye.eve.mailInviteInbox.service.DwMailInviteInboxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "是非题结果保存表管理", tags = "是非题结果保存表管理", modelName = "是非题结果保存表管理")
public class DwMailInviteInboxController {

    @Autowired
    private DwMailInviteInboxService dwMailInviteInboxService;

    /**
     * 添加或修改是非题结果保存表保存表信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeDwMailInviteInbox", value = "新增/编辑是非题结果保存表保存表信息", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = DwMailInviteInbox.class)
    @RequestMapping("/post/DwMailInviteInboxController/writeDwMailInviteInbox")
    public void writeDwMailInviteInbox(InputObject inputObject, OutputObject outputObject) {
        dwMailInviteInboxService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 获取是非题结果保存表信息列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryDwMailInviteInboxList", value = "获取是非题结果保存表信息", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/DwMailInviteInboxController/queryDwMailInviteInboxList")
    public void queryDwMailInviteInboxList(InputObject inputObject, OutputObject outputObject) {
        dwMailInviteInboxService.queryPageList(inputObject, outputObject);
    }

    /**
     * 删除是非题结果保存表信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteDwMailInviteInboxById", value = "根据ID删除是非题结果保存表信息", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/DwMailInviteInboxController/deleteDwMailInviteInboxById")
    public void deleteDwMailInviteInboxById(InputObject inputObject, OutputObject outputObject) {
        dwMailInviteInboxService.deleteById(inputObject, outputObject);
    }

    /**
     * 根据id获取是非题结果保存表列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryDwMailInviteInboxListById", value = "根据id获取是非题结果保存表列表", method = "GET", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/DwMailInviteInboxController/queryDwMailInviteInboxListById")
    public void queryDwMailInviteInboxListById(InputObject inputObject, OutputObject outputObject) {
        dwMailInviteInboxService.queryDwMailInviteInboxListById(inputObject, outputObject);
    }
}
