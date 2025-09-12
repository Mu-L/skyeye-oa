/*******************************************************************************
 * Copyright ${author} QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package ${project.packageName}.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import java.util.Date;
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
@RedisCacheField(name = "gw:model")
</#if>
@ApiModel(value = "${tables[0].entityName}", description = "${tables[0].tableComment!''}")
@TableName("${tables[0].tableName}")
public class ${tables[0].entityName} {
<#list tables[0].fields as field>
    
    <#if field.isPrimaryKey>
    @TableId(type = IdType.AUTO)
    <#else>
    @TableField("${field.fieldName}")
    </#if>
    @ApiModelProperty(value = "${field.fieldComment!''}", name = "${field.propertyName}")
    private ${field.javaType} ${field.propertyName};
</#list>
}
