package net.zincstudios.scgextra.entity.whaler.armoredwhale;

import com.mojang.blaze3d.vertex.PoseStack.Pose;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.entity.PartEntity;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.UUID;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ArmoredWhalePart extends PartEntity<ArmoredWhaleEntity>{
    public final ArmoredWhaleEntity parentMob;
    public final String name;
    private final EntityDimensions size;
    public ArmoredWhalePart(ArmoredWhaleEntity parent, String pName, float pWidth, float pHeight) {
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
        return Objects.requireNonNull(this.parentMob.getPickResult());
    }

    public boolean hurt(DamageSource pSource, float pAmount) {
        if(this.name.equals("gem")){
            return !this.isInvulnerableTo(pSource) && this.parentMob.hurt(pSource, pAmount * 2);
        }
        return !this.isInvulnerableTo(pSource) && this.parentMob.hurt(pSource, pAmount);
    }

    public boolean is(Entity pEntity) {
        return this == pEntity || this.parentMob == pEntity;
    }

    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return super.getAddEntityPacket();
    }

    @SuppressWarnings("unused")
    public EntityDimensions getDimensions(Pose pPose) {
        return this.size;
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
    public ArmoredWhaleEntity getParent() {
        return this.parentMob;
    }

    @Override
    public Entity getVehicle() {
        return this.parentMob;
    }

    @Override
    protected AABB getBoundingBoxForPose(net.minecraft.world.entity.Pose pPose) {
        return this.size.makeBoundingBox(this.getX(), this.getY(), this.getZ());
    }

    @Override
    public AABB getBoundingBoxForCulling() {
        return this.size.makeBoundingBox(this.getX(), this.getY(), this.getZ());
    }

    public EntityDimensions getSize() {
        return this.size;
    }

    @SuppressWarnings("unused")
    public ArmoredWhaleEntity getParentMob() {
        return this.parentMob;
    }

    @Override
    public EntityDimensions getDimensions(net.minecraft.world.entity.Pose pPose) {
        return this.size;
    }
    //giving the parent's UUID makes it so any commands that target this always get's the parent
    @Override
    public UUID getUUID() {
        return this.parentMob.getUUID();
    }
    @Override
    public String getStringUUID() {
        return this.parentMob.getStringUUID();
    }
}