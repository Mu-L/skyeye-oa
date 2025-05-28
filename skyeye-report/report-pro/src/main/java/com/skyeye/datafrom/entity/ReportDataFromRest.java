/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.datafrom.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.CommonInfo;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: ReportDataFromRest
 * @Description: Rest格式的数据来源
 * @author: skyeye云系列--卫志强
 * @date: 2024/4/3 22:13
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "report_data_from_rest", autoResultMap = true)
@ApiModel("Rest格式的数据来源")
public class ReportDataFromRest extends CommonInfo {

    @TableId("id")
    @ApiModelProperty("主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "from_id")
    @Property(value = "来源id")
    private String fromId;

    @TableField("service_str")
    @ApiModelProperty(value = "接口对应的服务，由前端进行配置，方便前端解析")
    private String serviceStr;

    @TableField(value = "rest_url")
    @ApiModelProperty(value = "接口地址", required = "required")
    private String restUrl;

    @TableField(value = "method")
    @ApiModelProperty(value = "接口请求类型", required = "required")
    private String method;

    @TableField(value = "header", typeHandler = JacksonTypeHandler.class)
    @ApiModelProperty(value = "header请求头")
    private List<Map<String, Object>> header;

    @TableField(value = "request_body")
    @ApiModelProperty(value = "请求体")
    private String requestBody;

    @TableField(exist = false)
    @ApiModelProperty(value = "解析的数据")
    private List<ReportDataFromRestAnalysis> analysisList;

}
