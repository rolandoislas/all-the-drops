package com.rolandoislas.allthedrops.registry;

import com.rolandoislas.allthedrops.items.ItemDevTool;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * Created by Rolando on 2/23/2017.
 */
public class ModItems {
	public static final Item DEV_TOOL = new ItemDevTool();

	public static void registerTextures() {
		ModelLoader.setCustomModelResourceLocation(DEV_TOOL, 0,
				new ModelResourceLocation(DEV_TOOL.getRegistryName(), ""));
	}

	public static void register() {
		GameRegistry.register(DEV_TOOL);
	}
}
