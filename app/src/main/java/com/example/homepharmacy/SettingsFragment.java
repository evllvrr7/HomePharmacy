package com.example.homepharmacy;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.google.android.material.materialswitch.MaterialSwitch;

public class SettingsFragment extends Fragment {

    private SharedPreferences preferences;

    private ActivityResultLauncher<String>
            notificationPermissionLauncher;

    public SettingsFragment() {
        super(R.layout.fragment_settings);
    }

    @Override
    public void onCreate(
            @Nullable Bundle savedInstanceState
    ) {
        super.onCreate(savedInstanceState);

        // Запрос разрешения на уведомления
        notificationPermissionLauncher =
                registerForActivityResult(
                        new ActivityResultContracts.RequestPermission(),
                        isGranted -> {

                            if (isGranted) {

                                preferences
                                        .edit()
                                        .putBoolean(
                                                "notifications_enabled",
                                                true
                                        )
                                        .apply();

                                MainActivity
                                        .scheduleMedicineNotifications(
                                                requireContext()
                                        );

                            } else {

                                Toast.makeText(
                                        requireContext(),
                                        "Разрешение на уведомления не предоставлено",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        }
                );
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(
                view,
                savedInstanceState
        );

        preferences =
                requireContext()
                        .getSharedPreferences(
                                "app_settings",
                                Context.MODE_PRIVATE
                        );

        // =====================================================
        // ПОКАЗ КАТЕГОРИИ
        // =====================================================

        MaterialSwitch switchShowCategory =
                view.findViewById(
                        R.id.switch_show_category
                );

        boolean showCategory =
                preferences.getBoolean(
                        "show_category",
                        true
                );

        switchShowCategory.setChecked(
                showCategory
        );

        switchShowCategory.setOnCheckedChangeListener(
                (buttonView, isChecked) ->
                        preferences
                                .edit()
                                .putBoolean(
                                        "show_category",
                                        isChecked
                                )
                                .apply()
        );

        // =====================================================
        // ПОДТВЕРЖДЕНИЕ УДАЛЕНИЯ
        // =====================================================

        MaterialSwitch switchConfirmDelete =
                view.findViewById(
                        R.id.switch_confirm_delete
                );

        boolean confirmDelete =
                preferences.getBoolean(
                        "confirm_delete",
                        true
                );

        switchConfirmDelete.setChecked(
                confirmDelete
        );

        switchConfirmDelete.setOnCheckedChangeListener(
                (buttonView, isChecked) ->
                        preferences
                                .edit()
                                .putBoolean(
                                        "confirm_delete",
                                        isChecked
                                )
                                .apply()
        );

        MaterialSwitch switchNotifications =
                view.findViewById(
                        R.id.switch_notifications
                );
        Spinner spinnerNotificationDays =
                view.findViewById(
                        R.id.spinner_notification_days
                );

        boolean notificationsEnabled =
                preferences.getBoolean(
                        "notifications_enabled",
                        false
                );

        // Если уведомления были включены,
        // но разрешение отозвано в настройках Android,
        // показываем переключатель выключенным
        if (notificationsEnabled
                && !hasNotificationPermission()) {

            notificationsEnabled = false;

            preferences
                    .edit()
                    .putBoolean(
                            "notifications_enabled",
                            false
                    )
                    .apply();

            MainActivity
                    .cancelMedicineNotifications(
                            requireContext()
                    );
        }

        switchNotifications.setChecked(
                notificationsEnabled
        );

        switchNotifications.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {

                    if (isChecked) {

                        enableNotifications(
                                switchNotifications
                        );

                    } else {

                        preferences
                                .edit()
                                .putBoolean(
                                        "notifications_enabled",
                                        false
                                )
                                .apply();

                        MainActivity
                                .cancelMedicineNotifications(
                                        requireContext()
                                );
                    }
                }
        );
        String[] notificationDays = {
                "7 дней",
                "14 дней",
                "30 дней"
        };

        ArrayAdapter<String> notificationDaysAdapter =
                new ArrayAdapter<>(
                        requireContext(),
                        android.R.layout.simple_spinner_item,
                        notificationDays
                );

        notificationDaysAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerNotificationDays.setAdapter(
                notificationDaysAdapter
        );

        int savedDays =
                preferences.getInt(
                        "notification_days",
                        7
                );

        if (savedDays == 14) {

            spinnerNotificationDays.setSelection(1);

        } else if (savedDays == 30) {

            spinnerNotificationDays.setSelection(2);

        } else {

            spinnerNotificationDays.setSelection(0);
        }

        spinnerNotificationDays.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            View selectedView,
                            int position,
                            long id
                    ) {

                        int days;

                        if (position == 1) {

                            days = 14;

                        } else if (position == 2) {

                            days = 30;

                        } else {

                            days = 7;
                        }

                        preferences
                                .edit()
                                .putInt(
                                        "notification_days",
                                        days
                                )
                                .apply();
                    }

                    @Override
                    public void onNothingSelected(
                            AdapterView<?> parent
                    ) {
                    }
                }
        );
        // =====================================================
        // ТЕМА
        // =====================================================

        Spinner spinnerTheme =
                view.findViewById(
                        R.id.spinner_theme
                );

        String[] themes = {
                "Системная",
                "Светлая",
                "Тёмная"
        };

        ArrayAdapter<String> themeAdapter =
                new ArrayAdapter<>(
                        requireContext(),
                        android.R.layout.simple_spinner_item,
                        themes
                );

        themeAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerTheme.setAdapter(
                themeAdapter
        );

        String savedTheme =
                preferences.getString(
                        "theme",
                        "Системная"
                );

        if (savedTheme.equals("Светлая")) {

            spinnerTheme.setSelection(1);

        } else if (savedTheme.equals("Тёмная")) {

            spinnerTheme.setSelection(2);

        } else {

            spinnerTheme.setSelection(0);
        }

        spinnerTheme.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            View selectedView,
                            int position,
                            long id
                    ) {

                        String selectedTheme =
                                themes[position];

                        preferences
                                .edit()
                                .putString(
                                        "theme",
                                        selectedTheme
                                )
                                .apply();

                        applyTheme(
                                selectedTheme
                        );
                    }

                    @Override
                    public void onNothingSelected(
                            AdapterView<?> parent
                    ) {
                    }
                }
        );
    }

    // =========================================================
    // ВКЛЮЧЕНИЕ УВЕДОМЛЕНИЙ
    // =========================================================

    private void enableNotifications(
            MaterialSwitch switchNotifications
    ) {

        if (Build.VERSION.SDK_INT >=
                Build.VERSION_CODES.TIRAMISU) {

            if (requireContext()
                    .checkSelfPermission(
                            Manifest.permission.POST_NOTIFICATIONS
                    )
                    != PackageManager.PERMISSION_GRANTED) {

                // Пока не включаем переключатель окончательно
                switchNotifications.setChecked(
                        false
                );

                notificationPermissionLauncher.launch(
                        Manifest.permission.POST_NOTIFICATIONS
                );

                return;
            }
        }

        preferences
                .edit()
                .putBoolean(
                        "notifications_enabled",
                        true
                )
                .apply();

        MainActivity
                .scheduleMedicineNotifications(
                        requireContext()
                );
    }

    // =========================================================
    // ПРОВЕРКА РАЗРЕШЕНИЯ
    // =========================================================

    private boolean hasNotificationPermission() {

        if (Build.VERSION.SDK_INT <
                Build.VERSION_CODES.TIRAMISU) {

            return true;
        }

        return requireContext()
                .checkSelfPermission(
                        Manifest.permission.POST_NOTIFICATIONS
                )
                == PackageManager.PERMISSION_GRANTED;
    }

    // =========================================================
    // ТЕМА
    // =========================================================

    private void applyTheme(
            String theme
    ) {

        if (theme.equals("Светлая")) {

            AppCompatDelegate
                    .setDefaultNightMode(
                            AppCompatDelegate
                                    .MODE_NIGHT_NO
                    );

        } else if (theme.equals("Тёмная")) {

            AppCompatDelegate
                    .setDefaultNightMode(
                            AppCompatDelegate
                                    .MODE_NIGHT_YES
                    );

        } else {

            AppCompatDelegate
                    .setDefaultNightMode(
                            AppCompatDelegate
                                    .MODE_NIGHT_FOLLOW_SYSTEM
                    );
        }
    }
}