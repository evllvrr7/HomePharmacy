package com.example.homepharmacy;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.graphics.Typeface;
import android.view.Gravity;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.homepharmacy.data.Medicine;
import com.example.homepharmacy.viewmodel.MedicineViewModel;
import com.google.android.material.card.MaterialCardView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class HomeFragment extends Fragment {

    private MedicineViewModel medicineViewModel;

    private TextView textTotalCount;
    private TextView textSafeCount;
    private TextView textExpiringCount;
    private TextView textExpiredCount;

    private TextView textNoAttention;
    private LinearLayout layoutAttentionMedicines;
    private TextView textShowAllAttention;

    public HomeFragment() {
        super(R.layout.fragment_home);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Статистика
        textTotalCount = view.findViewById(R.id.text_total_count);
        textSafeCount = view.findViewById(R.id.text_safe_count);
        textExpiringCount = view.findViewById(R.id.text_expiring_count);
        textExpiredCount = view.findViewById(R.id.text_expired_count);

        // Блок "Требуют внимания"
        textNoAttention = view.findViewById(R.id.text_no_attention);
        layoutAttentionMedicines =
                view.findViewById(R.id.layout_attention_medicines);
        textShowAllAttention =
                view.findViewById(R.id.text_show_all_attention);

        medicineViewModel =
                new ViewModelProvider(this)
                        .get(MedicineViewModel.class);

        // Получаем препараты из Room
        medicineViewModel.getAllMedicines().observe(
                getViewLifecycleOwner(),
                medicines -> {
                    updateStatistics(medicines);
                    updateAttentionBlock(medicines);
                }
        );

        // =========================
        // КЛИКИ ПО СТАТИСТИКЕ
        // =========================

        View cardTotal = view.findViewById(R.id.card_total);
        View cardSafe = view.findViewById(R.id.card_safe);
        View cardExpiring = view.findViewById(R.id.card_expiring);
        View cardExpired = view.findViewById(R.id.card_expired);

        cardTotal.setOnClickListener(v ->
                openMedicineList("Все")
        );

        cardSafe.setOnClickListener(v ->
                openMedicineList("В порядке")
        );

        cardExpiring.setOnClickListener(v ->
                openMedicineList("Скоро истекает")
        );

        cardExpired.setOnClickListener(v ->
                openMedicineList("Просрочен")
        );

        // =========================
        // КНОПКА "ПОКАЗАТЬ ВСЕ"
        // =========================

        textShowAllAttention.setOnClickListener(v ->
                openMedicineList("Требуют внимания")
        );
    }

    /**
     * Открывает список препаратов
     * с выбранным фильтром.
     */
    private void openMedicineList(String filter) {

        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(
                        R.id.main_container,
                        MedicineListFragment.newInstance(filter)
                )
                .addToBackStack(null)
                .commit();
    }

    /**
     * Обновление статистики.
     */
    private void updateStatistics(List<Medicine> medicines) {

        int total = medicines.size();
        int safe = 0;
        int expiringSoon = 0;
        int expired = 0;

        for (Medicine medicine : medicines) {

            String status =
                    getExpirationStatus(
                            medicine.getExpirationDate()
                    );

            switch (status) {

                case "В порядке":
                    safe++;
                    break;

                case "Скоро истекает":
                    expiringSoon++;
                    break;

                case "Просрочен":
                    expired++;
                    break;
            }
        }

        textTotalCount.setText(String.valueOf(total));
        textSafeCount.setText(String.valueOf(safe));
        textExpiringCount.setText(String.valueOf(expiringSoon));
        textExpiredCount.setText(String.valueOf(expired));
    }

    /**
     * Обновление блока "Требуют внимания".
     */
    private void updateAttentionBlock(List<Medicine> medicines) {

        layoutAttentionMedicines.removeAllViews();

        List<Medicine> attentionMedicines =
                new ArrayList<>();

        for (Medicine medicine : medicines) {

            String status =
                    getExpirationStatus(
                            medicine.getExpirationDate()
                    );

            if (status.equals("Просрочен")
                    || status.equals("Скоро истекает")) {

                attentionMedicines.add(medicine);
            }
        }

        // Если проблемных препаратов нет
        if (attentionMedicines.isEmpty()) {

            textNoAttention.setVisibility(View.VISIBLE);
            layoutAttentionMedicines.setVisibility(View.GONE);
            textShowAllAttention.setVisibility(View.GONE);

            return;
        }

        // Если есть препараты, требующие внимания
        textNoAttention.setVisibility(View.GONE);
        layoutAttentionMedicines.setVisibility(View.VISIBLE);
        textShowAllAttention.setVisibility(View.VISIBLE);

        // Показываем максимум 3 карточки
        int maxItems =
                Math.min(attentionMedicines.size(), 3);

        for (int i = 0; i < maxItems; i++) {

            Medicine medicine =
                    attentionMedicines.get(i);

            View card =
                    createAttentionCard(medicine);

            layoutAttentionMedicines.addView(card);
        }
    }

    /**
     * Создание карточки препарата
     * в блоке "Требуют внимания".
     */
    private View createAttentionCard(Medicine medicine) {

        MaterialCardView card =
                new MaterialCardView(requireContext());

        LinearLayout container =
                new LinearLayout(requireContext());

        container.setOrientation(
                LinearLayout.HORIZONTAL
        );

        container.setGravity(
                Gravity.CENTER_VERTICAL
        );

        // Цветовая полоска слева
        View indicator =
                new View(requireContext());

        LinearLayout.LayoutParams indicatorParams =
                new LinearLayout.LayoutParams(
                        dpToPx(5),
                        LinearLayout.LayoutParams.MATCH_PARENT
                );

        indicator.setLayoutParams(
                indicatorParams
        );

        String status =
                getExpirationStatus(
                        medicine.getExpirationDate()
                );

        if (status.equals("Просрочен")) {

            indicator.setBackgroundColor(
                    Color.rgb(239, 120, 120)
            );

        } else {

            indicator.setBackgroundColor(
                    Color.rgb(255, 183, 77)
            );
        }

        container.addView(indicator);

        // Основное содержимое
        LinearLayout content =
                new LinearLayout(requireContext());

        content.setOrientation(
                LinearLayout.VERTICAL
        );

        content.setPadding(
                dpToPx(14),
                dpToPx(12),
                dpToPx(14),
                dpToPx(12)
        );

        LinearLayout.LayoutParams contentParams =
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1
                );

        content.setLayoutParams(
                contentParams
        );

        // Название
        TextView name =
                new TextView(requireContext());

        name.setText(
                medicine.getName()
        );

        name.setTextSize(
                16
        );

        name.setTypeface(
                null,
                Typeface.BOLD
        );

        TypedArray typedArray =
                requireContext()
                        .obtainStyledAttributes(
                                new int[]{
                                        android.R.attr.textColorPrimary
                                }
                        );

        ColorStateList primaryTextColor =
                typedArray.getColorStateList(0);

        typedArray.recycle();

        if (primaryTextColor != null) {
            name.setTextColor(primaryTextColor);
        }

        content.addView(name);

        // Срок годности
        TextView expiration =
                new TextView(requireContext());

        expiration.setText(
                "Срок годности: "
                        + medicine.getExpirationDate()
        );

        expiration.setTextSize(
                13
        );

        TypedArray secondaryArray =
                requireContext()
                        .obtainStyledAttributes(
                                new int[]{
                                        android.R.attr.textColorSecondary
                                }
                        );

        ColorStateList secondaryTextColor =
                secondaryArray.getColorStateList(0);

        secondaryArray.recycle();

        if (secondaryTextColor != null) {
            expiration.setTextColor(secondaryTextColor);
        }

        LinearLayout.LayoutParams expirationParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        expirationParams.topMargin =
                dpToPx(5);

        expiration.setLayoutParams(
                expirationParams
        );

        content.addView(expiration);

        // Статус
        TextView statusText =
                new TextView(requireContext());

        statusText.setText(
                status
        );

        statusText.setTextSize(
                13
        );

        statusText.setTypeface(
                null,
                Typeface.BOLD
        );

        if (status.equals("Просрочен")) {

            statusText.setTextColor(
                    Color.rgb(239, 120, 120)
            );

        } else {

            statusText.setTextColor(
                    Color.rgb(255, 183, 77)
            );
        }

        LinearLayout.LayoutParams statusParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        statusParams.topMargin =
                dpToPx(4);

        statusText.setLayoutParams(
                statusParams
        );

        content.addView(statusText);

        container.addView(content);

        card.addView(container);

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        cardParams.topMargin =
                dpToPx(8);

        card.setLayoutParams(
                cardParams
        );

        card.setRadius(
                dpToPx(14)
        );

        card.setCardElevation(
                dpToPx(1)
        );

        card.setStrokeWidth(
                dpToPx(1)
        );

        card.setStrokeColor(
                Color.argb(
                        45,
                        255,
                        255,
                        255
                )
        );

        // Нажатие открывает детали препарата
        card.setClickable(true);
        card.setFocusable(true);

        card.setOnClickListener(v -> {

            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(
                            R.id.main_container,
                            MedicineDetailsFragment
                                    .newInstance(medicine)
                    )
                    .addToBackStack(null)
                    .commit();
        });

        return card;
    }
    private int dpToPx(int dp) {
        return Math.round(
                dp * getResources()
                        .getDisplayMetrics()
                        .density
        );
    }
    private String getExpirationStatus(String expirationDate) {

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
                    dateFormat.parse(expirationDate);

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
                    TimeUnit.MILLISECONDS
                            .toDays(difference);

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

    private int dp(int value) {

        return (int) (
                value
                        * getResources()
                        .getDisplayMetrics()
                        .density
        );
    }
}