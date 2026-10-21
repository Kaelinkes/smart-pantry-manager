package com.example.smartpantry.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.smartpantry.R;
import com.example.smartpantry.db.PantryRepository.RecipeMatch;
import com.example.smartpantry.db.RecipeIngredient;
import java.util.ArrayList;
import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {
    public interface Listener{void onRecipeClicked(RecipeMatch match);}
    private List<RecipeMatch> matches=new ArrayList<>(); private final Listener listener;
    public RecipeAdapter(Listener listener){this.listener=listener;}
    public void submitList(List<RecipeMatch> newMatches){matches=newMatches;notifyDataSetChanged();}
    @NonNull @Override public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent,int viewType){return new RecipeViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe,parent,false));}
    @Override public void onBindViewHolder(@NonNull RecipeViewHolder holder,int position){RecipeMatch match=matches.get(position);holder.name.setText(match.recipe.getName());holder.ingredientSummary.setText(buildIngredientSummary(match.recipe.getIngredients()));if(match.isFullMatch())holder.missing.setVisibility(View.GONE);else{holder.missing.setVisibility(View.VISIBLE);String missingList=android.text.TextUtils.join(", ",match.missingIngredients);holder.missing.setText(holder.itemView.getContext().getString(R.string.label_missing,missingList));}holder.itemView.setOnClickListener(v->listener.onRecipeClicked(match));}
    private String buildIngredientSummary(List<RecipeIngredient> ingredients){StringBuilder b=new StringBuilder();b.append(ingredients.size()).append(" ingredients: ");for(int i=0;i<ingredients.size();i++){if(i>0)b.append(", ");b.append(ingredients.get(i).getIngredientName());}return b.toString();}
    @Override public int getItemCount(){return matches.size();}
    static class RecipeViewHolder extends RecyclerView.ViewHolder{TextView name,ingredientSummary,missing;RecipeViewHolder(@NonNull View v){super(v);name=v.findViewById(R.id.text_recipe_name);ingredientSummary=v.findViewById(R.id.text_recipe_ingredients);missing=v.findViewById(R.id.text_recipe_missing);}}
}
