package com.example.homepharmacy.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homepharmacy.R;
import com.example.homepharmacy.data.Medicine;
import com.google.android.material.card.MaterialCardView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MedicineAdapter
        extends RecyclerView.Adapter<MedicineAdapter.MedicineViewHolder> {

    private final List<Medicine> medicines;
    private final OnMedicineClickListener listener;

    public interface OnMedicineClickListener {
        void onMedicineClick(Medicine medicine);
    }

    public MedicineAdapter(
            List<Medicine> medicines,
            OnMedicineClickListener listener
    ) {
        this.medicines = medicines;
        this.listener = listener;
    }

    public void setMedicines(List<Medicine> medicines) {

        this.medicines.clear();
        this.medicines.addAll(medicines);

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MedicineViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view =
                LayoutInflater.from(parent.getContext())
                        .inflate(
                                R.layout.item_medicine,
                                parent,
                                false
                        );

        return new MedicineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull MedicineViewHolder holder,
            int position
    ) {

        Medicine medicine =
                medicines.get(position);

        Context context =
                holder.itemView.getContext();

        // Название
        holder.nameText.setText(
                medicine.getName()
        );

        // Категория
        holder.categoryText.setText(
                medicine.getCategory()
        );

        SharedPreferences preferences =
                context.getSharedPreferences(
                        "app_settings",
                        Context.MODE_PRIVATE
                );

        boolean showCategory =
                preferences.getBoolean(
                        "show_category",
                        true
                );

        if (showCategory) {
            holder.categoryText.setVisibility(
                    View.VISIBLE
            );
        } else {
            holder.categoryText.setVisibility(
                    View.GONE
            );
        }

        // Количество
        holder.quantityText.setText(
                "Количество: "
                        + medicine.getQuantity()
                        + " "
                        + medicine.getUnit()
        );

        // Срок годности
        holder.expirationText.setText(
                "Срок годности: "
                        + medicine.getExpirationDate()
        );

        // Статус
        String status =
                getExpirationStatus(
                        medicine.getExpirationDate()
                );

        holder.statusText.setText(
                status
        );

        setStatusStyle(
                holder,
                status
        );

        // Нажатие
        holder.itemView.setOnClickListener(v -> {

            int adapterPosition =
                    holder.getAdapterPosition();

            if (adapterPosition !=
                    RecyclerView.NO_POSITION) {

                Medicine clickedMedicine =
                        medicines.get(
                                adapterPosition
                        );

                if (listener != null) {

                    listener.onMedicineClick(
                            clickedMedicine
                    );
                }
            }
        });
    }

    private void setStatusStyle(
            MedicineViewHolder holder,
            String status
    ) {

        Context context =
                holder.itemView.getContext();

        int statusColor;
        int backgroundColor;

        if (status.equals("Просрочен")) {

            statusColor =
                    ContextCompat.getColor(
                            context,
                            R.color.medicine_expired
                    );

            backgroundColor =
                    context.getResources()
                            .getColor(
                                    R.color.home_expired_bg,
                                    context.getTheme()
                            );

        } else if (status.equals(
                "Скоро истекает"
        )) {

            statusColor =
                    ContextCompat.getColor(
                            context,
                            R.color.medicine_expiring
                    );

            backgroundColor =
                    context.getResources()
                            .getColor(
                                    R.color.home_expiring_bg,
                                    context.getTheme()
                            );

        } else if (status.equals(
                "В порядке"
        )) {

            statusColor =
                    ContextCompat.getColor(
                            context,
                            R.color.medicine_safe
                    );

            backgroundColor =
                    context.getResources()
                            .getColor(
                                    R.color.home_safe_bg,
                                    context.getTheme()
                            );

        } else {

            statusColor =
                    ContextCompat.getColor(
                            context,
                            R.color.medicine_neutral
                    );

            backgroundColor =
                    Color.TRANSPARENT;
        }

        // Цветная полоска слева
        holder.statusIndicator.setBackgroundColor(
                statusColor
        );

        // Цвет текста
        holder.statusText.setTextColor(
                statusColor
        );

        // Фон бейджа
        GradientDrawable badgeBackground =
                new GradientDrawable();

        badgeBackground.setColor(
                backgroundColor
        );

        badgeBackground.setCornerRadius(
                dpToPx(
                        context,
                        10
                )
        );

        holder.statusText.setBackground(
                badgeBackground
        );

        // Обводка всей карточки
        holder.card.setStrokeColor(
                statusColor
        );

        holder.card.setStrokeWidth(
                dpToPx(
                        context,
                        1
                )
        );
    }

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

            Date today =
                    new Date();

            long difference =
                    expiration.getTime()
                            - today.getTime();

            long days =
                    TimeUnit.MILLISECONDS.toDays(
                            difference
                    );

            if (days < 0) {

                return "Просрочен";

            } else if (days <= 30) {

                return "Скоро истекает";

            } else {

                return "В порядке";
            }

        } catch (ParseException exception) {

            return "Неверная дата";
        }
    }

    private int dpToPx(
            Context context,
            int dp
    ) {

        return Math.round(
                dp * context
                        .getResources()
                        .getDisplayMetrics()
                        .density
        );
    }

    @Override
    public int getItemCount() {

        return medicines.size();
    }

    static class MedicineViewHolder
            extends RecyclerView.ViewHolder {

        MaterialCardView card;

        View statusIndicator;

        TextView nameText;
        TextView categoryText;
        TextView quantityText;
        TextView expirationText;
        TextView statusText;

        public MedicineViewHolder(
                @NonNull View itemView
        ) {

            super(itemView);

            card =
                    itemView.findViewById(
                            R.id.card_medicine
                    );

            statusIndicator =
                    itemView.findViewById(
                            R.id.view_status_indicator
                    );

            nameText =
                    itemView.findViewById(
                            R.id.text_medicine_name
                    );

            categoryText =
                    itemView.findViewById(
                            R.id.text_medicine_category
                    );

            quantityText =
                    itemView.findViewById(
                            R.id.text_medicine_quantity
                    );

            expirationText =
                    itemView.findViewById(
                            R.id.text_medicine_expiration
                    );

            statusText =
                    itemView.findViewById(
                            R.id.text_medicine_status
                    );
        }
    }
}