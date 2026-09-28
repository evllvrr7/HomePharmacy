package com.example.homepharmacy.model;

public class Medicine {

    private String name;
    private int quantity;
    private String unit;
    private String expirationDate;

    public Medicine(
            String name,
            int quantity,
            String unit,
            String expirationDate
    ) {
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.expirationDate = expirationDate;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getUnit() {
        return unit;
    }

    public String getExpirationDate() {
        return expirationDate;
    }
}