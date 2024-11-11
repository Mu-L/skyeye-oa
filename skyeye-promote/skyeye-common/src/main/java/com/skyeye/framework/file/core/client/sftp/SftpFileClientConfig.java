/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.framework.file.core.client.sftp;

import com.skyeye.annotation.api.ApiModel;
import com.skyeye.framework.file.core.client.FileClientConfig;
import lombok.Data;

import javax.validation.Validator;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @ClassName: SftpFileClientConfig
 * @Description: Sftp 文件客户端的配置类
 * @author: skyeye云系列--卫志强
 * @date: 2024/8/18 11:59
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@ApiModel("Sftp 文件客户端的配置类")
public class SftpFileClientConfig implements FileClientConfig {

    @NotBlank(message = "基础路径", groups = {Config.class})
    private String basePath;

    @NotBlank(message = "自定义域名", groups = {Config.class})
    private String domain;

    @NotBlank(message = "主机地址", groups = {Config.class})
    private String host;

    @NotNull(message = "主机端口", groups = {Config.class})
    private Integer port;

    @NotBlank(message = "用户名", groups = {Config.class})
    private String username;

    @NotBlank(message = "密码", groups = {Config.class})
    private String password;

    public interface Config {
    }

    @Override
    public void validate(Validator validator) {
        validator.validate(this, Config.class);
    }
}
