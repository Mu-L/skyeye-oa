package com.skyeye.exam.examanenumqu.contriller;

import com.skyeye.annotation.api.Api;
import com.skyeye.exam.examanenumqu.service.ExamAnEnumquService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: ExamAnEnumquController
 * @Description: 答卷 枚举题答案控制层
 * @author: skyeye云系列--lqy
 * @date: 2024/7/16 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "答卷 枚举题答案", tags = "答卷 枚举题答案", modelName = "答卷 枚举题答案")
public class ExamAnEnumquController {

    @Autowired
    private ExamAnEnumquService examAnEnumquService;
}
