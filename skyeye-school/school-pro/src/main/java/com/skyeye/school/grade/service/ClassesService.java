/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.grade.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.school.grade.entity.Classes;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: ClassesService
 * @Description: 班级管理服务接口层
 * @author: xqz
 * @date: 2023/8/9 15:52
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface ClassesService extends SkyeyeBusinessService<Classes> {

    void queryClassListByMajorId(InputObject inputObject, OutputObject outputObject);

    List<Classes> queryClassListById(String classId);

    List<Classes> selectClssByIds(List<String> stringList);

    Map<String, List<Classes>> selectByIdList(List<String> classIds);
}
