/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.personrequire.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.personrequire.entity.PersonRequire;

/**
 * @ClassName: PersonRequireService
 * @Description: 人员需求服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2022/4/8 16:01
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface PersonRequireService extends SkyeyeBusinessService<PersonRequire> {

    void setPersonLiable(InputObject inputObject, OutputObject outputObject);

    void queryMyChargePersonRequireList(InputObject inputObject, OutputObject outputObject);

    void queryAllPersonRequireList(InputObject inputObject, OutputObject outputObject);

    /**
     * 修改招聘人数num
     *
     * @param id
     * @param num
     */
    void updatePersonRequireNum(String id, Integer num);
}
