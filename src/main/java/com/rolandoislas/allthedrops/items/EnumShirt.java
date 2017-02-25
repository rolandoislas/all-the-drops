package com.rolandoislas.allthedrops.items;

import net.minecraft.util.IStringSerializable;

/**
 * Created by Rolando on 2/24/2017.
 */
public enum EnumShirt implements IStringSerializable {
	ALL(0),
	ALL_100(1),
	ENTITY(2),
	ENTITY_100(3),
	BLOCKS(4);

	private final int meta;

	EnumShirt(int meta) {
		this.meta = meta;
	}

	@Override
	public String getName() {
		return this.name().toLowerCase();
	}

	public static EnumShirt getShirtFromMeta(int meta) {
		for (EnumShirt food : values())
			if (food.getMeta() == meta)
				return food;
		return ALL;
	}

	public int getMeta() {
		return meta;
	}

	public String getUnlocalizedName() {
		return getName();
	}
}

