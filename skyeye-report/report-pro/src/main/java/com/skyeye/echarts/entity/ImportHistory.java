/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.echarts.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.features.OperatorUserInfo;
import lombok.Data;

/**
 * @ClassName: ImportHistory
 * @Description: Echarts导入历史
 * @author: skyeye云系列--卫志强
 * @date: 2024/4/3 9:10
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "report_import_history", autoResultMap = true)
@ApiModel("Echarts导入历史实体类")
public class ImportHistory extends OperatorUserInfo {

    @TableId("id")
    @Property("主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("`name`")
    @Property(value = "名称", fuzzyLike = true)
    private String name;

    @TableField("model_code")
    @Property(value = "模型code")
    private String modelCode;

    @TableField("size")
    @Property(value = "文件大小")
    private String size;

}
