package com.skyeye.exam.examanenumqu.service.impl;

import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.exam.examanenumqu.dao.ExamAnEnumquDao;
import com.skyeye.exam.examanenumqu.entity.ExamAnEnumqu;
import com.skyeye.exam.examanenumqu.service.ExamAnEnumquService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ClassName: ExamAnEnumquServiceImpl
 * @Description: 答卷 枚举题答案服务层
 * @author: skyeye云系列--lqy
 * @date: 2024/7/16 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "试卷管理", groupName = "试卷管理")
public class ExamAnEnumquServiceImpl extends SkyeyeBusinessServiceImpl<ExamAnEnumquDao, ExamAnEnumqu> implements ExamAnEnumquService {

    @Autowired
    private ExamAnEnumquService examAnEnumquService;
}
