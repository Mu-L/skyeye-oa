package com.skyeye.school.personnel.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.school.personnel.dao.SysEveUserStaffDao;
import com.skyeye.school.personnel.entity.SysEveUserStaff;
import com.skyeye.school.personnel.service.SysEveUserStaffService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@SkyeyeService(name = "员工管理", groupName = "员工管理")
public class SysEveUserStaffServiceImpl extends SkyeyeBusinessServiceImpl<SysEveUserStaffDao, SysEveUserStaff> implements SysEveUserStaffService {

    @Override
    public QueryWrapper<SysEveUserStaff> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<SysEveUserStaff> queryWrapper = super.getQueryWrapper(commonPageInfo);
        Integer type = Integer.valueOf(commonPageInfo.getType());
        queryWrapper.eq(MybatisPlusUtil.toColumns(SysEveUserStaff::getType), type);
        return queryWrapper;
    }

    @Override
    public List<SysEveUserStaff> selectByName(String name, String jobNumber) {
        QueryWrapper<SysEveUserStaff> queryWrapper = new QueryWrapper<>();
        if (StrUtil.isNotEmpty(name)) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(SysEveUserStaff::getUserName), name);
        }
        if (StrUtil.isNotEmpty(jobNumber)) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(SysEveUserStaff::getJobNumber), jobNumber);
        }
        return list(queryWrapper);
    }
}
