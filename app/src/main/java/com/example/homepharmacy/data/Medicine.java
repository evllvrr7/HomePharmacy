package com.example.homepharmacy.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.io.Serializable;

@Entity(tableName = "medicines")
public class Medicine implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String name;
    private String form;
    private int quantity;
    private String unit;
    private String expirationDate;
    private String category;
    private String storagePlace;
    private String note;

    public Medicine(
            String name,
            String form,
            int quantity,
            String unit,
            String expirationDate,
            String category,
            String storagePlace,
            String note
    ) {
        this.name = name;
        this.form = form;
        this.quantity = quantity;
        this.unit = unit;
        this.expirationDate = expirationDate;
        this.category = category;
        this.storagePlace = storagePlace;
        this.note = note;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getForm() {
        return form;
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

    public String getCategory() {
        return category;
    }

    public String getStoragePlace() {
        return storagePlace;
    }

    public String getNote() {
        return note;
    }
    public void setName(String name) {
        this.name = name;
    }

    public void setForm(String form) {
        this.form = form;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setStoragePlace(String storagePlace) {
        this.storagePlace = storagePlace;
    }

    public void setNote(String note) {
        this.note = note;
    }
}