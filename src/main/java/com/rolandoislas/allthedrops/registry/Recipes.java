package com.rolandoislas.allthedrops.registry;

import com.rolandoislas.allthedrops.AllTheDrops;
import com.rolandoislas.allthedrops.items.EnumShirt;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapelessOreRecipe;

/**
 * Created by Rolando on 2/25/2017.
 */
public class Recipes {
	public static void register() {
		// Add recipe dependent to the ore dictionary
		OreDictionary.registerOre("dyeBlack", new ItemStack(Items.DYE, 1,
				EnumDyeColor.BLACK.getDyeDamage()));
		OreDictionary.registerOre("woolWhite", new ItemStack(Blocks.WOOL, 1,
				EnumDyeColor.WHITE.getMetadata()));
		OreDictionary.registerOre("stick", Items.STICK);
		// Add additional resource blocks to the ore dictionary
		OreDictionary.registerOre("cropBeetroot", Items.BEETROOT);
		OreDictionary.registerOre("cropPumpkin", Blocks.PUMPKIN);
		OreDictionary.registerOre("cropMelon", Blocks.MELON_BLOCK);
		OreDictionary.registerOre("blockClay", Blocks.CLAY);
		// Shirt recipe
		ResourceLocation name = new ResourceLocation(AllTheDrops.NAME);
		ForgeRegistries.RECIPES.register(new ShapelessOreRecipe(name, new ItemStack(ModItems.BAUBLE_SHIRT, 1, 0),
				"stick", "woolWhite", "dyeBlack").setRegistryName("shirt"));
		// Shirts conversion recipe
		for (EnumShirt shirt : EnumShirt.values()) {
			EnumShirt nextShirt = EnumShirt.getShirtFromMeta(shirt.getMeta() + 1);
			ForgeRegistries.RECIPES.register(new ShapelessOreRecipe(name, new ItemStack(ModItems.BAUBLE_SHIRT, 1, shirt.getMeta()),
					new ItemStack(ModItems.BAUBLE_SHIRT, 1, nextShirt.getMeta()))
					.setRegistryName(String.format("shirt.%s", shirt.getUnlocalizedName())));
		}
		// Charm Recipe
		ForgeRegistries.RECIPES.register(new ShapelessOreRecipe(name, new ItemStack(ModItems.BAUBLE_CHARM, 1, 0),
				"blockGlass", "stone").setRegistryName("charm"));
	}
}
