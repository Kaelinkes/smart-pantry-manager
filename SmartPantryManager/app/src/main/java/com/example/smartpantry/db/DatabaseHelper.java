package com.example.smartpantry.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME="smart_pantry.db"; private static final int DATABASE_VERSION=1;
    public static final String TABLE_PANTRY="pantry_items", COL_PANTRY_ID="_id", COL_PANTRY_NAME="name", COL_PANTRY_QTY="quantity", COL_PANTRY_UNIT="unit", COL_PANTRY_EXPIRY="expiry_date";
    public static final String TABLE_RECIPES="recipes", COL_RECIPE_ID="_id", COL_RECIPE_NAME="name", COL_RECIPE_INSTRUCTIONS="instructions";
    public static final String TABLE_RECIPE_INGREDIENTS="recipe_ingredients", COL_RI_ID="_id", COL_RI_RECIPE_ID="recipe_id", COL_RI_NAME="ingredient_name", COL_RI_QTY="quantity", COL_RI_UNIT="unit";
    private static DatabaseHelper instance;
    public static synchronized DatabaseHelper getInstance(Context context){if(instance==null)instance=new DatabaseHelper(context.getApplicationContext());return instance;}
    private DatabaseHelper(Context context){super(context,DATABASE_NAME,null,DATABASE_VERSION);}
    @Override public void onCreate(SQLiteDatabase db){
        db.execSQL("CREATE TABLE "+TABLE_PANTRY+" ("+COL_PANTRY_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+COL_PANTRY_NAME+" TEXT NOT NULL, "+COL_PANTRY_QTY+" REAL NOT NULL, "+COL_PANTRY_UNIT+" TEXT NOT NULL, "+COL_PANTRY_EXPIRY+" TEXT)");
        db.execSQL("CREATE TABLE "+TABLE_RECIPES+" ("+COL_RECIPE_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+COL_RECIPE_NAME+" TEXT NOT NULL, "+COL_RECIPE_INSTRUCTIONS+" TEXT NOT NULL)");
        db.execSQL("CREATE TABLE "+TABLE_RECIPE_INGREDIENTS+" ("+COL_RI_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+COL_RI_RECIPE_ID+" INTEGER NOT NULL, "+COL_RI_NAME+" TEXT NOT NULL, "+COL_RI_QTY+" REAL NOT NULL, "+COL_RI_UNIT+" TEXT NOT NULL, FOREIGN KEY("+COL_RI_RECIPE_ID+") REFERENCES "+TABLE_RECIPES+"("+COL_RECIPE_ID+"))");
        RecipeSeeder.seed(db);
    }
    @Override public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion){db.execSQL("DROP TABLE IF EXISTS "+TABLE_RECIPE_INGREDIENTS);db.execSQL("DROP TABLE IF EXISTS "+TABLE_RECIPES);db.execSQL("DROP TABLE IF EXISTS "+TABLE_PANTRY);onCreate(db);}
    @Override public void onConfigure(SQLiteDatabase db){super.onConfigure(db);db.setForeignKeyConstraintsEnabled(true);}
    public long insertPantryItem(PantryItem item){return getWritableDatabase().insert(TABLE_PANTRY,null,pantryToContentValues(item));}
    public int updatePantryItem(PantryItem item){return getWritableDatabase().update(TABLE_PANTRY,pantryToContentValues(item),COL_PANTRY_ID+"=?",new String[]{String.valueOf(item.getId())});}
    public int deletePantryItem(long id){return getWritableDatabase().delete(TABLE_PANTRY,COL_PANTRY_ID+"=?",new String[]{String.valueOf(id)});}
    @Nullable public PantryItem getPantryItem(long id){Cursor c=getReadableDatabase().query(TABLE_PANTRY,null,COL_PANTRY_ID+"=?",new String[]{String.valueOf(id)},null,null,null);PantryItem item=null;if(c.moveToFirst())item=pantryFromCursor(c);c.close();return item;}
    public List<PantryItem> getAllPantryItems(){List<PantryItem> items=new ArrayList<>();Cursor c=getReadableDatabase().query(TABLE_PANTRY,null,null,null,null,null,COL_PANTRY_NAME+" COLLATE NOCASE ASC");while(c.moveToNext())items.add(pantryFromCursor(c));c.close();return items;}
    private ContentValues pantryToContentValues(PantryItem item){ContentValues cv=new ContentValues();cv.put(COL_PANTRY_NAME,item.getName());cv.put(COL_PANTRY_QTY,item.getQuantity());cv.put(COL_PANTRY_UNIT,item.getUnit());cv.put(COL_PANTRY_EXPIRY,item.getExpiryDate());return cv;}
    private PantryItem pantryFromCursor(Cursor c){PantryItem item=new PantryItem();item.setId(c.getLong(c.getColumnIndexOrThrow(COL_PANTRY_ID)));item.setName(c.getString(c.getColumnIndexOrThrow(COL_PANTRY_NAME)));item.setQuantity(c.getDouble(c.getColumnIndexOrThrow(COL_PANTRY_QTY)));item.setUnit(c.getString(c.getColumnIndexOrThrow(COL_PANTRY_UNIT)));int idx=c.getColumnIndexOrThrow(COL_PANTRY_EXPIRY);item.setExpiryDate(c.isNull(idx)?null:c.getString(idx));return item;}
    public List<Recipe> getAllRecipesShallow(){List<Recipe> recipes=new ArrayList<>();Cursor c=getReadableDatabase().query(TABLE_RECIPES,null,null,null,null,null,COL_RECIPE_NAME+" COLLATE NOCASE ASC");while(c.moveToNext())recipes.add(new Recipe(c.getLong(c.getColumnIndexOrThrow(COL_RECIPE_ID)),c.getString(c.getColumnIndexOrThrow(COL_RECIPE_NAME)),c.getString(c.getColumnIndexOrThrow(COL_RECIPE_INSTRUCTIONS))));c.close();return recipes;}
    public List<RecipeIngredient> getIngredientsForRecipe(long recipeId){List<RecipeIngredient> list=new ArrayList<>();Cursor c=getReadableDatabase().query(TABLE_RECIPE_INGREDIENTS,null,COL_RI_RECIPE_ID+"=?",new String[]{String.valueOf(recipeId)},null,null,COL_RI_ID+" ASC");while(c.moveToNext())list.add(new RecipeIngredient(c.getLong(c.getColumnIndexOrThrow(COL_RI_ID)),c.getLong(c.getColumnIndexOrThrow(COL_RI_RECIPE_ID)),c.getString(c.getColumnIndexOrThrow(COL_RI_NAME)),c.getDouble(c.getColumnIndexOrThrow(COL_RI_QTY)),c.getString(c.getColumnIndexOrThrow(COL_RI_UNIT))));c.close();return list;}
    @Nullable public Recipe getRecipeWithIngredients(long recipeId){Cursor c=getReadableDatabase().query(TABLE_RECIPES,null,COL_RECIPE_ID+"=?",new String[]{String.valueOf(recipeId)},null,null,null);Recipe r=null;if(c.moveToFirst())r=new Recipe(c.getLong(c.getColumnIndexOrThrow(COL_RECIPE_ID)),c.getString(c.getColumnIndexOrThrow(COL_RECIPE_NAME)),c.getString(c.getColumnIndexOrThrow(COL_RECIPE_INSTRUCTIONS)));c.close();if(r!=null)r.setIngredients(getIngredientsForRecipe(recipeId));return r;}
}
