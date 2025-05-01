/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.rest.wages.rest;

import com.skyeye.common.client.ClientConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @ClassName: IWagesRest
 * @Description: 薪资管理模块接口
 * @author: skyeye云系列--卫志强
 * @date: 2025/5/1 10:52
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@FeignClient(value = "${webroot.skyeye-wages}", configuration = ClientConfiguration.class)
public interface IWagesRest {

    /**
     * 保存员工与薪资字段关系
     *
     * @return
     */
    @PostMapping("/addWagesStaffMationByStaffId")
    String addWagesStaffMationByStaffId(@RequestParam("staffId") String staffId);

}
