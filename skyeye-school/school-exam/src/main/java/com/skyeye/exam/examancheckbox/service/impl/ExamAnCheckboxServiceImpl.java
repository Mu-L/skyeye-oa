package com.skyeye.exam.examancheckbox.service.impl;


import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.exam.examancheckbox.dao.ExamAnCheckboxDao;
import com.skyeye.exam.examancheckbox.entitiy.ExamAnCheckbox;
import com.skyeye.exam.examancheckbox.service.ExamAnCheckboxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ClassName: ExamAnCheckboxServiceImpl
 * @Description: 答卷 多选题保存表服务层
 * @author: skyeye云系列--lqy
 * @date: 2024/7/19 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */

@Service
@SkyeyeService(name = "答卷 多选题保存表", groupName = "答卷 多选题保存表")
public class ExamAnCheckboxServiceImpl extends SkyeyeBusinessServiceImpl<ExamAnCheckboxDao, ExamAnCheckbox> implements ExamAnCheckboxService{



}
