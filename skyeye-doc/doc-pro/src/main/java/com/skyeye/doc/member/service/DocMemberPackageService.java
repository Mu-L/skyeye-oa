/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.doc.member.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.doc.member.entity.DocMemberPackage;

import java.util.List;

/**
 * @ClassName: DocMemberPackageService
 * @Description: 会员购买的包服务层接口
 * @author: skyeye云系列--卫志强
 * @date: 2025/8/21 8:21
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
public interface DocMemberPackageService extends SkyeyeBusinessService<DocMemberPackage> {

    void saveList(String memberId, List<String> packageIds);

    void deleteByMemberId(String memberId);

    List<DocMemberPackage> selectByMemberId(String memberId);

}
