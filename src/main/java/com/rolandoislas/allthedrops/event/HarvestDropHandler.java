package com.rolandoislas.allthedrops.event;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import com.rolandoislas.allthedrops.data.Config;
import com.rolandoislas.allthedrops.items.EnumShirt;
import com.rolandoislas.allthedrops.registry.ModItems;
import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
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
			"ore*",
			"crop*",
			"sugarcane",
			"blockCactus",
			"obsidian",
			"glowstone",
			"blockClay"
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
		// Check for liquid
		if (event.getState().getBlock() instanceof BlockLiquid)
			return;
		// Make sure the harvester is a player
		if (event.getHarvester() == null || !(event.getHarvester() instanceof EntityPlayer))
			return;
		// Check baubles
		boolean normalBlockDrops = true;
		boolean commonBlockDrops = Config.commonBlockDrops;
		if (Config.enableBaubles) {
			normalBlockDrops = false;
			commonBlockDrops = false;
			IBaublesItemHandler handler = BaublesApi.getBaublesHandler(event.getHarvester());
			boolean found = false;
			for (int slot = 0; slot < handler.getSlots(); slot++) {
				if (ItemStack.areItemsEqual(handler.getStackInSlot(slot),
						new ItemStack(ModItems.BAUBLE_SHIRT, 1, EnumShirt.ALL.getMeta()))) {
					found = true;
					normalBlockDrops = true;
				}
				if (ItemStack.areItemsEqual(handler.getStackInSlot(slot),
						new ItemStack(ModItems.BAUBLE_SHIRT, 1, EnumShirt.ALL_100.getMeta()))) {
					found = true;
					normalBlockDrops = true;
				}
				if (ItemStack.areItemsEqual(handler.getStackInSlot(slot),
						new ItemStack(ModItems.BAUBLE_SHIRT, 1, EnumShirt.BLOCKS.getMeta()))) {
					found = true;
					normalBlockDrops = true;
				}
				if (ItemStack.areItemsEqual(handler.getStackInSlot(slot),
						new ItemStack(ModItems.BAUBLE_CHARM, 1, 0))) {
					found = true;
					commonBlockDrops = Config.commonBlockDrops;
				}
			}
			if (!found)
				return;
		}
		// Multiply drops
		if (isResourceBlock(event.getState(), normalBlockDrops, commonBlockDrops))
			for (ItemStack drop : event.getDrops())
				drop.setCount(drop.getCount() * Config.dropMultiplier);
	}

	public static boolean isResourceBlock(IBlockState state, boolean normalBlockDrops, boolean commonBlockDrops) {
		ItemStack itemStack;
		try {
			itemStack = getItemStackFromState(state);
		} catch (NoItemException e) {
			itemStack = e.getDrop();
		}
		int[] ids = OreDictionary.getOreIDs(itemStack);
		for (int id : ids) {
			if (normalBlockDrops)
				for (String dictName : Config.resourceBlocks)
					if (isOreNameValid(OreDictionary.getOreName(id), dictName))
						return true;
			if (commonBlockDrops)
				for (String dictName : commonResourceBlocks)
					if (isOreNameValid(OreDictionary.getOreName(id), dictName))
						return true;
		}
		return false;
	}

	private static boolean isOreNameValid(String oreName, String dictName) {
		String dictNameStripped = dictName.replace("*", "");
		// Match *something*
		if (dictName.startsWith("*") && oreName.endsWith("*"))
			return oreName.contains(dictNameStripped);
		// Match *something
		if (dictName.startsWith("*"))
			return oreName.endsWith(dictNameStripped);
		// Match something*
		if (dictName.endsWith("*"))
			return oreName.startsWith(dictNameStripped);
		// Match "something"
		return oreName.equals(dictNameStripped);
	}

	public static ItemStack getItemStackFromState(IBlockState state) throws NoItemException {
		// Get item from block
		ItemStack itemStack = new ItemStack(state.getBlock(), 1, state.getBlock().damageDropped(state));
		// Block does not have an item
		if (itemStack.getItem() == Items.AIR) {
			// Redstone has odd states
			if (state.getBlock() instanceof BlockRedstoneOre)
				itemStack = new ItemStack(Blocks.REDSTONE_ORE);
			// Netherwart
			else if (state.getBlock() instanceof BlockNetherWart)
				itemStack = new ItemStack(Items.NETHER_WART);
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
