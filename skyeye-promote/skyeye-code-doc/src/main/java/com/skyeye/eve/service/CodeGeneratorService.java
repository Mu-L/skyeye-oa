/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

/**
 * @ClassName: CodeGeneratorService
 * @Description: 代码生成器服务接口
 * @author: skyeye云系列--卫志强
 * @date: 2024/12/19
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 */
public interface CodeGeneratorService {

    void getDatabaseTables(InputObject inputObject, OutputObject outputObject);

    void getTableColumns(InputObject inputObject, OutputObject outputObject);

    void previewCode(InputObject inputObject, OutputObject outputObject);

    void downloadCode(InputObject inputObject, OutputObject outputObject);

    void getAvailableTemplates(InputObject inputObject, OutputObject outputObject);

}
