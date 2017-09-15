package com.rolandoislas.allthedrops.items;

import baubles.api.BaubleType;
import com.rolandoislas.allthedrops.AllTheDrops;
import com.rolandoislas.allthedrops.registry.ModCreativeTabs;
import com.rolandoislas.allthedrops.registry.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

/**
 * Created by Rolando on 2/23/2017.
 */
public class ItemBaubleShirt extends ItemBauble {
	private final ModelPlayer model;

	public ItemBaubleShirt() {
		super();
		this.setUnlocalizedName(AllTheDrops.MODID + ".bauble_shirt");
		this.setRegistryName(AllTheDrops.MODID, "bauble_shirt");
		this.setModelTexture("bauble_shirt");
		this.setCreativeTab(ModCreativeTabs.MAIN);
		this.setHasSubtypes(true);
		model = new ModelPlayer(1, false);
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
		for (EnumShirt shirt : EnumShirt.values())
			subItems.add(new ItemStack(ModItems.BAUBLE_SHIRT, 1, shirt.getMeta()));
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return super.getUnlocalizedName() + "." + EnumShirt.getShirtFromMeta(stack.getMetadata()).getUnlocalizedName();
	}

	@Override
	public BaubleType getBaubleType(ItemStack itemStack) {
		return BaubleType.BODY;
	}

	@Override
	public void onPlayerBaubleRender(ItemStack itemStack, EntityPlayer entityPlayer, RenderType renderType, float v) {
		if (renderType != RenderType.BODY)
			return;
		Minecraft.getMinecraft().renderEngine.bindTexture(getModelTexture());
		Helper.rotateIfSneaking(entityPlayer);
		GlStateManager.translate(0, 0, 0);
		float scale = 1f / 16f;
		GlStateManager.scale(scale, scale, scale);
		GlStateManager.enableAlpha();
		model.setRotationAngles(entityPlayer.limbSwing, entityPlayer.limbSwingAmount, entityPlayer.ticksExisted,
				entityPlayer.cameraYaw, entityPlayer.cameraPitch, 1, entityPlayer);
		model.setLivingAnimations(entityPlayer, entityPlayer.limbSwing, entityPlayer.limbSwingAmount,
				entityPlayer.ticksExisted);
		model.bipedBody.render(1);
		model.bipedLeftArm.render(1);
		model.bipedRightArm.render(1);
	}
}
