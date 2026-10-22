package com.example.smartpantry.util;

import com.example.smartpantry.db.PantryItem;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Helper functions behind the strict-matching rule (see PantryRepository#getSuggestedAndAlmostThere).
 *
 * Three responsibilities live here:
 *  1. normalizeName()      - makes ingredient names comparable despite case, spacing and simple
 *                            plurals ("tomato" vs "Tomatoes ").
 *  2. buildAvailability()  - turns the pantry rows into ONE total per ingredient and unit family,
 *                            so "Eggs 2 pcs" + "Egg 3 pcs" count as 5 eggs.
 *  3. isSatisfied()        - checks whether a recipe line is covered by those totals.
 *
 * Unit rules (deliberately simple and explainable, not a full conversion library):
 *  - Weight units convert to grams, volume units convert to millilitres, so 1 kg covers 500 g.
 *  - Counting units (pcs, pieces ...) are one family; any other unit text is its own family.
 *  - Different families are NEVER interchangeable: 50 g does not satisfy 50 ml, and
 *    "1 pcs" of chicken does not satisfy "500 g" of chicken. If the units can't be compared
 *    safely, the ingredient is treated as missing, because the app must never suggest a recipe
 *    the user can't genuinely cook.
 */
public final class MatchingUtils {

    private MatchingUtils() {
        // static helpers only
    }

    private static final String FAMILY_WEIGHT = "weight";
    private static final String FAMILY_VOLUME = "volume";

    // How many base units (grams / millilitres) one of each unit is worth.
    private static final Map<String, Double> TO_GRAMS = new HashMap<>();
    private static final Map<String, Double> TO_MILLILITRES = new HashMap<>();

    static {
        TO_GRAMS.put("mg", 0.001);
        TO_GRAMS.put("g", 1.0);
        TO_GRAMS.put("kg", 1000.0);

        TO_MILLILITRES.put("ml", 1.0);
        TO_MILLILITRES.put("l", 1000.0);
        TO_MILLILITRES.put("tsp", 5.0);
        TO_MILLILITRES.put("tbsp", 15.0);
        TO_MILLILITRES.put("cup", 240.0);
    }

    /** A quantity expressed in its family's base unit, plus the family it belongs to. */
    public static class BaseQuantity {
        public final double amount;
        public final String family;

        public BaseQuantity(double amount, String family) {
            this.amount = amount;
            this.family = family;
        }
    }

    /**
     * Normalizes an ingredient name: trim, lower-case, collapse whitespace, then strip a simple
     * plural ending so "tomatoes"/"tomato" and "onions"/"onion" produce the same key.
     * A heuristic on purpose - no dictionary needed for short grocery nouns.
     */
    public static String normalizeName(String rawName) {
        if (rawName == null) return "";
        String name = rawName.trim().toLowerCase(Locale.ROOT).replaceAll("\\s+", " ");

        if (name.endsWith("ies") && name.length() > 3) {
            name = name.substring(0, name.length() - 3) + "y";      // berries -> berry
        } else if (name.endsWith("oes") && name.length() > 3) {
            name = name.substring(0, name.length() - 2);            // tomatoes -> tomato
        } else if (name.endsWith("es") && name.length() > 2) {
            name = name.substring(0, name.length() - 2);            // dishes -> dish
        } else if (name.endsWith("s") && !name.endsWith("ss") && name.length() > 1) {
            name = name.substring(0, name.length() - 1);            // eggs -> egg, grass stays
        }
        return name;
    }

    /** Lower-cases a unit and folds the common spellings of "pieces" into one. */
    public static String normalizeUnit(String rawUnit) {
        if (rawUnit == null) return "";
        String unit = rawUnit.trim().toLowerCase(Locale.ROOT);
        switch (unit) {
            case "pc":
            case "piece":
            case "pieces":
            case "unit":
            case "units":
                return "pcs";
            default:
                return unit;
        }
    }

    /** Converts a quantity into its family's base unit (grams, millilitres, or a plain count). */
    public static BaseQuantity toBaseUnit(double quantity, String rawUnit) {
        String unit = normalizeUnit(rawUnit);
        if (TO_GRAMS.containsKey(unit)) {
            return new BaseQuantity(quantity * TO_GRAMS.get(unit), FAMILY_WEIGHT);
        }
        if (TO_MILLILITRES.containsKey(unit)) {
            return new BaseQuantity(quantity * TO_MILLILITRES.get(unit), FAMILY_VOLUME);
        }
        // Counting units: only comparable with the very same unit text ("pcs" with "pcs").
        return new BaseQuantity(quantity, "count:" + unit);
    }

    private static String availabilityKey(String ingredientName, String family) {
        return normalizeName(ingredientName) + "|" + family;
    }

    /**
     * Totals the pantry per ingredient AND unit family, in base units. Two rows for the same
     * ingredient ("Eggs 2 pcs", "egg 3 pcs") are added together instead of one overwriting the other.
     */
    public static Map<String, Double> buildAvailability(List<PantryItem> pantry) {
        Map<String, Double> totals = new HashMap<>();
        for (PantryItem item : pantry) {
            BaseQuantity base = toBaseUnit(item.getQuantity(), item.getUnit());
            String key = availabilityKey(item.getName(), base.family);
            Double existing = totals.get(key);
            totals.put(key, (existing == null ? 0.0 : existing) + base.amount);
        }
        return totals;
    }

    /**
     * True only if the pantry holds at least the required amount of this ingredient, in a unit
     * family that can be compared with the recipe's unit. A tiny epsilon absorbs floating-point
     * noise (e.g. 0.1 l vs 100 ml).
     */
    public static boolean isSatisfied(Map<String, Double> availability,
                                      String ingredientName, double requiredQty, String requiredUnit) {
        BaseQuantity required = toBaseUnit(requiredQty, requiredUnit);
        Double have = availability.get(availabilityKey(ingredientName, required.family));
        return have != null && have + 1e-9 >= required.amount;
    }
}
