package com.example.homepharmacy.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.homepharmacy.data.Medicine;
import com.example.homepharmacy.data.MedicineRepository;

import java.util.List;

public class MedicineViewModel extends AndroidViewModel {

    private final MedicineRepository repository;
    private final LiveData<List<Medicine>> allMedicines;

    public MedicineViewModel(@NonNull Application application) {
        super(application);

        repository = new MedicineRepository(application);
        allMedicines = repository.getAllMedicines();
    }
    public LiveData<List<Medicine>> getMedicinesSortedByName() {
        return repository.getMedicinesSortedByName();
    }

    public LiveData<List<Medicine>> getMedicinesSortedByExpiration() {
        return repository.getMedicinesSortedByExpiration();
    }
    public LiveData<List<Medicine>> searchMedicinesByName(String query) {
        return repository.searchMedicinesByName(query);
    }

    public LiveData<List<Medicine>> searchMedicinesByExpiration(String query) {
        return repository.searchMedicinesByExpiration(query);
    }
    public LiveData<List<Medicine>> getAllMedicines() {
        return allMedicines;
    }
    public LiveData<Integer> getTotalCount() {
        return repository.getTotalCount();
    }

    public LiveData<Integer> getWithoutExpirationDate() {
        return repository.getWithoutExpirationDate();
    }
    public void insert(Medicine medicine) {
        repository.insert(medicine);
    }

    public void update(Medicine medicine) {
        repository.update(medicine);
    }

    public void delete(Medicine medicine) {
        repository.delete(medicine);
    }
}