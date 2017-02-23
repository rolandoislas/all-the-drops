package com.rolandoislas.allthedrops.event;

import com.rolandoislas.allthedrops.AllTheDrops;
import com.rolandoislas.allthedrops.data.Config;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockRedstoneOre;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.oredict.OreDictionary;

/**
 * Created by Rolando on 2/21/2017.
 */
public class HarvestDropHandler {
	private static final String[] resourceBlocks = new String[]{
			"logWood",
			"treeLeaves",
			"vine",
			"ore",
			"crop",
			"sugarcane",
			"blockCactus",
			"obsidian",
			"glowstone"
	};
	private static final String[] commonResourceBlocks = new String[]{
			"dirt",
			"gravel",
			"sand",
			"grass",
			"stone",
			"cobblestone",
			"sandstone",
			"netherrack",
			"endstone"
	};

	public static void harvestBlockDropEvent(BlockEvent.HarvestDropsEvent event) {
		// All the Drops!
		if (Config.guaranteeDrops) {
			event.getDrops().clear();
			event.getDrops().addAll(event.getState().getBlock()
					.getDrops(event.getWorld(), event.getPos(), event.getState(), 3));
		}
		// Multiply drops
		if (isResourceBlock(event.getState()))
			for (ItemStack drop : event.getDrops())
				drop.setCount(drop.getCount() * Config.dropMultiplier);
	}

	private static boolean isResourceBlock(IBlockState state) {
		ItemStack itemStack = new ItemStack(state.getBlock());
		if (itemStack.getItem() == Items.AIR) {
			if (state.getBlock() instanceof BlockRedstoneOre)
				return true;
			else if(state.getBlock() instanceof BlockCrops) {
				BlockCrops crop = (BlockCrops) state.getBlock();
				return crop.isMaxAge(state);
			}
			else if (state.getBlock() instanceof IPlantable)
				return true;
			AllTheDrops.logger.debug("Block Drop: Could not get itemstack for block: " +
					state.getBlock().getUnlocalizedName());
			AllTheDrops.logger.debug("Block Drop: Could not handle block based on instance: " +
					state.getBlock().getClass().getCanonicalName());
			return false;
		}
		int[] ids = OreDictionary.getOreIDs(itemStack);
		for (int id : ids) {
			for (String dictName : Config.resourceBlocks)
				if (OreDictionary.getOreName(id).contains(dictName))
					return true;
			if (Config.commonBlockDrops)
				for (String dictName : commonResourceBlocks)
					if (OreDictionary.getOreName(id).contains(dictName))
						return true;
		}
		return false;
	}

	public static String[] getResourceBlocks() {
		return resourceBlocks;
	}
}
