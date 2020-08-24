package cn.link.inkmall.search.repository;

import cn.link.inkmall.search.entity.Goods;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @Author: Link
 * @Date: 2020/5/18 2:17
 * @Version 1.0
 */
public interface GoodsRepository extends ElasticsearchRepository<Goods, Long> {
}
