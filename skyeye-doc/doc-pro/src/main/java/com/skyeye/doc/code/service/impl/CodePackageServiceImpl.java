/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.doc.code.service.impl;

import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.doc.code.dao.CodePackageDao;
import com.skyeye.doc.code.entity.CodePackage;
import com.skyeye.doc.code.service.CodePackageService;
import org.springframework.stereotype.Service;

/**
 * @ClassName: CodePackageServiceImpl
 * @Description: 代码包管理服务实现类
 * @author: skyeye云系列--卫志强
 * @date: 2025/8/17 17:37
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Service
@SkyeyeService(name = "源代码包管理", groupName = "源代码包管理", tenant = TenantEnum.PLATE)
public class CodePackageServiceImpl extends SkyeyeBusinessServiceImpl<CodePackageDao, CodePackage> implements CodePackageService {


}
