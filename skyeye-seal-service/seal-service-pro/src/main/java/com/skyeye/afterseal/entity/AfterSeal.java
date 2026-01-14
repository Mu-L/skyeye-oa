/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.afterseal.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.skyeye.afterseal.classenum.AfterSealState;
import com.skyeye.afterseal.classenum.ProductWarrantyType;
import com.skyeye.afterseal.classenum.SealOrderType;
import com.skyeye.afterseal.classenum.ServiceAssignType;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.base.handler.enclosure.bean.Enclosure;
import com.skyeye.common.base.handler.enclosure.bean.EnclosureFace;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.worker.entity.SealWorker;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: AfterSeal
 * @Description: 工单实体类
 * @author: skyeye云系列--卫志强
 * @date: 2023/8/10 13:20
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Data
@RedisCacheField(name = "seal:server:order", cacheTime = RedisConstants.HALF_A_YEAR_SECONDS)
@TableName(value = "crm_service", autoResultMap = true)
@ApiModel("工单实体类")
public class AfterSeal extends OperatorUserInfo implements EnclosureFace {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("odd_number")
    @Property(value = "单据编号", fuzzyLike = true)
    private String oddNumber;

    @TableField(value = "declaration_time")
    @ApiModelProperty(value = "报单时间", required = "required")
    private String declarationTime;

    @TableField(value = "declaration_id", updateStrategy = FieldStrategy.NEVER)
    @Property(value = "用户报单人的id")
    private String declarationId;

    @TableField(exist = false)
    @Property(value = "用户报单人信息")
    private Map<String, Object> declarationMation;

    @TableField(value = "declaration_open_id")
    @ApiModelProperty(value = "微信报单人的open_id")
    private String declarationOpenId;

    @TableField(value = "project_id", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "项目ID")
    private String projectId;

    @TableField(value = "order_type_id")
    @ApiModelProperty(value = "工单类型ID", required = "required")
    private String orderTypeId;

    @TableField(exist = false)
    @Property(value = "工单类型信息")
    private com.skyeye.ordertype.entity.SealOrderType orderTypeMation;

    @TableField(exist = false)
    @Property(value = "微信报单人信息")
    private Map<String, Object> declarationOpenMation;

    @TableField("holder_id")
    @ApiModelProperty(value = "关联的客户/会员id")
    private String holderId;

    @TableField(exist = false)
    @Property(value = "关联的客户/会员的className")
    private Map<String, Object> holderMation;

    @TableField("holder_key")
    @ApiModelProperty(value = "关联的客户/会员的className")
    private String holderKey;

    @TableField(value = "contacts")
    @ApiModelProperty(value = "联系人姓名", required = "required")
    private String contacts;

    @TableField(value = "phone")
    @ApiModelProperty(value = "联系电话", required = "required")
    private String phone;

    @TableField(value = "type_id")
    @ApiModelProperty(value = "服务类型，参考数据字典", required = "required")
    private String typeId;

    @TableField(exist = false)
    @Property(value = "服务类型信息")
    private Map<String, Object> typeMation;

    @TableField(value = "product_id")
    @ApiModelProperty(value = "产品id")
    private String productId;

    @TableField(exist = false)
    @Property(value = "产品信息")
    private Map<String, Object> productMation;

    @TableField(value = "product_warranty")
    @ApiModelProperty(value = "质保类型", enumClass = ProductWarrantyType.class, required = "num")
    private Integer productWarranty;

    @TableField(value = "mode_id")
    @ApiModelProperty(value = "服务处理方式，参考数据字典", required = "required")
    private String modeId;

    @TableField(value = "content")
    @ApiModelProperty(value = "服务内容", required = "required")
    private String content;

    @TableField(value = "urgency_id")
    @ApiModelProperty(value = "紧急程度，参考数据字典", required = "required")
    private String urgencyId;

    @TableField(value = "assign_type")
    @ApiModelProperty(value = "服务人员指派方式", enumClass = ServiceAssignType.class)
    private String assignType;

    @TableField(value = "service_user_id")
    @ApiModelProperty(value = "服务人员id")
    private String serviceUserId;

    @TableField(exist = false)
    @Property(value = "服务人员信息")
    private SealWorker serviceUserMation;

    @TableField(value = "service_time")
    @Property(value = "派工时间")
    private String serviceTime;

    @TableField(value = "cooperation_user_id", typeHandler = JacksonTypeHandler.class)
    @ApiModelProperty(value = "协作人员id", required = "json")
    private List<String> cooperationUserId;

    @TableField(exist = false)
    @Property(value = "协作人员信息")
    private List<Map<String, Object>> cooperationUserMation;

    @TableField(value = "sheet_picture")
    @ApiModelProperty(value = "工单图片")
    private String sheetPicture;

    @TableField(exist = false)
    @ApiModelProperty(value = "附件", required = "json")
    private Enclosure enclosureInfo;

    @TableField(value = "state")
    @Property(value = "状态", enumClass = AfterSealState.class)
    private String state;

    @TableField(value = "type", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "工单来源类型", enumClass = SealOrderType.class, required = "required,num", defaultValue = "2")
    private Integer type;

    @TableField(value = "install_fee")
    @ApiModelProperty(value = "安装费用", required = "double", defaultValue = "0")
    private String installFee;

}
