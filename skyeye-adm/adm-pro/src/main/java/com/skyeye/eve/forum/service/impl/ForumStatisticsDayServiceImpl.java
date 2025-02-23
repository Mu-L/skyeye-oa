package com.skyeye.eve.forum.service.impl;


import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.eve.forum.dao.ForumStatisticsDayDao;
import com.skyeye.eve.forum.entity.ForumStatisticsDay;
import com.skyeye.eve.forum.service.ForumStatisticsDayService;
import org.springframework.stereotype.Service;

/**
 * @ClassName: ForumStatisticsDayServiceImpl
 * @Description: 论坛贴子每日的统计表实现层
 * @author: skyeye云系列--卫志强
 * @date: 2022/8/9 9:22
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */

@Service
@SkyeyeService(name = "论坛贴子每日的统计表管理", groupName = "论坛贴子每日的统计表管理")
public class ForumStatisticsDayServiceImpl extends SkyeyeBusinessServiceImpl<ForumStatisticsDayDao, ForumStatisticsDay> implements ForumStatisticsDayService {
}
