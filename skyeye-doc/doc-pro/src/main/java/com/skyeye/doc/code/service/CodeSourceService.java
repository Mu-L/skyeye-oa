/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.doc.code.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.doc.code.entity.CodeSource;

/**
 * @ClassName: CodeSourceService
 * @Description: 源代码服务接口
 * @author: skyeye云系列--卫志强
 * @date: 2025/8/19 8:25
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
public interface CodeSourceService extends SkyeyeBusinessService<CodeSource> {

    void queryAllReleaseCodeList(InputObject inputObject, OutputObject outputObject);

    void removeCodeSource(InputObject inputObject, OutputObject outputObject);

    String getCacheKey(String year);
}
