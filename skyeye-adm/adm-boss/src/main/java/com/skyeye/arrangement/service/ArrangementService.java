/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.arrangement.service;

import com.skyeye.arrangement.entity.Arrangement;
import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

/**
 * @ClassName: ArrangementService
 * @Description: 面试安排服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2022/4/14 11:46
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface ArrangementService extends SkyeyeBusinessService<Arrangement> {

    void nullifyArrangement(InputObject inputObject, OutputObject outputObject);

    void queryMyEntryBossPersonRequireAboutArrangementList(InputObject inputObject, OutputObject outputObject);

    void setBossInterviewer(InputObject inputObject, OutputObject outputObject);

    void queryArrangementInterviewerIsMyList(InputObject inputObject, OutputObject outputObject);

    void setBossInterviewResult(InputObject inputObject, OutputObject outputObject);

    void setInductionResult(InputObject inputObject, OutputObject outputObject);
}
