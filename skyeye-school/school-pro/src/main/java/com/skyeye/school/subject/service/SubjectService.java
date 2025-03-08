/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.subject.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.school.subject.entity.Subject;

/**
 * @ClassName: SubjectService
 * @Description: 科目管理服务接口层
 * @author: xqz
 * @date: 2023/8/9 9:52
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface SubjectService extends SkyeyeBusinessService<Subject> {

    void querySubjectListByUserId(InputObject inputObject, OutputObject outputObject);

    void querySubjectListByMajorId(InputObject inputObject, OutputObject outputObject);

    void searchSubjectList(InputObject inputObject, OutputObject outputObject);

//    void queryTeacherMessage(InputObject inputObject, OutputObject outputObject);
}
