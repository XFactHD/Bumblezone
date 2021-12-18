package com.telepathicgrunt.the_bumblezone.capabilities;

import net.minecraft.nbt.CompoundTag;


public class EntityFlyingSpeed implements EntityFlyingSpeed {
	private float originalFlyingSpeed = 0.02f;

	@Override
	public void setOriginalFlyingSpeed(float originalFlyingSpeed) {
		this.originalFlyingSpeed = originalFlyingSpeed;
	}

	@Override
	public float getOriginalFlyingSpeed() {
		return originalFlyingSpeed;
	}

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag nbt = new CompoundTag();
		nbt.putFloat("original_flying_speed", this.getOriginalFlyingSpeed());
		return nbt;
	}

	@Override
	public void deserializeNBT(CompoundTag nbtTag) {
		this.setOriginalFlyingSpeed(nbtTag.getFloat("original_flying_speed"));
	}
}