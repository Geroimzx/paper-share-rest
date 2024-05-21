package com.papershare.papershare.model;

public class UserRatingRequest {
    private int informationAccuracy;
    private int shippingSpeed;
    private int overallExperience;

    public UserRatingRequest() {
    }

    public UserRatingRequest(int informationAccuracy, int shippingSpeed, int overallExperience) {
        this.informationAccuracy = informationAccuracy;
        this.shippingSpeed = shippingSpeed;
        this.overallExperience = overallExperience;
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
