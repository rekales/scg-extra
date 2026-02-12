package com.daragetsu.scgextra.entity.salmonsaurs;


import java.util.ArrayList;
import java.util.Random;

import com.daragetsu.scgextra.Faction;
import com.daragetsu.scgextra.entity.ModEntities;
import com.daragetsu.scgextra.entity.fishfolk.FishFolkEntity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.RegistryObject;
import top.ribs.scguns.init.ModItems;
import top.ribs.scguns.item.animated.AnimatedUnderWaterGunItem;

public class SalmonsaursEntity extends Hoglin{
    private boolean riderSpawned = false;
    public SalmonsaursEntity(EntityType<? extends SalmonsaursEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        
    }
    public static AttributeSupplier.Builder createAttributes() {
      return Monster.createMonsterAttributes()
      .add(Attributes.MOVEMENT_SPEED, (double)0.5F)
      .add(Attributes.KNOCKBACK_RESISTANCE, (double)0.6F)
      .add(Attributes.ATTACK_KNOCKBACK, 1.0D)
      .add(Attributes.ATTACK_DAMAGE, 8.0D)
      .add(Attributes.MAX_HEALTH, 30.0D);
    }
    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide() && !riderSpawned && this.getPassengers().isEmpty()) {
            FishFolkEntity rider = new FishFolkEntity(ModEntities.FISH_FOLK.get(), this.level());
            int i = new Random().nextInt(20);
            if (i < 10) {
                rider.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.TRIDENT));
            } else {
                ArrayList<Item> guns = new ArrayList<>();
                for(RegistryObject<Item> item : ModItems.REGISTER.getEntries()){
                    if(item.get() instanceof AnimatedUnderWaterGunItem){
                        guns.add(item.get());
                    }
                }
                ItemStack gun = new ItemStack(guns.get(new Random().nextInt(guns.size())));
                gun.getOrCreateTag().putBoolean("IgnoreAmmo", true);
                rider.setItemSlot(EquipmentSlot.MAINHAND, gun);
            }
            rider.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
            this.level().addFreshEntity(rider);
            rider.startRiding(this, true);
            riderSpawned = true;
        }
        if (this.getLastHurtByMob() != null &&
            !Faction.isFriendlies(this, this.getLastHurtByMob())) {
            this.setTarget((LivingEntity)this.getLastHurtByMob());
        }
    }
    @Override
    public boolean isConverting() {
        return false;
    }
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true, false));
    }
    @Override
    public boolean canBeLeashed(Player pPlayer) {
        return false;
    }
    @Override
    public void setTarget(LivingEntity pTarget) {
        if(pTarget==null || !Faction.isFriendlies(this, pTarget)){
            super.setTarget(pTarget);
        }
    }
    @Override
    public boolean isBaby() {
        return false;
    }
}