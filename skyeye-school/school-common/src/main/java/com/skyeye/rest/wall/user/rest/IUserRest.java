/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.rest.wall.user.rest;

import com.skyeye.common.client.ClientConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.yaml.snakeyaml.events.Event;

/**
 * @ClassName: IUserRest
 * @Description: 用户信息
 * @author: skyeye云系列--卫志强
 * @date: 2024/6/12 8:27
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@FeignClient(value = "${webroot.skyeye-wall}", configuration = ClientConfiguration.class)
public interface IUserRest {

    /**
     * 根据id批量获取用户信息
     *
     * @param ids 主键id
     */
    @PostMapping("/queryUserByIds")
    String queryUserByIds(@RequestParam("ids") String ids);

    /**
     * 根据姓名或者学号获取用户信息
     *
     * @param realName 用户真实姓名
     * @param studentNumber 学号
     */
    @PostMapping("/queryUserByRealNameOrStudentNumber")
    String queryUserByRealNameOrStudentNumber(@RequestParam(value = "realName",required = false) String realName ,
                                              @RequestParam(value = "studentNumber",required = false) String studentNumber);



}
