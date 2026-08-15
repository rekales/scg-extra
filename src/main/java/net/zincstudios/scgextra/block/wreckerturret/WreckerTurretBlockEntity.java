package net.zincstudios.scgextra.block.wreckerturret;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.zincstudios.scgextra.block.ModBlockEntities;
import net.zincstudios.scgextra.entity.common.client.ExpandedAnimationController;
import net.zincstudios.scgextra.entity.turret.TurretAim;
import net.zincstudios.scgextra.entity.turret.TurretSeatEntity;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;
import top.ribs.scguns.common.AmmoContext;
import top.ribs.scguns.common.Gun;
import top.ribs.scguns.entity.projectile.turret.TurretProjectileEntity;
import top.ribs.scguns.init.ModItems;
import top.ribs.scguns.init.ModSounds;

import javax.annotation.ParametersAreNonnullByDefault;

// TODO: ammo type defined in constructor
// TODO: replace gun implementation with SimulatedGun
// TODO: muzzle flash position
@ParametersAreNonnullByDefault
public class WreckerTurretBlockEntity extends BlockEntity implements GeoBlockEntity {

    private static final int DISABLE_TICKS = 200;
    private static final int FIRE_COOLDOWN_TICKS = 3;
    private static final float DAMAGE = 10.0F;
    private static final float ARMOR_PENETRATION = 2.5F;
    private static final float PROJECTILE_SPEED = 3.0F;
    private static final float INACCURACY = 1.5F;
    private static final double MUZZLE_HEIGHT = 1.3D;
    private static final double MUZZLE_REACH = 0.8D;
    private static final double CONVERGENCE_RANGE = 48.0D;

    private static final RawAnimation IDLE = RawAnimation.begin().thenPlayAndHold("idle");
    private static final RawAnimation FIRE_LEFT = RawAnimation.begin().thenPlay("fire_left");
    private static final RawAnimation FIRE_RIGHT = RawAnimation.begin().thenPlay("fire_right");

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private int disabledTicks;
    private int fireCooldown;
    private int manningPlayerId = -1;
    private boolean triggerHeld;
    boolean firingLeft = false;

    // Client-side only, for use in renderer
    // made public cuz can't be assed to make getters
    public long leftFlashTick = 0;
    public long rightFlashTick = 0;

