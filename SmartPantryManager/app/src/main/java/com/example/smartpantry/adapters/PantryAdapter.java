package com.example.smartpantry.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantry.R;
import com.example.smartpantry.db.PantryItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Binds the list of PantryItem objects to a RecyclerView on the Pantry List screen.
 * Supports click (edit) and a delete button per row.
 */
public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.PantryViewHolder> {

    public interface Listener {
        void onItemClicked(PantryItem item);
        void onDeleteClicked(PantryItem item);
    }

    private List<PantryItem> items = new ArrayList<>();
    private final Listener listener;

    /** Mirrors the "expiring soon alerts" switch on the Settings screen. */
    private boolean highlightExpiring = true;

    public PantryAdapter(Listener listener) {
        this.listener = listener;
    }

    public void setHighlightExpiring(boolean highlightExpiring) {
        this.highlightExpiring = highlightExpiring;
        notifyDataSetChanged();
    }

    public void submitList(List<PantryItem> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PantryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pantry, parent, false);
        return new PantryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PantryViewHolder holder, int position) {
        PantryItem item = items.get(position);
        holder.name.setText(item.getName());

        String qtyText = formatQuantity(item.getQuantity()) + " " + item.getUnit();
        holder.quantity.setText(qtyText);

        if (item.getExpiryDate() != null && !item.getExpiryDate().isEmpty()) {
            holder.expiry.setVisibility(View.VISIBLE);
            holder.expiry.setText("Expires: " + item.getExpiryDate());
            if (highlightExpiring && isExpiringSoon(item.getExpiryDate())) {
                holder.expiry.setTextColor(
                        holder.itemView.getContext().getColor(R.color.alert_red));
            } else {
                holder.expiry.setTextColor(
                        holder.itemView.getContext().getColor(R.color.text_secondary));
            }
        } else {
            holder.expiry.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> listener.onItemClicked(item));
        holder.deleteButton.setOnClickListener(v -> listener.onDeleteClicked(item));
    }

    /**
     * Returns true if the given "yyyy-MM-dd" date is today, in the past, or within the next
     * 3 days. Uses SimpleDateFormat rather than java.time so the app does not depend on
     * java.time desugaring settings.
     */
    private boolean isExpiringSoon(String isoDate) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ROOT);
            Date expiry = format.parse(isoDate);
            if (expiry == null) return false;

            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);

            long millisDiff = expiry.getTime() - today.getTimeInMillis();
            long daysDiff = millisDiff / (1000L * 60 * 60 * 24);
            return daysDiff <= 3;
        } catch (ParseException e) {
            // A malformed date simply isn't highlighted - it should never crash the list.
            return false;
        }
    }

    private String formatQuantity(double quantity) {
        if (quantity == Math.floor(quantity)) {
            return String.valueOf((long) quantity);
        }
        return String.valueOf(quantity);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class PantryViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView quantity;
        TextView expiry;
        ImageButton deleteButton;

        PantryViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.text_item_name);
            quantity = itemView.findViewById(R.id.text_item_quantity);
            expiry = itemView.findViewById(R.id.text_item_expiry);
            deleteButton = itemView.findViewById(R.id.btn_delete_item);
        }
    }
}
