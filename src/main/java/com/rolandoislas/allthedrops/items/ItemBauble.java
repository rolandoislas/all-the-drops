package com.rolandoislas.allthedrops.items;

import baubles.api.IBauble;
import baubles.api.render.IRenderBauble;
import com.rolandoislas.allthedrops.AllTheDrops;
import com.rolandoislas.allthedrops.data.Config;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by Rolando on 2/23/2017.
 */
@Optional.InterfaceList({
		@Optional.Interface(modid = Config.BAUBLES_MODID, iface = "baubles.api.IBauble"),
		@Optional.Interface(modid = Config.BAUBLES_MODID, iface = "baubles.api.render.IRenderBauble")
})
abstract class ItemBauble extends Item implements IBauble, IRenderBauble {
	private ResourceLocation modelTexture;

	public ItemBauble() {
		super();
		this.setMaxStackSize(1);
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		String prefix = getUnlocalizedName(stack);
		tooltip.add(I18n.format(prefix + ".info.0"));
		tooltip.add(I18n.format(prefix + ".info.1"));
	}

	void setModelTexture(String name) {
		modelTexture = new ResourceLocation(AllTheDrops.MODID, "textures/models/" + name + ".png");
	}

	ResourceLocation getModelTexture() {
		return modelTexture;
	}
}
