package com.example.homepharmacy.data;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MedicineRepository {

    private final MedicineDao medicineDao;
    private final LiveData<List<Medicine>> allMedicines;

    private final ExecutorService executorService =
            Executors.newSingleThreadExecutor();

    public MedicineRepository(Application application) {
        MedicineDatabase database =
                MedicineDatabase.getDatabase(application);

        medicineDao = database.medicineDao();
        allMedicines = medicineDao.getAllMedicines();
    }
    public LiveData<List<Medicine>> getMedicinesSortedByName() {
        return medicineDao.getMedicinesSortedByName();
    }

    public LiveData<List<Medicine>> getMedicinesSortedByExpiration() {
        return medicineDao.getMedicinesSortedByExpiration();
    }
    public LiveData<List<Medicine>> searchMedicinesByName(String query) {
        return medicineDao.searchMedicinesByName(query);
    }

    public LiveData<List<Medicine>> searchMedicinesByExpiration(String query) {
        return medicineDao.searchMedicinesByExpiration(query);
    }
    public LiveData<List<Medicine>> getAllMedicines() {
        return allMedicines;
    }
    public LiveData<Integer> getTotalCount() {
        return medicineDao.getTotalCount();
    }

    public LiveData<Integer> getWithoutExpirationDate() {
        return medicineDao.getWithoutExpirationDate();
    }
    public void insert(Medicine medicine) {
        executorService.execute(() -> medicineDao.insert(medicine));
    }

    public void update(Medicine medicine) {
        executorService.execute(() -> medicineDao.update(medicine));
    }

    public void delete(Medicine medicine) {
        executorService.execute(() -> medicineDao.delete(medicine));
    }
}