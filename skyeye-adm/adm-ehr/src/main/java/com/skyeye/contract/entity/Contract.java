/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.contract.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.common.base.handler.enclosure.bean.Enclosure;
import com.skyeye.common.base.handler.enclosure.bean.EnclosureFace;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.OperatorUserInfo;
import lombok.Data;

import java.util.Map;

/**
 * @ClassName: Contract
 * @Description: 员工合同信息实体类
 * @author: skyeye云系列--卫志强
 * @date: 2023/11/15 11:45
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@UniqueField(value = {"contractNumber"})
@RedisCacheField(name = "ehr:contract", cacheTime = RedisConstants.THIRTY_DAY_SECONDS)
@TableName(value = "sys_staff_contract")
@ApiModel("员工合同信息实体类")
public class Contract extends OperatorUserInfo implements EnclosureFace {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "contract_number")
    @ApiModelProperty(value = "合同编号", required = "required", fuzzyLike = true)
    private String contractNumber;

    @TableField(value = "type_id")
    @ApiModelProperty(value = "合同类别，参考数据字典", required = "required")
    private String typeId;

    @TableField(exist = false)
    @Property(value = "合同类别信息")
    private Map<String, Object> typeMation;

    @TableField(value = "mold_id")
    @ApiModelProperty(value = "合同类型，参考数据字典", required = "required")
    private String moldId;

    @TableField(exist = false)
    @Property(value = "合同类型信息")
    private Map<String, Object> moldMation;

    @TableField(value = "start_time")
    @ApiModelProperty(value = "开始时间", required = "required")
    private String startTime;

    @TableField(value = "end_time")
    @ApiModelProperty(value = "结束时间", required = "required")
    private String endTime;

    @TableField(value = "object_id", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "所属第三方业务数据id(员工id)", required = "required")
    private String objectId;

    @TableField(value = "object_key", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "所属第三方业务数据的key(员工key)", required = "required")
    private String objectKey;

    @TableField(exist = false)
    @ApiModelProperty(value = "附件", required = "json")
    private Enclosure enclosure;

}
