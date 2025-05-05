package com.skyeye.store.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.features.AreaInfo;
import lombok.Data;

import java.util.Map;

@Data
//@RedisCacheField(name = "shop:addresshistory", cacheTime = RedisConstants.THIRTY_DAY_SECONDS)
@TableName(value = "shop_address_history")
@ApiModel("历史收件地址管理实体类")
public class ShopAddressHistory extends AreaInfo {

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

    @TableField(value = "order_id")
    @ApiModelProperty(value = "订单id,为空则为修改address表产生的历史记录，不为空则为修改订单产生历史数据", required = "required")
    private String orderId;

    @TableField(exist = false)
    @Property(value = "地址标签id")
    private Map<String, Object> labelMation;

    @TableField(value = "parent_id")
    @Property(value = "address表的id")
    private String parentId;
}
