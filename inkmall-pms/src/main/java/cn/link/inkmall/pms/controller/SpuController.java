package cn.link.inkmall.pms.controller;

import java.util.List;

import cn.link.inkmall.pms.vo.ItemAttrValueVo;
import cn.link.inkmall.pms.vo.SpuVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.link.inkmall.pms.entity.SpuEntity;
import cn.link.inkmall.pms.service.SpuService;
import cn.link.inkmall.common.bean.PageResultVo;
import cn.link.inkmall.common.bean.ResponseVo;
import cn.link.inkmall.common.bean.PageParamVo;

/**
 * spu信息
 *
 * @author link
 * @email linkwu981002@gmail.com
 * @date 2020-05-11 22:03:17
 */
@Api(tags = "spu信息 管理")
@RestController
@RequestMapping("pms/spu")
public class SpuController {

    @Autowired
    private SpuService spuService;

    @Autowired
    RabbitTemplate rabbitTemplate;

    /**
     * 通过 spuId 获取 spu 下 所有sku的销售属性及属性值
     */
    @GetMapping("item/attrs/{spuId}")
    public ResponseVo<List<ItemAttrValueVo>> getItemAttrValuesBySpuId(@PathVariable("spuId") Long spuId) {

       List<ItemAttrValueVo> itemAttrValueVos = spuService.getItemAttrValuesBySpuId(spuId);

        return ResponseVo.ok(itemAttrValueVos);

    }

    /**
     * 用于远程调用的分页查询
     * @return
     */
    @PostMapping("page")
    public ResponseVo<List<SpuEntity>> getSpusByPage(@RequestBody PageParamVo pageParamVo){

        List<SpuEntity> spuEntities = (List<SpuEntity>) spuService.queryPage(pageParamVo).getList();

        return ResponseVo.ok(spuEntities);
    }

    /**
     * 保存spu，将spuVo对象的各个数都写入数据库
     */
    @PostMapping
    @ApiOperation("保存")
    public ResponseVo<Object> save(@RequestBody SpuVo spuVo){

        spuService.bigSave(spuVo);

        return ResponseVo.ok();
    }

    /**
     * 通过categoryId和key分页查询商品，若参数不存在就查询所有
     * @param categoryId
     * @param paramVo
     * @return
     */
    @GetMapping("category/{categoryId}")
    public ResponseVo<PageResultVo> getSpuListByCategoryId(@PathVariable("categoryId") Long categoryId,
                                                           PageParamVo paramVo) {

        PageResultVo pageResultVo = spuService.getSpuListByCategoryId(categoryId, paramVo);

        return ResponseVo.ok(pageResultVo);

    }

    /**
     * 列表
     */
    @GetMapping
    @ApiOperation("分页查询")
    public ResponseVo<PageResultVo> list(PageParamVo paramVo){
        PageResultVo pageResultVo = spuService.queryPage(paramVo);

        return ResponseVo.ok(pageResultVo);
    }


    /**
     * 信息
     */
    @GetMapping("{id}")
    @ApiOperation("详情查询")
    public ResponseVo<SpuEntity> querySpuById(@PathVariable("id") Long id){
		SpuEntity spu = spuService.getById(id);

        return ResponseVo.ok(spu);
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    @ApiOperation("修改")
    public ResponseVo update(@RequestBody SpuEntity spu){
		spuService.updateById(spu);

		//修改商品时发送消息让cart服务修改实时价格
        if (spu != null) {
            rabbitTemplate.convertAndSend("pms.item.exchange", "item.update", spu.getId());
        }

        return ResponseVo.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @ApiOperation("删除")
    public ResponseVo delete(@RequestBody List<Long> ids){
		spuService.removeByIds(ids);

        return ResponseVo.ok();
    }

}
