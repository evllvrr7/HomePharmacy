package com.example.homepharmacy;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.homepharmacy.data.Medicine;
import com.example.homepharmacy.viewmodel.MedicineViewModel;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MedicineDetailsFragment extends Fragment {

    private Medicine medicine;
    private MedicineViewModel medicineViewModel;

    public MedicineDetailsFragment() {
        super(R.layout.fragment_medicine_details);
    }

    public static MedicineDetailsFragment newInstance(Medicine medicine) {
        MedicineDetailsFragment fragment =
                new MedicineDetailsFragment();

        Bundle args = new Bundle();
        args.putSerializable("medicine", medicine);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            medicine =
                    (Medicine) getArguments()
                            .getSerializable("medicine");
        }

        if (medicine == null) {
            return;
        }

        medicineViewModel =
                new ViewModelProvider(requireActivity())
                        .get(MedicineViewModel.class);

        TextView textName =
                view.findViewById(R.id.text_detail_name);

        TextView textQuantity =
                view.findViewById(R.id.text_detail_quantity);

        TextView textExpiration =
                view.findViewById(R.id.text_detail_expiration);

        TextView textCategory =
                view.findViewById(R.id.text_detail_category);

        TextView textStorage =
                view.findViewById(R.id.text_detail_storage);

        TextView textForm =
                view.findViewById(R.id.text_detail_form);

        TextView textStatus =
                view.findViewById(R.id.text_detail_status);

        TextView textNote =
                view.findViewById(R.id.text_detail_note);

        MaterialCardView cardNote =
                view.findViewById(R.id.card_detail_note);

        textName.setText(medicine.getName());

        textQuantity.setText(
                medicine.getQuantity()
                        + " "
                        + medicine.getUnit()
        );

        textExpiration.setText(
                medicine.getExpirationDate()
        );

        textCategory.setText(
                medicine.getCategory()
        );

        textStorage.setText(
                medicine.getStoragePlace()
        );

        textForm.setText(
                medicine.getForm()
        );

        String status =
                getExpirationStatus(
                        medicine.getExpirationDate()
                );

        textStatus.setText(status);

        setStatusStyle(
                textStatus,
                status
        );

        String note = medicine.getNote();

        if (note == null
                || note.trim().isEmpty()) {

            cardNote.setVisibility(View.GONE);

        } else {

            textNote.setText(note);
        }

        // Редактирование
        view.findViewById(R.id.button_edit_medicine)
                .setOnClickListener(v -> {

                    requireActivity()
                            .getSupportFragmentManager()
                            .beginTransaction()
                            .replace(
                                    R.id.main_container,
                                    AddMedicineFragment.newInstance(
                                            medicine
                                    )
                            )
                            .addToBackStack(null)
                            .commit();
                });

        // Удаление
        view.findViewById(R.id.button_delete_medicine)
                .setOnClickListener(v -> handleDelete());
    }

    private void handleDelete() {

        SharedPreferences preferences =
                requireContext()
                        .getSharedPreferences(
                                "app_settings",
                                Context.MODE_PRIVATE
                        );

        boolean confirmDelete =
                preferences.getBoolean(
                        "confirm_delete",
                        true
                );

        if (confirmDelete) {

            showDeleteDialog();

        } else {

            deleteMedicine();
        }
    }

    private void showDeleteDialog() {

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Удалить препарат?")
                .setMessage(
                        "Препарат «"
                                + medicine.getName()
                                + "» будет удалён из аптечки."
                )
                .setNegativeButton(
                        "Отмена",
                        null
                )
                .setPositiveButton(
                        "Удалить",
                        (dialog, which) ->
                                deleteMedicine()
                )
                .show();
    }

    private void deleteMedicine() {

        medicineViewModel.delete(medicine);

        requireActivity()
                .getSupportFragmentManager()
                .popBackStack();
    }

    private String getExpirationStatus(
            String expirationDate
    ) {

        if (expirationDate == null
                || expirationDate.isEmpty()) {

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
                    expiration.getTime()
                            - today.getTime();

            long days =
                    TimeUnit.MILLISECONDS.toDays(
                            difference
                    );

            if (days < 0) {
                return "Просрочен";
            }

            if (days <= 30) {
                return "Скоро истекает";
            }

            return "В порядке";

        } catch (ParseException e) {

            return "Неверная дата";
        }
    }

    private void setStatusStyle(
            TextView textView,
            String status
    ) {

        if (status.equals("Просрочен")) {

            textView.setTextColor(
                    Color.rgb(198, 40, 40)
            );

            textView.setBackgroundColor(
                    Color.rgb(255, 235, 238)
            );

        } else if (status.equals("Скоро истекает")) {

            textView.setTextColor(
                    Color.rgb(230, 126, 34)
            );

            textView.setBackgroundColor(
                    Color.rgb(255, 243, 224)
            );

        } else if (status.equals("В порядке")) {

            textView.setTextColor(
                    Color.rgb(46, 125, 50)
            );

            textView.setBackgroundColor(
                    Color.rgb(232, 245, 233)
            );

        } else {

            textView.setTextColor(
                    Color.rgb(117, 117, 117)
            );

            textView.setBackgroundColor(
                    Color.rgb(245, 245, 245)
            );
        }
    }
}