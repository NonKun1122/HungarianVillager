package com.nonkungch.HungarianVillager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class NameGenerator {
    private static final Random random = new Random();
    // เก็บชื่อที่ถูกใช้งานไปแล้ว เพื่อไม่ให้สุ่มซ้ำใน Session นั้นๆ
    private static final Set<String> usedNames = new HashSet<>();

    public static String generate(HungarianVillager plugin) {
        // ดึง List จาก Config
        List<String> last = plugin.getConfig().getStringList("names.last_names");
        List<String> male = plugin.getConfig().getStringList("names.first_names_male");
        List<String> female = plugin.getConfig().getStringList("names.first_names_female");

        // ป้องกันกรณี Config คืนค่า null หรือยังโหลดข้อมูลไม่เสร็จ
        if (last == null) last = new ArrayList<>();
        if (male == null) male = new ArrayList<>();
        if (female == null) female = new ArrayList<>();

        // รวมชื่อจริงทั้งชายและหญิง
        List<String> allFirst = new ArrayList<>(male);
        allFirst.addAll(female);

        // กรณีไม่มีข้อมูลในฐานข้อมูลเลย ให้ใช้ชื่อสำรองเพื่อไม่ให้ระบบพัง
        if (last.isEmpty() || allFirst.isEmpty()) {
            return "Nagy Bence"; 
        }

        String fullName;
        int attempts = 0;
        int maxAttempts = 100; // ป้องกัน infinite loop
        
        // คำนวณขีดจำกัดความหลากหลายของชื่อ (นามสกุล x ชื่อต้น)
        int totalPossibilities = last.size() * allFirst.size();

        do {
            String ln = last.get(random.nextInt(last.size()));
            String fn = allFirst.get(random.nextInt(allFirst.size()));
            fullName = ln + " " + fn; // รูปแบบการเขียนชื่อแบบฮังการี (นามสกุลขึ้นก่อน)
            attempts++;
        } while (usedNames.contains(fullName) && attempts < maxAttempts);

        // บันทึกชื่อที่ใช้แล้ว
        usedNames.add(fullName);
        
        // ถ้าชื่อที่ใช้ไปแล้วมีจำนวนถึง 80% ของความเป็นไปได้ทั้งหมด ให้ล้างค่าเพื่อเริ่มใหม่
        if (usedNames.size() > (totalPossibilities * 0.8)) {
            usedNames.clear();
        }

        return fullName;
    }
}
