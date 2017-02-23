package com.rolandoislas.allthedrops.data;

import com.rolandoislas.allthedrops.event.HarvestDropHandler;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;

import java.io.File;
import java.util.ArrayList;

import static com.rolandoislas.allthedrops.AllTheDrops.MODID;

/**
 * Created by Rolando on 2/21/2017.
 */
public class Config {
	private static final String CATEGORY_ADVANCED = "advanced";
	private static final String BASE_LANG = MODID + ".config.";
	private static Configuration config;
	public static int dropMultiplier;
	public static boolean guaranteeDrops;
	public static boolean requirePlayerKill;
	public static boolean commonBlockDrops;
	public static String[] resourceBlocks;

	public static void setConfigFile(File configFile) {
		config = new Configuration(configFile);
	}

	public static void load() {
		config.load();
		// General
		config.setCategoryLanguageKey(Configuration.CATEGORY_GENERAL, BASE_LANG + "general");
		dropMultiplier = config.getInt("dropmultiplier", Configuration.CATEGORY_GENERAL, 10, 1, 100,
				"", BASE_LANG + "general.dropmultiplier");
		guaranteeDrops = config.getBoolean("guaranteedrops", Configuration.CATEGORY_GENERAL, true,
				"", BASE_LANG + "general.guaranteedrops");
		requirePlayerKill = config.getBoolean("requireplayerkill", Configuration.CATEGORY_GENERAL, true,
				"", BASE_LANG + "general.requireplayerkill");
		commonBlockDrops = config.getBoolean("commonblockdrops", Configuration.CATEGORY_GENERAL, false,
				"", BASE_LANG + "general.commonblockdrops");
		// Baubles
		// TODO use baubles
		// Advanced
		config.setCategoryLanguageKey(CATEGORY_ADVANCED, BASE_LANG + "advanced");
		resourceBlocks = config.getStringList("resourceblocks", CATEGORY_ADVANCED,
				HarvestDropHandler.getResourceBlocks(),
				"", new String[]{}, BASE_LANG + "advanced.resourceblocks");
		config.save();
	}

	public static void configChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
		if (event.getModID().equals(MODID)) {
			Config.config.save();
			Config.load();
		}
	}

	public static Configuration getConfig() {
		return config;
	}

	public static ArrayList<IConfigElement> getCategories() {
		ArrayList<IConfigElement> categories = new ArrayList<IConfigElement>();
		categories.addAll(new ConfigElement(getConfig().getCategory(Configuration.CATEGORY_GENERAL))
				.getChildElements());
		categories.add(new ConfigElement(getConfig().getCategory(CATEGORY_ADVANCED)));
		return categories;
	}
}
