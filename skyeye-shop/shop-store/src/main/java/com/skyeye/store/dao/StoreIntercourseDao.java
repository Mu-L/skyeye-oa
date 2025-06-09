/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.store.dao;

import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.entity.intercourse.StoreIntercourseQueryDo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: StoreIntercourseDao
 * @Description: 门店往来管理数据层
 * @author: skyeye云系列--卫志强
 * @date: 2022/3/10 21:54
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface StoreIntercourseDao {

    List<Map<String, Object>> queryStoreIntercourseByDay(@Param("day") String day);

    void insertStoreIntercourse(List<Map<String, Object>> shopStoreIntercourseMationList);

    List<Map<String, Object>> queryStoreIntercourseList(StoreIntercourseQueryDo storeIntercourseQuery);

    Map<String, Object> queryStoreIntercourseById(@Param("id") String id);

    void editStoreIntercourseState(@Param("id") String id, @Param("state") Integer state);
}
