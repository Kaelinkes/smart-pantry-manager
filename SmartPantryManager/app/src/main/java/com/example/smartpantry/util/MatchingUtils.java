package com.example.smartpantry.util;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class MatchingUtils {
    private MatchingUtils() {}
    private static final Map<String,Double> WEIGHT_TO_GRAMS=new HashMap<>(); private static final Map<String,Double> VOLUME_TO_ML=new HashMap<>();
    static{WEIGHT_TO_GRAMS.put("g",1.0);WEIGHT_TO_GRAMS.put("kg",1000.0);VOLUME_TO_ML.put("ml",1.0);VOLUME_TO_ML.put("l",1000.0);VOLUME_TO_ML.put("tsp",5.0);VOLUME_TO_ML.put("tbsp",15.0);VOLUME_TO_ML.put("cup",240.0);}
    public enum UnitCategory{WEIGHT,VOLUME,COUNT}
    public static class BaseQuantity{public final double amount;public final UnitCategory category;public BaseQuantity(double a,UnitCategory c){amount=a;category=c;}}
    public static String normalizeName(String rawName){if(rawName==null)return "";String name=rawName.trim().toLowerCase(Locale.ROOT).replaceAll("\\s+"," ");if(name.endsWith("ies")&&name.length()>3)name=name.substring(0,name.length()-3)+"y";else if(name.endsWith("oes")&&name.length()>3)name=name.substring(0,name.length()-2);else if(name.endsWith("es")&&name.length()>2)name=name.substring(0,name.length()-2);else if(name.endsWith("s")&&!name.endsWith("ss")&&name.length()>1)name=name.substring(0,name.length()-1);return name;}
    public static String normalizeUnit(String rawUnit){if(rawUnit==null)return "";return rawUnit.trim().toLowerCase(Locale.ROOT);}
    public static BaseQuantity toBaseUnit(double quantity,String rawUnit){String unit=normalizeUnit(rawUnit);if(WEIGHT_TO_GRAMS.containsKey(unit))return new BaseQuantity(quantity*WEIGHT_TO_GRAMS.get(unit),UnitCategory.WEIGHT);if(VOLUME_TO_ML.containsKey(unit))return new BaseQuantity(quantity*VOLUME_TO_ML.get(unit),UnitCategory.VOLUME);return new BaseQuantity(quantity,UnitCategory.COUNT);}
    public static boolean hasEnough(double pantryQty,String pantryUnit,double requiredQty,String requiredUnit){BaseQuantity p=toBaseUnit(pantryQty,pantryUnit),r=toBaseUnit(requiredQty,requiredUnit);if(p.category!=r.category)return true;if(p.category==UnitCategory.COUNT&&!normalizeUnit(pantryUnit).equals(normalizeUnit(requiredUnit)))return true;return p.amount>=r.amount;}
}
