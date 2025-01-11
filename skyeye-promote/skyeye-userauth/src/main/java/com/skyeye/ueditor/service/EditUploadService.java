/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.ueditor.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public interface EditUploadService {

    void uploadContentPic(InputObject inputObject, OutputObject outputObject);

    Map<String, Object> downloadContentPic(HttpServletRequest req, String userId);

}
