/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.doc.code.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.BaseGeneralInfo;
import lombok.Data;

/**
 * @ClassName: CodePackage
 * @Description: 代码包实体类
 * @author: skyeye云系列--卫志强
 * @date: 2025/8/17 17:34
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Data
@UniqueField
@RedisCacheField(name = "code:package", cacheTime = RedisConstants.THIRTY_DAY_SECONDS)
@TableName(value = "sys_staff_archives")
@ApiModel("源代码包")
public class CodePackage extends BaseGeneralInfo {

}
