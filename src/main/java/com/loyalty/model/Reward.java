package com.loyalty.model;
public class Reward {
    private String name;
    private double pointCost;
    private String icon;       // emoji string e.g. "🎁"
    private boolean available;

    public Reward(String name, double pointCost, String icon) {
        this.name      = name;
        this.pointCost = pointCost;
        this.icon      = icon;
        this.available = true;
    }

    // Getters
    public String  getName()      { return name; }
    public double  getPointCost() { return pointCost; }
    public String  getIcon()      { return icon; }
    public boolean isAvailable()  { return available; }
    public void    setAvailable(boolean available) { this.available = available; }
}