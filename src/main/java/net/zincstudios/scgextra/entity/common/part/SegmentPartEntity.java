package net.zincstudios.scgextra.entity.common.part;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.entity.PartEntity;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class SegmentPartEntity <T extends LivingEntity> extends PartEntity<T> {

    private final EntityDimensions size;
    private final boolean collision;

    public SegmentPartEntity(T parent, float width, float height) {
        this(parent, width, height, true);
    }

    public SegmentPartEntity(T parent, float width, float height, boolean collision) {
        super(parent);
        this.size = EntityDimensions.fixed(width, height);
        this.refreshDimensions();
        this.collision = collision;
    }

    @Override
    public EntityDimensions getDimensions(Pose pPose) {
        return this.size;
    }

    @Override
    protected AABB getBoundingBoxForPose(Pose pPose) {
        return this.size.makeBoundingBox(this.getX(), this.getY(), this.getZ());
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return !this.isInvulnerableTo(source) && this.getParent().hurt(source, amount);
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Nullable
    public ItemStack getPickResult() {
        return this.getParent().getPickResult();
    }

    @Override
    public boolean is(Entity entity) {
        return this == entity || this.getParent() == entity;
    }

    @Override
    public boolean shouldBeSaved() {
        return false;
    }

    @Override
    public boolean canBeCollidedWith() {
        return this.collision && getParent().isAlive();
    }

    @Override
    public PushReaction getPistonPushReaction() {
        return PushReaction.IGNORE;
    }

    @Override
    public UUID getUUID() {
        return this.getParent().getUUID();
    }

    @Override
    public String getStringUUID() {
        return this.getParent().getStringUUID();
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
    }

}
