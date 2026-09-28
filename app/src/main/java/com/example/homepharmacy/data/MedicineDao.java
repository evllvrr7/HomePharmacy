package com.example.homepharmacy.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface MedicineDao {

    @Insert
    void insert(Medicine medicine);

    @Update
    void update(Medicine medicine);

    @Delete
    void delete(Medicine medicine);
    @Query("SELECT * FROM medicines")
    List<Medicine> getAllMedicinesSync();
    @Query("SELECT * FROM medicines ORDER BY name ASC")
    LiveData<List<Medicine>> getMedicinesSortedByName();
    @Query("SELECT * FROM medicines ORDER BY " +
            "substr(expirationDate, 7, 4) ASC, " +
            "substr(expirationDate, 4, 2) ASC, " +
            "substr(expirationDate, 1, 2) ASC")
    LiveData<List<Medicine>> getMedicinesSortedByExpiration();
    @Query("SELECT COUNT(*) FROM medicines")
    LiveData<Integer> getTotalCount();

    @Query("SELECT COUNT(*) FROM medicines WHERE expirationDate = ''")
    LiveData<Integer> getWithoutExpirationDate();
    @Query("SELECT * FROM medicines ORDER BY name ASC")
    LiveData<List<Medicine>> getAllMedicines();

    @Query("SELECT * FROM medicines " +
            "WHERE name LIKE '%' || :searchQuery || '%' " +
            "ORDER BY name ASC")
    LiveData<List<Medicine>> searchMedicinesByName(String searchQuery);

    @Query("SELECT * FROM medicines " +
            "WHERE name LIKE '%' || :searchQuery || '%' " +
            "ORDER BY " +
            "substr(expirationDate, 7, 4) ASC, " +
            "substr(expirationDate, 4, 2) ASC, " +
            "substr(expirationDate, 1, 2) ASC")
    LiveData<List<Medicine>> searchMedicinesByExpiration(String searchQuery);
}