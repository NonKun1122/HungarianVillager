package com.nonkungch.HungarianVillager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class NameGenerator {
    private static final Random random = new Random();
    // เก็บชื่อที่ถูกใช้งานไปแล้ว เพื่อไม่ให้สุ่มซ้ำ
    private static final Set<String> usedNames = new HashSet<>();

    public static String generate(HungarianVillager plugin) {
        List<String> last = plugin.getConfig().getStringList("names.last_names");
        List<String> male = plugin.getConfig().getStringList("names.first_names_male");
        List<String> female = plugin.getConfig().getStringList("names.first_names_female");

        List<String> allFirst = new ArrayList<>(male);
        allFirst.addAll(female);

        if (last.isEmpty() || allFirst.isEmpty()) return "Nagy Bence";

        String fullName;
        int attempts = 0;
        int maxAttempts = 100; // ป้องกันการวนลูปไม่รู้จบกรณีชื่อในฐานข้อมูลใกล้หมด

        do {
            String ln = last.get(random.nextInt(last.size()));
            String fn = allFirst.get(random.nextInt(allFirst.size()));
            fullName = ln + " " + fn;
            attempts++;
        } while (usedNames.contains(fullName) && attempts < maxAttempts);

        // บันทึกว่าชื่อนี้ถูกใช้แล้ว
        usedNames.add(fullName);
        
        // ถ้าชื่อใน usedNames เยอะเกินไป (เช่นเกิน 80% ของความเป็นไปได้ทั้งหมด) 
        // อาจจะสั่งล้าง usedNames.clear() เพื่อให้เริ่มสุ่มใหม่ได้
        if (usedNames.size() > (last.size() * allFirst.size() * 0.8)) {
            usedNames.clear();
        }

        return fullName;
    }
}
