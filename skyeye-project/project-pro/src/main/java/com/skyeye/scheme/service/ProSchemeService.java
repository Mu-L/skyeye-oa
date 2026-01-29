/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.scheme.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.scheme.entity.ProScheme;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

/**
 * @ClassName: ProSchemeService
 * @Description: 项目方案服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2025/12/23 12:09
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface ProSchemeService extends SkyeyeBusinessService<ProScheme> {

    /**
     * 根据项目id查询方案列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    void querySchemeListByProjectId(InputObject inputObject, OutputObject outputObject);

    void querySchemeListByVersionNo(InputObject inputObject, OutputObject outputObject);
}

