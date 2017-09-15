package com.rolandoislas.allthedrops.registry;

import com.rolandoislas.allthedrops.data.Config;
import com.rolandoislas.allthedrops.items.EnumShirt;
import com.rolandoislas.allthedrops.items.ItemBaubleCharm;
import com.rolandoislas.allthedrops.items.ItemBaubleShirt;
import com.rolandoislas.allthedrops.items.ItemDevTool;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * Created by Rolando on 2/23/2017.
 */
public class ModItems {
	public static final Item DEV_TOOL = new ItemDevTool();
	public static final Item BAUBLE_SHIRT = new ItemBaubleShirt();
	public static final Item BAUBLE_CHARM = new ItemBaubleCharm();

	public static void registerTextures() {
		ModelLoader.setCustomModelResourceLocation(DEV_TOOL, 0,
				new ModelResourceLocation(DEV_TOOL.getRegistryName(), "inventory"));
		for (EnumShirt shirt : EnumShirt.values())
			ModelLoader.setCustomModelResourceLocation(BAUBLE_SHIRT, shirt.getMeta(),
					new ModelResourceLocation(BAUBLE_SHIRT.getRegistryName(), "inventory"));
		ModelLoader.setCustomModelResourceLocation(BAUBLE_CHARM, 0,
				new ModelResourceLocation(BAUBLE_CHARM.getRegistryName(), "inventory"));
	}

	public static void register() {
		ForgeRegistries.ITEMS.register(DEV_TOOL);
		if (Config.enableBaubles) {
			ForgeRegistries.ITEMS.register(BAUBLE_SHIRT);
			ForgeRegistries.ITEMS.register(BAUBLE_CHARM);
		}
	}
}
