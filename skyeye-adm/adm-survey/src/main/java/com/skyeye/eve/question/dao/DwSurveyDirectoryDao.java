/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.question.dao;


/**
 * @ClassName: DwSurveyDirectoryDao
 * @Description: 问卷目录及问卷交互层
 * @author: skyeye云系列--卫志强
 * @date: 2021/5/16 23:19
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye-report Inc. All rights reserved.
 * 注意：本内容具体规则请参照readme执行，地址：https://gitee.com/doc_wei01/skyeye-report/blob/master/README.md
 */

import com.skyeye.eve.dao.SkyeyeBaseMapper;
import com.skyeye.eve.question.entity.DwSurveyDirectory;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

public interface DwSurveyDirectoryDao extends SkyeyeBaseMapper<DwSurveyDirectory> {

    Map<String, Object> querySurveyMationById(@Param("id") String id);

    int editSurveyStateToEndNumZdById(Map<String, Object> map);

}


