package com.rolandoislas.allthedrops.items;

import baubles.api.BaubleType;
import com.rolandoislas.allthedrops.AllTheDrops;
import com.rolandoislas.allthedrops.registry.ModCreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * Created by Rolando on 2/25/2017.
 */
public class ItemBaubleCharm extends ItemBauble {
	public ItemBaubleCharm() {
		super();
		this.setUnlocalizedName(AllTheDrops.MODID + ".bauble_charm");
		this.setRegistryName(AllTheDrops.MODID, "bauble_charm");
		this.setCreativeTab(ModCreativeTabs.MAIN);
	}

	@Override
	public BaubleType getBaubleType(ItemStack itemStack) {
		return BaubleType.CHARM;
	}

	@Override
	public void onPlayerBaubleRender(ItemStack itemStack, EntityPlayer entityPlayer, RenderType renderType, float v) {

	}
}
