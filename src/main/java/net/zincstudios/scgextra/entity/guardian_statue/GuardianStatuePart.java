package net.zincstudios.scgextra.entity.guardian_statue;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.entity.PartEntity;
import net.minecraft.world.entity.Pose;

public class GuardianStatuePart extends PartEntity<GuardianStatueEntity>{
    public final GuardianStatueEntity parentMob;
    public final String name;
    private final EntityDimensions size;
    public GuardianStatuePart(GuardianStatueEntity parent, String pName, float pWidth, float pHeight) {
        super(parent);
        this.size = EntityDimensions.fixed(pWidth, pHeight);
        this.refreshDimensions();
        this.parentMob = parent;
        this.name = pName;
    }

    protected void defineSynchedData() {
    }

    protected void readAdditionalSaveData(CompoundTag pCompound) {
    }

    protected void addAdditionalSaveData(CompoundTag pCompound) {
    }

    public boolean isPickable() {
        return true;
    }

    public ItemStack getPickResult() {
        return this.parentMob.getPickResult();
    }

    public boolean hurt(DamageSource pSource, float pAmount) {
        if(this.name.equals("eye")){
            return this.isInvulnerableTo(pSource) ? false : this.parentMob.hurt(pSource, pAmount*2);
        }
        return this.isInvulnerableTo(pSource) ? false : this.parentMob.hurt(pSource, pAmount);
    }

    public boolean is(Entity pEntity) {
        return this == pEntity || this.parentMob == pEntity;
    }

    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return super.getAddEntityPacket();
    }

    public boolean shouldBeSaved() {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        this.refreshDimensions();
    }

    @Override
    public GuardianStatueEntity getParent() {
        return this.parentMob;
    }

    @Override
    public Entity getVehicle() {
        return this.parentMob;
    }
    @Override
    protected AABB getBoundingBoxForPose(Pose pPose) {
        return this.size.makeBoundingBox(this.getX(), this.getY(), this.getZ());
    }
    @Override
    public AABB getBoundingBoxForCulling() {
        return this.size.makeBoundingBox(this.getX(), this.getY(), this.getZ());
    }
    public EntityDimensions getSize() {
        return this.size;
    }
    public GuardianStatueEntity getParentMob() {
        return this.parentMob;
    }
    @Override
    public EntityDimensions getDimensions(Pose pPose) {
        return this.size;
    }
    @Override
    public boolean isPushable() {
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
}
