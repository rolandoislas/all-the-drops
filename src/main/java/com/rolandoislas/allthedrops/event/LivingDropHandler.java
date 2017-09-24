package com.rolandoislas.allthedrops.event;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import com.google.common.collect.Lists;
import com.rolandoislas.allthedrops.AllTheDrops;
import com.rolandoislas.allthedrops.data.Config;
import com.rolandoislas.allthedrops.items.EnumShirt;
import com.rolandoislas.allthedrops.registry.ModItems;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.*;
import net.minecraft.world.storage.loot.conditions.LootConditionManager;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraft.world.storage.loot.functions.SetCount;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by Rolando on 2/21/2017.
 *
 * Note: MCP Mapping Viewer for MCP to SRG names
 *   http://www.minecraftforum.net/forums/mapping-and-modding/minecraft-tools/1265548-mcp-mapping-viewer
 */
public class LivingDropHandler {
	public static void livingEntityDropEvent(LivingDropsEvent event) {
		// Ignore player death/drops
		if (event.getEntity() instanceof EntityPlayer)
			return;
		// Respect the mob loot gamerule
		if (!event.getEntityLiving().world.getGameRules().getBoolean("doMobLoot"))
			return;
		// Ignore if death was not caused by a player
		if (Config.requirePlayerKill && !(event.getSource().getEntity() instanceof EntityPlayer))
			return;
		// Check baubles
		boolean guaranteeDrops = Config.guaranteeMobDrops;
		if (Config.enableBaubles && event.getSource().getEntity() instanceof EntityPlayer) {
			IBaublesItemHandler handler = BaublesApi.getBaublesHandler(
					(EntityPlayer) event.getSource().getEntity());
			boolean found = false;
			for (int slot = 0; slot < handler.getSlots(); slot++) {
				if (ItemStack.areItemsEqual(handler.getStackInSlot(slot),
						new ItemStack(ModItems.BAUBLE_SHIRT, 1, EnumShirt.ALL.getMeta()))) {
					found = true;
					guaranteeDrops = false;
				}
				if (ItemStack.areItemsEqual(handler.getStackInSlot(slot),
						new ItemStack(ModItems.BAUBLE_SHIRT, 1, EnumShirt.ALL_100.getMeta())))
					found = true;
				if (ItemStack.areItemsEqual(handler.getStackInSlot(slot),
						new ItemStack(ModItems.BAUBLE_SHIRT, 1, EnumShirt.ENTITY.getMeta()))) {
					found = true;
					guaranteeDrops = false;
				}
				if (ItemStack.areItemsEqual(handler.getStackInSlot(slot),
						new ItemStack(ModItems.BAUBLE_SHIRT, 1, EnumShirt.ENTITY_100.getMeta())))
					found = true;
			}
			if (!found)
				return;
		}
		// All the Drops!
		EntityLiving entity = (EntityLiving) event.getEntityLiving();
		if (guaranteeDrops) {
			// Try to get loot table from field
			ResourceLocation resourcelocation = ReflectionHelper.getPrivateValue(EntityLiving.class, entity,
					"deathLootTable", "field_184659_bA");
			// Try to get loot table from method call
			if (resourcelocation == null) {
				for (String methodName : new String[]{"getLootTable", "func_184647_J"}) {
					try {
						Method getLootTable = entity.getClass().getDeclaredMethod(methodName);
						getLootTable.setAccessible(true);
						resourcelocation = (ResourceLocation) getLootTable.invoke(entity);
						break;
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					} catch (NoSuchMethodException e) {
						AllTheDrops.logger.debug(Arrays.toString(e.getStackTrace()));
					}
				}
			}
			// Populate drops
			if (resourcelocation != null) {
				event.getDrops().clear();
				// Add drops
				event.getDrops().addAll(getAllDrops(event, resourcelocation));
				// Add Equipment
				for (ItemStack itemStack : entity.getEquipmentAndArmor())
					event.getDrops().add(new EntityItem(entity.world, entity.getPosition().getX(),
							entity.getPosition().getY(), entity.getPosition().getZ(),
							itemStack));
			}
		}
		ArrayList<ItemStack> equipment = Lists.newArrayList(entity.getEquipmentAndArmor());
		// Multiply drops
		for (EntityItem entityItem : event.getDrops())
			if (!equipment.contains(entityItem.getEntityItem()))
				entityItem.getEntityItem().setCount(entityItem.getEntityItem().getCount() * Config.dropMultiplier);
	}

	private static List<EntityItem> getAllDrops(LivingDropsEvent event, LootEntryTable lootEntryTable) {
		ResourceLocation table = ReflectionHelper.getPrivateValue(LootEntryTable.class, lootEntryTable,
				"table", "field_186371_a");
		return getAllDrops(event, table);
	}

	private static List<EntityItem> getAllDrops(LivingDropsEvent event, ResourceLocation resourceLocation) {
		List<EntityItem> drops = new ArrayList<EntityItem>();
		EntityLivingBase entity = event.getEntityLiving();
		LootTable lootTable = event.getEntityLiving().world.getLootTableManager()
				.getLootTableFromLocation(resourceLocation);
		List<LootPool> pools = ReflectionHelper.getPrivateValue(LootTable.class, lootTable, "pools",
				"field_186466_c");
		LootContext lootcontext = new LootContext.Builder((WorldServer)entity.world).withLootedEntity(entity)
				.withDamageSource(event.getSource()).build();
		// Add all loot from loot tables
		for (LootPool pool : pools) {
			List<LootEntry> lootEntries = ReflectionHelper.getPrivateValue(LootPool.class, pool,
					"lootEntries", "field_186453_a");
			for (LootEntry entry : lootEntries) {
				if (!(entry instanceof LootEntryItem)) {
					if (entry instanceof LootEntryTable)
						drops.addAll(getAllDrops(event, (LootEntryTable)entry));
					continue;
				}
				Item item = ReflectionHelper.getPrivateValue(LootEntryItem.class, (LootEntryItem)entry,
						"item", "field_186368_a");
				LootFunction[] functions = ReflectionHelper.getPrivateValue(LootEntryItem.class,
						(LootEntryItem)entry, "functions", "field_186369_b");
				ItemStack itemstack = new ItemStack(item);
				// Apply loot functuions (damage, enchant, etc)
				Random rand = new Random();
				for (LootFunction function : functions) {
					if (LootConditionManager.testAllConditions(function.getConditions(), rand, lootcontext))
						if (function instanceof SetCount) {
							SetCount setCountFunction = (SetCount) function;
							RandomValueRange countRange = ReflectionHelper.getPrivateValue(SetCount.class,
									setCountFunction, "countRange", "field_186568_a");
							RandomValueRange maxCountRange = new RandomValueRange(countRange.getMax());
							ReflectionHelper.setPrivateValue(SetCount.class, setCountFunction,
									maxCountRange, "countRange", "field_186568_a");
							itemstack = setCountFunction.apply(itemstack, rand, lootcontext);
							ReflectionHelper.setPrivateValue(SetCount.class, setCountFunction,
									countRange, "countRange", "field_186568_a");
						}
						else
							itemstack = function.apply(itemstack, rand, lootcontext);
				}
				EntityItem entityItem = new EntityItem(entity.world, entity.getPosition().getX(),
						entity.getPosition().getY(), entity.getPosition().getZ(), itemstack);
				entityItem.setPickupDelay(10);
				drops.add(entityItem);
			}
		}
		return drops;
	}
}
