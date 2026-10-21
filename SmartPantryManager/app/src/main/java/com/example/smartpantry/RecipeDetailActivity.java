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

public class RecipeDetailActivity extends AppCompatActivity {
    public static final String EXTRA_RECIPE_ID="com.example.smartpantry.EXTRA_RECIPE_ID";
    @Override protected void onCreate(Bundle savedInstanceState){super.onCreate(savedInstanceState);setContentView(R.layout.activity_recipe_detail);Toolbar toolbar=findViewById(R.id.toolbar);setSupportActionBar(toolbar);if(getSupportActionBar()!=null)getSupportActionBar().setDisplayHomeAsUpEnabled(true);long recipeId=getIntent().getLongExtra(EXTRA_RECIPE_ID,-1L);if(recipeId==-1L){Toast.makeText(this,"Recipe not found",Toast.LENGTH_SHORT).show();finish();return;}Recipe recipe=DatabaseHelper.getInstance(this).getRecipeWithIngredients(recipeId);if(recipe==null){Toast.makeText(this,"Recipe not found",Toast.LENGTH_SHORT).show();finish();return;}setTitle(recipe.getName());((TextView)findViewById(R.id.text_detail_name)).setText(recipe.getName());((TextView)findViewById(R.id.text_detail_ingredients)).setText(buildIngredientList(recipe));((TextView)findViewById(R.id.text_detail_method)).setText(recipe.getInstructions());}
    private String buildIngredientList(Recipe recipe){StringBuilder b=new StringBuilder();for(RecipeIngredient i:recipe.getIngredients())b.append("•  ").append(formatQuantity(i.getQuantity())).append(" ").append(i.getUnit()).append("  ").append(i.getIngredientName()).append("\n");return b.toString().trim();}
    private String formatQuantity(double q){return q==Math.floor(q)?String.valueOf((long)q):String.valueOf(q);}
    @Override public boolean onOptionsItemSelected(MenuItem item){if(item.getItemId()==android.R.id.home){finish();return true;}return super.onOptionsItemSelected(item);}
}
