package com.example.smartpantry;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantry.adapters.RecipeAdapter;
import com.example.smartpantry.db.PantryRepository;
import com.example.smartpantry.db.PantryRepository.RecipeMatch;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

/**
 * Runs the strict-matching rule (Section 2.3) and shows only the recipes the user can cook
 * right now with the ingredients already in their pantry.
 *
 * A second, clearly-separated "Almost There" list shows recipes missing exactly one ingredient.
 * That list is a bonus feature and is never merged into the strict suggestions above it.
 */
public class SuggestedRecipesActivity extends AppCompatActivity implements RecipeAdapter.Listener {

    private PantryRepository repository;

    private RecipeAdapter suggestedAdapter;
    private RecipeAdapter almostThereAdapter;

    private RecyclerView suggestedRecycler;
    private RecyclerView almostThereRecycler;
    private TextView emptySuggestions;
    private View almostThereSection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(R.string.title_suggested_recipes);

        repository = new PantryRepository(this);

        suggestedRecycler = findViewById(R.id.recycler_suggested);
        almostThereRecycler = findViewById(R.id.recycler_almost_there);
        emptySuggestions = findViewById(R.id.text_empty_suggestions);
        almostThereSection = findViewById(R.id.layout_almost_there_section);

        suggestedAdapter = new RecipeAdapter(this);
        suggestedRecycler.setLayoutManager(new LinearLayoutManager(this));
        suggestedRecycler.setAdapter(suggestedAdapter);
        suggestedRecycler.setNestedScrollingEnabled(false);

        almostThereAdapter = new RecipeAdapter(this);
        almostThereRecycler.setLayoutManager(new LinearLayoutManager(this));
        almostThereRecycler.setAdapter(almostThereAdapter);
        almostThereRecycler.setNestedScrollingEnabled(false);

        setupBottomNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Returning via Back leaves the nav bar highlighting the tab we left from, so re-check ours.
        ((BottomNavigationView) findViewById(R.id.bottom_navigation))
                .getMenu().findItem(R.id.nav_suggestions).setChecked(true);
        // Re-run the matching every time the screen is shown, because the pantry may have
        // changed since the user was last here.
        runMatching();
    }

    private void runMatching() {
        List<List<RecipeMatch>> results = repository.getSuggestedAndAlmostThere();
        List<RecipeMatch> suggested = results.get(0);
        List<RecipeMatch> almostThere = results.get(1);

        suggestedAdapter.submitList(suggested);
        almostThereAdapter.submitList(almostThere);

        // Friendly message rather than a blank screen when nothing matches.
        boolean noneMatch = suggested.isEmpty();
        emptySuggestions.setVisibility(noneMatch ? View.VISIBLE : View.GONE);
        suggestedRecycler.setVisibility(noneMatch ? View.GONE : View.VISIBLE);

        almostThereSection.setVisibility(almostThere.isEmpty() ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onRecipeClicked(RecipeMatch match) {
        // Pass only the recipe id through the Intent; the detail screen re-reads the recipe
        // from the database, which keeps the Intent payload small and the data authoritative.
        Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_ID, match.recipe.getId());
        startActivity(intent);
    }

    private void setupBottomNavigation() {
        BottomNavigationView nav = findViewById(R.id.bottom_navigation);
        nav.setSelectedItemId(R.id.nav_suggestions);
        nav.setOnItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();
            if (id == R.id.nav_suggestions) {
                return true;
            } else if (id == R.id.nav_pantry) {
                startActivity(new Intent(this, PantryListActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.nav_settings) {
                startActivity(new Intent(this, SettingsActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });
    }
}
