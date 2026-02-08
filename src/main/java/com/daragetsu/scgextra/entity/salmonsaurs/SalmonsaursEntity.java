package com.daragetsu.scgextra.entity.salmonsaurs;


import com.daragetsu.scgextra.entity.ModEntities;
import com.daragetsu.scgextra.entity.fishfolk.FishFolkEntity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.level.Level;

public class SalmonsaursEntity extends Hoglin{
    private boolean riderSpawned = false;
    public SalmonsaursEntity(EntityType<? extends SalmonsaursEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }
    public static AttributeSupplier.Builder createAttributes() {
      return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 40.0D)
      .add(Attributes.MOVEMENT_SPEED, (double)0.3F)
      .add(Attributes.KNOCKBACK_RESISTANCE, (double)0.6F)
      .add(Attributes.ATTACK_KNOCKBACK, 1.0D)
      .add(Attributes.ATTACK_DAMAGE, 6.0D);
    }
    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide() && !riderSpawned) {
            FishFolkEntity rider = new FishFolkEntity(ModEntities.FISH_FOLK.get(), this.level());
            rider.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
            this.level().addFreshEntity(rider);
            rider.startRiding(this, true);
            riderSpawned = true;
        }
    }
    @Override
    public boolean isConverting() {
        return false;
    }
}
