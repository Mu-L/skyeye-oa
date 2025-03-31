/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.forum.service.impl;

import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.eve.forum.dao.ForumReportHiDao;
import com.skyeye.eve.forum.entity.ForumReportHi;
import com.skyeye.eve.forum.service.ForumReportHiService;
import org.springframework.stereotype.Service;

/**
 * @ClassName: ForumReportHiServiceImpl
 * @Description: 论坛帖子举报成功的记录表接口实现类
 * @author: skyeye云系列--卫志强
 * @date: 2022/8/9 9:22
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */

@Service
@SkyeyeService(name = "论坛帖子举报成功的记录表管理", groupName = "论坛帖子举报成功的记录表管理")
public class ForumReportHiServiceImpl extends SkyeyeBusinessServiceImpl<ForumReportHiDao, ForumReportHi> implements ForumReportHiService {
}
