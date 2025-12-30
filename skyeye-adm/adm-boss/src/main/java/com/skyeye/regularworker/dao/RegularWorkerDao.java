/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.regularworker.dao;

import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.eve.dao.SkyeyeBaseMapper;
import com.skyeye.regularworker.entity.RegularWorker;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: RegularWorkerDao
 * @Description: 转正申请数据接口层
 * @author: skyeye云系列--卫志强
 * @date: 2022-04-24 15:16:26
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface RegularWorkerDao extends SkyeyeBaseMapper<RegularWorker> {

    List<Map<String, Object>> queryBossRegularWorkerList(CommonPageInfo pageInfo);

    /**
     * 修改员工状态为正式工
     *
     * @param userId      用户id
     * @param state       状态
     * @param regularTime 转正日期
     */
    void updateUserStaffState(@Param("userId") String userId, @Param("state") Integer state,
                              @Param("regularTime") String regularTime);
}
