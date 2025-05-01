/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.rest.checkwork.rest;

import com.skyeye.common.client.ClientConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @ClassName: ICheckWorkTimeRest
 * @Description: 考勤班次服务接口
 * @author: skyeye云系列--卫志强
 * @date: 2025/5/1 10:13
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@FeignClient(value = "${webroot.skyeye-checkwork}", configuration = ClientConfiguration.class)
public interface ICheckWorkTimeRest {

    /**
     * 根据id批量获取考勤班次信息
     *
     * @param ids 主键id，多个用逗号隔开
     */
    @PostMapping("/queryCheckWorkTimeByIds")
    String queryCheckWorkTimeByIds(@RequestParam("ids") String ids);

}
