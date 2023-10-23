package com.shop.repository;

import com.shop.dto.ItemSearchDto;
import com.shop.dto.MainItemDto;
import com.shop.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepositoryCustom {
    Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable);

    Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable);

    List<Item> findByItemNm(String itemNm);

    List<Item> findByPriceEqualOrLessThan(Integer price);

    List<Item> findByPriceEqualOrLessThanOrderByPriceDesc(Integer price);

    List<Item> findByItemDetailOrderByPriceDesc(String itemDetail);
}
