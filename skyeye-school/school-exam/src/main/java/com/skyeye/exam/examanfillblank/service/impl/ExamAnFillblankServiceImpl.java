package com.skyeye.exam.examanfillblank.service.impl;

import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.exam.examanfillblank.dao.ExamAnFillblankDao;
import com.skyeye.exam.examanfillblank.entity.ExamAnFillblank;
import com.skyeye.exam.examanfillblank.service.ExamAnFillblankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ClassName: ExamAnFillblankController
 * @Description: 答卷 填空题保存表控制层
 * @author: skyeye云系列--lqy
 * @date: 2024/7/16 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "填空题保存表", groupName = "填空题保存表")
public class ExamAnFillblankServiceImpl extends SkyeyeBusinessServiceImpl<ExamAnFillblankDao, ExamAnFillblank> implements ExamAnFillblankService {
    @Autowired
    private ExamAnFillblankService examAnFillblankService;
}
