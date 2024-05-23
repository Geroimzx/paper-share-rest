package com.papershare.papershare.service;

import com.papershare.papershare.model.WishlistItem;

import java.util.List;

public interface WishlistItemService {
    List<WishlistItem> getAllWishlistItems();

    List<WishlistItem> getAllWishlistItemsByUserId(Long userId);

    WishlistItem getWishlistItemById(Long id);

    WishlistItem save(WishlistItem wishlistItem);

    void delete(Long id);

    boolean existsByUserIdAndBookId(Long id, Long bookId);

    WishlistItem getWishlistItemByUserIdAndBookId(Long userId, Long bookId);
}
