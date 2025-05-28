package com.skyeye.product.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.entity.ErpOrderHead;
import lombok.Data;

@Data
@TableName(value = "erp_depothead")
@ApiModel("借出申请实体类")
public class ProductLead extends ErpOrderHead {

}
