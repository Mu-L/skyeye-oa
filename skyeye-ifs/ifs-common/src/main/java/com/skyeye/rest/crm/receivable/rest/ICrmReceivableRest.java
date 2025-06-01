package com.skyeye.rest.crm.receivable.rest;


import com.skyeye.common.client.ClientConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "${webroot.skyeye-crm}", configuration = ClientConfiguration.class)
public interface ICrmReceivableRest {

    /**
     * 根据id获取客户应收信息
     *
     * @param ids 主键ids
     */
    @PostMapping("/queryReceivableByIds")
    String queryReceivableByIds(@RequestParam("ids") String ids);

    /**
     * 根据id修改回收金额
     *
     * @param id    主键ids
     * @param price 修改的回收金额
     */
    @PostMapping("/updateReceivableById")
    String updateReceivableById(@RequestParam("id") String id, @RequestParam("price") String price);

}
