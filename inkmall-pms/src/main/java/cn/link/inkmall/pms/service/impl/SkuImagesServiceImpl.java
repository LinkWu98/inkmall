package cn.link.inkmall.pms.service.impl;

import cn.link.inkmall.pms.service.SkuService;
import cn.link.inkmall.pms.vo.SkuVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.link.inkmall.common.bean.PageResultVo;
import cn.link.inkmall.common.bean.PageParamVo;

import cn.link.inkmall.pms.mapper.SkuImagesMapper;
import cn.link.inkmall.pms.entity.SkuImagesEntity;
import cn.link.inkmall.pms.service.SkuImagesService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;


@Service("skuImagesService")
public class SkuImagesServiceImpl extends ServiceImpl<SkuImagesMapper, SkuImagesEntity> implements SkuImagesService {

    @Autowired
    SkuService skuService;

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<SkuImagesEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<SkuImagesEntity>()
        );

        return new PageResultVo(page);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveSkuImages(SkuVo skuVo) {
        List<String> skuImages = skuVo.getImages();
        if (!CollectionUtils.isEmpty(skuImages)) {
            List<SkuImagesEntity> skuImagesEntities = skuImages
                    .stream()
                    .map(skuImage -> new SkuImagesEntity(skuVo.getId(), skuImage, 0, 0))
                    .collect(Collectors.toList());

            //若spuVo中没有默认图片，就将第一张作为默认图片
            if (skuVo.getDefaultImage() == null) {
                skuVo.setDefaultImage(skuImages.get(0));
                skuImagesEntities.get(0).setDefaultStatus(1);
                //设置后更新数据库信息
                skuService.updateById(skuVo);
            }
            this.saveBatch(skuImagesEntities);
        }
    }


}