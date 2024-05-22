package com.papershare.papershare.model;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    private String email;
    private String firstName;
    private String lastName;

    private String imageUrl;

    @OneToMany(mappedBy = "owner")
    private Collection<Book> ownedBooks;

    @OneToMany(mappedBy = "initiator")
    private Collection<ExchangeRequest> exchangeRequests;

    @OneToMany(mappedBy = "user")
    private Collection<WishlistItem> wishlistItems;

    @OneToMany(mappedBy = "rater", cascade = CascadeType.ALL, orphanRemoval = true)
    private Collection<UserRating> ratedUserRating;

    @OneToMany(mappedBy = "ratee", cascade = CascadeType.ALL, orphanRemoval = true)
    private Collection<UserRating> rateeUserRating;

    private Double averageInformationAccuracy;

    private Double averageShippingSpeed;

    private Double averageOverallExperience;

    private String roles;

    public User() {
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Collection<Book> getOwnedBooks() {
        return ownedBooks;
    }

    public void setOwnedBooks(Collection<Book> ownedBooks) {
        this.ownedBooks = ownedBooks;
    }

    public Collection<ExchangeRequest> getExchangeRequests() {
        return exchangeRequests;
    }

    public void setExchangeRequests(Collection<ExchangeRequest> exchangeRequests) {
        this.exchangeRequests = exchangeRequests;
    }

    public Collection<WishlistItem> getWishlistItems() {
        return wishlistItems;
    }

    public void setWishlistItems(Collection<WishlistItem> wishlistItems) {
        this.wishlistItems = wishlistItems;
    }

    public Collection<UserRating> getRatedUserRating() {
        return ratedUserRating;
    }

    public void setRatedUserRating(Collection<UserRating> ratedUserRating) {
        this.ratedUserRating = ratedUserRating;
    }

    public Collection<UserRating> getRateeUserRating() {
        return rateeUserRating;
    }

    public void setRateeUserRating(Collection<UserRating> rateeUserRating) {
        this.rateeUserRating = rateeUserRating;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public Double getAverageInformationAccuracy() {
        return averageInformationAccuracy;
    }

    public void setAverageInformationAccuracy(Double averageInformationAccuracy) {
        this.averageInformationAccuracy = averageInformationAccuracy;
    }

    public Double getAverageShippingSpeed() {
        return averageShippingSpeed;
    }

    public void setAverageShippingSpeed(Double averageShippingSpeed) {
        this.averageShippingSpeed = averageShippingSpeed;
    }

    public Double getAverageOverallExperience() {
        return averageOverallExperience;
    }

    public void setAverageOverallExperience(Double averageOverallExperience) {
        this.averageOverallExperience = averageOverallExperience;
    }
}