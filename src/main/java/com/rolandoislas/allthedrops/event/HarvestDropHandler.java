package com.rolandoislas.allthedrops.event;

import com.rolandoislas.allthedrops.data.Config;
import com.rolandoislas.allthedrops.registry.ModItems;
import net.minecraft.block.BlockMelon;
import net.minecraft.block.BlockNetherWart;
import net.minecraft.block.BlockPumpkin;
import net.minecraft.block.BlockRedstoneOre;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Random;

/**
 * Created by Rolando on 2/21/2017.
 */
public class HarvestDropHandler {
	private static final String[] resourceBlocks = new String[]{
			"logWood",
			"treeLeaves",
			"vine", // TODO vines are not dropping
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

	public static boolean isResourceBlock(IBlockState state) {
		ItemStack itemStack;
		try {
			itemStack = getItemStackFromState(state);
		} catch (NoItemException e) {
			itemStack = e.getDrop();
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

	public static ItemStack getItemStackFromState(IBlockState state) throws NoItemException {
		// Get item from block
		ItemStack itemStack = new ItemStack(state.getBlock());
		// Block does not have an item
		if (itemStack.getItem() == Items.AIR) {
			// Redstone has odd states
			if (state.getBlock() instanceof BlockRedstoneOre)
				itemStack = new ItemStack(Blocks.REDSTONE_ORE);
			// Netherwart
			else if (state.getBlock() instanceof BlockNetherWart)
				itemStack = new ItemStack(Items.NETHER_WART);
			else if (state.getBlock() instanceof BlockMelon)
				itemStack = new ItemStack(Items.MELON);
			else if (state.getBlock() instanceof BlockPumpkin)
				itemStack = new ItemStack(Blocks.PUMPKIN);
			else {
				ItemStack drop = state.getBlock().getItemDropped(state, new Random(),
						Integer.MAX_VALUE).getDefaultInstance();
				throw new NoItemException(drop);
			}
		}
		return itemStack;
	}

	public static String[] getResourceBlocks() {
		return resourceBlocks;
	}

	public static class NoItemException extends Exception {
		private ItemStack drop;

		public NoItemException(ItemStack drop) {
			this.drop = drop;
		}

		public ItemStack getDrop() {
			return (drop == null || drop.getItem() == Items.AIR)
					? ModItems.DEV_TOOL.getDefaultInstance() : drop;
		}
	}
}
