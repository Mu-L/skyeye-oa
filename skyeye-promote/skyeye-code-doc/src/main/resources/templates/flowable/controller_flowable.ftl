package ${project.packageName}.controller;

import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.controller.SkyeyeFlowableController;
import ${project.packageName}.entity.${tables[0].entityName};
import ${project.packageName}.service.${tables[0].entityName}Service;
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
@RequestMapping("/${tables[0].entityName?uncap_first}")
@SkyeyeService(name = "${tables[0].tableComment}", groupName = "${tables[0].tableName}", flowable = true)
public class ${tables[0].entityName}Controller extends SkyeyeFlowableController<${tables[0].entityName}Service, ${tables[0].entityName}> {

}
