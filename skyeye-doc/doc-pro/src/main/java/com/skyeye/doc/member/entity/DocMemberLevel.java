/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.doc.member.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.common.entity.features.BaseGeneralInfo;
import com.skyeye.common.enumeration.WhetherEnum;
import lombok.Data;

/**
 * @ClassName: DocMemverLevel
 * @Description: 文档会员等级实体类
 * @author: skyeye云系列--卫志强
 * @date: 2025/8/20 9:08
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Data
@UniqueField
@RedisCacheField(name = "member:level")
@TableName(value = "member_level", autoResultMap = true)
@ApiModel("文档会员等级实体类")
public class DocMemberLevel extends BaseGeneralInfo {

    @TableField(value = "get_permanent")
    @ApiModelProperty(value = "是否永久获取所有版本", enumClass = WhetherEnum.class, required = "required,num")
    private String getPermanent;

}
