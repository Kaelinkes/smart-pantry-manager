# Smart Pantry Manager

An Android application written in Java that tracks pantry ingredients and suggests recipes that can be made from the ingredients currently available.

## Features
- Add, edit and delete pantry ingredients
- SQLite persistence for pantry and recipe data
- 18 seeded recipes
- Strict recipe matching using ingredient presence and quantity
- Ingredient name normalisation and compatible unit conversion
- Separate "Almost There" suggestions for recipes missing exactly one ingredient
- Recipe detail screen with ingredients and method
- Settings for expiry alerts and preferred units
- Bottom navigation between Pantry, Suggestions and Settings

## Technology
Java / Android Studio · Android SDK 34 · minimum SDK 26 · SQLiteOpenHelper · AndroidX RecyclerView · Material Components · ConstraintLayout · JUnit 4

## Running
Open the `SmartPantryManager` folder in Android Studio, allow Gradle to sync, then run on an Android emulator or physical device running API 26 or newer. No API keys or backend services are required; the local database is created and seeded on first launch.

## Core matching behaviour
A recipe appears in the main suggestions only when every required ingredient is available in sufficient quantity. Ingredient names are normalised for case, whitespace and simple plural differences. Compatible weight and volume units are converted to common base units before comparison. Recipes missing exactly one ingredient are displayed separately under "Almost There".
