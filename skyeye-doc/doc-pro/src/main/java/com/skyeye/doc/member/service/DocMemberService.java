/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.doc.member.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.doc.member.entity.DocMember;

/**
 * @ClassName: DocMemberService
 * @Description: 会员服务接口
 * @author: skyeye云系列--卫志强
 * @date: 2025/8/19 22:23
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
public interface DocMemberService extends SkyeyeBusinessService<DocMember> {

    void loginDocMember(InputObject inputObject, OutputObject outputObject);

    void logoutDocMember(InputObject inputObject, OutputObject outputObject);

    void editDocMemberPassword(InputObject inputObject, OutputObject outputObject);

    void docMemberLoginMation(InputObject inputObject, OutputObject outputObject);

    void queryCurrentLoginMember(InputObject inputObject, OutputObject outputObject);

    void checkUserGitCodeToken(InputObject inputObject, OutputObject outputObject);

    void bingUserGitCodeToken(InputObject inputObject, OutputObject outputObject);

    DocMember queryMemberByPhone(String phone);
}
