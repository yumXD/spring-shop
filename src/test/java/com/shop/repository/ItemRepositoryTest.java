package com.shop.repository;

import com.shop.constant.ItemSellStatus;
import com.shop.entity.Item;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
class ItemRepositoryTest {
    @Autowired
    ItemRepository itemRepository;

    @PersistenceContext
    EntityManager em;

    @Test
    @DisplayName("상품 하나 저장 테스트")
    public void createItemTest() {
        // given
        Item item = new Item();
        item.setItemNm("테스트 상품");
        item.setPrice(10000);
        item.setItemDetail("테스트 상품 상세 설명");
        item.setItemSellStatus(ItemSellStatus.SELL);
        item.setStockNumber(100);

        // when
        Item savedItem = itemRepository.save(item);

        // then
        assertNotNull(savedItem.getId());
    }

    public int createItemList() {
        int numberOfItemsToCreate = 10; // 생성할 상품 수

        List<Item> itemsToSave = new ArrayList<>();

        for (int i = 1; i <= numberOfItemsToCreate; i++) {
            Item item = new Item();
            item.setItemNm("상품 " + i);
            item.setPrice(10000 + i * 1000);
            item.setItemDetail("상품 " + i + " 상세 설명");
            item.setItemSellStatus(ItemSellStatus.SELL);
            item.setStockNumber(100 - i);
            itemsToSave.add(item);
        }

        // 상품 저장
        itemRepository.saveAll(itemsToSave);
        return numberOfItemsToCreate;
    }

    @Test
    @DisplayName("상품 여러개 저장 테스트")
    public void createItemListTest() {
        // given
        int numberOfItemsToCreate = this.createItemList();

        // when
        List<Item> foundItems = itemRepository.findAll();

        // then
        assertEquals(numberOfItemsToCreate, foundItems.size());
    }

    @Test
    @DisplayName("상품명 조회 테스트")
    public void findByItemNmTest() {
        //given
        this.createItemList();

        // when
        List<Item> items = itemRepository.findByItemNm("상품 5");

        // then
        assertEquals(1, items.size());
        assertEquals("상품 5", items.get(0).getItemNm());
    }

    @Test
    @DisplayName("가격 LessThan 테스트")
    public void findByPriceLessThanTest() {
        // given
        this.createItemList();

        // when
        List<Item> items = itemRepository.findByPriceEqualOrLessThan(15000);

        // then
        assertEquals(5, items.size());
    }

    @Test
    @DisplayName("가격 내림차순 조회 테스트")
    public void findByPriceLessThanOrderByPriceDesc() {
        // given
        this.createItemList();

        // when
        List<Item> items = itemRepository.findByPriceEqualOrLessThanOrderByPriceDesc(15000);

        // then
        assertEquals(5, items.size());

        int previousPrice = Integer.MAX_VALUE;
        for (Item item : items) {
            assertTrue(item.getPrice() <= previousPrice);
            previousPrice = item.getPrice();
        }
    }

    @Test
    @DisplayName("상품 설명 조회 테스트")
    public void findByItemDetailTest() {
        int items = this.createItemList();
        List<Item> itemList = itemRepository.findByItemDetailOrderByPriceDesc("상세 설명");

        assertEquals(items, itemList.size());

        int previousPrice = Integer.MAX_VALUE;
        for (Item item : itemList) {
            System.out.println(item.getPrice());
            assertTrue(item.getPrice() <= previousPrice);
            previousPrice = item.getPrice();
        }
    }
}