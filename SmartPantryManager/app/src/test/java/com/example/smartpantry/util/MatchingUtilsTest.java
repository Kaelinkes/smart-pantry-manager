package com.example.smartpantry.util;

import static org.junit.Assert.*;
import org.junit.Test;

public class MatchingUtilsTest {
    @Test public void pluralAndSingularNormaliseToSameKey(){assertEquals(MatchingUtils.normalizeName("tomato"),MatchingUtils.normalizeName("Tomatoes"));assertEquals(MatchingUtils.normalizeName("egg"),MatchingUtils.normalizeName("EGGS"));assertEquals(MatchingUtils.normalizeName("onion"),MatchingUtils.normalizeName("  Onions "));}
    @Test public void berriesBecomeBerry(){assertEquals("berry",MatchingUtils.normalizeName("berries"));}
    @Test public void wordsEndingInDoubleSAreNotStripped(){assertEquals("grass",MatchingUtils.normalizeName("grass"));}
    @Test public void nullNameIsSafe(){assertEquals("",MatchingUtils.normalizeName(null));}
    @Test public void kilogramsSatisfyGramRequirement(){assertTrue(MatchingUtils.hasEnough(1,"kg",500,"g"));}
    @Test public void tooLittleIsRejected(){assertFalse(MatchingUtils.hasEnough(200,"g",500,"g"));assertFalse(MatchingUtils.hasEnough(1,"pcs",3,"pcs"));}
    @Test public void volumeUnitsConvert(){assertTrue(MatchingUtils.hasEnough(1,"l",300,"ml"));assertTrue(MatchingUtils.hasEnough(1,"cup",2,"tbsp"));assertFalse(MatchingUtils.hasEnough(100,"ml",1,"l"));}
    @Test public void incompatibleUnitsFallBackToPresenceOnly(){assertTrue(MatchingUtils.hasEnough(1,"pcs",500,"g"));}
    @Test public void unitCaseIsIgnored(){assertTrue(MatchingUtils.hasEnough(1,"KG",500,"g"));}
}
