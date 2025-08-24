/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.doc.document.dao;

import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.doc.document.entity.Document;
import com.skyeye.eve.dao.SkyeyeBaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @ClassName: DocumentDao
 * @Description: 文档数据访问层
 * @author: skyeye云系列--卫志强
 * @date: 2025/8/24 11:17
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
public interface DocumentDao extends SkyeyeBaseMapper<Document> {

    /**
     * 根据父id查询所有的子节点信息(包含父id)，如果是多个
     *
     * @param ids 父id
     * @return
     */
    @IgnoreTenant
    List<String> queryAllChildIdsByParentId(@Param("ids") List<String> ids);

}
