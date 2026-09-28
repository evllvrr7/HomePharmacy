package com.example.homepharmacy;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import android.text.Editable;
import android.text.TextWatcher;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.homepharmacy.data.Medicine;
import com.example.homepharmacy.viewmodel.MedicineViewModel;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;

public class AddMedicineFragment extends Fragment {

    private TextInputEditText editName;
    private TextInputEditText editQuantity;
    private TextInputEditText editExpiration;
    private TextInputEditText editStorage;
    private TextView textExpirationStatus;
    private TextInputEditText editNote;
    private Spinner spinnerForm;
    private Spinner spinnerUnit;
    private Spinner spinnerCategory;
    private MedicineViewModel medicineViewModel;
    private Medicine medicineToEdit;

    public AddMedicineFragment() {
        super(R.layout.fragment_add_medicine);
    }

    public static AddMedicineFragment newInstance(Medicine medicine) {

        AddMedicineFragment fragment =
                new AddMedicineFragment();

        Bundle args = new Bundle();
        args.putSerializable("medicine", medicine);

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
                R.layout.fragment_add_medicine,
                container,
                false
        );

        editName =
                view.findViewById(R.id.edit_medicine_name);



        editQuantity =
                view.findViewById(R.id.edit_medicine_quantity);



        editExpiration =
                view.findViewById(R.id.edit_medicine_expiration);

        textExpirationStatus =
                view.findViewById(
                        R.id.text_expiration_status
                );

        editStorage =
                view.findViewById(R.id.edit_medicine_storage);

        editNote =
                view.findViewById(R.id.edit_medicine_note);

        editExpiration.setOnClickListener(
                v -> showDatePicker()
        );
        editName.setOnFocusChangeListener(
                (v, hasFocus) -> {
                    if (hasFocus) {
                        editName.setError(null);
                    }
                }
        );

        editQuantity.setOnFocusChangeListener(
                (v, hasFocus) -> {
                    if (hasFocus) {
                        editQuantity.setError(null);
                    }
                }
        );

        editExpiration.setOnFocusChangeListener(
                (v, hasFocus) -> {
                    if (hasFocus) {
                        editExpiration.setError(null);
                    }
                }
        );
        editName.addTextChangedListener(
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
                        editName.setError(null);
                    }

