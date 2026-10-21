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

public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.PantryViewHolder> {
    public interface Listener{void onItemClicked(PantryItem item);void onDeleteClicked(PantryItem item);}
    private List<PantryItem> items=new ArrayList<>();private final Listener listener;private boolean highlightExpiring=true;
    public PantryAdapter(Listener listener){this.listener=listener;}
    public void setHighlightExpiring(boolean value){highlightExpiring=value;notifyDataSetChanged();}
    public void submitList(List<PantryItem> newItems){items=newItems;notifyDataSetChanged();}
    @NonNull @Override public PantryViewHolder onCreateViewHolder(@NonNull ViewGroup parent,int viewType){return new PantryViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pantry,parent,false));}
    @Override public void onBindViewHolder(@NonNull PantryViewHolder holder,int position){PantryItem item=items.get(position);holder.name.setText(item.getName());holder.quantity.setText(formatQuantity(item.getQuantity())+" "+item.getUnit());if(item.getExpiryDate()!=null&&!item.getExpiryDate().isEmpty()){holder.expiry.setVisibility(View.VISIBLE);holder.expiry.setText("Expires: "+item.getExpiryDate());holder.expiry.setTextColor(holder.itemView.getContext().getColor(highlightExpiring&&isExpiringSoon(item.getExpiryDate())?R.color.expiring_soon:R.color.text_secondary));}else holder.expiry.setVisibility(View.GONE);holder.itemView.setOnClickListener(v->listener.onItemClicked(item));holder.deleteButton.setOnClickListener(v->listener.onDeleteClicked(item));}
    private boolean isExpiringSoon(String date){try{SimpleDateFormat f=new SimpleDateFormat("yyyy-MM-dd",Locale.ROOT);Date expiry=f.parse(date);if(expiry==null)return false;Calendar today=Calendar.getInstance();today.set(Calendar.HOUR_OF_DAY,0);today.set(Calendar.MINUTE,0);today.set(Calendar.SECOND,0);today.set(Calendar.MILLISECOND,0);return (expiry.getTime()-today.getTimeInMillis())/(1000L*60*60*24)<=3;}catch(ParseException e){return false;}}
    private String formatQuantity(double q){return q==Math.floor(q)?String.valueOf((long)q):String.valueOf(q);}
    @Override public int getItemCount(){return items.size();}
    static class PantryViewHolder extends RecyclerView.ViewHolder{TextView name,quantity,expiry;ImageButton deleteButton;PantryViewHolder(@NonNull View v){super(v);name=v.findViewById(R.id.text_item_name);quantity=v.findViewById(R.id.text_item_quantity);expiry=v.findViewById(R.id.text_item_expiry);deleteButton=v.findViewById(R.id.btn_delete_item);}}
}
