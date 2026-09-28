package com.example.homepharmacy;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homepharmacy.adapter.MedicineAdapter;
import com.example.homepharmacy.data.Medicine;
import com.example.homepharmacy.viewmodel.MedicineViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MedicineListFragment extends Fragment {

    private TextView textEmptyMedicines;
    private TextView textEmptyHint;
    private LinearLayout layoutEmptyMedicines;

    private MedicineViewModel medicineViewModel;
    private MedicineAdapter adapter;

    private List<Medicine> currentMedicines =
            new ArrayList<>();

    private String currentSearchQuery = "";

    private String currentSortOption =
            "По названию";

    private String currentFilterOption =
            "Все";

    private String currentCategoryOption =
            "Все";

    private SharedPreferences listPreferences;

    public MedicineListFragment() {
        super(R.layout.fragment_medicine_list);
    }

    public static MedicineListFragment newInstance(String filter) {

        MedicineListFragment fragment =
                new MedicineListFragment();

        Bundle args = new Bundle();

        args.putString(
                "filter",
                filter
        );

        fragment.setArguments(args);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {

        View view = inflater.inflate(
                R.layout.fragment_medicine_list,
                container,
                false
        );

        // Настройки списка
        listPreferences =
                requireContext()
                        .getSharedPreferences(
                                "list_settings",
                                Context.MODE_PRIVATE
                        );

        // Восстанавливаем сортировку
        currentSortOption =
                listPreferences.getString(
                        "sort",
                        "По названию"
                );

        // Восстанавливаем категорию
        currentCategoryOption =
                listPreferences.getString(
                        "category",
                        "Все"
                );

        // Восстанавливаем фильтр,
        // если экран открыт обычным способом
        currentFilterOption =
                listPreferences.getString(
                        "filter",
                        "Все"
                );

        // Если фильтр передан с главного экрана,
        // используем именно его
        if (getArguments() != null) {

            String filter =
                    getArguments()
                            .getString("filter");

            if (filter != null) {
                currentFilterOption = filter;
            }
        }

        // RecyclerView
        RecyclerView recyclerMedicines =
                view.findViewById(
                        R.id.recycler_medicines
                );

        // Пустое состояние
        layoutEmptyMedicines =
                view.findViewById(
                        R.id.layout_empty_medicines
                );

        textEmptyMedicines =
                view.findViewById(
                        R.id.text_empty_medicines
                );

        textEmptyHint =
                view.findViewById(
                        R.id.text_empty_hint
                );

        // Поиск
        TextInputEditText editSearch =
                view.findViewById(
                        R.id.edit_search
                );

        // Кнопки
        MaterialButton buttonSort =
                view.findViewById(
                        R.id.button_sort
                );

        MaterialButton buttonFilter =
                view.findViewById(
                        R.id.button_filter
                );

        MaterialButton buttonCategory =
                view.findViewById(
                        R.id.button_category
                );

        MaterialButton buttonReset =
                view.findViewById(
                        R.id.button_reset
                );

        // Адаптер
        adapter = new MedicineAdapter(
                new ArrayList<>(),
                medicine -> {

                    requireActivity()
                            .getSupportFragmentManager()
                            .beginTransaction()
                            .replace(
                                    R.id.main_container,
                                    MedicineDetailsFragment
                                            .newInstance(
                                                    medicine
                                            )
                            )
                            .addToBackStack(null)
                            .commit();
                }
        );

        recyclerMedicines.setLayoutManager(
                new LinearLayoutManager(
                        requireContext()
                )
        );

        recyclerMedicines.setAdapter(
                adapter
        );

        // ViewModel
        medicineViewModel =
                new ViewModelProvider(this)
                        .get(MedicineViewModel.class);

        // Показываем текущие значения
        updateSortButton(buttonSort);
        updateFilterButton(buttonFilter);
        updateCategoryButton(buttonCategory);

        // Сортировка
        buttonSort.setOnClickListener(v ->
                showSortDialog(buttonSort)
        );

        // Фильтр
        buttonFilter.setOnClickListener(v ->
                showFilterDialog(buttonFilter)
        );

        // Категория
        buttonCategory.setOnClickListener(v ->
                showCategoryDialog(buttonCategory)
        );

        // Сброс
        buttonReset.setOnClickListener(v -> {

            currentSearchQuery = "";
            currentSortOption = "По названию";
            currentFilterOption = "Все";
            currentCategoryOption = "Все";

            editSearch.setText("");

            updateSortButton(buttonSort);
            updateFilterButton(buttonFilter);
            updateCategoryButton(buttonCategory);

            saveListSettings();

            updateDisplayedMedicines();
        });

        // Поиск
        editSearch.addTextChangedListener(
                new TextWatcher() {

                    @Override
                    public void beforeTextChanged(
                            CharSequence s,
                            int start,
                            int count,
                            int after
                    ) {
                    }

                    @Override
                    public void onTextChanged(
                            CharSequence s,
                            int start,
                            int before,
                            int count
                    ) {

                        currentSearchQuery =
                                s.toString()
                                        .trim();

                        updateDisplayedMedicines();
                    }

                    @Override
                    public void afterTextChanged(
                            Editable s
                    ) {
                    }
                }
        );

        // Добавление препарата
        view.findViewById(
                R.id.fab_add_medicine
        ).setOnClickListener(v -> {

            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(
                            R.id.main_container,
                            new AddMedicineFragment()
                    )
                    .addToBackStack(null)
                    .commit();
        });

        // Room
        medicineViewModel
                .getAllMedicines()
                .observe(
                        getViewLifecycleOwner(),
                        medicines -> {

                            currentMedicines =
                                    new ArrayList<>(
                                            medicines
                                    );

                            updateDisplayedMedicines();
                        }
                );

        return view;
    }

    // =========================================================
    // ДИАЛОГ СОРТИРОВКИ
    // =========================================================

    private void showSortDialog(
            MaterialButton buttonSort
    ) {

        String[] options = {
                "По названию",
                "По сроку годности",
                "По количеству"
        };

        int selectedPosition;

        if (currentSortOption.equals(
                "По сроку годности"
        )) {

            selectedPosition = 1;

        } else if (currentSortOption.equals(
                "По количеству"
        )) {

            selectedPosition = 2;

        } else {

            selectedPosition = 0;
        }

        new MaterialAlertDialogBuilder(
                requireContext()
        )
                .setTitle("Сортировка")
                .setSingleChoiceItems(
                        options,
                        selectedPosition,
                        (dialog, which) -> {

                            currentSortOption =
                                    options[which];

                            updateSortButton(
                                    buttonSort
                            );

                            saveListSettings();

                            updateDisplayedMedicines();

                            dialog.dismiss();
                        }
                )
                .show();
    }

    // =========================================================
    // ДИАЛОГ ФИЛЬТРА
    // =========================================================

    private void showFilterDialog(
            MaterialButton buttonFilter
    ) {

        String[] options = {
                "Все",
                "В порядке",
                "Скоро истекает",
                "Просрочен",
                "Требуют внимания"
        };

        int selectedPosition = 0;

        for (int i = 0;
             i < options.length;
             i++) {

            if (options[i].equals(
                    currentFilterOption
            )) {

                selectedPosition = i;
                break;
            }
        }

        new MaterialAlertDialogBuilder(
                requireContext()
        )
                .setTitle("Фильтр")
                .setSingleChoiceItems(
                        options,
                        selectedPosition,
                        (dialog, which) -> {

                            currentFilterOption =
                                    options[which];

                            updateFilterButton(
                                    buttonFilter
                            );

                            saveListSettings();

                            updateDisplayedMedicines();

                            dialog.dismiss();
                        }
                )
                .show();
    }

    // =========================================================
    // ДИАЛОГ КАТЕГОРИИ
    // =========================================================

    private void showCategoryDialog(
            MaterialButton buttonCategory
    ) {

        String[] categories = {
                "Все",
                "Обезболивающие",
                "Жаропонижающие",
                "Противовоспалительные",
                "Антисептики",
                "Противоаллергические",
                "Желудочно-кишечные",
                "Витамины",
                "Сердечно-сосудистые",
                "Для наружного применения",
                "Другое"
        };

        int selectedPosition = 0;

        for (int i = 0;
             i < categories.length;
             i++) {

            if (categories[i].equals(
                    currentCategoryOption
            )) {

                selectedPosition = i;
                break;
            }
        }

        new MaterialAlertDialogBuilder(
                requireContext()
        )
                .setTitle("Категория")
                .setSingleChoiceItems(
                        categories,
                        selectedPosition,
                        (dialog, which) -> {

                            currentCategoryOption =
                                    categories[which];

                            updateCategoryButton(
                                    buttonCategory
                            );

                            saveListSettings();

                            updateDisplayedMedicines();

                            dialog.dismiss();
                        }
                )
                .show();
    }

    // =========================================================
    // КНОПКИ
    // =========================================================

    private void updateSortButton(
            MaterialButton buttonSort
    ) {

        buttonSort.setText(
                "Сортировка: "
                        + currentSortOption
        );
    }

    private void updateFilterButton(
            MaterialButton buttonFilter
    ) {

        buttonFilter.setText(
                "Фильтр: "
                        + currentFilterOption
        );
    }

    private void updateCategoryButton(
            MaterialButton buttonCategory
    ) {

        buttonCategory.setText(
                "Категория: "
                        + currentCategoryOption
        );
    }

    // =========================================================
    // СОХРАНЕНИЕ НАСТРОЕК СПИСКА
    // =========================================================

    private void saveListSettings() {

        if (listPreferences == null) {
            return;
        }

        listPreferences
                .edit()
                .putString(
                        "sort",
                        currentSortOption
                )
                .putString(
                        "filter",
                        currentFilterOption
                )
                .putString(
                        "category",
                        currentCategoryOption
                )
                .apply();
    }

    // =========================================================
    // ФИЛЬТРАЦИЯ И СОРТИРОВКА
    // =========================================================

    private void updateDisplayedMedicines() {

        List<Medicine> result =
                new ArrayList<>(
                        currentMedicines
                );

        // Поиск
        if (!currentSearchQuery.isEmpty()) {

            String query =
                    currentSearchQuery
                            .toLowerCase(
                                    Locale.getDefault()
                            );

            List<Medicine> filtered =
                    new ArrayList<>();

            for (Medicine medicine : result) {

                String name =
                        medicine.getName();

                if (name != null &&
                        name.toLowerCase(
                                Locale.getDefault()
                        ).contains(query)) {

                    filtered.add(
                            medicine
                    );
                }
            }

            result = filtered;
        }

        // Фильтр по статусу
        if (!currentFilterOption.equals(
                "Все"
        )) {

            List<Medicine> filteredByStatus =
                    new ArrayList<>();

            for (Medicine medicine : result) {

                String status =
                        getExpirationStatus(
                                medicine.getExpirationDate()
                        );

                if (currentFilterOption.equals(
                        "Требуют внимания"
                )) {

                    if (status.equals(
                            "Просрочен"
                    )
                            || status.equals(
                            "Скоро истекает"
                    )) {

                        filteredByStatus.add(
                                medicine
                        );
                    }

                } else {

                    if (status.equals(
                            currentFilterOption
                    )) {

                        filteredByStatus.add(
                                medicine
                        );
                    }
                }
            }

            result = filteredByStatus;
        }

        // Фильтр по категории
        if (!currentCategoryOption.equals(
                "Все"
        )) {

            List<Medicine> filteredByCategory =
                    new ArrayList<>();

            for (Medicine medicine : result) {

                String category =
                        medicine.getCategory();

                if (category != null &&
                        category.equals(
                                currentCategoryOption
                        )) {

                    filteredByCategory.add(
                            medicine
                    );
                }
            }

            result = filteredByCategory;
        }

        // Сортировка по названию
        if (currentSortOption.equals(
                "По названию"
        )) {

            Collections.sort(
                    result,
                    (medicine1, medicine2) -> {

                        String name1 =
                                medicine1.getName();

                        String name2 =
                                medicine2.getName();

                        if (name1 == null &&
                                name2 == null) {
                            return 0;
                        }

                        if (name1 == null) {
                            return 1;
                        }

                        if (name2 == null) {
                            return -1;
                        }

                        return name1
                                .compareToIgnoreCase(
                                        name2
                                );
                    }
            );

            // Сортировка по сроку
        } else if (currentSortOption.equals(
                "По сроку годности"
        )) {

            SimpleDateFormat dateFormat =
                    new SimpleDateFormat(
                            "dd.MM.yyyy",
                            Locale.getDefault()
                    );

            dateFormat.setLenient(false);

            Collections.sort(
                    result,
                    (medicine1, medicine2) -> {

                        Date date1 =
                                parseDate(
                                        medicine1
                                                .getExpirationDate(),
                                        dateFormat
                                );

                        Date date2 =
                                parseDate(
                                        medicine2
                                                .getExpirationDate(),
                                        dateFormat
                                );

                        if (date1 == null &&
                                date2 == null) {
                            return 0;
                        }

                        if (date1 == null) {
                            return 1;
                        }

                        if (date2 == null) {
                            return -1;
                        }

                        return date1.compareTo(
                                date2
                        );
                    }
            );

            // Сортировка по количеству
        } else if (currentSortOption.equals(
                "По количеству"
        )) {

            Collections.sort(
                    result,
                    (medicine1, medicine2) ->
                            Integer.compare(
                                    medicine1.getQuantity(),
                                    medicine2.getQuantity()
                            )
            );
        }

        adapter.setMedicines(result);

        // Пустое состояние
        if (result.isEmpty()) {

            layoutEmptyMedicines.setVisibility(
                    View.VISIBLE
            );

            if (currentMedicines.isEmpty()) {

                textEmptyMedicines.setText(
                        "В аптечке пока нет препаратов"
                );

                textEmptyHint.setText(
                        "Нажмите «+», чтобы добавить препарат."
                );

            } else if (!currentSearchQuery.isEmpty()) {

                textEmptyMedicines.setText(
                        "По вашему запросу ничего не найдено"
                );

                textEmptyHint.setText(
                        "Попробуйте изменить поисковый запрос."
                );

            } else if (!currentCategoryOption.equals(
                    "Все"
            )) {

                textEmptyMedicines.setText(
                        "Нет препаратов этой категории"
                );

                textEmptyHint.setText(
                        "Выберите другую категорию."
                );

            } else if (!currentFilterOption.equals(
                    "Все"
            )) {

                textEmptyMedicines.setText(
                        "Нет препаратов, соответствующих фильтру"
                );

                textEmptyHint.setText(
                        "Выберите другой фильтр."
                );

            } else {

                textEmptyMedicines.setText(
                        "Препараты не найдены"
                );

                textEmptyHint.setText(
                        "Попробуйте изменить условия поиска."
                );
            }

        } else {

            layoutEmptyMedicines.setVisibility(
                    View.GONE
            );
        }
    }

    // =========================================================
    // РАЗБОР ДАТЫ
    // =========================================================

    private Date parseDate(
            String dateString,
            SimpleDateFormat dateFormat
    ) {

        if (dateString == null ||
                dateString.isEmpty()) {

            return null;
        }

        try {

            return dateFormat.parse(
                    dateString
            );

        } catch (ParseException e) {

            return null;
        }
    }

    // =========================================================
    // ОПРЕДЕЛЕНИЕ СТАТУСА
    // =========================================================

    private String getExpirationStatus(
            String expirationDate
    ) {

        if (expirationDate == null ||
                expirationDate.isEmpty()) {

            return "Дата не указана";
        }

        SimpleDateFormat dateFormat =
                new SimpleDateFormat(
                        "dd.MM.yyyy",
                        Locale.getDefault()
                );

        dateFormat.setLenient(false);

        try {

            Date expiration =
                    dateFormat.parse(
                            expirationDate
                    );

            if (expiration == null) {
                return "Неверная дата";
            }

            Date today =
                    new Date();

            long difference =
                    expiration.getTime()
                            - today.getTime();

            long days =
                    difference
                            / (1000 * 60 * 60 * 24);

            if (days < 0) {

                return "Просрочен";

            } else if (days <= 30) {

                return "Скоро истекает";

            } else {

                return "В порядке";
            }

        } catch (ParseException e) {

            return "Неверная дата";
        }
    }
}