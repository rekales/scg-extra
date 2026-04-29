package net.zincstudios.scgextra.entity.asgharian.soulripper;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.PartEntity;
import net.zincstudios.scgextra.SCGExtra;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class LanternPartEntity extends PartEntity<SoulRipperEntity> {

    private final EntityDimensions size;
    private final Vec3 offset;

    public LanternPartEntity(SoulRipperEntity parent, Vec3 offset , float width, float height) {
        super(parent);
        this.size = EntityDimensions.fixed(width, height);
        this.refreshDimensions();
        this.offset = offset;
    }

    public void updatePos() {
        this.setOldPosAndRot();
//        Vec3 tempOffset = new Vec3(-1.25, 2.3, -0.025);
//        Vec3 tempOffset = new Vec3(0.725, 2.25, -0.2);
//        Vec3 tempOffset = new Vec3(0, 2.825, -0.275);
        this.setPos(this.getParent().position().add(this.offset.yRot(-this.getParent().yBodyRot * Mth.DEG_TO_RAD)));
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
        return !this.isInvulnerableTo(source) && this.getParent().hurt(this, source, amount);
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
        return this.isAlive();
    }
    @Override
    public PushReaction getPistonPushReaction() {
        return PushReaction.IGNORE;
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
