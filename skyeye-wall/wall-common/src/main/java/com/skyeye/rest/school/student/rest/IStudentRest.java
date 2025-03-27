package com.skyeye.rest.school.student.rest;

import com.skyeye.common.client.ClientConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * @ClassName: IStudentRest
 * @Description: 学生信息
 * @author: skyeye云系列--卫志强
 * @date: 2024/6/12 8:29
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@FeignClient(value = "${webroot.skyeye-school}", configuration = ClientConfiguration.class)
public interface IStudentRest {

    /**
     * 新增学生信息
     *
     * @Param: Map<String, Object> map
     * @Param: @return
     * */
    @PostMapping("/writeStudent")
    String addStudent(@RequestBody Map<String, Object> map);
}
