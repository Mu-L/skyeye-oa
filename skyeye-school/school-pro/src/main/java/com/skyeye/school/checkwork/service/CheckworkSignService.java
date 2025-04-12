/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.checkwork.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.school.checkwork.entity.CheckworkSign;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: CheckworkSignService
 * @Description: 学生考勤签到服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2024/7/24 10:49
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface CheckworkSignService extends SkyeyeBusinessService<CheckworkSign> {

    void deleteCheckworkSignByCheckworkId(String checkworkId);

    List<CheckworkSign> queryCheckworkSignByCheckworkId(String checkworkId);

    Map<String, List<CheckworkSign>> queryCheckworkSignByCheckworkId(String... checkworkId);

    void createCheckworkSignBySourceCode(InputObject inputObject, OutputObject outputObject);

    void createCheckworkSignById(InputObject inputObject, OutputObject outputObject);

    void queryCheckworkWaitSignList(InputObject inputObject, OutputObject outputObject);

    void queryCheckworkAlreadySignList(InputObject inputObject, OutputObject outputObject);

    Map<String, Long> queryStuCheckWorkSignNums(String subjectClassId, List<String> stuIds);

    Long queryCheckWorkPersonNum(List<String> ids);

    void queryStuCheckworkSignCount(InputObject inputObject, OutputObject outputObject);

    Map<String, Object> queryStuCheckworkSignByStuId(String stuId);
}
