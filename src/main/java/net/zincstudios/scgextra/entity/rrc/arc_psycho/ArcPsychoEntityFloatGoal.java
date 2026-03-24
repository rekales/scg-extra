package net.zincstudios.scgextra.entity.rrc.arc_psycho;

import java.util.EnumSet;

import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

public class ArcPsychoEntityFloatGoal extends Goal{
    private final ArcPsychoEntity parent;
    private final int height;
    private final float fSpeed;
    private final float mSpeed;
    private final int distance;
    private int floatTime = 0;
    private int floatDuration = 0;
    public ArcPsychoEntityFloatGoal(ArcPsychoEntity mob, int floatHeight, float floatSpeed, float movementSpeed, int distance, int extraFloatTime){
        this.parent = mob;
        this.height = floatHeight;
        this.fSpeed = floatSpeed;
        this.mSpeed = movementSpeed;
        this.distance = distance;
        this.floatDuration = extraFloatTime;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }
    @Override
    public boolean canUse() {
        return this.parent.getTarget()!=null;
    }
    @Override
    public boolean canContinueToUse() {
        if(this.parent.getTarget()==null && this.floatTime>0){
            this.floatTime--;
        }
        return this.parent.getTarget()!=null || this.floatTime>0;
    }
    @Override
    public void start() {
        super.start();
        this.floatTime = floatDuration;
    }
    @Override
    public void tick() {
        super.tick();
        if(!this.parent.level().isClientSide()){
            BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(this.parent.getX(), this.parent.getY(), this.parent.getZ());
            while (this.parent.level().getBlockState(pos).isAir()) {
                pos.move(Direction.DOWN);
            }
            double distance = this.parent.getY() - pos.getY();
            if(distance < height){
                this.parent.addDeltaMovement(new Vec3(0, fSpeed, 0));
            }
            LivingEntity target = this.parent.getTarget();
            if (target == null) return;
            double dx = target.getX() - this.parent.getX();
            double dz = target.getZ() - this.parent.getZ();
            double dist = Math.sqrt(dx*dx + dz*dz);
            if(dist > this.distance){
                this.parent.setDeltaMovement(
                    dx / dist * mSpeed,
                    this.parent.getDeltaMovement().y(),
                    dz / dist * mSpeed
                );
            }
            this.parent.lookAt(Anchor.EYES, target.position());
        }
    }
    @Override
    public void stop() {
        super.stop();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(this.parent.getX(), this.parent.getY(), this.parent.getZ());
        while (this.parent.level().getBlockState(pos).isAir()) {
            pos.move(Direction.DOWN);
        }
        double distance = this.parent.getY() - pos.getY();
        int sec = 1;
        for(double i = 0; i < distance; i+=0.1){
            if(i % 4.5 == 0){
                sec++;
            }
        }
        this.parent.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, sec*20, 1, true, false));
    }
}