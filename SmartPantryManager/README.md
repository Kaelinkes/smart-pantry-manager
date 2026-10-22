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
NLP solution:

**1. Ingredient names are normalised** (`MatchingUtils.normalizeName`)
Lower-cased, trimmed, internal whitespace collapsed, and simple plurals reduced to singular, so
these all match each other:

```
"Tomatoes" → "tomato"      "EGGS"  → "egg"
"  Onions" → "onion"       "berries" → "berry"
```

A naive exact-string match would break on every one of these.

**2. Rows for the same ingredient are added together** (`MatchingUtils.buildAvailability`)
If the pantry holds `Eggs 2 pcs` and `egg 3 pcs` as separate rows, the app treats that as 5 eggs,
so a recipe needing 4 is still correctly suggested.

**3. Quantities are compared in a common base unit** (`MatchingUtils.isSatisfied`)
Weights convert to grams and volumes convert to millilitres, so **1 kg** of flour in the pantry
satisfies a recipe needing **500 g**, and **0.1 l** satisfies **100 ml**.

**4. Different unit families are never interchangeable.**
`50 g` of salt does **not** satisfy `50 ml`, and `1 pcs` of chicken does not satisfy `500 g` of
chicken. If the units cannot be compared safely the ingredient counts as missing, because the app
must never suggest a recipe the user cannot genuinely cook. (Limitation: salt entered in spoons
will not match a recipe written in grams; the seed recipes use the units people usually store
each item in.)

**"Almost There" (optional bonus feature)** — recipes missing exactly **one** ingredient are
collected into a second, visually separated list with its own heading and the missing ingredient
named. It is never merged into the strict suggestions list.

---

## Database choice: SQLite (via `SQLiteOpenHelper`)

**SQLite was chosen over Firebase and PostgreSQL** for these reasons:

1. **The data is inherently private and single-user.** A pantry belongs to one person on one
   device. There is no sharing, no multi-device sync requirement, and no collaboration — so the
   complexity and account management of a cloud database buys nothing here.
2. **The app must work with no internet connection.** Cooking happens in a kitchen, often with
   poor signal. A local database means the app is never unavailable and never shows a loading
   spinner.
3. **The strict-matching query runs entirely offline and instantly.** Matching reads the whole
   pantry and recipe catalogue on every visit to the Suggestions screen. Over a network that
   would mean latency and failure states; locally it is immediate.
4. **No ongoing cost or backend maintenance**, unlike PostgreSQL, which would additionally have
   required building and hosting a REST API purely to reach the data.
5. `SQLiteOpenHelper` keeps the raw SQL visible in `DatabaseHelper.java`, which makes the schema
   and CRUD operations straightforward to explain and defend.

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
`SharedPreferences` rather than SQLite, since they are settings rather than relational records.

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

1. **Clone the repository**
   ```bash
   git clone <your-repository-url>
   cd SmartPantryManager
   ```
2. **Open in Android Studio** — choose *File → Open* and select the project folder. Let Gradle
   sync finish (it will download the AndroidX and Material dependencies).
3. **Set up a device** — either create an emulator via *Device Manager* (any device running
   API 26 or higher), or connect a physical Android phone with USB debugging enabled.
4. **Run** — press the green ▶ Run button, or `Shift + F10`.

No API keys, accounts, or backend setup are required — the database is created and seeded with
18 recipes automatically on first launch.

### Trying the strict-matching rule

1. Open **Suggestions** on a fresh install — the pantry is empty, so you see
   *"No recipes match your pantry yet."*
2. Add `Eggs` (3 pcs), `Tomatoes` (2 pcs) and `Cooking oil` (100 ml). Check Suggestions: *Tomato
   Egg Stir-Fry* appears under **Almost There**, missing `salt` — it is correctly **excluded**
   from the strict list.
3. Add `Salt` (500 g). Return to Suggestions: the recipe now appears in the strict
   **"You can make right now"** list.
4. Delete the salt again — the recipe drops straight back out of the suggestions.
5. Try `Tomato` (singular) instead of `Tomatoes`, or record salt as `0.5 kg` instead of `500 g` —
   matching still works.

---

## Project structure

```
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
│   ├── PantryRepository.java        ★ strict-matching algorithm
│   ├── RecipeSeeder.java            18 seeded recipes
│   ├── PantryItem.java  Recipe.java  RecipeIngredient.java
└── util/
    └── MatchingUtils.java           ★ name normalisation, totals per ingredient, unit conversion
```

## Out of scope

Per the brief, this app uses **no** Google Maps, mapping SDK, or GPS/location services, and
handles no payments. Its scope is strictly the user's own pantry and recipe matching.
