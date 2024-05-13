package com.papershare.papershare.service.impl;

import com.papershare.papershare.model.WishlistItem;
import com.papershare.papershare.repository.WishlistItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WishlistItemService {

    @Autowired
    private WishlistItemRepository wishlistItemRepository;

    public List<WishlistItem> getAllWishlistItems() {
        return wishlistItemRepository.findAll();
    }

    public WishlistItem getWishlistItemById(Long id) {
        return wishlistItemRepository.findById(id).orElse(null);
    }

    public WishlistItem createWishlistItem(WishlistItem wishlistItem) {
        return wishlistItemRepository.save(wishlistItem);
    }

    public WishlistItem updateWishlistItem(Long id, WishlistItem wishlistItem) {
        WishlistItem existingWishlistItem = wishlistItemRepository.findById(id).orElse(null);
        if (existingWishlistItem != null) {
            existingWishlistItem.setUser(wishlistItem.getUser());
            existingWishlistItem.setBook(wishlistItem.getBook());
            return wishlistItemRepository.save(existingWishlistItem);
        }
        return null;
    }

    public void deleteWishlistItem(Long id) {
        wishlistItemRepository.deleteById(id);
    }
}