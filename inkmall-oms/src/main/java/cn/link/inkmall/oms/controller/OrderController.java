package cn.link.inkmall.oms.controller;

import java.util.List;

import cn.link.inkmall.oms.vo.OrderSubmitVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.link.inkmall.oms.entity.OrderEntity;
import cn.link.inkmall.oms.service.OrderService;
import cn.link.inkmall.common.bean.PageResultVo;
import cn.link.inkmall.common.bean.ResponseVo;
import cn.link.inkmall.common.bean.PageParamVo;

/**
 * 订单
 *
 * @author link
 * @email linkwu981002@gmail.com
 * @date 2020-05-11 23:01:21
 */
@Api(tags = "订单 管理")
@RestController
@RequestMapping("oms/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 保存订单和订单详情
     */
    @PostMapping
    @ApiOperation("保存订单和订单详情")
    public ResponseVo<OrderEntity> save(@RequestBody OrderSubmitVo orderSubmitVo){

        OrderEntity orderEntity = orderService.saveOrder(orderSubmitVo);

        return ResponseVo.ok(orderEntity);
    }

    /**
     * 列表
     */
    @GetMapping
    @ApiOperation("分页查询")
    public ResponseVo<PageResultVo> list(PageParamVo paramVo){
        PageResultVo pageResultVo = orderService.queryPage(paramVo);

        return ResponseVo.ok(pageResultVo);
    }


    /**
     * 信息
     */
    @GetMapping("{id}")
    @ApiOperation("详情查询")
    public ResponseVo<OrderEntity> queryOrderById(@PathVariable("id") Long id){

		OrderEntity order = orderService.getById(id);

        return ResponseVo.ok(order);

    }

    /**
     * 修改
     */
    @PostMapping("/update")
    @ApiOperation("修改")
    public ResponseVo update(@RequestBody OrderEntity order){

		orderService.updateById(order);

        return ResponseVo.ok();

    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @ApiOperation("删除")
    public ResponseVo delete(@ApiParam(value = "商品id集合", required = true) @RequestBody List<Long> ids){

		orderService.removeByIds(ids);

        return ResponseVo.ok();
    }

}
