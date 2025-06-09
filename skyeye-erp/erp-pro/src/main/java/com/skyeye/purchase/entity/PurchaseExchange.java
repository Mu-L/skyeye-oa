package com.skyeye.purchase.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.entity.ErpOrderHead;
import lombok.Data;

/**
 * @ClassName: PurchaseReturn
 * @Description: 采购换货单实体类
 * @author: skyeye云系列--卫志强
 * @date: 2022/3/23 16:19
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@RedisCacheField(name = "erp:order:purchaseExchange", cacheTime = RedisConstants.TOW_MONTH_SECONDS)
@TableName(value = "erp_depothead", autoResultMap = true)
@ApiModel("采购退货单实体类")
public class PurchaseExchange extends ErpOrderHead {
}
