/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.jobtransfer.dao;

import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.eve.dao.SkyeyeBaseMapper;
import com.skyeye.jobtransfer.entity.JobTransfer;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: JobTransferDao
 * @Description: 岗位调动申请数据接口层
 * @author: skyeye云系列--卫志强
 * @date: 2022-04-27 15:57:58
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface JobTransferDao extends SkyeyeBaseMapper<JobTransfer> {

    List<Map<String, Object>> queryJobTransferList(CommonPageInfo pageInfo);

    void updateBossInterviewJobMation(JobTransfer jobTransfer);
}
