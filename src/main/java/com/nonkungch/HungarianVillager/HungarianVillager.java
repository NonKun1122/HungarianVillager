package com.nonkungch.HungarianVillager;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HungarianVillager extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        
        // ถ้า config ว่างเปล่า ให้เริ่มดูดข้อมูลทั้งหมดทันที
        if (getConfig().getStringList("names.last_names").isEmpty()) {
            startDeepScraping();
        }

        getServer().getPluginManager().registerEvents(new VillagerListener(this), this);
        getLogger().info("HungarianVillager Enabled!");
    }

    private void startDeepScraping() {
        new BukkitRunnable() {
            @Override
            public void run() {
                getLogger().info("Starting Full Database Download from magyarnevek.hu...");
                
                // ดูดนามสกุล และชื่อแยกเพศ
                List<String> lastNames = scrapeCategory("https://magyarnevek.hu/nevek/csaladnevek/");
                List<String> maleNames = scrapeCategory("https://magyarnevek.hu/nevek/utonevek/ferfi/");
                List<String> femaleNames = scrapeCategory("https://magyarnevek.hu/nevek/utonevek/noi/");

                getConfig().set("names.last_names", lastNames);
                getConfig().set("names.first_names_male", maleNames);
                getConfig().set("names.first_names_female", femaleNames);
                saveConfig();
                
                getLogger().info("Download Complete! Collected " + (lastNames.size() + maleNames.size() + femaleNames.size()) + " names.");
            }
        }.runTaskAsynchronously(this);
    }

    private List<String> scrapeCategory(String baseUrl) {
        List<String> collected = new ArrayList<>();
        // เว็บนี้แบ่งหน้าตามตัวอักษร และเลขหน้า
        // โค้ดจะวนลูปดึงข้อมูลพื้นฐานจากหน้าแรกๆ จนครบ
        for (int p = 1; p <= 20; p++) { // ดึงสูงสุด 20 หน้าต่อหมวดหมู่ (ครอบคลุมเกือบทั้งหมด)
            try {
                URL url = new URL(baseUrl + p);
                URLConnection conn = url.openConnection();
                conn.setRequestProperty("User-Agent", "Mozilla/5.0"); // ป้องกันเว็บ Block
                
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                String line;
                // ดึงชื่อที่อยู่ใน <a> ภายใน <li>
                Pattern pattern = Pattern.compile("<li><a href=\".*?\">(.*?)</a></li>");
                
                boolean foundInPage = false;
                while ((line = reader.readLine()) != null) {
                    Matcher matcher = pattern.matcher(line);
                    while (matcher.find()) {
                        String name = matcher.group(1).trim();
                        if (!name.isEmpty() && !collected.contains(name)) {
                            collected.add(name);
                            foundInPage = true;
                        }
                    }
                }
                reader.close();
                if (!foundInPage) break; // ถ้าหน้าถัดไปไม่มีชื่อแล้ว ให้หยุด
            } catch (Exception e) {
                break;
            }
        }
        return collected;
    }

    public void logToFile(String message) {
        // โค้ดบันทึก Log ลง villager_history.txt เหมือนเดิม
        try {
            File file = new File(getDataFolder(), "villager_history.txt");
            if (!file.exists()) file.createNewFile();
            FileWriter fw = new FileWriter(file, true);
            fw.write("[" + java.time.LocalDateTime.now() + "] " + message + "\n");
            fw.close();
        } catch (IOException e) { e.printStackTrace(); }
    }
}
