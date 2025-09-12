/*******************************************************************************
 * Copyright ${author} QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package ${project.packageName}.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.features.SubmitSkyeyeFlowable;
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
public class ${tables[0].entityName}Controller {

    @Autowired
    private ${tables[0].entityName}Service ${tables[0].entityName?uncap_first}Service;

    @ApiOperation(id = "query${tables[0].entityName}List", value = "获取${tables[0].tableComment}列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/${tables[0].entityName}Controller/query${tables[0].entityName}List")
    public void query${tables[0].entityName}List(InputObject inputObject, OutputObject outputObject) {
        ${tables[0].entityName?uncap_first}Service.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "write${tables[0].entityName}", value = "新增/编辑${tables[0].tableComment}", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = ${tables[0].entityName}.class)
    @RequestMapping("/post/${tables[0].entityName}Controller/write${tables[0].entityName}")
    public void write${tables[0].entityName}(InputObject inputObject, OutputObject outputObject) {
        ${tables[0].entityName?uncap_first}Service.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "query${tables[0].entityName}ById", value = "根据id获取${tables[0].tableComment}", method = "GET", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/${tables[0].entityName}Controller/query${tables[0].entityName}ById")
    public void query${tables[0].entityName}ById(InputObject inputObject, OutputObject outputObject) {
        ${tables[0].entityName?uncap_first}Service.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "delete${tables[0].entityName}ById", value = "删除${tables[0].tableComment}信息", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/${tables[0].entityName}Controller/delete${tables[0].entityName}ById")
    public void delete${tables[0].entityName}ById(InputObject inputObject, OutputObject outputObject) {
        ${tables[0].entityName?uncap_first}Service.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "submit${tables[0].entityName}ToApproval", value = "${tables[0].tableComment}提交审核", method = "PUT", allUse = "1")
    @ApiImplicitParams(classBean = SubmitSkyeyeFlowable.class)
    @RequestMapping("/post/${tables[0].entityName}Controller/submit${tables[0].entityName}ToApproval")
    public void submit${tables[0].entityName}ToApproval(InputObject inputObject, OutputObject outputObject) {
        ${tables[0].entityName?uncap_first}Service.submitToApproval(inputObject, outputObject);
    }

    @ApiOperation(id = "revoke${tables[0].entityName}Approval", value = "撤销${tables[0].tableComment}审批", method = "PUT", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "processInstanceId", name = "processInstanceId", value = "流程实例id", required = "required")})
    @RequestMapping("/post/${tables[0].entityName}Controller/revoke${tables[0].entityName}Approval")
    public void revoke${tables[0].entityName}Approval(InputObject inputObject, OutputObject outputObject) {
        ${tables[0].entityName?uncap_first}Service.revokeApproval(inputObject, outputObject);
    }


}
