package net.zincstudios.scgextra.entity.projectile;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.zincstudios.scgextra.entity.ModEntities;
import net.zincstudios.scgextra.item.ModItems;
import top.ribs.scguns.init.ModParticleTypes;

import javax.annotation.ParametersAreNonnullByDefault;

// Copy of LargeFireball
// TODO: copy DragonFireball instead
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class LargeSoulFireball extends Fireball {

    private int explosionPower = 1;

    public LargeSoulFireball(EntityType<LargeSoulFireball> entityType, Level level) {
        super(entityType, level);
    }

    public LargeSoulFireball(Level level, LivingEntity shooter, double offsetX, double offsetY, double offsetZ, int explosionPower) {
        super(ModEntities.LARGE_SOUL_FIREBALL.get(), shooter, offsetX, offsetY, offsetZ, level);
        this.explosionPower = explosionPower;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level() instanceof ClientLevel level) {
            if (this.tickCount % 2 == 0) {
                level.addParticle(
                        ParticleTypes.SOUL_FIRE_FLAME,
                        this.position().x + (level.getRandom().nextDouble() - 0.5) * 0.5,
                        this.position().y + (level.getRandom().nextDouble() - 0.5) * 0.5,
                        this.position().z + (level.getRandom().nextDouble() - 0.5) * 0.5,
                        (level.getRandom().nextDouble() - 0.5) * 0.05,
                        (level.getRandom().nextDouble() - 0.5) * 0.05,
                        (level.getRandom().nextDouble() - 0.5) * 0.05
                );

                level.addParticle(
                        ParticleTypes.SMOKE,
                        this.position().x + (level.getRandom().nextDouble() - 0.5) * 0.5,
                        this.position().y + (level.getRandom().nextDouble() - 0.5) * 0.5,
                        this.position().z + (level.getRandom().nextDouble() - 0.5) * 0.5,
                        (level.getRandom().nextDouble() - 0.5) * 0.05,
                        (level.getRandom().nextDouble() - 0.5) * 0.05,
                        (level.getRandom().nextDouble() - 0.5) * 0.05
                );
            }

            if (this.tickCount % 3 == 0) {
                level.addParticle(
                        ModParticleTypes.SOUL_FIREBALL.get(),
                        this.position().x + (level.getRandom().nextDouble() - 0.5) * 0.5,
                        this.position().y + (level.getRandom().nextDouble() - 0.5) * 0.5,
                        this.position().z + (level.getRandom().nextDouble() - 0.5) * 0.5,
                        + (level.getRandom().nextDouble() - 0.5) * 0.05,
                        + (level.getRandom().nextDouble() - 0.5) * 0.05,
                        + (level.getRandom().nextDouble() - 0.5) * 0.05
                );
            }
        }


    }

    /**
     * Called when this EntityFireball hits a block or entity.
     */
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!this.level().isClientSide) {
            // TODO 1.19.3: The creation of Level.ExplosionInteraction means this code path will fire EntityMobGriefingEvent twice. Should we try and fix it? -SS
            boolean flag = net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.level(), this.getOwner());
            this.level().explode(this, this.getX(), this.getY(), this.getZ(), (float)this.explosionPower, flag, Level.ExplosionInteraction.MOB);
            this.discard();
        }

    }

    /**
     * Called when the arrow hits an entity
     */
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (!this.level().isClientSide) {
            Entity entity = result.getEntity();
            Entity entity1 = this.getOwner();
            entity.hurt(this.damageSources().fireball(this, entity1), 6.0F);
            if (entity1 instanceof LivingEntity) {
                this.doEnchantDamageEffects((LivingEntity)entity1, entity);
            }

        }
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putByte("ExplosionPower", (byte)this.explosionPower);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("ExplosionPower", 99)) {
            this.explosionPower = compound.getByte("ExplosionPower");
        }
    }

    public ItemStack getItem() {
        ItemStack itemstack = this.getItemRaw();
        return itemstack.isEmpty() ? new ItemStack(ModItems.SOUL_FIRE_CHARGE.get()) : itemstack;
    }

    protected boolean shouldBurn() {
        return false;
    }

}
