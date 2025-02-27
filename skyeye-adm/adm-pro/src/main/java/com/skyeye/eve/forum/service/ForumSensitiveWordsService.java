/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.forum.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.forum.entity.ForumSensitiveWords;

public interface ForumSensitiveWordsService extends SkyeyeBusinessService<ForumSensitiveWords> {

    void queryForumSensitiveWordsList(InputObject inputObject, OutputObject outputObject);

}
