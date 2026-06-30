/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.tenant.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.constans.CacheConstants;
import com.skyeye.common.entity.CommonInfo;
import lombok.Data;

import java.util.Map;

/**
 * @ClassName: PlatformBaseSetting
 * @Description: 平台基础信息设置实体类
 * <p>
 * 多租户模式下平台级全局配置，全系统仅一条记录。
 * settingData 示例：{"tenant": {"accountUnitPrice": "99.00"}}
 */
@Data
@RedisCacheField(name = CacheConstants.PLATFORM_BASE_SETTING_CACHE_KEY)
@TableName(value = "platform_base_setting", autoResultMap = true)
@ApiModel("平台基础信息设置实体类")
public class PlatformBaseSetting extends CommonInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "setting_data", typeHandler = JacksonTypeHandler.class)
    @ApiModelProperty(value = "分组设置数据。key 为分组标识（见 PlatformBaseSettingGroup），value 为该分组下的键值对", required = "json")
    private Map<String, Map<String, Object>> settingData;

}