                    @Override
                    public void afterTextChanged(
                            Editable s
                    ) {
                    }
                }
        );

        editQuantity.addTextChangedListener(
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
                        editQuantity.setError(null);
                    }

                    @Override
                    public void afterTextChanged(
                            Editable s
                    ) {
                    }
                }
        );

        editExpiration.addTextChangedListener(
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
                        editExpiration.setError(null);
                    }

                    @Override
                    public void afterTextChanged(
                            Editable s
                    ) {
                    }
                }
        );
        spinnerForm =
                view.findViewById(R.id.spinner_form);

        spinnerUnit =
                view.findViewById(R.id.spinner_unit);
        spinnerCategory =
                view.findViewById(R.id.spinner_category);
        String[] categories = {
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

        ArrayAdapter<String> categoryAdapter =
                new ArrayAdapter<>(
                        requireContext(),
                        android.R.layout.simple_spinner_item,
                        categories
                );

        categoryAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerCategory.setAdapter(categoryAdapter);
        String[] forms = {
                "Таблетки",
                "Капсулы",
                "Сироп",
                "Раствор",
                "Мазь",
                "Крем",
                "Гель",
                "Спрей",
                "Капли",
                "Порошок",
                "Другое"
        };

        ArrayAdapter<String> formAdapter =
                new ArrayAdapter<>(
                        requireContext(),
                        android.R.layout.simple_spinner_item,
                        forms
                );

        formAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerForm.setAdapter(formAdapter);


        String[] units = {
                "шт.",
                "таблеток",
                "капсул",
                "мл",
                "г",
                "упаковок",
                "доз",
                "другое"
        };

        ArrayAdapter<String> unitAdapter =
                new ArrayAdapter<>(
                        requireContext(),
                        android.R.layout.simple_spinner_item,
                        units
                );

        unitAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerUnit.setAdapter(unitAdapter);
        medicineViewModel =
                new ViewModelProvider(requireActivity())
                        .get(MedicineViewModel.class);

        // Проверяем, открыт ли экран в режиме редактирования
        if (getArguments() != null) {

            medicineToEdit =
                    (Medicine) getArguments()
                            .getSerializable("medicine");

            if (medicineToEdit != null) {

                fillFields();
                updateExpirationStatus();

                TextView title =
                        view.findViewById(R.id.text_form_title);

                if (title != null) {
                    title.setText("Редактирование препарата");
                }
            }
        }
        if (medicineToEdit != null) {
            TextView title = view.findViewById(R.id.text_form_title);
            title.setText("Редактирование препарата");
        }
        view.findViewById(R.id.button_save_medicine)
                .setOnClickListener(
                        v -> saveMedicine()
                );

        return view;
    }

    private void fillFields() {

        editName.setText(
                medicineToEdit.getName()
        );

        selectSpinnerValue(
                spinnerForm,
                medicineToEdit.getForm()
        );

        editQuantity.setText(
                String.valueOf(
                        medicineToEdit.getQuantity()
                )
        );

        selectSpinnerValue(
                spinnerUnit,
                medicineToEdit.getUnit()
        );

        editExpiration.setText(
                medicineToEdit.getExpirationDate()
        );

        selectSpinnerValue(
                spinnerCategory,
                medicineToEdit.getCategory()
        );

        editStorage.setText(
                medicineToEdit.getStoragePlace()
        );

        editNote.setText(
                medicineToEdit.getNote()
        );
    }
    private void selectSpinnerValue(
            Spinner spinner,
            String value
    ) {
        if (value == null) {
            return;
        }

        @SuppressWarnings("unchecked")
        ArrayAdapter<String> adapter =
                (ArrayAdapter<String>) spinner.getAdapter();

        int position =
                adapter.getPosition(value);

        if (position >= 0) {
            spinner.setSelection(position);
        }
    }
    private void saveMedicine() {

        // Сначала проверяем все обязательные поля
        if (!validateInputs()) {
            return;
        }

        String name =
                getText(editName);

        String form =
                spinnerForm.getSelectedItem().toString();

        String quantityText =
                getText(editQuantity);

        String unit =
                spinnerUnit.getSelectedItem().toString();

        String expiration =
                getText(editExpiration);

        String category =
                spinnerCategory.getSelectedItem().toString();

        String storage =
                getText(editStorage);

        String note =
                getText(editNote);

        int quantity =
                Integer.parseInt(quantityText);

        // =====================================================
        // ДОБАВЛЕНИЕ НОВОГО ПРЕПАРАТА
        // =====================================================

        if (medicineToEdit == null) {

            Medicine medicine =
                    new Medicine(
                            name,
                            form,
                            quantity,
                            unit,
                            expiration,
                            category,
                            storage,
                            note
                    );

            medicineViewModel.insert(
                    medicine
            );

            Toast.makeText(
                    requireContext(),
                    "Препарат сохранён",
                    Toast.LENGTH_SHORT
            ).show();

        } else {

            // =================================================
            // РЕДАКТИРОВАНИЕ СУЩЕСТВУЮЩЕГО
            // =================================================

            medicineToEdit.setName(
                    name
            );

            medicineToEdit.setForm(
                    form
            );

            medicineToEdit.setQuantity(
                    quantity
            );

            medicineToEdit.setUnit(
                    unit
            );

            medicineToEdit.setExpirationDate(
                    expiration
            );

            medicineToEdit.setCategory(
                    category
            );

            medicineToEdit.setStoragePlace(
                    storage
            );

            medicineToEdit.setNote(
                    note
            );

            medicineViewModel.update(
                    medicineToEdit
            );

            Toast.makeText(
                    requireContext(),
                    "Изменения сохранены",
                    Toast.LENGTH_SHORT
            ).show();
        }

        // Возвращаемся на предыдущий экран
        requireActivity()
                .getSupportFragmentManager()
                .popBackStack();
    }

    private void showDatePicker() {

        Calendar calendar =
                Calendar.getInstance();

        DatePickerDialog datePickerDialog =
                new DatePickerDialog(
                        requireContext(),

                        (view, year, month, dayOfMonth) -> {

                            Calendar selectedDate =
                                    Calendar.getInstance();

                            selectedDate.set(
                                    year,
                                    month,
                                    dayOfMonth
                            );

                            SimpleDateFormat dateFormat =
                                    new SimpleDateFormat(
                                            "dd.MM.yyyy",
                                            Locale.getDefault()
                                    );

                            editExpiration.setText(
                                    dateFormat.format(
                                            selectedDate.getTime()
                                    )
                            );
                            updateExpirationStatus();
                        },

                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                );

        datePickerDialog.show();
    }

    private String getText(
            TextInputEditText field
    ) {

        if (field.getText() == null) {
            return "";
        }

        return field.getText()
                .toString()
                .trim();
    }
    private boolean validateInputs() {

        String name =
                editName.getText()
                        .toString()
                        .trim();

        String quantityText =
                editQuantity.getText()
                        .toString()
                        .trim();

        String expiration =
                editExpiration.getText()
                        .toString()
                        .trim();

        boolean isValid = true;

        // Проверка названия
        if (name.isEmpty()) {

            editName.setError(
                    "Введите название препарата"
            );

            editName.requestFocus();

            isValid = false;
        } else {

            editName.setError(null);
        }

        // Проверка количества
        if (quantityText.isEmpty()) {

            editQuantity.setError(
                    "Введите количество"
            );

            if (isValid) {
                editQuantity.requestFocus();
            }

            isValid = false;

        } else {

            try {

                int quantity =
                        Integer.parseInt(
                                quantityText
                        );

                if (quantity <= 0) {

                    editQuantity.setError(
                            "Количество должно быть больше 0"
                    );

                    if (isValid) {
                        editQuantity.requestFocus();
                    }

                    isValid = false;

                } else {

                    editQuantity.setError(null);
                }

            } catch (NumberFormatException e) {

                editQuantity.setError(
                        "Введите целое число"
                );

                if (isValid) {
                    editQuantity.requestFocus();
                }

                isValid = false;
            }
        }

        // Проверка срока годности
        if (expiration.isEmpty()) {

            editExpiration.setError(
                    "Укажите срок годности"
            );

            if (isValid) {
                editExpiration.requestFocus();
            }

            isValid = false;

        } else {

            SimpleDateFormat dateFormat =
                    new SimpleDateFormat(
                            "dd.MM.yyyy",
                            Locale.getDefault()
                    );

            dateFormat.setLenient(false);

            try {

                Date date =
                        dateFormat.parse(
                                expiration
                        );

                if (date == null) {
                    throw new ParseException(
                            "Неверная дата",
                            0
                    );
                }

                editExpiration.setError(null);

            } catch (ParseException e) {

                editExpiration.setError(
                        "Введите дату в формате ДД.ММ.ГГГГ"
                );

                if (isValid) {
                    editExpiration.requestFocus();
                }

                isValid = false;
            }
        }

        return isValid;
    }
    private void updateExpirationStatus() {

        String expiration =
                getText(editExpiration);

        if (expiration.isEmpty()) {

            textExpirationStatus.setVisibility(
                    View.GONE
            );

            return;
        }

        SimpleDateFormat dateFormat =
                new SimpleDateFormat(
                        "dd.MM.yyyy",
                        Locale.getDefault()
                );

        dateFormat.setLenient(false);

        try {

            Date expirationDate =
                    dateFormat.parse(
                            expiration
                    );

            if (expirationDate == null) {

                textExpirationStatus.setVisibility(
                        View.GONE
                );

                return;
            }

            Calendar calendar =
                    Calendar.getInstance();

            calendar.set(
                    Calendar.HOUR_OF_DAY,
                    0
            );

            calendar.set(
                    Calendar.MINUTE,
                    0
            );

            calendar.set(
                    Calendar.SECOND,
                    0
            );

            calendar.set(
                    Calendar.MILLISECOND,
                    0
            );

            Date today =
                    calendar.getTime();

            long difference =
                    expirationDate.getTime()
                            - today.getTime();

            long days =
                    difference
                            / (1000 * 60 * 60 * 24);

            String status;

            if (days < 0) {

                status = "Просрочен";

            } else if (days <= 30) {

                status = "Скоро истекает";

            } else {

                status = "В порядке";
            }

            textExpirationStatus.setText(
                    status
            );

            textExpirationStatus.setVisibility(
                    View.VISIBLE
            );

            if (status.equals("Просрочен")) {

                textExpirationStatus.setTextColor(
                        requireContext().getColor(
                                R.color.medicine_expired
                        )
                );

                textExpirationStatus.setBackgroundColor(
                        requireContext().getColor(
                                R.color.home_expired_bg
                        )
                );

            } else if (status.equals(
                    "Скоро истекает"
            )) {

                textExpirationStatus.setTextColor(
                        requireContext().getColor(
                                R.color.medicine_expiring
                        )
                );

                textExpirationStatus.setBackgroundColor(
                        requireContext().getColor(
                                R.color.home_expiring_bg
                        )

                );

            } else {

                textExpirationStatus.setTextColor(
                        requireContext().getColor(
                                R.color.medicine_safe
                        )
                );

                textExpirationStatus.setBackgroundColor(
                        requireContext().getColor(
                                R.color.home_safe_bg
                        )
                );
            }

        } catch (ParseException e) {

            textExpirationStatus.setVisibility(
                    View.GONE
            );
        }
    }
}