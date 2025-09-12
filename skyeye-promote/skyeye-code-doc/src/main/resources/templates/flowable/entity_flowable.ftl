package ${project.packageName}.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.common.entity.features.SkyeyeFlowable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @ClassName: ${tables[0].entityName}
 * @Description: ${tables[0].tableComment}实体类
 * @author: skyeye云系列--卫志强
 * @date: ${.now?string("yyyy/MM/dd HH:mm")}
 * @Copyright: ${.now?string("yyyy")} https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("${tables[0].tableName}")
public class ${tables[0].entityName} extends SkyeyeFlowable {

<#list tables[0].fields as field>
    /**
     * ${field.remarks!""}
     */
    private ${field.javaType} ${field.propertyName};

</#list>
}
