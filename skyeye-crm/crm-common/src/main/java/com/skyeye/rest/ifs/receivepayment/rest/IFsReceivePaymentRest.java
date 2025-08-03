package com.skyeye.rest.ifs.receivepayment.rest;

import com.skyeye.common.client.ClientConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(value = "${webroot.skyeye-ifs}", configuration = ClientConfiguration.class)
public interface IFsReceivePaymentRest {

    /**
     * 供应商付款管理审批成功后新增收款管理
     * */
    @PostMapping("/addReceivePayment")
    String addIFsReceivePayment(@RequestBody Map<String, Object> map);

    /**
     * 编辑 发票审批成功后 编辑收款管理
     * */
    @PostMapping("/updateReceivePayment")
    String updateReceivePayment(@RequestBody Map<String, Object> map);

}
