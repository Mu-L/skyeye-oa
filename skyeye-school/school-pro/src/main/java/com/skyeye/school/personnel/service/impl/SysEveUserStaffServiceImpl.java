package com.skyeye.school.personnel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.school.personnel.dao.SysEveUserStaffDao;
import com.skyeye.school.personnel.entity.SysEveUserStaff;
import com.skyeye.school.personnel.service.SysEveUserStaffService;
import com.skyeye.school.subject.entity.Subject;
import org.springframework.stereotype.Service;

import static com.skyeye.school.personnel.classnum.SysEveUserStaffType.SIMPLE_STAFF;
import static com.skyeye.school.personnel.classnum.SysEveUserStaffType.TEACHER;

@Service
@SkyeyeService(name = "员工管理", groupName = "员工管理")
public class SysEveUserStaffServiceImpl extends SkyeyeBusinessServiceImpl<SysEveUserStaffDao, SysEveUserStaff> implements SysEveUserStaffService {

    @Override
    public QueryWrapper<SysEveUserStaff> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<SysEveUserStaff> queryWrapper = super.getQueryWrapper(commonPageInfo);
        Integer type = Integer.valueOf(commonPageInfo.getType());
        queryWrapper.eq(MybatisPlusUtil.toColumns(SysEveUserStaff::getType),type);
        return queryWrapper;
    }

}
