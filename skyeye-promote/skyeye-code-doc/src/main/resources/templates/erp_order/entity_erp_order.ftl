/*******************************************************************************
 * Copyright ${author} QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package ${project.packageName}.entity;

import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.entity.ErpOrderCommon;
import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
<#if generate.generateRedisCache>
import com.skyeye.annotation.cache.RedisCacheField;
</#if>

/**
 * @ClassName: ${tables[0].entityName}
 * @Description: ${tables[0].tableComment}实体类
 * @author: skyeye云系列--卫志强
 * @date: ${.now?string("yyyy/MM/dd HH:mm")}
 * @Copyright: ${.now?string("yyyy")} https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
<#if generate.generateRedisCache>
@RedisCacheField(name = "${generate.redisCachePrefix}")
</#if>
@ApiModel("${tables[0].tableComment!''}")
@TableName(value = "${tables[0].tableName}", autoResultMap = true)
public class ${tables[0].entityName} extends ErpOrderCommon {
<#list tables[0].fields as field>

    @TableField(value = "${field.columnName}")
    @ApiModelProperty(value = "${field.remarks!''}"<#if !field.nullable>, required = "required"</#if>)
    private ${field.javaType} ${field.propertyName};
</#list>
}
