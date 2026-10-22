package com.example.smartpantry;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.smartpantry.db.DatabaseHelper;
import com.example.smartpantry.db.Recipe;
import com.example.smartpantry.db.RecipeIngredient;

/**
 * Shows the full ingredient list and preparation method for one recipe.
 * The recipe id arrives via an Intent extra from SuggestedRecipesActivity.
 */
public class RecipeDetailActivity extends AppCompatActivity {

    public static final String EXTRA_RECIPE_ID = "com.example.smartpantry.EXTRA_RECIPE_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        long recipeId = getIntent().getLongExtra(EXTRA_RECIPE_ID, -1L);
        if (recipeId == -1L) {
            Toast.makeText(this, "Recipe not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        DatabaseHelper dbHelper = DatabaseHelper.getInstance(this);
        Recipe recipe = dbHelper.getRecipeWithIngredients(recipeId);

        if (recipe == null) {
            Toast.makeText(this, "Recipe not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setTitle(recipe.getName());

        TextView nameView = findViewById(R.id.text_detail_name);
        TextView ingredientsView = findViewById(R.id.text_detail_ingredients);
        TextView methodView = findViewById(R.id.text_detail_method);

        nameView.setText(recipe.getName());
        ingredientsView.setText(buildIngredientList(recipe));
        methodView.setText(recipe.getInstructions());
    }

    /** Formats the ingredient rows into a readable bulleted block. */
    private String buildIngredientList(Recipe recipe) {
        StringBuilder builder = new StringBuilder();
        for (RecipeIngredient ingredient : recipe.getIngredients()) {
            builder.append("•  ")
                    .append(formatQuantity(ingredient.getQuantity()))
                    .append(" ")
                    .append(ingredient.getUnit())
                    .append("  ")
                    .append(ingredient.getIngredientName())
                    .append("\n");
        }
        return builder.toString().trim();
    }

    private String formatQuantity(double quantity) {
        if (quantity == Math.floor(quantity)) {
            return String.valueOf((long) quantity);
        }
        return String.valueOf(quantity);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
