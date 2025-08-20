/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.doc.member.service.impl;

import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.doc.member.dao.DocMemverLevelDao;
import com.skyeye.doc.member.entity.DocMemverLevel;
import com.skyeye.doc.member.service.DocMemverLevelService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName: DocMemverLevelServiceImpl
 * @Description: 会员等级服务实现类
 * @author: skyeye云系列--卫志强
 * @date: 2025/8/20 9:11
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Service
@SkyeyeService(name = "会员等级管理", groupName = "会员等级管理", tenant = TenantEnum.PLATE)
public class DocMemverLevelServiceImpl extends SkyeyeBusinessServiceImpl<DocMemverLevelDao, DocMemverLevel> implements DocMemverLevelService {

    @Override
    public void queryAllDocMemverLevelList(InputObject inputObject, OutputObject outputObject) {
        List<DocMemverLevel> docMemverLevelList = queryAllData();
        outputObject.setBeans(docMemverLevelList);
        outputObject.settotal(docMemverLevelList.size());
    }
}
