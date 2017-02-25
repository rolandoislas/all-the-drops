package com.rolandoislas.allthedrops.items;

import com.rolandoislas.allthedrops.AllTheDrops;
import com.rolandoislas.allthedrops.event.HarvestDropHandler;
import com.rolandoislas.allthedrops.registry.ModCreativeTabs;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

import static com.rolandoislas.allthedrops.event.HarvestDropHandler.getItemStackFromState;

/**
 * Created by Rolando on 2/23/2017.
 */
public class ItemDevTool extends Item {
	private String MESSAGE_PREFIX = "[" + AllTheDrops.MODID + "] ";

	public ItemDevTool() {
		this.setUnlocalizedName(AllTheDrops.MODID + ".devtool");
		this.setRegistryName(AllTheDrops.MODID, "devtool");
		this.setCreativeTab(ModCreativeTabs.MAIN);
		this.setFull3D();
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand,
									  EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (worldIn.isRemote) {
			EntityPlayerSP p = (EntityPlayerSP)player;
			IBlockState state = worldIn.getBlockState(pos);
			// Get item
			ItemStack itemStack;
			boolean itemDropFallback = false;
			try {
				itemStack = getItemStackFromState(state);
			} catch (HarvestDropHandler.NoItemException e) {
				itemDropFallback = true;
				itemStack = e.getDrop();
			}
			// Block info
			sendMessage(p, "Block Name: " + state.getBlock().getLocalizedName() + " | " +
					state.getBlock().getUnlocalizedName());
			sendMessage(p, "Class: " + state.getBlock().getClass().getName());
			if (player.isSneaking()) {
				Class blockClass = state.getBlock().getClass();
				while (blockClass != null) {
					sendMessage(p, blockClass.getName(), true);
					blockClass = blockClass.getSuperclass();
				}
			}
			sendMessage(p, "Resource Block: " +
					HarvestDropHandler.isResourceBlock(state, true, true));
			// Ore dict
			sendMessage(p, "Ore Dictionary: " + (itemDropFallback ?
					"<dropped item>" : ""));
			int[] ids = OreDictionary.getOreIDs(itemStack);
			if (ids.length == 0)
				sendMessage(p, "<none>", true);
			for (int id : ids)
				sendMessage(p, OreDictionary.getOreName(id), true);
		}
		return EnumActionResult.SUCCESS;
	}

	private void sendMessage(EntityPlayerSP player, String message) {
		sendMessage(player, message, false);
	}

	private void sendMessage(EntityPlayerSP player, String message, boolean tab) {
		String prefix = MESSAGE_PREFIX + (tab ? "  " : "");
		player.sendMessage(new TextComponentString(prefix + message));
	}
}
