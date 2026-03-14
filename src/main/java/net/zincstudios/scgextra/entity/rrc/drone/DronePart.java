package net.zincstudios.scgextra.entity.rrc.drone;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.entity.PartEntity;

public class DronePart extends PartEntity<DroneEntity>{
    public final DroneEntity parentMob;
    public final String name;
    private final EntityDimensions size;
    public DronePart(DroneEntity parent, String pName, float pWidth, float pHeight) {
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
        if(this.name.equals("back") || this.name.equals("pipe")){
            return this.isInvulnerableTo(pSource) ? false : this.parentMob.hurt(pSource, pAmount*1.5F);
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
    public DroneEntity getParent() {
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
    public DroneEntity getParentMob() {
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
