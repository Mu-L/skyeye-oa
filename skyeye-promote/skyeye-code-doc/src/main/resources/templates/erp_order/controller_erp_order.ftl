package ${project.packageName}.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.business.controller.SkyeyeErpOrderController;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import ${project.packageName}.entity.${tables[0].entityName};
import ${project.packageName}.service.${tables[0].entityName}Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: ${tables[0].entityName}Controller
 * @Description: ${tables[0].tableComment}控制器
 * @author: skyeye云系列--卫志强
 * @date: ${.now?string("yyyy/MM/dd HH:mm")}
 * @Copyright: ${.now?string("yyyy")} https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "${tables[0].tableComment}", tags = "${tables[0].tableComment}", modelName = "${tables[0].tableComment}")
@RequestMapping("/${tables[0].entityName?uncap_first}")
@SkyeyeService(name = "${tables[0].tableComment}", groupName = "${tables[0].tableName}", flowable = true)
public class ${tables[0].entityName}Controller extends SkyeyeErpOrderController<${tables[0].entityName}Service, ${tables[0].entityName}> {

    @Autowired
    private ${tables[0].entityName}Service ${tables[0].entityName?uncap_first}Service;

    /**
     * 获取${tables[0].tableComment}列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "query${tables[0].entityName}List", value = "获取${tables[0].tableComment}列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/${tables[0].entityName}Controller/query${tables[0].entityName}List")
    public void query${tables[0].entityName}List(InputObject inputObject, OutputObject outputObject) {
        ${tables[0].entityName?uncap_first}Service.queryPageList(inputObject, outputObject);
    }

    /**
     * 新增/编辑${tables[0].tableComment}信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "write${tables[0].entityName}Mation", value = "新增/编辑${tables[0].tableComment}信息", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = ${tables[0].entityName}.class)
    @RequestMapping("/post/${tables[0].entityName}Controller/write${tables[0].entityName}Mation")
    public void write${tables[0].entityName}Mation(InputObject inputObject, OutputObject outputObject) {
        ${tables[0].entityName?uncap_first}Service.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 根据id获取${tables[0].tableComment}信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "query${tables[0].entityName}ById", value = "根据id获取${tables[0].tableComment}信息", method = "GET", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/${tables[0].entityName}Controller/query${tables[0].entityName}ById")
    public void query${tables[0].entityName}ById(InputObject inputObject, OutputObject outputObject) {
        ${tables[0].entityName?uncap_first}Service.selectById(inputObject, outputObject);
    }

    /**
     * 根据id批量获取${tables[0].tableComment}信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "query${tables[0].entityName}ByIds", value = "根据id批量获取${tables[0].tableComment}信息", method = "POST", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "ids", name = "ids", value = "主键id", required = "required")})
    @RequestMapping("/post/${tables[0].entityName}Controller/query${tables[0].entityName}ByIds")
    public void query${tables[0].entityName}ByIds(InputObject inputObject, OutputObject outputObject) {
        ${tables[0].entityName?uncap_first}Service.selectByIds(inputObject, outputObject);
    }

    /**
     * 删除${tables[0].tableComment}信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "delete${tables[0].entityName}ById", value = "删除${tables[0].tableComment}信息", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/${tables[0].entityName}Controller/delete${tables[0].entityName}ById")
    public void delete${tables[0].entityName}ById(InputObject inputObject, OutputObject outputObject) {
        ${tables[0].entityName?uncap_first}Service.deleteById(inputObject, outputObject);
    }

    /**
     * 获取所有${tables[0].tableComment}列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryAll${tables[0].entityName}List", value = "获取所有${tables[0].tableComment}列表", method = "GET", allUse = "2")
    @RequestMapping("/post/${tables[0].entityName}Controller/queryAll${tables[0].entityName}List")
    public void queryAll${tables[0].entityName}List(InputObject inputObject, OutputObject outputObject) {
        ${tables[0].entityName?uncap_first}Service.queryAllList(inputObject, outputObject);
    }

    /**
     * 获取${tables[0].tableComment}详情信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "query${tables[0].entityName}DetailsMationById", value = "获取${tables[0].tableComment}详情信息", method = "GET", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/${tables[0].entityName}Controller/query${tables[0].entityName}DetailsMationById")
    public void query${tables[0].entityName}DetailsMationById(InputObject inputObject, OutputObject outputObject) {
        ${tables[0].entityName?uncap_first}Service.queryDetailsMationById(inputObject, outputObject);
    }

    /**
     * 提交${tables[0].tableComment}审核
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "submit${tables[0].entityName}ToApproval", value = "提交${tables[0].tableComment}审核", method = "PUT", allUse = "1")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required"),
        @ApiImplicitParam(id = "approvalId", name = "approvalId", value = "审批人", required = "required"),
        @ApiImplicitParam(id = "modelKey", name = "modelKey", value = "模型的key", required = "required")})
    @RequestMapping("/post/${tables[0].entityName}Controller/submit${tables[0].entityName}ToApproval")
    public void submit${tables[0].entityName}ToApproval(InputObject inputObject, OutputObject outputObject) {
        ${tables[0].entityName?uncap_first}Service.submitToApproval(inputObject, outputObject);
    }

    /**
     * 撤销${tables[0].tableComment}审批
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "revoke${tables[0].entityName}Approval", value = "撤销${tables[0].tableComment}审批", method = "PUT", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "processInstanceId", name = "processInstanceId", value = "流程实例id", required = "required")})
    @RequestMapping("/post/${tables[0].entityName}Controller/revoke${tables[0].entityName}Approval")
    public void revoke${tables[0].entityName}Approval(InputObject inputObject, OutputObject outputObject) {
        ${tables[0].entityName?uncap_first}Service.revokeApproval(inputObject, outputObject);
    }

}
