package net.zincstudios.scgextra.entity.turret;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;
import net.zincstudios.scgextra.block.wreckerturret.WreckerTurretBlockEntity;
import net.zincstudios.scgextra.entity.ModEntities;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TurretSeatEntity extends Entity implements IEntityAdditionalSpawnData {

    private BlockPos turretPos = BlockPos.ZERO;

    public TurretSeatEntity(EntityType<? extends TurretSeatEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.setNoGravity(true);
        this.setInvisible(true);
    }

    public static TurretSeatEntity spawn(Level level, BlockPos turretPos, double x, double y, double z, float yaw) {
        TurretSeatEntity seat = new TurretSeatEntity(ModEntities.TURRET_SEAT.get(), level);
        seat.turretPos = turretPos.immutable();
        seat.moveTo(x, y, z, yaw, 0.0F);
        level.addFreshEntity(seat);
        return seat;
    }

    public BlockPos getTurretPos() {
        return this.turretPos;
    }

    @Override
    public void tick() {
        if (!this.getPassengers().isEmpty() && this.getPassengers().get(0) instanceof LivingEntity entity) {
            Vec3 offset = new Vec3(0, 0, -0.75);
            Vec3 pos = Vec3.atBottomCenterOf(this.getTurretPos());
            this.setPos(pos.add(offset.yRot(-entity.yHeadRot * Mth.DEG_TO_RAD)));
        } else {
            this.discard();
            return;
        }

        if (this.level().isClientSide()) return;

        if (!(this.level().getBlockEntity(this.turretPos) instanceof WreckerTurretBlockEntity)) {
            this.ejectPassengers();
            this.discard();
        }
    }

    @Override
    public void remove(RemovalReason reason) {
        if (!this.level().isClientSide()
                && this.level().getBlockEntity(this.turretPos) instanceof WreckerTurretBlockEntity turret) {
            turret.onSeatRemoved(this);
        }
        super.remove(reason);
    }

    @Override
    protected void positionRider(Entity passenger, MoveFunction move) {
        if (this.hasPassenger(passenger)) {
            move.accept(passenger, this.getX(), this.getY(), this.getZ());
        }
    }

    @Override
    public boolean shouldRiderSit() {
        return false;
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    public boolean hurt(net.minecraft.world.damagesource.DamageSource source, float amount) {
        return false;
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        this.turretPos = BlockPos.of(tag.getLong("TurretPos"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putLong("TurretPos", this.turretPos.asLong());
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(this.turretPos);
    }

    @Override
    public void readSpawnData(FriendlyByteBuf additionalData) {
        this.turretPos = additionalData.readBlockPos();
    }
}
