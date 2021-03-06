package org.mengyun.tcctransaction.sample.http.order.infrastructure.dao;

import java.util.List;

import org.mengyun.tcctransaction.sample.http.order.domain.entity.Product;

/**
 * Created by twinkle.zhou on 16/11/10.
 */
public interface ProductDao {

    Product findById(long productId);

    List<Product> findByShopId(long shopId);
}
