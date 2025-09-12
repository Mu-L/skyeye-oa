package ${project.packageName}.dao;

import com.skyeye.eve.dao.SkyeyeBaseMapper;
import ${project.packageName}.entity.${tables[0].entityName};
import org.apache.ibatis.annotations.Mapper;

/**
 * @ClassName: ${tables[0].entityName}Dao
 * @Description: ${tables[0].tableComment}数据访问层
 * @author: skyeye云系列--卫志强
 * @date: ${.now?string("yyyy/MM/dd HH:mm")}
 * @Copyright: ${.now?string("yyyy")} https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Mapper
public interface ${tables[0].entityName}Dao extends SkyeyeBaseMapper<${tables[0].entityName}> {

}
