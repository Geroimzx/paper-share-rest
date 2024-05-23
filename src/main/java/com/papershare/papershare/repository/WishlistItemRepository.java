package com.papershare.papershare.repository;

import com.papershare.papershare.model.User;
import com.papershare.papershare.model.WishlistItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WishlistItemRepository extends JpaRepository<WishlistItem, Long> {
    List<WishlistItem> findAllByUserId(Long userId);

    boolean existsByUserIdAndBookId(Long userId, Long bookId);

    WishlistItem getWishlistItemByUserIdAndBookId(Long userId, Long bookId);
}