/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.afterseal.entity;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.base.handler.enclosure.bean.Enclosure;
import com.skyeye.common.base.handler.enclosure.bean.EnclosureFace;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.OperatorUserInfo;
import lombok.Data;

import java.util.List;

/**
 * @ClassName: SealFault
 * @Description: 工单故障信息实体类
 * @author: skyeye云系列--卫志强
 * @date: 2023/8/12 17:23
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Data
@RedisCacheField(name = "seal:server:fault", cacheTime = RedisConstants.THIRTY_DAY_SECONDS)
@TableName(value = "crm_service_fault")
@ApiModel("工单故障信息实体类")
public class SealFault extends OperatorUserInfo implements EnclosureFace {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("odd_number")
    @Property(value = "单据编号", fuzzyLike = true)
    private String oddNumber;

    @TableField(value = "type_id")
    @ApiModelProperty(value = "故障类型，参考数据字典", required = "required")
    private String typeId;

    @TableField(value = "object_id")
    @ApiModelProperty(value = "工单id", required = "required")
    private String objectId;

    @TableField(value = "object_key")
    @ApiModelProperty(value = "工单的key", required = "required")
    private String objectKey;

    @TableField(value = "com_execution")
    @ApiModelProperty(value = "完成情况", required = "required")
    private String comExecution;

    @TableField(value = "com_pic")
    @ApiModelProperty(value = "完工拍照")
    private String comPic;

    @TableField(exist = false)
    @ApiModelProperty(value = "完工附件", required = "json")
    private Enclosure enclosureInfo;

    @TableField(value = "material_cost")
    @ApiModelProperty(value = "材料费")
    private String materialCost;

    @TableField(value = "cover_cost")
    @ApiModelProperty(value = "服务费", required = "double", defaultValue = "0")
    private String coverCost;

    @TableField(value = "other_cost")
    @ApiModelProperty(value = "其他费用", required = "double", defaultValue = "0")
    private String otherCost;

    @TableField(value = "all_price")
    @ApiModelProperty(value = "总费用")
    private String allPrice;

    @TableField(value = "fault_key_parts_id")
    @ApiModelProperty(value = "故障关键组件id")
    private String faultKeyPartsId;

    @TableField(value = "actual_failure")
    @ApiModelProperty(value = "实际故障")
    private String actualFailure;

    @TableField(value = "solution")
    @ApiModelProperty(value = "解决方案")
    private String solution;

    @TableField("remark")
    @ApiModelProperty(value = "完工备注")
    private String remark;

    @TableField(exist = false)
    @ApiModelProperty(value = "售后服务故障配件使用信息", required = "json")
    private List<SealFaultUseMaterial> sealFaultUseMaterialList;

}
