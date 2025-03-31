/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.chen.service;


import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.chen.entity.DwQuChenColumn;
import com.skyeye.eve.chen.entity.DwQuChenRow;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: DwQuChenColumnService
 * @Description: 矩陈题列选项接口层
 * @author: skyeye云系列--卫志强
 * @date: 2021/5/16 23:21
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye-report Inc. All rights reserved.
 * 注意：本内容具体规则请参照readme执行，地址：https://gitee.com/doc_wei01/skyeye-report/blob/master/README.md
 */
public interface DwQuChenColumnService extends SkyeyeBusinessService<DwQuChenColumn>{
    void saveList(List<DwQuChenColumn> column, List<DwQuChenRow> row, String quId, String userId);

    void changeVisibility(InputObject inputObject, OutputObject outputObject);

    void removeByQuId(String quId);

    List<DwQuChenColumn> selectQuChenColumn(String copyFromId);

    Map<String, List<DwQuChenColumn>> selectByBelongId(List<String> id);
}
