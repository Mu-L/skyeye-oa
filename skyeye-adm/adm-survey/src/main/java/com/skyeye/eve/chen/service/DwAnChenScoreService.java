/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.chen.service;


import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.chen.entity.DwAnChenScore;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: DwAnChenScoreService
 * @Description: 答卷矩阵评分题接口层
 * @author: skyeye云系列--卫志强
 * @date: 2021/5/16 23:21
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye-report Inc. All rights reserved.
 * 注意：本内容具体规则请参照readme执行，地址：https://gitee.com/doc_wei01/skyeye-report/blob/master/README.md
 */
public interface DwAnChenScoreService extends SkyeyeBusinessService<DwAnChenScore> {

    void queryDwAnChenScoreListById(InputObject inputObject, OutputObject outputObject);

    List<DwAnChenScore> selectBySurveyId(String surveyId);

    List<DwAnChenScore> slectByQuId(String id);

    Map<String, List<DwAnChenScore>> selectByQuIdAndStuId(List<String> chenIds, String studentId, String id);
}
