package com.shop.controller;

import com.shop.dto.ItemFormDto;
import com.shop.dto.ItemSearchDto;
import com.shop.entity.Item;
import com.shop.service.ItemService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @GetMapping(value = "/admin/item/new")
    public String addItem(Model model) {
        log.info("상품등록 페이지");
        model.addAttribute("itemFormDto", new ItemFormDto());
        return "item/itemForm";
    }

    @PostMapping(value = "/admin/item/new")
    public String addItem(@Valid ItemFormDto itemFormDto, BindingResult bindingResult,
                          Model model,
                          @RequestParam("itemImgFile") List<MultipartFile> itemImgFileList,
                          RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            log.error("상품등록 처리에러");
            return "item/itemForm";
        }

        if (itemImgFileList.get(0).isEmpty() && itemFormDto.getId() == null) {
            log.error("상품등록 처리에러");
            model.addAttribute("errorMessage", "첫번째 상품 이미지는 필수 입력 값 입니다.");
            return "item/itemForm";
        }

        try {
            itemService.saveItem(itemFormDto, itemImgFileList);
            log.info("상품등록 성공");
        } catch (Exception e) {
            log.error("상품등록 예외발생");
            model.addAttribute("errorMessage", "상품 등록 중 에러가 발생하였습니다.");
            return "item/itemForm";
        }
        redirectAttributes.addFlashAttribute("successMessage", "상품 등록 성공하였습니다.");
        return "redirect:/admin/items";
    }

    @GetMapping(value = "/admin/item/{itemId}")
    public String itemUpdate(@PathVariable("itemId") Long itemId,
                             Model model,
                             RedirectAttributes redirectAttributes) {

        try {
            ItemFormDto itemFormDto = itemService.getItemDtl(itemId);
            log.info("상품수정 페이지");
            model.addAttribute("itemFormDto", itemFormDto);
        } catch (EntityNotFoundException e) {
            log.error("존재하지 않은 상품");
            redirectAttributes.addFlashAttribute("errorMessage", "존재하지 않는 상품 입니다.");
            return "redirect:/admin/items";
        }

        return "item/itemForm";
    }

    @PostMapping(value = "/admin/item/{itemId}")
    public String itemUpdate(@Valid ItemFormDto itemFormDto,
                             BindingResult bindingResult,
                             @RequestParam("itemImgFile") List<MultipartFile> itemImgFileList,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            log.error("상품수정 처리에러");
            return "item/itemForm";
        }

        if (itemImgFileList.get(0).isEmpty() && itemFormDto.getId() == null) {
            log.error("상품수정 처리에러");
            model.addAttribute("errorMessage", "첫번째 상품 이미지는 필수 입력 값 입니다.");
            return "item/itemForm";
        }

        try {
            itemService.updateItem(itemFormDto, itemImgFileList);
        } catch (Exception e) {
            log.error("상품수정 예외발생");
            model.addAttribute("errorMessage", "상품 수정 중 에러가 발생하였습니다.");
            return "item/itemForm";
        }
        redirectAttributes.addFlashAttribute("successMessage", "상품 수정 성공하였습니다.");
        return "redirect:/admin/items";
    }

    @GetMapping(value = {"/admin/items", "/admin/items/{page}"})
    public String itemManage(ItemSearchDto itemSearchDto, @PathVariable("page") Optional<Integer> page, Model model) {
        log.info("상품관리 페이지");
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 3);
        Page<Item> items = itemService.getAdminItemPage(itemSearchDto, pageable);

        model.addAttribute("items", items);
        model.addAttribute("itemSearchDto", itemSearchDto);
        model.addAttribute("maxPage", 5);

        return "item/itemMng";
    }

    @GetMapping(value = "/item/{itemId}")
    public String itemDtl(Model model,
                          @PathVariable("itemId") Long itemId,
                          RedirectAttributes redirectAttributes) {
        try {
            log.info("상품 상세 페이지");
            ItemFormDto itemFormDto = itemService.getItemDtl(itemId);
            model.addAttribute("item", itemFormDto);
        } catch (EntityNotFoundException ex) {
            log.error("존재하지 않은 상품");
            redirectAttributes.addFlashAttribute("errorMessage", "존재하지 않는 상품 입니다.");
            return "redirect:/";
        }
        return "item/itemDtl";
    }

    @DeleteMapping("/api/admin/item/{itemId}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long itemId) {
        itemService.deleteItem(itemId);
        return ResponseEntity.ok()
                .build();
    }
}
