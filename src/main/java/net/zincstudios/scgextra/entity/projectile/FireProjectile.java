package net.zincstudios.scgextra.entity.projectile;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import top.ribs.scguns.entity.projectile.EnemyProjectileEntity;

public class FireProjectile extends EnemyProjectileEntity{
    private LivingEntity shooter;
    public FireProjectile(EntityType<? extends EnemyProjectileEntity> type, Level world) {
      super(type, world);
    }
    public FireProjectile(Level world, LivingEntity pShooter) {
      super(world, pShooter);
      this.shooter = pShooter;
    }
    @Override
    public void tick() {
        super.tick();
        if(this.tickCount%2==0){
            if(!this.level().isClientSide()){
                ServerLevel sl = (ServerLevel) this.level();
                sl.sendParticles(
                    ParticleTypes.LAVA,
                    this.getX(),
                    this.getY(),
                    this.getZ(),
                    5,
                    0.2,
                    0.2,
                    0.2,
                    0.2
                );
            }
        }
    }
    @SuppressWarnings("unchecked")
    public static FireProjectile create(EntityType<? extends Entity> type, Level world) {
        return new FireProjectile((EntityType<? extends EnemyProjectileEntity>) type, world);
    }
    @Override
    protected void onHitEntity(EntityHitResult result) {
        result.getEntity().setSecondsOnFire(5);
        this.discard();
    }
    @Override
    protected void onHitBlock(BlockHitResult result) {
        this.discard();
    }
    @Override
    protected boolean canHitEntity(Entity p_36743_) {
        return super.canHitEntity(p_36743_) && !(p_36743_.is(this.shooter));
    }
}