/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.subject.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.school.subject.entity.SubjectClasses;

import java.util.List;

/**
 * @ClassName: SubjectClassesService
 * @Description: 科目表与班级表的关系服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2024/6/10 14:52
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface SubjectClassesService extends SkyeyeBusinessService<SubjectClasses> {

    void querySubjectClassesBySourceCode(InputObject inputObject, OutputObject outputObject);

    /**
     * 根据科目id查询科目与班级的关系
     *
     * @param objectId 科目id
     * @return
     */

    List<SubjectClasses> querySubjectClassesByObjectId(String... objectId);

    /**
     * 编辑科目班级关系的学生人数
     *
     * @param id    科目班级关系id
     * @param isAdd 是否增加
     */

    void editSubjectClassesPeopleNum(String id, Boolean isAdd);

    void updateEnabled(String SubjectClassesId, Integer isEnabled);

    void changeEnabled(InputObject inputObject, OutputObject outputObject);

    void changeQuit(InputObject inputObject, OutputObject outputObject);

    void updatePeopleNum(String subClassLinkId, Integer count);

    void queryTeacherMessage(InputObject inputObject, OutputObject outputObject);

    void queryNoPageSubjectClassesList(InputObject inputObject, OutputObject outputObject);

    SubjectClasses getSubjectClassesByObjectIdAndClassesId(String objectId, String classesId);
    void querySubjectClassesInfo(InputObject inputObject, OutputObject outputObject);
}