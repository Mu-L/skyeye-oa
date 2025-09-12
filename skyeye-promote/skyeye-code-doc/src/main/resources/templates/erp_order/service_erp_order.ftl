package ${project.packageName}.service;

import com.skyeye.business.service.SkyeyeErpOrderService;
import ${project.packageName}.entity.${tables[0].entityName};

/**
 * @ClassName: ${tables[0].entityName}Service
 * @Description: ${tables[0].tableComment}服务接口
 * @author: skyeye云系列--卫志强
 * @date: ${.now?string("yyyy/MM/dd HH:mm")}
 * @Copyright: ${.now?string("yyyy")} https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface ${tables[0].entityName}Service extends SkyeyeErpOrderService<${tables[0].entityName}> {

}
