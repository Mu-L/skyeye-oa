/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.entity.edit;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.common.entity.features.OperatorUserInfo;
import lombok.Data;

/**
 * @ClassName: EditUpload
 * @Description: 富文本资源上传的实体类
 * @author: skyeye云系列--卫志强
 * @date: 2025/6/22 14:21
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "ueditor_img")
@ApiModel("富文本资源上传的实体类")
public class EditUpload extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "img_path")
    @ApiModelProperty(value = "图片路径", required = "required")
    private String imgPath;

    @TableField(value = "file_original_name")
    @ApiModelProperty(value = "文件原始名称", required = "required")
    private String fileOriginalName;

}
