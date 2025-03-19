/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.archives.service.impl;

import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.archives.dao.ArchivesDao;
import com.skyeye.archives.entity.Archives;
import com.skyeye.archives.service.ArchivesService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.organization.service.ICompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: ArchivesServiceImpl
 * @Description: 员工档案管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 22:36
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "员工档案", groupName = "员工档案", teamAuth = true)
public class ArchivesServiceImpl extends SkyeyeBusinessServiceImpl<ArchivesDao, Archives> implements ArchivesService {

    @Autowired
    private ICompanyService iCompanyService;

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        List<Map<String, Object>> beans = skyeyeBaseMapper.querySysStaffArchivesList(commonPageInfo);
        return beans;
    }

    @Override
    public Archives selectById(String id) {
        Archives archives = super.selectById(id);
        archives.setCompanyMation(iCompanyService.queryDataMationById(archives.getCompanyId()));
        return archives;
    }
}
