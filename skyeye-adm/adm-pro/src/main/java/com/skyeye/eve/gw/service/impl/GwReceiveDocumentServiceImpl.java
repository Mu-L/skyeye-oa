/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.gw.service.impl;

import com.google.common.base.Joiner;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeFlowableServiceImpl;
import com.skyeye.common.constans.CommonCharConstants;
import com.skyeye.eve.gw.dao.GwReceiveDocumentDao;
import com.skyeye.eve.gw.entity.GwReceiveDocument;
import com.skyeye.eve.gw.service.GwReceiveDocumentService;
import com.skyeye.organization.service.IDepmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: GwReceiveDocumentServiceImpl
 * @Description: 公文收文管理服务层--强隔离
 * @author: skyeye云系列--卫志强
 * @date: 2024/4/26 22:40
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "公文收文管理", groupName = "公文收文管理", flowable = true)
public class GwReceiveDocumentServiceImpl extends SkyeyeFlowableServiceImpl<GwReceiveDocumentDao, GwReceiveDocument> implements GwReceiveDocumentService {

    @Autowired
    private IDepmentService iDepmentService;

    @Override
    public GwReceiveDocument selectById(String id) {
        GwReceiveDocument gwReceiveDocument = super.selectById(id);
        String departmentIdStr = Joiner.on(CommonCharConstants.COMMA_MARK).join(gwReceiveDocument.getReceiveDepartmentId());
        List<Map<String, Object>> departmentList = iDepmentService.queryDataMationByIds(departmentIdStr);
        gwReceiveDocument.setReceiveDepartmentMation(departmentList);
        return gwReceiveDocument;
    }
}
