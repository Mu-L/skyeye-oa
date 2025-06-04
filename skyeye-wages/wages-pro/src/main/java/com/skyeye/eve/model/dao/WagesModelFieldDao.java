/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.model.dao;

import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.eve.dao.SkyeyeBaseMapper;
import com.skyeye.eve.model.entity.WagesModelField;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: WagesModelFieldDao
 * @Description: 薪资模板关联的字段数据接口层
 * @author: skyeye云系列--卫志强
 * @date: 2024/1/21 14:03
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
public interface WagesModelFieldDao extends SkyeyeBaseMapper<WagesModelField> {

    /**
     * 获取指定员工用有的薪资模板对应的薪资要素字段信息，如果薪资模板中有重复的薪资要素字段，则根据薪资要素字段的key进行分组
     *
     * @param modelIds   该员工拥有的薪资模板id集合
     * @param staffId    员工id
     * @param jobScoreId 职位定级id
     * @return 薪资要素字段信息，该员工的薪资信息以及对应的薪资描述薪资
     */
    @IgnoreTenant
    public List<Map<String, Object>> queryWagesModelFieldByModelIdsAndStaffId(@Param("list") List<String> modelIds,
                                                                              @Param("staffId") String staffId,
                                                                              @Param("jobScoreId") String jobScoreId,
                                                                              @Param("tenantId") String tenantId);
}
