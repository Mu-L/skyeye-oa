package com.skyeye.exam.examanchencheckbox.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.exam.examanchencheckbox.service.ExamAnChenCheckboxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: ExamAnChenCheckboxController
 * @Description: 答卷 矩阵多选题控制层
 * @author: skyeye云系列--lqy
 * @date: 2024/7/16 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */

@RestController
@Api(value = "答卷 矩阵多选题", tags = "答卷 矩阵多选题", modelName = "答卷 矩阵多选题")
public class ExamAnChenCheckboxController {

    @Autowired
    private ExamAnChenCheckboxService examAnChenCheckboxService;
}
