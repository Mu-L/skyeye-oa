/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.role.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.object.InputObject;
import com.skyeye.role.dao.RoleDao;
import com.skyeye.role.entity.Role;
import com.skyeye.role.service.RoleService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: ShopDeliveryCompanyController
 * @Description: ai角色服务类
 * @author: skyeye云系列--卫志强
 * @date: 2024/10/8 10:06
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "AI角色", groupName = "AI角色")
public class RoleServiceImpl extends SkyeyeBusinessServiceImpl<RoleDao, Role> implements RoleService {

    @Override
    public List<Map<String, Object>> queryDataList(InputObject inputObject) {
        QueryWrapper<Role> queryWrapper = new QueryWrapper<>();
        List<Role> beans = list(queryWrapper);
        return JSONUtil.toList(JSONUtil.toJsonStr(beans), null);
    }
}
