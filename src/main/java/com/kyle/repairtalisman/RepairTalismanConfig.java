package com.kyle.repairtalisman;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/** Tiny properties-file config at config/repairtalisman.properties. Missing keys get defaults. */
public final class RepairTalismanConfig {
    /** Ticks between repair pulses (20 ticks = 1 second). */
    public static int intervalTicks = 60;
    /** Durability points restored per damaged item per pulse. */
    public static int repairAmount = 1;
    /** If true the talisman only works while it sits in the hotbar; otherwise anywhere in the inventory. */
    public static boolean onlyInHotbar = false;
    public static boolean repairArmor = true;
    public static boolean repairOffhand = true;

    private RepairTalismanConfig() {}

    public static void load(Path file) {
        Properties p = new Properties();
        if (Files.exists(file)) {
            try (InputStream in = Files.newInputStream(file)) {
                p.load(in);
            } catch (IOException e) {
                RepairTalismanMod.LOGGER.warn("Could not read {}; using defaults", file, e);
            }
        }
        intervalTicks = clamp(readInt(p, "intervalTicks", intervalTicks), 1, 72000);
        repairAmount = clamp(readInt(p, "repairAmount", repairAmount), 1, 10000);
        onlyInHotbar = readBool(p, "onlyInHotbar", onlyInHotbar);
        repairArmor = readBool(p, "repairArmor", repairArmor);
        repairOffhand = readBool(p, "repairOffhand", repairOffhand);
        if (!Files.exists(file)) {
            save(file);
        }
    }

    private static void save(Path file) {
        Properties p = new Properties();
        p.setProperty("intervalTicks", Integer.toString(intervalTicks));
        p.setProperty("repairAmount", Integer.toString(repairAmount));
        p.setProperty("onlyInHotbar", Boolean.toString(onlyInHotbar));
        p.setProperty("repairArmor", Boolean.toString(repairArmor));
        p.setProperty("repairOffhand", Boolean.toString(repairOffhand));
        try {
            Files.createDirectories(file.getParent());
            try (OutputStream out = Files.newOutputStream(file)) {
                p.store(out, "Repair Talisman settings. intervalTicks: ticks between repair pulses (20 = 1 second). "
                        + "repairAmount: durability restored per item per pulse. onlyInHotbar: talisman must be in the hotbar to work.");
            }
        } catch (IOException e) {
            RepairTalismanMod.LOGGER.warn("Could not write {}", file, e);
        }
    }

    private static int readInt(Properties p, String key, int def) {
        String v = p.getProperty(key);
        if (v == null) return def;
        try {
            return Integer.parseInt(v.trim());
        } catch (NumberFormatException e) {
            RepairTalismanMod.LOGGER.warn("Bad value for {} in config ({}); using {}", key, v, def);
            return def;
        }
    }

    private static boolean readBool(Properties p, String key, boolean def) {
        String v = p.getProperty(key);
        return v == null ? def : Boolean.parseBoolean(v.trim());
    }

    private static int clamp(int v, int lo, int hi) {
        return Math.max(lo, Math.min(hi, v));
    }
}
