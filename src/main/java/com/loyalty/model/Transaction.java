package com.loyalty.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction {
    public enum Type { EARNED, REDEEMED }

    private String        description;
    private double        points;
    private Type          type;
    private LocalDateTime timestamp;

    public Transaction(String description, double points, Type type) {
        this.description = description;
        this.points      = points;
        this.type        = type;
        this.timestamp   = LocalDateTime.now();
    }

    public String getFormattedDate() {
        return timestamp.format(DateTimeFormatter.ofPattern("MMM dd, yyyy  h:mm a"));
    }

    // Getters
    public String getDescription() { return description; }
    public double getPoints()      { return points; }
    public Type   getType()        { return type; }
}

