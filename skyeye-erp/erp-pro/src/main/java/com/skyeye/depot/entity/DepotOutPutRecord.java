package com.skyeye.depot.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.common.entity.CommonInfo;
import lombok.Data;

/**
 * @ClassName: DepotOutPutRecord
 * @Description: 仓库出入库记录实体类
 * @author: skyeye云系列--卫志强
 * @date: 2022/8/14 9:20
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "erp_depot_output_record", autoResultMap = true)
@ApiModel("仓库出入库记录实体类")
public class DepotOutPutRecord extends CommonInfo {

    @TableId("id")
    @ApiModelProperty("主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "object_id", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "所属第三方业务数据id", required = "required")
    private String objectId;

    @TableField(value = "object_key", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "所属第三方业务数据的key", required = "required")
    private String objectKey;

    @TableField("material_id")
    @ApiModelProperty(value = "商品id", required = "required")
    private String materialId;

    @TableField("norms_id")
    @ApiModelProperty(value = "商品规格id", required = "required")
    private String normsId;

    @TableField("borrow_id")
    @ApiModelProperty(value = "借出申请单id")
    private String borrowId;

    @TableField("repay_id")
    @ApiModelProperty(value = "归还申请单id")
    private String repayId;

    @TableField("borrow_time")
    @ApiModelProperty(value = "借出申请时间")
    private String borrowTime;

    @TableField("repay_time")
    @ApiModelProperty(value = "归还申请时间")
    private String repayTime;

    @TableField("out_depot_time")
    @ApiModelProperty(value = "出库时间")
    private String outDepotTime;

    @TableField("put_depot_time")
    @ApiModelProperty(value = "入库时间")
    private String putDepotTime;

    @TableField("out_count")
    @ApiModelProperty(value = "出库数量")
    private Integer outCount;

    @TableField("put_count")
    @ApiModelProperty(value = "入库数量")
    private Integer putCount;

    @TableField("state")
    @ApiModelProperty(value = "0未还、1部分还、2已还")
    private Integer state;

    @TableField("norms_code")
    @ApiModelProperty(value = "商品规格编码")
    private String normsCode;
}
