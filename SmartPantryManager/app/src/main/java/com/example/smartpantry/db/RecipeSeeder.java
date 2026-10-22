package com.example.smartpantry.db;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

/**
 * Seeds the recipes and recipe_ingredients tables the first time the database is created.
 * 18 recipes are included, comfortably inside the 15-20 required by the brief (Section 2.2).
 *
 * Units are the ones people actually keep these items in (salt and sugar in g, oils and honey in ml),
 * because MatchingUtils never treats weight and volume as interchangeable. Compatible units still
 * convert (g <-> kg, ml <-> l <-> tbsp <-> cup).
 */
final class RecipeSeeder {

    private RecipeSeeder() {
    }

    static void seed(SQLiteDatabase db) {
        addRecipe(db, "Tomato Egg Stir-Fry",
                "Beat the eggs and season with a pinch of salt. Heat oil in a pan, scramble the eggs " +
                        "until just set, then remove. Add a little more oil, fry the chopped tomato until soft " +
                        "and saucy, return the eggs to the pan, toss together and serve.",
                new String[][]{
                        {"eggs", "3", "pcs"},
                        {"tomatoes", "2", "pcs"},
                        {"cooking oil", "30", "ml"},
                        {"salt", "5", "g"}
                });

        addRecipe(db, "Garlic Butter Rice",
                "Melt butter in a pot, fry the minced garlic until fragrant. Stir in cooked rice, " +
                        "season with salt, and warm through for 3-4 minutes.",
                new String[][]{
                        {"rice", "300", "g"},
                        {"garlic", "3", "pcs"},
                        {"butter", "30", "g"},
                        {"salt", "5", "g"}
                });

        addRecipe(db, "Classic Pancakes",
                "Whisk flour, sugar and a pinch of salt. Add milk and eggs, whisk to a smooth batter. " +
                        "Cook spoonfuls on a buttered pan until bubbles form, flip, and cook the other side.",
                new String[][]{
                        {"flour", "250", "g"},
                        {"milk", "300", "ml"},
                        {"eggs", "2", "pcs"},
                        {"sugar", "25", "g"},
                        {"butter", "20", "g"}
                });

        addRecipe(db, "Simple Tomato Pasta",
                "Boil the pasta until al dente. Meanwhile fry garlic in oil, add chopped tomatoes and " +
                        "simmer to a sauce. Season with salt, toss through the drained pasta.",
                new String[][]{
                        {"pasta", "300", "g"},
                        {"tomatoes", "4", "pcs"},
                        {"garlic", "2", "pcs"},
                        {"cooking oil", "30", "ml"},
                        {"salt", "5", "g"}
                });

        addRecipe(db, "Cheesy Scrambled Eggs",
                "Whisk eggs with a splash of milk. Cook gently in a buttered pan, folding until nearly " +
                        "set, then stir through grated cheese until melted.",
                new String[][]{
                        {"eggs", "3", "pcs"},
                        {"milk", "30", "ml"},
                        {"cheese", "50", "g"},
                        {"butter", "10", "g"}
                });

        addRecipe(db, "Onion & Potato Hash",
                "Dice the potatoes and onion. Fry the onion in oil until soft, add potatoes and cook, " +
                        "stirring occasionally, until golden and tender. Season with salt.",
                new String[][]{
                        {"potatoes", "4", "pcs"},
                        {"onions", "1", "pcs"},
                        {"cooking oil", "45", "ml"},
                        {"salt", "5", "g"}
                });

        addRecipe(db, "Chicken & Rice Bowl",
                "Season and pan-fry the chicken until cooked through, then slice. Serve over warm rice " +
                        "with a drizzle of oil and salt to taste.",
                new String[][]{
                        {"chicken breast", "2", "pcs"},
                        {"rice", "300", "g"},
                        {"cooking oil", "30", "ml"},
                        {"salt", "5", "g"}
                });

        addRecipe(db, "Cream Cheese Bagel",
                "Slice and lightly toast the bagel. Spread generously with cream cheese and serve.",
                new String[][]{
                        {"bagel", "1", "pcs"},
                        {"cream cheese", "40", "g"}
                });

        addRecipe(db, "Basic Vegetable Soup",
                "Fry the chopped onion and garlic in oil until soft. Add diced carrots and potatoes with " +
                        "enough water to cover, simmer until vegetables are tender, season with salt.",
                new String[][]{
                        {"onions", "1", "pcs"},
                        {"garlic", "2", "pcs"},
                        {"carrots", "2", "pcs"},
                        {"potatoes", "2", "pcs"},
                        {"cooking oil", "15", "ml"},
                        {"salt", "5", "g"}
                });

        addRecipe(db, "Honey Yoghurt Bowl",
                "Spoon the yoghurt into a bowl, drizzle generously with honey and stir gently before eating.",
                new String[][]{
                        {"yoghurt", "200", "g"},
                        {"honey", "30", "ml"}
                });

        addRecipe(db, "Garlic Butter Mushrooms",
                "Melt butter in a hot pan, add sliced mushrooms and cook until golden. Stir in minced " +
                        "garlic for the last minute and season with salt.",
                new String[][]{
                        {"mushrooms", "250", "g"},
                        {"garlic", "2", "pcs"},
                        {"butter", "30", "g"},
                        {"salt", "5", "g"}
                });

        addRecipe(db, "Banana Milk Smoothie",
                "Blend the banana, milk and honey together until smooth and frothy.",
                new String[][]{
                        {"bananas", "2", "pcs"},
                        {"milk", "300", "ml"},
                        {"honey", "15", "ml"}
                });

        addRecipe(db, "Carrot & Onion Soup",
                "Fry onion in oil until translucent, add sliced carrots and water to cover, simmer until " +
                        "soft, then blend until smooth. Season with salt.",
                new String[][]{
                        {"carrots", "4", "pcs"},
                        {"onions", "1", "pcs"},
                        {"cooking oil", "15", "ml"},
                        {"salt", "5", "g"}
                });

        addRecipe(db, "Egg Fried Rice",
                "Scramble the eggs in oil and set aside. Fry the rice in the same pan, stir the eggs back " +
                        "through, season with salt, and heat until piping hot.",
                new String[][]{
                        {"rice", "300", "g"},
                        {"eggs", "2", "pcs"},
                        {"cooking oil", "30", "ml"},
                        {"salt", "5", "g"}
                });

        addRecipe(db, "Grilled Cheese Sandwich",
                "Butter one side of each bread slice. Place cheese between the unbuttered sides and grill " +
                        "in a pan until golden on both sides and the cheese has melted.",
                new String[][]{
                        {"bread", "2", "pcs"},
                        {"cheese", "60", "g"},
                        {"butter", "15", "g"}
                });

        addRecipe(db, "Chicken Garlic Stir-Fry",
                "Slice the chicken and fry in oil until cooked. Add minced garlic and a splash of soy " +
                        "sauce, tossing for another minute before serving.",
                new String[][]{
                        {"chicken breast", "2", "pcs"},
                        {"garlic", "3", "pcs"},
                        {"cooking oil", "30", "ml"},
                        {"soy sauce", "30", "ml"}
                });

        addRecipe(db, "Buttered Toast with Honey",
                "Toast the bread until golden, spread with butter while warm, then drizzle with honey.",
                new String[][]{
                        {"bread", "2", "pcs"},
                        {"butter", "15", "g"},
                        {"honey", "15", "ml"}
                });

        addRecipe(db, "Creamy Mushroom Pasta",
                "Boil the pasta. Fry sliced mushrooms and garlic in butter until soft, stir in milk and " +
                        "simmer to a light sauce, season with salt, then toss through the drained pasta.",
                new String[][]{
                        {"pasta", "300", "g"},
                        {"mushrooms", "200", "g"},
                        {"garlic", "2", "pcs"},
                        {"milk", "150", "ml"},
                        {"butter", "20", "g"},
                        {"salt", "5", "g"}
                });
    }

    private static void addRecipe(SQLiteDatabase db, String name, String instructions, String[][] ingredients) {
        ContentValues recipeValues = new ContentValues();
        recipeValues.put(DatabaseHelper.COL_RECIPE_NAME, name);
        recipeValues.put(DatabaseHelper.COL_RECIPE_INSTRUCTIONS, instructions);
        long recipeId = db.insert(DatabaseHelper.TABLE_RECIPES, null, recipeValues);

        for (String[] ing : ingredients) {
            ContentValues ingValues = new ContentValues();
            ingValues.put(DatabaseHelper.COL_RI_RECIPE_ID, recipeId);
            ingValues.put(DatabaseHelper.COL_RI_NAME, ing[0]);
            ingValues.put(DatabaseHelper.COL_RI_QTY, Double.parseDouble(ing[1]));
            ingValues.put(DatabaseHelper.COL_RI_UNIT, ing[2]);
            db.insert(DatabaseHelper.TABLE_RECIPE_INGREDIENTS, null, ingValues);
        }
    }
}
