/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.framework.file.core.client.ftp;

import com.skyeye.annotation.api.ApiModel;
import com.skyeye.framework.file.core.client.FileClientConfig;
import lombok.Data;

import javax.validation.Validator;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @ClassName: FtpFileClientConfig
 * @Description: Ftp 文件客户端的配置类
 * @author: skyeye云系列--卫志强
 * @date: 2024/8/18 11:10
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@ApiModel("Ftp 文件客户端的配置类")
public class FtpFileClientConfig implements FileClientConfig {

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

    /**
     * 连接模式
     * <p>
     * 使用 {@link  cn.hutool.extra.ftp.FtpMode} 对应的字符串
     */
    @NotBlank(message = "连接模式", groups = {Config.class})
    private String mode;

    public interface Config {
    }

    @Override
    public void validate(Validator validator) {
        validator.validate(this, Config.class);
    }

}
