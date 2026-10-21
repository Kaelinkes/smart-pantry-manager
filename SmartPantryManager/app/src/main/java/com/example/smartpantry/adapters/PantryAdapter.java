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
import java.util.ArrayList;
import java.util.List;

/** Binds pantry items to the RecyclerView and exposes row/delete callbacks. */
public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.PantryViewHolder> {
    public interface Listener { void onItemClicked(PantryItem item); void onDeleteClicked(PantryItem item); }
    private List<PantryItem> items = new ArrayList<>();
    private final Listener listener;
    public PantryAdapter(Listener listener){this.listener=listener;}
    public void submitList(List<PantryItem> newItems){items=newItems; notifyDataSetChanged();}
    @NonNull @Override public PantryViewHolder onCreateViewHolder(@NonNull ViewGroup parent,int viewType){
        return new PantryViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pantry,parent,false));
    }
    @Override public void onBindViewHolder(@NonNull PantryViewHolder holder,int position){
        PantryItem item=items.get(position); holder.name.setText(item.getName());
        holder.quantity.setText(formatQuantity(item.getQuantity())+" "+item.getUnit());
        holder.itemView.setOnClickListener(v->listener.onItemClicked(item));
        holder.deleteButton.setOnClickListener(v->listener.onDeleteClicked(item));
    }
    private String formatQuantity(double q){return q==Math.floor(q)?String.valueOf((long)q):String.valueOf(q);}
    @Override public int getItemCount(){return items.size();}
    static class PantryViewHolder extends RecyclerView.ViewHolder{
        TextView name,quantity; ImageButton deleteButton;
        PantryViewHolder(@NonNull View v){super(v);name=v.findViewById(R.id.text_item_name);quantity=v.findViewById(R.id.text_item_quantity);deleteButton=v.findViewById(R.id.btn_delete_item);}
    }
}
