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

/**
 * Custom adapter used by BOTH lists on the Suggested Recipes screen.
 *
 * The same row layout serves the strict "you can make this now" list and the optional
 * "almost there" list; the only difference is that an almost-there row also shows which
 * single ingredient is missing. The two lists remain separate RecyclerViews so the strict
 * suggestions are never mixed with partial matches (Section 2.3 of the brief).
 */
public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    public interface Listener {
        void onRecipeClicked(RecipeMatch match);
    }

    private List<RecipeMatch> matches = new ArrayList<>();
    private final Listener listener;

    public RecipeAdapter(Listener listener) {
        this.listener = listener;
    }

    public void submitList(List<RecipeMatch> newMatches) {
        this.matches = newMatches;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        RecipeMatch match = matches.get(position);

        holder.name.setText(match.recipe.getName());
        holder.ingredientSummary.setText(buildIngredientSummary(match.recipe.getIngredients()));

        if (match.isFullMatch()) {
            holder.missing.setVisibility(View.GONE);
        } else {
            holder.missing.setVisibility(View.VISIBLE);
            String missingList = android.text.TextUtils.join(", ", match.missingIngredients);
            holder.missing.setText(
                    holder.itemView.getContext().getString(R.string.label_missing, missingList));
        }

        holder.itemView.setOnClickListener(v -> listener.onRecipeClicked(match));
    }

    /** Builds a short "3 ingredients: eggs, tomatoes, salt" style preview line. */
    private String buildIngredientSummary(List<RecipeIngredient> ingredients) {
        StringBuilder builder = new StringBuilder();
        builder.append(ingredients.size()).append(" ingredients: ");
        for (int i = 0; i < ingredients.size(); i++) {
            if (i > 0) builder.append(", ");
            builder.append(ingredients.get(i).getIngredientName());
        }
        return builder.toString();
    }

    @Override
    public int getItemCount() {
        return matches.size();
    }

    static class RecipeViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView ingredientSummary;
        TextView missing;

        RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.text_recipe_name);
            ingredientSummary = itemView.findViewById(R.id.text_recipe_ingredients);
            missing = itemView.findViewById(R.id.text_recipe_missing);
        }
    }
}
