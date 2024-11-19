package com.skyeye.exam.examanfillblank.contriller;

import com.skyeye.annotation.api.Api;
import com.skyeye.exam.examanfillblank.service.ExamAnFillblankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: ExamAnFillblankController
 * @Description: 答卷 填空题保存表控制层
 * @author: skyeye云系列--lqy
 * @date: 2024/7/16 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "答卷 填空题保存表", tags = "答卷 填空题保存表", modelName = "答卷 填空题保存表")
public class ExamAnFillblankController {
    @Autowired
    private ExamAnFillblankService examAnFillblankService;
}
