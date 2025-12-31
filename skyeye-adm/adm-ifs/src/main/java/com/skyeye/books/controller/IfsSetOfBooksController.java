/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.books.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.books.entity.SetOfBooks;
import com.skyeye.books.service.IfsSetOfBooksService;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: IfsSetOfBooksController
 * @Description: 账套管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2023/3/12 12:22
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "账套管理", tags = "账套管理", modelName = "账套管理")
public class IfsSetOfBooksController {

    @Autowired
    private IfsSetOfBooksService ifsSetOfBooksService;

    /**
     * 获取账套列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "ifssetofbooks001", value = "获取账套列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/IfsSetOfBooksController/queryIfsSetOfBooksList")
    public void queryIfsSetOfBooksList(InputObject inputObject, OutputObject outputObject) {
        ifsSetOfBooksService.queryPageList(inputObject, outputObject);
    }

    /**
     * 新增/编辑账套信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeIfsSetOfBooks", value = "新增/编辑账套信息", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = SetOfBooks.class)
    @RequestMapping("/post/IfsSetOfBooksController/writeIfsSetOfBooks")
    public void writeIfsSetOfBooks(InputObject inputObject, OutputObject outputObject) {
        ifsSetOfBooksService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 删除账套信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "ifssetofbooks005", value = "删除账套信息", method = "DELETE", allUse = "1")
    @ApiImplicitParams({@ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/IfsSetOfBooksController/deleteIfsSetOfBooksById")
    public void deleteIfsSetOfBooksById(InputObject inputObject, OutputObject outputObject) {
        ifsSetOfBooksService.deleteById(inputObject, outputObject);
    }

}
