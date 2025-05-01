/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.field.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.field.entity.FieldStaffLink;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: FieldStaffLinkService
 * @Description: 员工与薪资字段关系管理服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2023/11/4 13:33
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
public interface FieldStaffLinkService extends SkyeyeBusinessService<FieldStaffLink> {

    void queryStaffWagesModelFieldMationListByStaffId(InputObject inputObject, OutputObject outputObject);

    void saveStaffWagesModelFieldMation(InputObject inputObject, OutputObject outputObject);

    void setLastMonthBe(InputObject inputObject, OutputObject outputObject);

    /**
     * 设置应出勤的班次以及小时
     *
     * @param staffWorkTime 员工对应的考勤班次
     * @param lastMonthDate 指定年月，格式为yyyy-MM
     * @return 员工拥有的所有薪资要素字段以及对应的值
     */
    Map<String, Object> setLastMonthBe(List<Map<String, Object>> staffWorkTime, String lastMonthDate);

    /**
     * 修改员工关联薪资字段的key
     *
     * @param oldKey 旧薪资字段的key
     * @param newKey 新薪资字段的key
     */
    void updateStaffFiledKey(String oldKey, String newKey);

    /**
     * 删除员工关联薪资字段的key
     *
     * @param key 薪资字段的key
     */
    void deleteStaffFiledKey(String key);

    void addWagesStaffMationByStaffId(InputObject inputObject, OutputObject outputObject);
}
