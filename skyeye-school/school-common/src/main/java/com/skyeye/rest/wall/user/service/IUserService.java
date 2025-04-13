/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.rest.wall.user.service;

import com.skyeye.base.rest.service.IService;
import com.skyeye.common.entity.search.CommonPageInfo;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: IUserService
 * @Description: 学生信息
 * @author: skyeye云系列--卫志强
 * @date: 2024/6/12 8:29
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface IUserService extends IService {

    void deleteUsersByIds(List<String> userIds);

    List<Map<String, Object>> queryEntityMationByIds(String ids);

    List<Map<String, Object>> queryUserByRealNameOrStudentNumber(CommonPageInfo commonPageInfo);

    List<Map<String, Object>> queryListBuStudentNumberList(String studentNumberList);
}
