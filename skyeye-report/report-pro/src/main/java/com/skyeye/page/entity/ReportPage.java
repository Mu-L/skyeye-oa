/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.page.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.common.constans.CacheConstants;
import com.skyeye.common.entity.features.BaseGeneralInfo;
import com.skyeye.common.enumeration.DeleteFlagEnum;
import lombok.Data;

/**
 * @ClassName: ReportPage
 * @Description: 报表页面实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/4/5 10:50
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@UniqueField
@RedisCacheField(name = CacheConstants.REPORT_PAGE_CACHE_KEY)
@TableName(value = "report_page", autoResultMap = true)
@ApiModel("报表页面实体类")
public class ReportPage extends BaseGeneralInfo {

    @TableField(value = "content")
    @ApiModelProperty(value = "页面报表json串")
    private String content;

    @TableField(value = "delete_flag")
    @Property(value = "删除标记", enumClass = DeleteFlagEnum.class)
    private Integer deleteFlag;

}
