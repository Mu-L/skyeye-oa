package ${project.packageName}.service.impl;

import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeFlowableServiceImpl;
import ${project.packageName}.dao.${tables[0].entityName}Dao;
import ${project.packageName}.entity.${tables[0].entityName};
import ${project.packageName}.service.${tables[0].entityName}Service;
import org.springframework.stereotype.Service;

/**
 * @ClassName: ${tables[0].entityName}ServiceImpl
 * @Description: ${tables[0].tableComment}服务实现类
 * @author: skyeye云系列--卫志强
 * @date: ${.now?string("yyyy/MM/dd HH:mm")}
 * @Copyright: ${.now?string("yyyy")} https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "${tables[0].tableComment}", groupName = "${tables[0].tableName}", flowable = true)
public class ${tables[0].entityName}ServiceImpl extends SkyeyeFlowableServiceImpl<${tables[0].entityName}Dao, ${tables[0].entityName}> implements ${tables[0].entityName}Service {

}
