/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service.impl;

import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.dao.SysDataBaseDao;
import com.skyeye.eve.service.SysDataBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: SysDataBaseServiceImpl
 * @Description: 数据库管理服务层--平台隔离
 * @author: skyeye云系列--卫志强
 * @date: 2025/6/24 8:40
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "数据库管理", groupName = "数据库管理", manageShow = false, tenant = TenantEnum.PLATE)
public class SysDataBaseServiceImpl implements SysDataBaseService {

    @Autowired
    private SysDataBaseDao sysDataBaseDao;

    @Value("${jdbc.database.name}")
    private String dbName;

    /**
     * 获取数据库表名信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void querySysDataBaseSelectList(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        map.put("dbName", dbName);
        List<Map<String, Object>> beans = sysDataBaseDao.querySysDataBaseSelectList(map);
        outputObject.setBeans(beans);
        if (!beans.isEmpty()) {
            outputObject.settotal(beans.size());
        }
    }

    /**
     * 获取数据库表备注信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void querySysDataBaseDescSelectList(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        map.put("dbName", dbName);
        List<Map<String, Object>> beans = sysDataBaseDao.querySysDataBaseDescSelectList(map);
        outputObject.setBeans(beans);
        if (!beans.isEmpty()) {
            outputObject.settotal(beans.size());
        }
    }

}
