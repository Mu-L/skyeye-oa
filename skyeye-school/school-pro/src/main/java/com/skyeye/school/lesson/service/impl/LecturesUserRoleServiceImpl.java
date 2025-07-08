package com.skyeye.school.lesson.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.school.lesson.dao.LecturesUserRoleDao;
import com.skyeye.school.lesson.entity.LecturesUserRole;
import com.skyeye.school.lesson.service.LecturesUserRoleService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: LecturesUserRoleService
 * @Description: 质评用户角色关联接口层
 * @author: skyeye云系列--卫志强
 * @date: 2023/8/8 14:55
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的F
 */
@Service
@SkyeyeService(name = "质评用户角色关联管理", groupName = "质评用户角色关联管理")
public class LecturesUserRoleServiceImpl extends SkyeyeBusinessServiceImpl<LecturesUserRoleDao, LecturesUserRole> implements LecturesUserRoleService {

    @Override
    protected void validatorEntity(LecturesUserRole entity) {
        super.validatorEntity(entity);
        QueryWrapper<LecturesUserRole> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(LecturesUserRole::getUserId),entity.getUserId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(LecturesUserRole::getRoleId),entity.getRoleId());
        if (count(queryWrapper) > 0) {
            throw new CustomException("该用户角色已存在");
        }
    }

    @Override
    public QueryWrapper<LecturesUserRole> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<LecturesUserRole> queryWrapper = super.getQueryWrapper(commonPageInfo);
        queryWrapper.eq(MybatisPlusUtil.toColumns(LecturesUserRole::getRoleId), commonPageInfo.getObjectId());
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(LecturesUserRole::getCreateTime));
        return queryWrapper;
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        // 设置用户信息
        iAuthUserService.setMationForMap(beans,"userId","userMation");
        return beans;
    }

    @Override
    public LecturesUserRole selectById(String id) {
        LecturesUserRole lecturesUserRole = super.selectById(id);
        iAuthUserService.setDataMation(lecturesUserRole,LecturesUserRole::getUserId);
        return lecturesUserRole;
    }

    @Override
    public void deleteByRoleId(String id) {
        QueryWrapper<LecturesUserRole> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(LecturesUserRole::getRoleId),id);
        remove(queryWrapper);
    }
}
