/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.forum.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: ContentStateEnum
 * @Description: 成绩子表信息的成绩类型枚举
 * @author: skyeye云系列--卫志强
 * @date: 2022/9/11 13:17
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ContentStateEnum implements SkyeyeEnumClass {

    NOT_DELETE(1,"正常",true,true),

    DELETE(2,"删除",true,false);

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;
}
