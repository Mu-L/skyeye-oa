/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.chen.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.eve.chen.entity.DwQuChenRow;
import com.skyeye.eve.question.entity.DwQuestion;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: DwQuChenRowService
 * @Description: 矩陈题行选项接口层
 * @author: skyeye云系列--卫志强
 * @date: 2021/5/16 23:21
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye-report Inc. All rights reserved.
 * 注意：本内容具体规则请参照readme执行，地址：https://gitee.com/doc_wei01/skyeye-report/blob/master/README.md
 */
public interface DwQuChenRowService  extends SkyeyeBusinessService<DwQuChenRow>{
    QueryWrapper<DwQuChenRow> QueryExamQuChenRowList(String holderId);

    void saveRowEntity(List<DwQuChenRow> quRow, String userId);

    void updateRowEntity(List<DwQuChenRow> editquRow, String userId);

    Integer QueryvisibilityInRow(String quId, String createId);

    void changeVisibility(String quId, String createId);

    void removeByQuId(String quId);

    List<DwQuChenRow> selectQuChenRow(String copyFromId);

    Map<String, List<DwQuChenRow>> selectByBelongId(List<String> id);

    List<String> createChenRows(List<DwQuestion> dwQuestionList, String userId);

    void updateChenRow(List<DwQuestion> dwQuestionList, String userId);
    void removeByQuIds(List<String> dwQuestionIds);


    List<DwQuChenRow> queryChenRowByQuId(String id);
}
