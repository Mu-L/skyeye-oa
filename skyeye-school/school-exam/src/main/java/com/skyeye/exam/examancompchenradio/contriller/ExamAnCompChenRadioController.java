package com.skyeye.exam.examancompchenradio.contriller;

import com.skyeye.annotation.api.Api;
import com.skyeye.exam.examancompchenradio.service.ExamAnCompChenRadioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: ExamAnCompChenRadioController
 * @Description: 答卷 复合矩阵单选题服务接口层
 * @author: skyeye云系列--lqy
 * @date: 2024/7/19 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "答卷 复合矩阵单选题", tags = "答卷 复合矩阵单选题", modelName = "答卷 复合矩阵单选题")
public class ExamAnCompChenRadioController {
    @Autowired
    private ExamAnCompChenRadioService examAnCompChenRadioService;
}
