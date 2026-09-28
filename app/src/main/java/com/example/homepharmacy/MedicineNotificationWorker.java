package com.example.homepharmacy;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.homepharmacy.data.Medicine;
import com.example.homepharmacy.data.MedicineDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MedicineNotificationWorker
        extends Worker {

    private static final String CHANNEL_ID =
            "medicine_expiration_channel";

    public MedicineNotificationWorker(
            @NonNull Context context,
            @NonNull WorkerParameters workerParams
    ) {
        super(
                context,
                workerParams
        );
    }

    @NonNull
    @Override
    public Result doWork() {

        createNotificationChannel();

        // Android 13+
        if (Build.VERSION.SDK_INT >=
                Build.VERSION_CODES.TIRAMISU) {

            if (getApplicationContext()
                    .checkSelfPermission(
                            Manifest.permission.POST_NOTIFICATIONS
                    )
                    != PackageManager.PERMISSION_GRANTED) {

                return Result.success();
            }
        }

        MedicineDatabase database =
                MedicineDatabase.getDatabase(
                        getApplicationContext()
                );

        List<Medicine> medicines =
                database
                        .medicineDao()
                        .getAllMedicinesSync();

        SharedPreferences preferences =
                getApplicationContext()
                        .getSharedPreferences(
                                "app_settings",
                                Context.MODE_PRIVATE
                        );

        int notificationDays =
                preferences.getInt(
                        "notification_days",
                        7
                );

        SimpleDateFormat dateFormat =
                new SimpleDateFormat(
                        "dd.MM.yyyy",
                        Locale.getDefault()
                );

        dateFormat.setLenient(false);

        // Сегодня без времени
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

        for (Medicine medicine : medicines) {

            String expirationDate =
                    medicine.getExpirationDate();

            if (expirationDate == null ||
                    expirationDate.isEmpty()) {

                continue;
            }

            try {

                Date expiration =
                        dateFormat.parse(
                                expirationDate
                        );

                if (expiration == null) {
                    continue;
                }

                long difference =
                        expiration.getTime()
                                - today.getTime();

                long days =
                        TimeUnit.MILLISECONDS.toDays(
                                difference
                        );

                // =========================================
                // ПРОСРОЧЕН
                // =========================================

                if (days < 0) {

                    showNotification(
                            medicine,
                            "Препарат просрочен",
                            "Срок годности истёк "
                                    + expirationDate
                    );

                    // =========================================
                    // СЕГОДНЯ
                    // =========================================

                } else if (days == 0) {

                    showNotification(
                            medicine,
                            "Срок годности истекает сегодня",
                            "Дата окончания срока годности: "
                                    + expirationDate
                    );

                    // =========================================
                    // ЗАВТРА
                    // =========================================

                } else if (days == 1) {

                    if (notificationDays >= 1) {

                        showNotification(
                                medicine,
                                "Срок годности истекает завтра",
                                "Дата окончания срока годности: "
                                        + expirationDate
                        );
                    }

                    // =========================================
                    // ЧЕРЕЗ НЕСКОЛЬКО ДНЕЙ
                    // =========================================

                } else if (days <= notificationDays) {

                    showNotification(
                            medicine,
                            "Срок годности приближается",
                            "Истекает через "
                                    + getDaysText(days)
                                    + " — "
                                    + expirationDate
                    );
                }

            } catch (ParseException e) {

                // Некорректную дату пропускаем
            }
        }

        return Result.success();
    }

    private String getDaysText(
            long days
    ) {

        long lastTwoDigits =
                days % 100;

        long lastDigit =
                days % 10;

        if (lastTwoDigits >= 11 &&
                lastTwoDigits <= 14) {

            return days + " дней";
        }

        if (lastDigit == 1) {

            return days + " день";
        }

        if (lastDigit >= 2 &&
                lastDigit <= 4) {

            return days + " дня";
        }

        return days + " дней";
    }

    private void showNotification(
            Medicine medicine,
            String title,
            String text
    ) {

        Context context =
                getApplicationContext();

        Intent intent =
                new Intent(
                        context,
                        MainActivity.class
                );

        intent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_SINGLE_TOP
        );

        intent.putExtra(
                "open_medicine_details",
                true
        );

        intent.putExtra(
                "medicine",
                medicine
        );

        PendingIntent pendingIntent =
                PendingIntent.getActivity(
                        context,
                        medicine.getId(),
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                                | PendingIntent.FLAG_IMMUTABLE
                );

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(
                        context,
                        CHANNEL_ID
                )
                        .setSmallIcon(
                                android.R.drawable.ic_dialog_info
                        )
                        .setContentTitle(
                                title
                        )
                        .setContentText(
                                medicine.getName()
                                        + ": "
                                        + text
                        )
                        .setStyle(
                                new NotificationCompat.BigTextStyle()
                                        .bigText(
                                                medicine.getName()
                                                        + ": "
                                                        + text
                                        )
                        )
                        .setPriority(
                                NotificationCompat.PRIORITY_DEFAULT
                        )
                        .setAutoCancel(true)
                        .setContentIntent(
                                pendingIntent
                        );

        try {

            NotificationManagerCompat
                    .from(context)
                    .notify(
                            medicine.getId(),
                            builder.build()
                    );

        } catch (SecurityException e) {

            // Уведомления запрещены
        }
    }

    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT <
                Build.VERSION_CODES.O) {

            return;
        }

        NotificationChannel channel =
                new NotificationChannel(
                        CHANNEL_ID,
                        "Сроки годности препаратов",
                        NotificationManager.IMPORTANCE_DEFAULT
                );

        channel.setDescription(
                "Уведомления о приближении и истечении сроков годности"
        );

        NotificationManager notificationManager =
                getApplicationContext()
                        .getSystemService(
                                NotificationManager.class
                        );

        if (notificationManager != null) {

            notificationManager
                    .createNotificationChannel(
                            channel
                    );
        }
    }
}