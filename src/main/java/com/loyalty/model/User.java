
package com.loyalty.model;

public class User {
    private String username;
    private double totalPoints;
    private String tierLevel;

    public User(String username) {
        this.username    = username;
        this.totalPoints = 0;
        this.tierLevel   = "Bronze";
    }

    public void addPoints(double points) {
        this.totalPoints += points;
        updateTier();
    }

    public boolean deductPoints(double points) {
        if (this.totalPoints >= points) {
            this.totalPoints -= points;
            updateTier();
            return true;
        }
        return false; // not enough points
    }

    private void updateTier() {
        if      (totalPoints >= 1000) tierLevel = "Gold";
        else if (totalPoints >= 500)  tierLevel = "Silver";
        else                          tierLevel = "Bronze";
    }

    // Getters
    public String getUsername()   { return username; }
    public double getTotalPoints(){ return totalPoints; }
    public String getTierLevel()  { return tierLevel; }
   
}
