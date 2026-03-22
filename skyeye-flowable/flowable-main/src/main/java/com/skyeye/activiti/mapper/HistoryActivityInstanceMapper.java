package com.skyeye.activiti.mapper;

import com.skyeye.annotation.tenant.IgnoreTenant;
import org.apache.ibatis.annotations.Param;

/**
 * @ClassName: HistoryActivityInstanceMapper
 * @Description:
 * @author: skyeye云系列--卫志强
 * @date: 2021/12/18 0:32
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface HistoryActivityInstanceMapper {

    @IgnoreTenant
    int delete(@Param("id") String id);

}
