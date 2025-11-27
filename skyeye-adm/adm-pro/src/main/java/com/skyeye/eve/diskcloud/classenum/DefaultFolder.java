/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.diskcloud.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import com.skyeye.common.constans.CommonNumConstants;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: DefaultFolder
 * @Description: 默认的文件夹
 * @author: skyeye云系列--卫志强
 * @date: 2024/1/26 22:37
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum DefaultFolder implements SkyeyeEnumClass {

    FAVORITES("1", "收藏夹", false, false),
    FOLDER("2", "我的文档", true, false),
    SKYDRIVE("3", "企业网盘", true, false);

    private String key;

    private String value;

    private Boolean show;

    private Boolean isDefault;

    public static List<Map<String, Object>> getFileConsoleISDefaultFolder() {
        List<Map<String, Object>> beans = new ArrayList<>();
        for (DefaultFolder bean : DefaultFolder.values()) {
            if (bean.show) {
                beans.add(fileConsoleNode(bean));
            }
        }
        return beans;
    }

    /**
     * 构造文件树节点对象
     *
     * @param bean
     * @return
     */
    public static Map<String, Object> fileConsoleNode(DefaultFolder bean) {
        Map<String, Object> node = new HashMap<>();
        node.put("id", bean.getKey());
        node.put("name", bean.getValue());
        node.put("pId", CommonNumConstants.NUM_ZERO);
        node.put("isParent", CommonNumConstants.NUM_ONE);
        return node;
    }

}
