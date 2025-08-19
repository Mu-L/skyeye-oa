/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.doc.code.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.common.entity.features.BaseGeneralInfo;
import com.skyeye.common.enumeration.WhetherEnum;
import lombok.Data;

/**
 * @ClassName: CodePackage
 * @Description: 代码包实体类
 * @author: skyeye云系列--卫志强
 * @date: 2025/8/17 17:34
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Data
@UniqueField
@RedisCacheField(name = "code:package")
@TableName(value = "code_package")
@ApiModel("源代码包")
public class CodePackage extends BaseGeneralInfo {

    @TableField("start_version_id")
    @ApiModelProperty(value = "起始版本id，从哪个版本开始的", required = "required")
    private String startVersionId;

    @TableField(exist = false)
    @Property(value = "起始版本信息")
    private CodeVersion startVersionMation;

    @TableField("end_version_id")
    @ApiModelProperty(value = "结束版本id，从哪个版本结束的")
    private String endVersionId;

    @TableField(exist = false)
    @Property(value = "结束版本信息")
    private CodeVersion endVersionMation;

    @TableField("need_buy")
    @ApiModelProperty(value = "是否需要单独购买", enumClass = WhetherEnum.class, required = "required,num")
    private Integer needBuy;

}
