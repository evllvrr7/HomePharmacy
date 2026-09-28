package com.example.homepharmacy.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(
        entities = {Medicine.class},
        version = 1,
        exportSchema = false
)
public abstract class MedicineDatabase extends RoomDatabase {

    public abstract MedicineDao medicineDao();

    private static volatile MedicineDatabase INSTANCE;

    public static MedicineDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            synchronized (MedicineDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            MedicineDatabase.class,
                            "home_pharmacy_database"
                    ).build();
                }
            }
        }

        return INSTANCE;
    }
}