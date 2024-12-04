/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.store.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.AreaInfo;
import com.skyeye.common.enumeration.WhetherEnum;
import lombok.Data;

import java.util.Map;

/**
 * @ClassName: ShopAddress
 * @Description: 收件地址实体类
 * @author: skyeye云系列--卫志强
 * @date: 2022/2/4 10:06
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@RedisCacheField(name = "shop:address", cacheTime = RedisConstants.THIRTY_DAY_SECONDS)
@TableName(value = "shop_address")
@ApiModel("收件地址管理实体类")
public class ShopAddress extends AreaInfo {

    @TableId("id")
    @ApiModelProperty("主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("name")
    @ApiModelProperty(value = "收件人名称", required = "required")
    private String name;

    @TableField("mobile")
    @ApiModelProperty(value = "手机号", required = "required")
    private String mobile;

    @TableField(value = "label_id")
    @ApiModelProperty(value = "地址标签id")
    private String labelId;

    @TableField(exist = false)
    @Property(value = "地址标签id")
    private Map<String, Object> labelMation;

    @TableField("is_default")
    @ApiModelProperty(value = "是否是默认地址", required = "required,num", enumClass = WhetherEnum.class)
    private Integer isDefault;
}