package com.example.homepharmacy;
import com.example.homepharmacy.data.Medicine;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Build;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(
            @Nullable Bundle savedInstanceState
    ) {

        // Применяем сохранённую тему
        applySavedTheme();

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_main
        );

        BottomNavigationView bottomNavigation =
                findViewById(
                        R.id.bottom_navigation
                );

        // Открываем главный экран только
        // при обычном запуске приложения
        if (savedInstanceState == null) {

            if (getIntent().getBooleanExtra(
                    "open_medicine_details",
                    false
            )) {

                openMedicineDetailsFromIntent(
                        getIntent()
                );

            } else if (getIntent().getBooleanExtra(
                    "open_medicines",
                    false
            )) {

                openFragment(
                        MedicineListFragment.newInstance(
                                "Все"
                        )
                );

            } else {

                openFragment(
                        new HomeFragment()
                );
            }
        }

        // Нижняя навигация
        bottomNavigation.setOnItemSelectedListener(
                item -> {

                    int itemId =
                            item.getItemId();

                    if (itemId ==
                            R.id.navigation_home) {

                        openFragment(
                                new HomeFragment()
                        );

                        return true;

                    } else if (itemId ==
                            R.id.navigation_medicines) {

                        openFragment(
                                new MedicineListFragment()
                        );

                        return true;

                    } else if (itemId ==
                            R.id.navigation_settings) {

                        openFragment(
                                new SettingsFragment()
                        );

                        return true;
                    }

                    return false;
                }
        );
    }

    // =========================================================
    // ОБРАБОТКА ПОВТОРНОГО ОТКРЫТИЯ ИЗ УВЕДОМЛЕНИЯ
    // =========================================================

    @Override
    protected void onNewIntent(
            Intent intent
    ) {
        super.onNewIntent(intent);

        setIntent(intent);

        if (intent.getBooleanExtra(
                "open_medicine_details",
                false
        )) {

            openMedicineDetailsFromIntent(
                    intent
            );

        } else if (intent.getBooleanExtra(
                "open_medicines",
                false
        )) {

            openFragment(
                    MedicineListFragment.newInstance(
                            "Все"
                    )
            );
        }
    }

    // =========================================================
    // ОТКРЫТИЕ FRAGMENT
    // =========================================================

    private void openFragment(
            Fragment fragment
    ) {

        getSupportFragmentManager()
                .beginTransaction()
                .replace(
                        R.id.main_container,
                        fragment
                )
                .commit();
    }

    // =========================================================
    // СОХРАНЁННАЯ ТЕМА
    // =========================================================

    private void applySavedTheme() {

        SharedPreferences preferences =
                getSharedPreferences(
                        "app_settings",
                        Context.MODE_PRIVATE
                );

        String savedTheme =
                preferences.getString(
                        "theme",
                        "Системная"
                );

        if (savedTheme.equals(
                "Светлая"
        )) {

            AppCompatDelegate
                    .setDefaultNightMode(
                            AppCompatDelegate
                                    .MODE_NIGHT_NO
                    );

        } else if (savedTheme.equals(
                "Тёмная"
        )) {

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

    // =========================================================
    // ЗАПУСК УВЕДОМЛЕНИЙ
    // =========================================================

    public static void scheduleMedicineNotifications(
            Context context
    ) {

        PeriodicWorkRequest request =
                new PeriodicWorkRequest.Builder(
                        MedicineNotificationWorker.class,
                        1,
                        TimeUnit.DAYS
                ).build();

        WorkManager
                .getInstance(context)
                .enqueueUniquePeriodicWork(
                        "medicine_expiration_check",
                        ExistingPeriodicWorkPolicy.KEEP,
                        request
                );
    }

    // =========================================================
    // ОТМЕНА УВЕДОМЛЕНИЙ
    // =========================================================

    public static void cancelMedicineNotifications(
            Context context
    ) {

        WorkManager
                .getInstance(context)
                .cancelUniqueWork(
                        "medicine_expiration_check"
                );
    }
    private void openMedicineDetailsFromIntent(
            Intent intent
    ) {

        Medicine medicine = null;

        if (Build.VERSION.SDK_INT >=
                Build.VERSION_CODES.TIRAMISU) {

            medicine =
                    intent.getSerializableExtra(
                            "medicine",
                            Medicine.class
                    );

        } else {

            Object extra =
                    intent.getSerializableExtra(
                            "medicine"
                    );

            if (extra instanceof Medicine) {

                medicine =
                        (Medicine) extra;
            }
        }

        if (medicine == null) {

            openFragment(
                    MedicineListFragment.newInstance(
                            "Все"
                    )
            );

            return;
        }

        openFragment(
                MedicineDetailsFragment.newInstance(
                        medicine
                )
        );
    }
}