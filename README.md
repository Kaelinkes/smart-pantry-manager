# Smart Pantry Manager

An Android application (Java) that helps reduce food waste by tracking the ingredients you
actually have at home and suggesting **only** the recipes you can cook right now — no shopping
trip required.

Module: Mobile App Development 700
Language: Java (Android Studio)
Minimum SDK: 26 (Android 8.0) · Target SDK: 34

---

## What the app does

| Screen | Purpose |
|---|---|
| **Pantry List** (`PantryListActivity`) | Launcher screen. Lists every ingredient in your pantry in a `RecyclerView`. Tap a row to edit it, tap the bin icon to delete, tap the **+** button to add. |
| **Add / Edit Ingredient** (`AddEditIngredientActivity`) | One form used for both creating and editing. Captures name, quantity, unit and an optional expiry date, with input validation. |
| **Suggested Recipes** (`SuggestedRecipesActivity`) | Runs the strict-matching rule and lists only recipes you can make right now. A separate "Almost There" section shows recipes missing exactly one ingredient. |
| **Recipe Detail** (`RecipeDetailActivity`) | Full ingredient list and preparation method for the selected recipe. |
| **Settings** (`SettingsActivity`) | Toggle expiring-soon highlighting and choose a preferred unit system. |

A bottom navigation bar moves between the three top-level screens.

---

## The strict-matching rule (core logic)

A recipe is shown as *suggested* **only if every single ingredient it requires is present in the
pantry in at least the required quantity.** A recipe needing 5 ingredients where the pantry has 4
of them will **not** appear.

The logic lives in `PantryRepository.getSuggestedAndAlmostThere()`, supported by
`util/MatchingUtils.java`. It is deliberately robust to everyday messiness without being a full
NLP solution.

**1. Ingredient names are normalised** (`MatchingUtils.normalizeName`)
Lower-cased, trimmed, internal whitespace collapsed, and simple plurals reduced to singular.

**2. Rows for the same ingredient are added together** (`MatchingUtils.buildAvailability`)
If the pantry holds separate rows for the same ingredient, the app totals them before matching.

**3. Quantities are compared in a common base unit** (`MatchingUtils.isSatisfied`)
Weights convert to grams and volumes convert to millilitres.

**4. Different unit families are never interchangeable.**
If units cannot be compared safely the ingredient counts as missing, so the app does not suggest a
recipe the user cannot genuinely cook.

**"Almost There"** — recipes missing exactly one ingredient are collected into a separate list
with the missing ingredient named. It is never merged into the strict suggestions list.

---

## Database choice: SQLite (via `SQLiteOpenHelper`)

SQLite was chosen because the data is private and single-user, the app works fully offline, the
matching logic can run immediately on-device, and there is no backend hosting or account overhead.
`SQLiteOpenHelper` also keeps the SQL schema and CRUD operations straightforward to explain.

### Schema

**`pantry_items`** — user-owned, full CRUD
| Column | Type | Notes |
|---|---|---|
| `_id` | INTEGER | PK, autoincrement |
| `name` | TEXT | NOT NULL |
| `quantity` | REAL | NOT NULL |
| `unit` | TEXT | NOT NULL |
| `expiry_date` | TEXT | nullable, `yyyy-MM-dd` |

**`recipes`** — seeded on first run
| Column | Type |
|---|---|
| `_id` | INTEGER PK |
| `name` | TEXT NOT NULL |
| `instructions` | TEXT NOT NULL |

**`recipe_ingredients`** — many rows per recipe
| Column | Type | Notes |
|---|---|---|
| `_id` | INTEGER | PK |
| `recipe_id` | INTEGER | FK → `recipes(_id)` |
| `ingredient_name` | TEXT | NOT NULL |
| `quantity` | REAL | NOT NULL |
| `unit` | TEXT | NOT NULL |

`recipes` → `recipe_ingredients` is a one-to-many relationship. **18 recipes** are seeded
automatically the first time the database is created (`RecipeSeeder.java`).

Small key–value preferences (the expiry toggle and unit preference) are stored in
`SharedPreferences` rather than SQLite.

### CRUD and persistence

| Operation | Where |
|---|---|
| **Create** | `DatabaseHelper.insertPantryItem()` — from the Add screen |
| **Read** | `DatabaseHelper.getAllPantryItems()` — populates the pantry `RecyclerView` |
| **Update** | `DatabaseHelper.updatePantryItem()` — from the Edit screen |
| **Delete** | `DatabaseHelper.deletePantryItem()` — bin icon, with a confirmation dialog |

Because the data is written to an on-device SQLite file, it is still present after the app is
fully closed and reopened.

---

## Setup and run instructions

1. **Clone the repository**.
2. **Open in Android Studio** and let Gradle sync finish.
3. **Set up a device** using an emulator running API 26 or higher, or a physical Android phone.
4. **Run** using the green Run button or `Shift + F10`.

No API keys, accounts, or backend setup are required — the database is created and seeded with
18 recipes automatically on first launch.

### Trying the strict-matching rule

1. Open **Suggestions** on a fresh install.
2. Add several pantry ingredients and inspect the **Almost There** section.
3. Add the final required ingredient and the recipe moves into **You can make right now**.
4. Delete an ingredient and it drops out of the strict suggestions.
5. Try singular/plural ingredient names or equivalent supported weight/volume units to test matching.

---

## Project structure

```text
app/src/main/java/com/example/smartpantry/
├── PantryListActivity.java          launcher, pantry RecyclerView, delete
├── AddEditIngredientActivity.java   add/edit form + validation
├── SuggestedRecipesActivity.java    runs matching, two separate lists
├── RecipeDetailActivity.java        ingredients + method
├── SettingsActivity.java            SharedPreferences settings
├── adapters/
│   ├── PantryAdapter.java           custom RecyclerView adapter (pantry)
│   └── RecipeAdapter.java           custom RecyclerView adapter (recipes)
├── db/
│   ├── DatabaseHelper.java          SQLiteOpenHelper, schema, all CRUD
│   ├── PantryRepository.java        strict-matching algorithm
│   ├── RecipeSeeder.java            18 seeded recipes
│   ├── PantryItem.java
│   ├── Recipe.java
│   └── RecipeIngredient.java
└── util/
    └── MatchingUtils.java           name normalisation and unit conversion
```

## Out of scope

Per the brief, this app uses **no** Google Maps, mapping SDK, or GPS/location services, and
handles no payments. Its scope is strictly the user's own pantry and recipe matching.
