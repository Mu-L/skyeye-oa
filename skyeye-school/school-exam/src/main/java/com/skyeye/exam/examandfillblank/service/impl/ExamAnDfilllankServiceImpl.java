package com.skyeye.exam.examandfillblank.service.impl;

import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.exam.examandfillblank.dao.ExamAnDfilllankDao;
import com.skyeye.exam.examandfillblank.entity.ExamAnDfillblank;
import com.skyeye.exam.examandfillblank.service.ExamAnDfilllankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ClassName: ExamAnDfilllankServiceImpl
 * @Description: 答卷 多行填空题保存表服务层
 * @author: skyeye云系列--lqy
 * @date: 2024/7/16 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "答卷 多行填空题保存表", groupName = "答卷 多行填空题保存表")
public class ExamAnDfilllankServiceImpl extends SkyeyeBusinessServiceImpl<ExamAnDfilllankDao, ExamAnDfillblank> implements ExamAnDfilllankService{

    @Autowired
    private ExamAnDfilllankService examAnDfilllankService;

}
