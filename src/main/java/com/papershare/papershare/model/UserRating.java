package com.papershare.papershare.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@Entity
public class UserRating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rater_id")
    private User rater;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ratee_id")
    private User ratee;

    @Min(1)
    @Max(5)
    private int informationAccuracy;

    @Min(1)
    @Max(5)
    private int shippingSpeed;

    @Min(1)
    @Max(5)
    private int overallExperience;

    public UserRating() {
    }

    public UserRating(User rater, User ratee, int informationAccuracy, int shippingSpeed, int overallExperience) {
        this.rater = rater;
        this.ratee = ratee;
        this.informationAccuracy = informationAccuracy;
        this.shippingSpeed = shippingSpeed;
        this.overallExperience = overallExperience;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getRater() {
        return rater;
    }

    public void setRater(User rater) {
        this.rater = rater;
    }

    public User getRatee() {
        return ratee;
    }

    public void setRatee(User ratee) {
        this.ratee = ratee;
    }

    public int getInformationAccuracy() {
        return informationAccuracy;
    }

    public void setInformationAccuracy(int informationAccuracy) {
        this.informationAccuracy = informationAccuracy;
    }

    public int getShippingSpeed() {
        return shippingSpeed;
    }

    public void setShippingSpeed(int shippingSpeed) {
        this.shippingSpeed = shippingSpeed;
    }

    public int getOverallExperience() {
        return overallExperience;
    }

    public void setOverallExperience(int overallExperience) {
        this.overallExperience = overallExperience;
    }
}