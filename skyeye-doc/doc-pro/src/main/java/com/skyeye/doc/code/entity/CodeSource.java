/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.doc.code.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.entity.features.OperatorUserInfo;
import lombok.Data;

/**
 * @ClassName: CodeSource
 * @Description: 源代码实体类
 * @author: skyeye云系列--卫志强
 * @date: 2025/8/17 21:12
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Data
@RedisCacheField(name = "code:source")
@TableName(value = "code_source")
@ApiModel("源代码")
public class CodeSource extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty("主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("version_id")
    @ApiModelProperty(value = "所属版本id", required = "required")
    private String versionId;

    @TableField(exist = false)
    @Property(value = "所属版本信息")
    private CodeVersion versionMation;

    @TableField("pachage_id")
    @ApiModelProperty(value = "所属源代码包id", required = "required")
    private String pachageId;

    @TableField(exist = false)
    @Property(value = "所属源代码包信息")
    private CodePackage pachageMation;

    @TableField("file_path")
    @ApiModelProperty(value = "文件地址", required = "required")
    private String filePath;

    @TableField(exist = false)
    @Property(value = "所属年份")
    private String year;

}
