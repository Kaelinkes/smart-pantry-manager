package com.example.smartpantry.db;

import java.util.ArrayList;
import java.util.List;

public class Recipe {
    private long id; private String name; private String instructions; private List<RecipeIngredient> ingredients = new ArrayList<>();
    public Recipe() {}
    public Recipe(long id,String name,String instructions){this.id=id;this.name=name;this.instructions=instructions;}
    public long getId(){return id;} public String getName(){return name;} public String getInstructions(){return instructions;} public List<RecipeIngredient> getIngredients(){return ingredients;}
    public void setIngredients(List<RecipeIngredient> ingredients){this.ingredients=ingredients;}
}
