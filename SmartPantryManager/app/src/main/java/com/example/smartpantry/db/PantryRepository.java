package com.example.smartpantry.db;

import android.content.Context;
import com.example.smartpantry.util.MatchingUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PantryRepository {
    private final DatabaseHelper dbHelper;
    public PantryRepository(Context context){this.dbHelper=DatabaseHelper.getInstance(context);}
    public static class RecipeMatch { public final Recipe recipe; public final List<String> missingIngredients; RecipeMatch(Recipe r,List<String> m){recipe=r;missingIngredients=m;} public boolean isFullMatch(){return missingIngredients.isEmpty();} }
    public List<List<RecipeMatch>> getSuggestedAndAlmostThere(){
        List<PantryItem> pantry=dbHelper.getAllPantryItems(); Map<String,PantryItem> pantryByName=new HashMap<>();
        for(PantryItem item:pantry)pantryByName.put(MatchingUtils.normalizeName(item.getName()),item);
        List<RecipeMatch> full=new ArrayList<>(), almost=new ArrayList<>();
        for(Recipe recipe:dbHelper.getAllRecipesShallow()){
            List<RecipeIngredient> required=dbHelper.getIngredientsForRecipe(recipe.getId()); List<String> missing=new ArrayList<>();
            for(RecipeIngredient req:required){PantryItem inPantry=pantryByName.get(MatchingUtils.normalizeName(req.getIngredientName())); boolean satisfied=inPantry!=null&&MatchingUtils.hasEnough(inPantry.getQuantity(),inPantry.getUnit(),req.getQuantity(),req.getUnit());if(!satisfied)missing.add(req.getIngredientName());}
            recipe.setIngredients(required); if(missing.isEmpty())full.add(new RecipeMatch(recipe,missing)); else if(missing.size()==1)almost.add(new RecipeMatch(recipe,missing));
        }
        List<List<RecipeMatch>> result=new ArrayList<>();result.add(full);result.add(almost);return result;
    }
}
