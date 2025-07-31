package com.skyeye.school.schedules.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.school.schedules.dao.ScheduleChildDao;
import com.skyeye.school.schedules.entity.ScheduleChild;
import com.skyeye.school.schedules.service.ScheduleChildService;
import org.springframework.stereotype.Service;
/**
 * @ClassName: ScheduleChildServiceImpl
 * @Description: 排课表子表接口层实现曾
 * @author: skyeye云系列--卫志强
 * @date: 2023/8/8 14:55
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的F
 */
@Service
@SkyeyeService(name = "排课表子表管理", groupName = "排课表管理")
public class ScheduleChildServiceImpl extends SkyeyeBusinessServiceImpl<ScheduleChildDao, ScheduleChild> implements ScheduleChildService {
    @Override
    public void deleteByScheduleId(String id) {
        QueryWrapper<ScheduleChild> queryWrapper = new QueryWrapper<>();
        remove(queryWrapper);
    }
}
