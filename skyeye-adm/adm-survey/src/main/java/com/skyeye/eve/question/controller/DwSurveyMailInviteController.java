package com.skyeye.eve.question.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.question.entity.DwSurveyDirectory;
import com.skyeye.eve.question.entity.DwSurveyMailInvite;
import com.skyeye.eve.question.service.DwSurveyDirectoryService;
import com.skyeye.eve.question.service.DwSurveyMailInviteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "问卷发送邮件服务管理", tags = "问卷发送邮件服务管理", modelName = "问卷发送邮件服务管理")
public class DwSurveyMailInviteController {

    @Autowired
    private DwSurveyMailInviteService dwSurveyMailInviteService;

    /**
     * 新增/编辑邮件
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeDwSurveyMailInvite", value = "新增/编辑邮件", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = DwSurveyMailInvite.class)
    @RequestMapping("/post/DwSurveyMailInviteController/writeDwSurveyMailInvite")
    public void writeDwSurveyMailInvite(InputObject inputObject, OutputObject outputObject) {
        dwSurveyMailInviteService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 分页获取所有邮件服务信息列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryDwSurveyMailInviteList", value = "分页获取所有邮件服务信息列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/DwSurveyMailInviteController/queryDwSurveyMailInviteList")
    public void queryDwSurveyMailInviteList(InputObject inputObject, OutputObject outputObject) {
        dwSurveyMailInviteService.queryDwSurveyMailInviteList(inputObject, outputObject);
    }

    /**
     * 分页获取我的邮件服务信息列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryMyDwSurveyMailInviteList", value = "分页获取我的邮件服务信息列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/DwSurveyMailInviteController/queryMyDwSurveyMailInviteList")
    public void queryMyDwSurveyMailInviteList(InputObject inputObject, OutputObject outputObject) {
        dwSurveyMailInviteService.queryMyDwSurveyMailInviteList(inputObject, outputObject);
    }

    /**
     * 根据ID删除邮件服务信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteDwSurveyMailInviteById", value = "根据ID删除邮件服务信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/DwSurveyMailInviteController/deleteDwSurveyMailInviteById")
    public void deleteDwSurveyMailInviteById(InputObject inputObject, OutputObject outputObject) {
        dwSurveyMailInviteService.deleteById(inputObject, outputObject);
    }

}
