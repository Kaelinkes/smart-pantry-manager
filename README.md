# Smart Pantry Manager

A robust, Java-based Android application designed to help users minimize food waste by tracking household pantry ingredients and suggesting recipes based strictly on available items.

---

## 📌 Features

* **Pantry Management:** Add, edit, and delete pantry items (including name, quantity, unit, and optional expiry dates)[cite: 3].
* **Strict-Matching Recipe Algorithm:** Suggests recipes *only* when every required ingredient is currently present in the user's pantry in at least the required quantity.
* **Pre-loaded Recipe Catalog:** Automatically seeds a database of 15–20 diverse recipes on first run[cite: 4].
* **Dynamic Lists & Adapters:** Displays pantry items and matching recipes smoothly using `RecyclerView` with custom adapters[cite: 4, 5].
* **Persistent Local Storage:** Retains all user data locally using SQLite, ensuring data persists across app sessions[cite: 5].
* **Settings & Customization:** Configure preferences such as expiring-soon alerts and unit preferences[cite: 4].

---

## 🗄️ Database Implementation & Justification

* **Chosen Database:** SQLite (via `SQLiteOpenHelper`)
* **Why SQLite?** SQLite was chosen because it provides a lightweight, reliable, on-device relational database solution built natively into Android[cite: 5]. It requires no external cloud configuration or active internet connection, making it ideal for a self-contained local pantry inventory app. It seamlessly handles local CRUD operations for both pantry items and the seeded recipe catalog while satisfying all offline persistence requirements.

---

## 🛠️ Tech Stack & Architecture

* **Language:** Java
* **IDE:** Android Studio
* **Architecture Pattern:** Activity/Fragment-based navigation using Intents
* **UI Components:** `RecyclerView`, custom adapters, and responsive layouts (`ConstraintLayout`, `LinearLayout`)
* **Version Control:** Git & GitHub
