/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.checkwork.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.checkwork.dao.SysEveUserStaffTimeDao;
import com.skyeye.checkwork.entity.SysEveUserStaffTime;
import com.skyeye.checkwork.service.SysEveUserStaffTimeService;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import org.springframework.stereotype.Service;

/**
 * @ClassName: SysEveUserStaffTimeServiceImpl
 * @Description: 员工工作时间服务实现类
 * @author: skyeye云系列--卫志强
 * @date: 2025/3/12 22:16
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "员工绑定的考勤班次", groupName = "考勤班次")
public class SysEveUserStaffTimeServiceImpl extends SkyeyeBusinessServiceImpl<SysEveUserStaffTimeDao, SysEveUserStaffTime> implements SysEveUserStaffTimeService {

    @Override
    public boolean checkIsExistByTimeId(String timeId) {
        QueryWrapper<SysEveUserStaffTime> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(SysEveUserStaffTime::getCheckWorkTimeId), timeId);
        if (count(queryWrapper) > 0) {
            return true;
        }
        return false;
    }
}
