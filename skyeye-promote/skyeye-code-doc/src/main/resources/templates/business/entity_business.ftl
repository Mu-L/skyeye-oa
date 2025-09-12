/*******************************************************************************
 * Copyright ${author} QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package ${project.packageName}.entity;

import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import ${project.inheritClassPath};
<#if generate.generateRedisCache>
import com.skyeye.annotation.cache.RedisCacheField;
</#if>

/**
 * @ClassName: ${tables[0].entityName}
 * @Description: ${tables[0].tableComment!''}
 * @author: ${author}
 * @date: ${date?string("yyyy/MM/dd HH:mm")}
 * @Copyright: ${date?string("yyyy")} https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 */
@Data
<#if generate.generateRedisCache>
@RedisCacheField(name = "${generate.redisCachePrefix}")
</#if>
@ApiModel("${tables[0].tableComment!''}")
@TableName(value = "${tables[0].tableName}", autoResultMap = true)
public class ${tables[0].entityName} extends ${project.inheritClass} {
<#list tables[0].fields as field>
    
    <#if field.isPrimaryKey>
    @TableId("id")
    @ApiModelProperty(value = "主键")
    <#else>
    @TableField(value = "${field.columnName}")
    @ApiModelProperty(value = "${field.remarks!''}"<#if !field.nullable>, required = "required"</#if>)
    </#if>
    private ${field.javaType} ${field.propertyName};
</#list>
}
