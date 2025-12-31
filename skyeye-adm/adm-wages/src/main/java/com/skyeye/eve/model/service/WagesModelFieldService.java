/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.model.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.eve.model.entity.WagesModelField;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: WagesModelFieldService
 * @Description: 薪资模板关联的字段服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2024/1/21 14:03
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
public interface WagesModelFieldService extends SkyeyeBusinessService<WagesModelField> {

    void deleteModelFieldByPId(String modelId);

    void saveModelField(String modelId, List<WagesModelField> modelFieldList);

    List<WagesModelField> queryModelFieldByPId(String modelId);

    Map<String, List<WagesModelField>> queryModelFieldByPId(List<String> modelId);

}
