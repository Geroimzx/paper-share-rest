package com.papershare.papershare.service.impl;

import com.papershare.papershare.model.WishlistItem;
import com.papershare.papershare.repository.WishlistItemRepository;
import com.papershare.papershare.service.WishlistItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WishlistItemServiceImpl implements WishlistItemService {

    @Autowired
    private WishlistItemRepository wishlistItemRepository;

    @Override
    public List<WishlistItem> getAllWishlistItems() {
        return wishlistItemRepository.findAll();
    }

    @Override
    public List<WishlistItem> getAllWishlistItemsByUserId(Long userId) {
        return wishlistItemRepository.findAllByUserId(userId);
    }

    @Override
    public WishlistItem getWishlistItemById(Long id) {
        return wishlistItemRepository.findById(id).orElse(null);
    }

    @Override
    public WishlistItem save(WishlistItem wishlistItem) {
        return wishlistItemRepository.save(wishlistItem);
    }

    @Override
    public void delete(Long id) {
        wishlistItemRepository.deleteById(id);
    }

    @Override
    public boolean existsByUserIdAndBookId(Long userId, Long bookId) {
        return wishlistItemRepository.existsByUserIdAndBookId(userId, bookId);
    }

    @Override
    public WishlistItem getWishlistItemByUserIdAndBookId(Long userId, Long bookId) {
        return wishlistItemRepository.getWishlistItemByUserIdAndBookId(userId, bookId);
    }
}