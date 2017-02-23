package com.rolandoislas.allthedrops.gui;

import com.rolandoislas.allthedrops.AllTheDrops;
import com.rolandoislas.allthedrops.data.Config;
import net.minecraft.client.gui.GuiScreen;

/**
 * Created by Rolando on 2/21/2017.
 */
public class GuiConfig extends net.minecraftforge.fml.client.config.GuiConfig {
	public GuiConfig(GuiScreen parentScreen) {
		super(parentScreen,
				Config.getCategories(),
				AllTheDrops.MODID,
				false,
				false,
				AllTheDrops.NAME + " Config");
		titleLine2 = Config.getConfig().getConfigFile().getAbsolutePath();
	}
}
