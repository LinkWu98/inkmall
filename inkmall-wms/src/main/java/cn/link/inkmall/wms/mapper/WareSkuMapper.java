package cn.link.inkmall.wms.mapper;

import cn.link.inkmall.wms.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品库存
 *
 * @author link
 * @email linkwu981002@gmail.com
 * @date 2020-05-11 22:52:38
 */
@Mapper
public interface WareSkuMapper extends BaseMapper<WareSkuEntity> {

    /**
     * 查询库存
     */
    List<WareSkuEntity> getWareSkuBySkuIdAndCount(@Param("skuId") Long skuId, @Param("count") Integer count);

    /**
     * 锁定库存
     */
    Integer lockStockByWareSkuIdAndCount(@Param("wareSKuId") Long wareSkuId, @Param("count") Integer count);

    void rollbackStockLocked(@Param("wareSKuId") Long wareSkuId, @Param("count") Integer count);

    void minusStock(@Param("wareSKuId") Long wareSkuId,@Param("count") Integer count);
}