    public WreckerTurretBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.WRECKER_TURRET.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, WreckerTurretBlockEntity turret) {
        turret.tick(level, pos);
    }

    private void tick(Level level, BlockPos pos) {
        if (this.fireCooldown > 0) {
            this.fireCooldown--;
        }
        if (this.disabledTicks > 0) {
            this.disabledTicks--;
            if (this.disabledTicks % 5 == 0 && level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                        pos.getX() + 0.5D, pos.getY() + 1.2D, pos.getZ() + 0.5D, 2, 0.3D, 0.3D, 0.3D, 0.05D);
            }
        }
        if (this.manningPlayerId == -1) {
            return;
        }
        Entity e = level.getEntity(this.manningPlayerId);
        if (!(e instanceof ServerPlayer player) || !player.isAlive() || !this.isSeatedHere(player, pos)) {
            this.clearManning();
            return;
        }
        if (this.triggerHeld) {
            this.fire(player, pos);
        }
    }

    private boolean isSeatedHere(Player player, BlockPos pos) {
        return player.getVehicle() instanceof TurretSeatEntity seat && seat.getTurretPos().equals(pos);
    }

    public boolean startManning(ServerPlayer player) {
        if (this.level == null || this.level.isClientSide() || this.isManned()) {
            return false;
        }
        BlockPos pos = this.getBlockPos();
        Direction facing = this.getBlockState().getValue(WreckerTurretBlock.FACING);
        Direction back = facing.getOpposite();
        double x = pos.getX() + 0.5D + back.getStepX() * 0.9D;
        double z = pos.getZ() + 0.5D + back.getStepZ() * 0.9D;
        double y = pos.getY();
        float yaw = facing.toYRot();

        player.connection.teleport(x, y, z, yaw, 0.0F);
        TurretSeatEntity seat = TurretSeatEntity.spawn(this.level, pos, x, y, z, yaw);
        if (!player.startRiding(seat, true)) {
            seat.discard();
            return false;
        }
        this.manningPlayerId = player.getId();
        this.triggerHeld = false;
        return true;
    }

    public void stopManning(Player player) {
        if (player.getVehicle() instanceof TurretSeatEntity seat && seat.getTurretPos().equals(this.getBlockPos())) {
            player.stopRiding();
            seat.discard();
        }
        this.clearManning();
    }

    public void onSeatRemoved(TurretSeatEntity seat) {
        if (seat.getTurretPos().equals(this.getBlockPos())) {
            this.clearManning();
        }
    }

    private void clearManning() {
        this.manningPlayerId = -1;
        this.triggerHeld = false;
    }

    public void setTriggerHeld(Player player, boolean held) {
        if (this.manningPlayerId == player.getId()) {
            this.triggerHeld = held;
        }
    }

    private void fire(ServerPlayer player, BlockPos pos) {
        if (this.level == null || this.disabledTicks > 0 || this.fireCooldown > 0) {
            return;
        }

        AmmoContext ammoContext = Gun.findAmmo(player, ModItems.COMPACT_ADVANCED_ROUND.get());
        ItemStack ammoStack = ammoContext.stack();
        if (ammoStack.isEmpty()) {
            player.displayClientMessage(Component.translatable("block.scgextra.wrecker_turret.no_ammo")
                    .withStyle(style -> style.withColor(ChatFormatting.RED)), true);
            return;
        }
        ammoStack.shrink(1);

        this.triggerAnim("main", this.firingLeft ? "left_fire" : "right_fire");
        this.firingLeft = !this.firingLeft;

        Vec3 look = this.clampToArc(player.getLookAngle());
        Vec3 muzzle = new Vec3(pos.getX() + 0.5D, pos.getY() + MUZZLE_HEIGHT, pos.getZ() + 0.5D)
                .add(look.scale(MUZZLE_REACH));
        Vec3 aimPoint = player.getEyePosition().add(player.getLookAngle().scale(CONVERGENCE_RANGE));
        Vec3 aim = this.clampToArc(aimPoint.subtract(muzzle).normalize());

        TurretProjectileEntity projectile = new TurretProjectileEntity(this.level);
        projectile.setOwner(player);
        projectile.setPos(muzzle.x, muzzle.y, muzzle.z);
        projectile.setBaseDamage(DAMAGE);
        projectile.setArmorPenetration(ARMOR_PENETRATION);
        projectile.shoot(aim.x, aim.y, aim.z, PROJECTILE_SPEED, INACCURACY);
        this.level.addFreshEntity(projectile);

        this.fireCooldown = FIRE_COOLDOWN_TICKS;
        this.setChanged();
        this.level.playSound(null, muzzle.x, muzzle.y, muzzle.z, ModSounds.GREASER_SMG_FIRE.get(),
                SoundSource.BLOCKS, 0.5F, 0.9F + this.level.getRandom().nextFloat() * 0.2F);
        if (this.level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.SMOKE,
                    muzzle.x, muzzle.y, muzzle.z, 2, 0.05D, 0.05D, 0.05D, 0.01D);
        }
    }

    private Vec3 clampToArc(Vec3 look) {
        float baseYaw = this.getBlockState().getValue(WreckerTurretBlock.FACING).toYRot();
        float[] yawPitch = TurretAim.clampYawPitch(look, baseYaw);
        return TurretAim.direction(yawPitch[0], yawPitch[1]);
    }

    public float[] clientTurretAim() {
        if (this.level == null) {
            return null;
        }
        Player player = this.findManningPlayer();
        if (player == null) {
            return null;
        }
        float baseYaw = this.getBlockState().getValue(WreckerTurretBlock.FACING).toYRot();
        float[] yawPitch = TurretAim.clampYawPitch(player.getViewVector(1.0F), baseYaw);
        return new float[]{TurretAim.relativeYawRadians(yawPitch[0], baseYaw), TurretAim.pitchRadians(yawPitch[1])};
    }

    private Player findManningPlayer() {
        for (TurretSeatEntity seat : this.level.getEntitiesOfClass(TurretSeatEntity.class,
                new AABB(this.worldPosition).inflate(1.0D),
                candidate -> candidate.getTurretPos().equals(this.worldPosition))) {
            if (!seat.getPassengers().isEmpty() && seat.getPassengers().get(0) instanceof Player player) {
                return player;
            }
        }
        return null;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new ExpandedAnimationController<>(this, "main", 0,
                state -> state.setAndContinue(IDLE))
                .triggerableAnim("left_fire", (ctr) -> {
                    WreckerTurretBlockEntity be = (WreckerTurretBlockEntity) ctr.getAnimatable();
                    if (be.getLevel() != null) {
                        be.leftFlashTick = be.getLevel().getGameTime()+1;
                    }
                    return FIRE_LEFT;
                })
                .triggerableAnim("right_fire", (ctr) -> {
                    WreckerTurretBlockEntity be = (WreckerTurretBlockEntity) ctr.getAnimatable();
                    if (be.getLevel() != null) {
                        be.rightFlashTick = be.getLevel().getGameTime()+1;
                    }
                    return FIRE_RIGHT;
                })
        );
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    public boolean isManned() {
        return this.manningPlayerId != -1;
    }

    public int getManningPlayerId() {
        return this.manningPlayerId;
    }

    public void onHitByLightningProjectile() {
        this.disabledTicks = DISABLE_TICKS;
        if (this.level instanceof ServerLevel serverLevel) {
            BlockPos pos = this.getBlockPos();
            serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                    pos.getX() + 0.5D, pos.getY() + 1.2D, pos.getZ() + 0.5D, 12, 0.4D, 0.5D, 0.4D, 0.15D);
        }
    }

    @Override
    public AABB getRenderBoundingBox() {
        return super.getRenderBoundingBox().inflate(5);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.disabledTicks = tag.getInt("DisabledTicks");
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("DisabledTicks", this.disabledTicks);
    }
}
