/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.chapter.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.school.chapter.entity.Chapter;

import java.util.List;

/**
 * @ClassName: ChapterService
 * @Description: 章节管理服务层接口
 * @author: xqz
 * @date: 2023/8/25 11:08
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface ChapterService extends SkyeyeBusinessService<Chapter> {

    void queryChapterListBySubjectId(InputObject inputObject, OutputObject outputObject);

    void queryChapterAnalysis(InputObject inputObject, OutputObject outputObject);

    List<Chapter> queryChaptersBySubjectId(String subjectId);
}
