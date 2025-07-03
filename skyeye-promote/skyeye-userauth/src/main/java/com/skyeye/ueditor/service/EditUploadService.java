/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.ueditor.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.entity.edit.EditUpload;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @ClassName: EditUploadService
 * @Description: 富文本资源上传服务接口
 * @author: skyeye云系列--卫志强
 * @date: 2025/6/22 14:26
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface EditUploadService extends SkyeyeBusinessService<EditUpload> {

    void uploadContentPic(InputObject inputObject, OutputObject outputObject);

    Map<String, Object> downloadContentPic(HttpServletRequest req, String userId);

}
