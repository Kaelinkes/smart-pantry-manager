package com.example.smartpantry.db;

import android.content.Context;

import com.example.smartpantry.util.MatchingUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Sits between the Activities and the DatabaseHelper. This is where the core "strict-matching"
 * business logic from Section 2.3 of the brief lives: a recipe is only "suggested" if every
 * single one of its ingredients is present in the pantry in at least the required quantity.
 */
public class PantryRepository {

    private final DatabaseHelper dbHelper;

    public PantryRepository(Context context) {
        this.dbHelper = DatabaseHelper.getInstance(context);
    }

    /** Result of matching one recipe against the current pantry. */
    public static class RecipeMatch {
        public final Recipe recipe;
        public final List<String> missingIngredients; // empty = fully matched

        RecipeMatch(Recipe recipe, List<String> missingIngredients) {
            this.recipe = recipe;
            this.missingIngredients = missingIngredients;
        }

        public boolean isFullMatch() {
            return missingIngredients.isEmpty();
        }
    }

    /**
     * Runs the strict-matching rule against every recipe in the catalogue.
     *
     * @return two lists: [0] = recipes the user can make RIGHT NOW (zero missing ingredients),
     *                     [1] = "almost there" recipes missing exactly one ingredient (bonus
     *                     feature from Section 8, kept clearly separate from the main list
     *                     as the brief requires).
     */
    public List<List<RecipeMatch>> getSuggestedAndAlmostThere() {
        List<PantryItem> pantry = dbHelper.getAllPantryItems();

        // One total per ingredient and unit family (rows for the same ingredient are added together).
        Map<String, Double> availability = MatchingUtils.buildAvailability(pantry);

        List<Recipe> allRecipes = dbHelper.getAllRecipesShallow();

        List<RecipeMatch> fullMatches = new ArrayList<>();
        List<RecipeMatch> almostThere = new ArrayList<>();

        for (Recipe recipe : allRecipes) {
            List<RecipeIngredient> required = dbHelper.getIngredientsForRecipe(recipe.getId());
            List<String> missing = new ArrayList<>();

            for (RecipeIngredient req : required) {
                boolean satisfied = MatchingUtils.isSatisfied(availability,
                        req.getIngredientName(), req.getQuantity(), req.getUnit());
                if (!satisfied) {
                    missing.add(req.getIngredientName());
                }
            }

            recipe.setIngredients(required);

            if (missing.isEmpty()) {
                fullMatches.add(new RecipeMatch(recipe, missing));
            } else if (missing.size() == 1) {
                almostThere.add(new RecipeMatch(recipe, missing));
            }
            // 2+ missing ingredients: excluded entirely, per the strict-matching rule.
        }

        List<List<RecipeMatch>> result = new ArrayList<>();
        result.add(fullMatches);
        result.add(almostThere);
        return result;
    }
}
