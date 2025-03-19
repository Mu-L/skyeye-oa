package com.skyeye.school.personnel.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.rest.promote.company.service.ICompanyAndDepartmentAndJobService;
import com.skyeye.school.chat.entity.FriendRelationship;
import com.skyeye.school.chat.service.FriendRelationshipService;
import com.skyeye.school.personnel.dao.SysEveUserStaffDao;
import com.skyeye.school.personnel.entity.SysEveUserStaff;
import com.skyeye.school.personnel.service.SysEveUserStaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@SkyeyeService(name = "员工管理", groupName = "员工管理")
public class SysEveUserStaffServiceImpl extends SkyeyeBusinessServiceImpl<SysEveUserStaffDao, SysEveUserStaff> implements SysEveUserStaffService {

    @Autowired
    private FriendRelationshipService friendRelationshipService;

    @Autowired
    private ICompanyAndDepartmentAndJobService iCompanyService;

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
            queryWrapper.like(MybatisPlusUtil.toColumns(SysEveUserStaff::getUserName), name);
        }
        if (StrUtil.isNotEmpty(jobNumber)) {
            queryWrapper.like(MybatisPlusUtil.toColumns(SysEveUserStaff::getJobNumber), jobNumber);
        }
        return list(queryWrapper);
    }

    @Override
    public void querySysUserStaffByUserId(InputObject inputObject, OutputObject outputObject) {
        String userId = inputObject.getParams().get("userId").toString();
        String id = inputObject.getParams().get("id").toString();
        QueryWrapper<SysEveUserStaff> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(SysEveUserStaff::getUserId), userId);
        List<SysEveUserStaff> sysEveUserStaffs = list(queryWrapper);
        for (SysEveUserStaff sysEveUserStaff : sysEveUserStaffs) {
            QueryWrapper<FriendRelationship> queryWrapper1 = new QueryWrapper<>();
            queryWrapper1
                    .eq(MybatisPlusUtil.toColumns(FriendRelationship::getUserId), sysEveUserStaff.getId())
                    .eq(MybatisPlusUtil.toColumns(FriendRelationship::getFriendId), id)
                    .or()
                    .eq(MybatisPlusUtil.toColumns(FriendRelationship::getFriendId), sysEveUserStaff.getId())
                    .eq(MybatisPlusUtil.toColumns(FriendRelationship::getUserId), id);
            List<FriendRelationship> list = friendRelationshipService.list(queryWrapper1);
            sysEveUserStaff.setFriendMation(list);
            String companyId = sysEveUserStaff.getCompanyId();
            String departmentId = sysEveUserStaff.getDepartmentId();
            String jobId = sysEveUserStaff.getJobId();
            List<Map<String, Object>> map = iCompanyService.queryCompanyInfoByCompanyIdAndDepartmentIdAndJobId(companyId, departmentId, jobId);
            sysEveUserStaff.setCompanyMation(map.get(CommonNumConstants.NUM_ZERO));
        }
        if (CollectionUtil.isNotEmpty(sysEveUserStaffs)) {
            outputObject.setBeans(sysEveUserStaffs);
            outputObject.settotal(sysEveUserStaffs.size());
        }
    }
}
