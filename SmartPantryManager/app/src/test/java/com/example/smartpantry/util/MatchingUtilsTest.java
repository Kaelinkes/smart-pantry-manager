package com.example.smartpantry.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.example.smartpantry.db.PantryItem;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Local JUnit tests for the pure-Java parts of the strict-matching rule.
 * Run: right-click this file in Android Studio > Run 'MatchingUtilsTest'.
 */
public class MatchingUtilsTest {

    private static PantryItem item(String name, double qty, String unit) {
        return new PantryItem(0, name, qty, unit, null);
    }

    private static Map<String, Double> pantry(PantryItem... items) {
        List<PantryItem> list = new ArrayList<>();
        for (PantryItem i : items) list.add(i);
        return MatchingUtils.buildAvailability(list);
    }

    // ---- name normalisation ----

    @Test
    public void pluralAndSingularNormaliseToSameKey() {
        assertEquals(MatchingUtils.normalizeName("tomato"), MatchingUtils.normalizeName("Tomatoes"));
        assertEquals(MatchingUtils.normalizeName("egg"), MatchingUtils.normalizeName("EGGS"));
        assertEquals(MatchingUtils.normalizeName("onion"), MatchingUtils.normalizeName("  Onions "));
    }

    @Test
    public void berriesBecomeBerry() {
        assertEquals("berry", MatchingUtils.normalizeName("berries"));
    }

    @Test
    public void wordsEndingInDoubleSAreNotStripped() {
        assertEquals("grass", MatchingUtils.normalizeName("grass"));
    }

    @Test
    public void nullNameIsSafe() {
        assertEquals("", MatchingUtils.normalizeName(null));
    }

    // ---- quantities and units ----

    @Test
    public void kilogramsSatisfyGramRequirement() {
        assertTrue(MatchingUtils.isSatisfied(pantry(item("Flour", 1, "kg")), "flour", 500, "g"));
    }

    @Test
    public void tooLittleIsRejected() {
        assertFalse(MatchingUtils.isSatisfied(pantry(item("Rice", 200, "g")), "rice", 500, "g"));
        assertFalse(MatchingUtils.isSatisfied(pantry(item("Eggs", 1, "pcs")), "eggs", 3, "pcs"));
    }

    @Test
    public void volumeUnitsConvert() {
        assertTrue(MatchingUtils.isSatisfied(pantry(item("Milk", 1, "l")), "milk", 300, "ml"));
        assertTrue(MatchingUtils.isSatisfied(pantry(item("Oil", 1, "cup")), "oil", 2, "tbsp"));
        assertFalse(MatchingUtils.isSatisfied(pantry(item("Milk", 100, "ml")), "milk", 1, "l"));
        assertTrue(MatchingUtils.isSatisfied(pantry(item("Oil", 0.1, "l")), "oil", 100, "ml"));
    }

    @Test
    public void weightAndVolumeAreNeverInterchangeable() {
        assertFalse(MatchingUtils.isSatisfied(pantry(item("Salt", 50, "g")), "salt", 50, "ml"));
    }

    @Test
    public void countAndWeightAreNeverInterchangeable() {
        assertFalse(MatchingUtils.isSatisfied(pantry(item("Chicken breast", 1, "pcs")), "chicken breast", 500, "g"));
    }

    @Test
    public void pieceSpellingsAreTreatedAsTheSameUnit() {
        assertTrue(MatchingUtils.isSatisfied(pantry(item("Eggs", 3, "pieces")), "eggs", 3, "pcs"));
    }

    @Test
    public void unitCaseIsIgnored() {
        assertTrue(MatchingUtils.isSatisfied(pantry(item("Flour", 1, "KG")), "flour", 500, "g"));
    }

    // ---- duplicate rows and name matching ----

    @Test
    public void duplicateRowsForSameIngredientAreAdded() {
        Map<String, Double> p = pantry(item("Eggs", 2, "pcs"), item("egg", 3, "pcs"));
        assertTrue(MatchingUtils.isSatisfied(p, "eggs", 5, "pcs"));
        assertFalse(MatchingUtils.isSatisfied(p, "eggs", 6, "pcs"));
    }

    @Test
    public void singularPantryNameMatchesPluralRecipeName() {
        assertTrue(MatchingUtils.isSatisfied(pantry(item("tomato", 4, "pcs")), "tomatoes", 4, "pcs"));
    }

    @Test
    public void missingIngredientIsNotSatisfied() {
        assertFalse(MatchingUtils.isSatisfied(pantry(), "salt", 5, "g"));
    }
}
