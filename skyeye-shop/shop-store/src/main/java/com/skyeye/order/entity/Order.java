package com.skyeye.order.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.constans.CacheConstants;
import com.skyeye.common.entity.features.AreaInfo;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@RedisCacheField(name = "shop:order")
@TableName("shop_order")
@ApiModel("商品订单管理实体类")
public class Order extends AreaInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id")
    private String id;

    @TableField("odd_number")
    @ApiModelProperty(value = "订单编号", required = "required")
    private String oddNumber;

    @TableField("type")
    @ApiModelProperty(value = "订单类型,参考#ShopOrderType", required = "required")
    private Integer type;

    @TableField("terminal")
    @ApiModelProperty(value = "订单来源,参考#ShopOrderTerminal", required = "required")
    private Integer terminal;

    @TableField("user_ip")
    @ApiModelProperty(value = "用户ip", required = "required")
    private String userIp;

    @TableField("state")
    @Property(value = "状态, 参考#ShopOrderState")
    private Integer state;

    @TableField("count")
    @Property(value = "商品的总数量")
    private Integer count;

    @TableField("finish_time")
    @Property(value = "订单完成时间")
    private String finishTime;

    @TableField("cancel_time")
    @Property(value = "订单取消时间")
    private String cancelTime;

    @TableField("cancel_type")
    @Property(value = "取消类型,参考#ShopOrderCancelType")
    private Integer cancelType;

    @TableField("comment_state")
    @Property(value = "是否评价,参考#ShopOrderCommentState")
    private Integer commentState;

    @TableField("brokerage_user_id")
    @ApiModelProperty(value = "分销用户id")
    private String brokerageUserId;

    @TableField("pay_time")
    @Property(value = "付款时间")
    private String payTime;

    @TableField("pay_type")
    @Property(value = "付款类型")
    private String payType;

    @TableField("total_price")
    @Property(value = "商品总价，单位：分")
    private String totalPrice;

    @TableField("discount_price")
    @Property(value = "优惠金额，单位：分")
    private String discountPrice;

    @TableField("delivery_price")
    @Property(value = "运费金额，单位：分")
    private String deliveryPrice;

    @TableField("adjust_price")
    @ApiModelProperty(value = "订单调价，单位：分，正数，加价；负数，减价")
    private String adjustPrice;

    @TableField("pay_price")
    @Property(value = "应付金额（总），单位：分")
    private String payPrice;

    @TableField("delivery_type")
    @ApiModelProperty(value = "配送方式")
    private Integer deliveryType;

    @TableField("tms_order_id")
    @ApiModelProperty(value = "物流单id")
    private String tmsOrderId;

    @TableField("receive_time")
    @Property(value = "收货时间")
    private String receiveTime;

    @TableField("receiver_name")
    @ApiModelProperty(value = "收件人姓名")
    private String receiverName;

    @TableField("receiver_mobile")
    @ApiModelProperty(value = "收件人手机")
    private String receiverMobile;

    @TableField("pick_up_store_id")
    @ApiModelProperty(value = "delivery_type=自提时，自提门店id")
    private String pickUpStoreId;

    @TableField("pick_up_verify_code")
    @ApiModelProperty(value = "自提核销码")
    private String pickUpVerifyCode;

    @TableField("coupon_id")
    @ApiModelProperty(value = "优惠券id")
    private String couponId;

    @TableField(exist = false)
    @ApiModelProperty(value = "优惠券信息")
    private Map<String, Object> couponMation;

    @TableField("coupon_price")
    @Property(value = "优惠劵减免金额，单位：分")
    private String couponPrice;

    @TableField("use_point")
    @Property(value = "使用的积分")
    private Integer usePoint;

    @TableField("point_price")
    @Property(value = "积分抵扣的金额，单位：分")
    private String pointPrice;

    @TableField("give_point")
    @ApiModelProperty(value = "赠送的积分")
    private Integer givePoint;

    @TableField("vip_price")
    @Property(value = "VIP 减免金额，单位：分")
    private String vipPrice;

    @TableField("seckill_activity_id")
    @ApiModelProperty(value = "秒杀活动id")
    private String seckillActivityId;

    @TableField("bargain_activity_id")
    @ApiModelProperty(value = "砍价活动id")
    private String bargainActivityId;

    @TableField("bargain_record_id")
    @ApiModelProperty(value = "砍价记录id")
    private String bargainRecordId;

    @TableField("combination_activity_id")
    @ApiModelProperty(value = "拼团活动id")
    private String combinationActivityId;

    @TableField("combination_record_id")
    @ApiModelProperty(value = "拼团记录id")
    private String combinationRecordId;

    @TableField("user_remark")
    @ApiModelProperty(value = "用户备注")
    private String userRemark;

    @TableField("remark")
    @ApiModelProperty(value = "商家备注")
    private String remark;

    @TableField(exist = false)
    @ApiModelProperty(value = "子单列表", required = "required,json")
    private List<OrderItem> orderItemList;
}