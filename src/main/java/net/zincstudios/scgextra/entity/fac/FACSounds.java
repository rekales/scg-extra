package net.zincstudios.scgextra.entity.fac;

import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.zincstudios.scgextra.SCGExtra;

public class FACSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, SCGExtra.MOD_ID);
    
    //TRENCHER
    public static final RegistryObject<SoundEvent> FAC_TRENCHER_DEATH_1 = SOUND_EVENTS.register("fac_trencher.death1", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_trencher.death1")));
    public static final RegistryObject<SoundEvent> FAC_TRENCHER_DEATH_2 = SOUND_EVENTS.register("fac_trencher.death2", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_trencher.death2")));
    public static final RegistryObject<SoundEvent> FAC_TRENCHER_DEATH_3 = SOUND_EVENTS.register("fac_trencher.death3", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_trencher.death3")));
    public static final RegistryObject<SoundEvent> FAC_TRENCHER_HURT_1 = SOUND_EVENTS.register("fac_trencher.hurt1", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_trencher.hurt1")));
    public static final RegistryObject<SoundEvent> FAC_TRENCHER_HURT_2 = SOUND_EVENTS.register("fac_trencher.hurt2", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_trencher.hurt2")));
    public static final RegistryObject<SoundEvent> FAC_TRENCHER_HURT_3 = SOUND_EVENTS.register("fac_trencher.hurt3", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_trencher.hurt3")));
    public static final RegistryObject<SoundEvent> FAC_TRENCHER_IDLE_1 = SOUND_EVENTS.register("fac_trencher.idle1", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_trencher.idle1")));
    public static final RegistryObject<SoundEvent> FAC_TRENCHER_IDLE_2 = SOUND_EVENTS.register("fac_trencher.idle2", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_trencher.idle2")));
    public static final RegistryObject<SoundEvent> FAC_TRENCHER_IDLE_3 = SOUND_EVENTS.register("fac_trencher.idle3", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_trencher.idle3")));
    
    //BLUECOAT
    public static final RegistryObject<SoundEvent> FAC_BLUECOAT_DEATH_1 = SOUND_EVENTS.register("fac_bluecoat.death1", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_bluecoat.death1")));
    public static final RegistryObject<SoundEvent> FAC_BLUECOAT_DEATH_2 = SOUND_EVENTS.register("fac_bluecoat.death2", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_bluecoat.death2")));
    public static final RegistryObject<SoundEvent> FAC_BLUECOAT_DEATH_3 = SOUND_EVENTS.register("fac_bluecoat.death3", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_bluecoat.death3")));
    public static final RegistryObject<SoundEvent> FAC_BLUECOAT_HURT_1 = SOUND_EVENTS.register("fac_bluecoat.hurt1", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_bluecoat.hurt1")));
    public static final RegistryObject<SoundEvent> FAC_BLUECOAT_HURT_2 = SOUND_EVENTS.register("fac_bluecoat.hurt2", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_bluecoat.hurt2")));
    public static final RegistryObject<SoundEvent> FAC_BLUECOAT_HURT_3 = SOUND_EVENTS.register("fac_bluecoat.hurt3", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_bluecoat.hurt3")));
    public static final RegistryObject<SoundEvent> FAC_BLUECOAT_IDLE_1 = SOUND_EVENTS.register("fac_bluecoat.idle1", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_bluecoat.idle1")));
    public static final RegistryObject<SoundEvent> FAC_BLUECOAT_IDLE_2 = SOUND_EVENTS.register("fac_bluecoat.idle2", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_bluecoat.idle2")));
    public static final RegistryObject<SoundEvent> FAC_BLUECOAT_IDLE_3 = SOUND_EVENTS.register("fac_bluecoat.idle3", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_bluecoat.idle3")));
    
    //TANK BUSTER
    public static final RegistryObject<SoundEvent> FAC_TANK_BUSTER_DEATH_1 = SOUND_EVENTS.register("fac_tank_buster.death1", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_tank_buster.death1")));
    public static final RegistryObject<SoundEvent> FAC_TANK_BUSTER_DEATH_2 = SOUND_EVENTS.register("fac_tank_buster.death2", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_tank_buster.death2")));
    public static final RegistryObject<SoundEvent> FAC_TANK_BUSTER_DEATH_3 = SOUND_EVENTS.register("fac_tank_buster.death3", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_tank_buster.death3")));
    public static final RegistryObject<SoundEvent> FAC_TANK_BUSTER_HURT_1 = SOUND_EVENTS.register("fac_tank_buster.hurt1", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_tank_buster.hurt1")));
    public static final RegistryObject<SoundEvent> FAC_TANK_BUSTER_HURT_2 = SOUND_EVENTS.register("fac_tank_buster.hurt2", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_tank_buster.hurt2")));
    public static final RegistryObject<SoundEvent> FAC_TANK_BUSTER_HURT_3 = SOUND_EVENTS.register("fac_tank_buster.hurt3", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_tank_buster.hurt3")));
    public static final RegistryObject<SoundEvent> FAC_TANK_BUSTER_IDLE_1 = SOUND_EVENTS.register("fac_tank_buster.idle1", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_tank_buster.idle1")));
    public static final RegistryObject<SoundEvent> FAC_TANK_BUSTER_IDLE_2 = SOUND_EVENTS.register("fac_tank_buster.idle2", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_tank_buster.idle2")));
    public static final RegistryObject<SoundEvent> FAC_TANK_BUSTER_IDLE_3 = SOUND_EVENTS.register("fac_tank_buster.idle3", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_tank_buster.idle3")));
    
    //COMMISSAR
    public static final RegistryObject<SoundEvent> FAC_COMMISSAR_ATTACK_1 = SOUND_EVENTS.register("fac_commissar.attack1", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_commissar.attack1")));
    public static final RegistryObject<SoundEvent> FAC_COMMISSAR_ATTACK_2 = SOUND_EVENTS.register("fac_commissar.attack2", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_commissar.attack2")));
    public static final RegistryObject<SoundEvent> FAC_COMMISSAR_DEATH_1 = SOUND_EVENTS.register("fac_commissar.death1", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_commissar.death1")));
    public static final RegistryObject<SoundEvent> FAC_COMMISSAR_DEATH_2 = SOUND_EVENTS.register("fac_commissar.death2", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_commissar.death2")));
    public static final RegistryObject<SoundEvent> FAC_COMMISSAR_FLARE_1 = SOUND_EVENTS.register("fac_commissar.flare1", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_commissar.flare1")));
    public static final RegistryObject<SoundEvent> FAC_COMMISSAR_FLARE_2 = SOUND_EVENTS.register("fac_commissar.flare2", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_commissar.flare2")));
    public static final RegistryObject<SoundEvent> FAC_COMMISSAR_HURT_1 = SOUND_EVENTS.register("fac_commissar.hurt1", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_commissar.hurt1")));
    public static final RegistryObject<SoundEvent> FAC_COMMISSAR_HURT_2 = SOUND_EVENTS.register("fac_commissar.hurt2", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_commissar.hurt2")));
    public static final RegistryObject<SoundEvent> FAC_COMMISSAR_HURT_3 = SOUND_EVENTS.register("fac_commissar.hurt3", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_commissar.hurt3")));
    public static final RegistryObject<SoundEvent> FAC_COMMISSAR_HURT_4 = SOUND_EVENTS.register("fac_commissar.hurt4", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_commissar.hurt4")));
    public static final RegistryObject<SoundEvent> FAC_COMMISSAR_IDLE_1 = SOUND_EVENTS.register("fac_commissar.idle1", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_commissar.idle1")));
    public static final RegistryObject<SoundEvent> FAC_COMMISSAR_IDLE_2 = SOUND_EVENTS.register("fac_commissar.idle2", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_commissar.idle2")));
    public static final RegistryObject<SoundEvent> FAC_COMMISSAR_IDLE_3 = SOUND_EVENTS.register("fac_commissar.idle3", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_commissar.idle3")));
    public static final RegistryObject<SoundEvent> FAC_COMMISSAR_IDLE_4 = SOUND_EVENTS.register("fac_commissar.idle4", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_commissar.idle4")));
    public static final RegistryObject<SoundEvent> FAC_COMMISSAR_LINE_2 = SOUND_EVENTS.register("fac_commissar.line2", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_commissar.line2")));
    
    //LION
    public static final RegistryObject<SoundEvent> FAC_LION_ATTACK_1 = SOUND_EVENTS.register("fac_lion.attack1", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_lion.attack1")));
    public static final RegistryObject<SoundEvent> FAC_LION_DEATH_1 = SOUND_EVENTS.register("fac_lion.death1", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_lion.death1")));
    public static final RegistryObject<SoundEvent> FAC_LION_DEATH_2 = SOUND_EVENTS.register("fac_lion.death2", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_lion.death2")));
    public static final RegistryObject<SoundEvent> FAC_LION_HURT_1 = SOUND_EVENTS.register("fac_lion.hurt1", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_lion.hurt1")));
    public static final RegistryObject<SoundEvent> FAC_LION_HURT_2 = SOUND_EVENTS.register("fac_lion.hurt2", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_lion.hurt2")));
    public static final RegistryObject<SoundEvent> FAC_LION_HURT_3 = SOUND_EVENTS.register("fac_lion.hurt3", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_lion.hurt3")));
    public static final RegistryObject<SoundEvent> FAC_LION_IDLE_1 = SOUND_EVENTS.register("fac_lion.idle1", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_lion.idle1")));
    public static final RegistryObject<SoundEvent> FAC_LION_IDLE_2 = SOUND_EVENTS.register("fac_lion.idle2", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_lion.idle2")));
    public static final RegistryObject<SoundEvent> FAC_LION_IDLE_3 = SOUND_EVENTS.register("fac_lion.idle3", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_lion.idle3")));
    
    //WALKER
    public static final RegistryObject<SoundEvent> FAC_WALKER_HURT_1 = SOUND_EVENTS.register("fac_walker.hurt1",
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_walker.hurt1")));
    public static final RegistryObject<SoundEvent> FAC_WALKER_HURT_2 = SOUND_EVENTS.register("fac_walker.hurt2",
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_walker.hurt2")));
    public static final RegistryObject<SoundEvent> FAC_WALKER_IDLE_1 = SOUND_EVENTS.register("fac_walker.idle1",
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_walker.idle1")));
    public static final RegistryObject<SoundEvent> FAC_WALKER_IDLE_2 = SOUND_EVENTS.register("fac_walker.idle2",
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_walker.idle2")));
    public static final RegistryObject<SoundEvent> FAC_WALKER_IDLE_3 = SOUND_EVENTS.register("fac_walker.idle3",
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_walker.idle3")));
    public static final RegistryObject<SoundEvent> FAC_WALKER_WALK = SOUND_EVENTS.register("fac_walker.walk",
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_walker.walk")));
    public static final RegistryObject<SoundEvent> FAC_WALKER_RUN = SOUND_EVENTS.register("fac_walker.run",
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_walker.run")));
    
    //TANK
    public static final RegistryObject<SoundEvent> FAC_TANK_HURT_1 = SOUND_EVENTS.register("fac_tank.hurt1",
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_tank.hurt1")));
    public static final RegistryObject<SoundEvent> FAC_TANK_HURT_2 = SOUND_EVENTS.register("fac_tank.hurt2",
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_tank.hurt2")));
    public static final RegistryObject<SoundEvent> FAC_TANK_IDLE_1 = SOUND_EVENTS.register("fac_tank.idle1",
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_tank.idle1")));
    public static final RegistryObject<SoundEvent> FAC_TANK_IDLE_2 = SOUND_EVENTS.register("fac_tank.idle2",
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_tank.idle2")));
    public static final RegistryObject<SoundEvent> FAC_TANK_WALK = SOUND_EVENTS.register("fac_tank.walk",
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_tank.walk")));
    public static final RegistryObject<SoundEvent> FAC_TANK_STUN = SOUND_EVENTS.register("fac_tank.stun",
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_tank.stun")));
    public static final RegistryObject<SoundEvent> FAC_TANK_CANNON_CHARGE = SOUND_EVENTS.register("fac_tank.cannon_charge",
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_tank.cannon_charge")));
    
    //TRENCH GOBLIN
    public static final RegistryObject<SoundEvent> TRENCH_GOBLIN_ATTACK_1 = SOUND_EVENTS.register("trench_goblin.attack1", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("trench_goblin.attack1")));
    public static final RegistryObject<SoundEvent> TRENCH_GOBLIN_ATTACK_2 = SOUND_EVENTS.register("trench_goblin.attack2", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("trench_goblin.attack2")));
    public static final RegistryObject<SoundEvent> TRENCH_GOBLIN_ATTACK_3 = SOUND_EVENTS.register("trench_goblin.attack3", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("trench_goblin.attack3")));
    public static final RegistryObject<SoundEvent> TRENCH_GOBLIN_DEATH_1 = SOUND_EVENTS.register("trench_goblin.death1", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("trench_goblin.death1")));
    public static final RegistryObject<SoundEvent> TRENCH_GOBLIN_DEATH_2 = SOUND_EVENTS.register("trench_goblin.death2", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("trench_goblin.death2")));
    public static final RegistryObject<SoundEvent> TRENCH_GOBLIN_HURT_1 = SOUND_EVENTS.register("trench_goblin.hurt1", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("trench_goblin.hurt1")));
    public static final RegistryObject<SoundEvent> TRENCH_GOBLIN_HURT_2 = SOUND_EVENTS.register("trench_goblin.hurt2", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("trench_goblin.hurt2")));
    public static final RegistryObject<SoundEvent> TRENCH_GOBLIN_HURT_3 = SOUND_EVENTS.register("trench_goblin.hurt3", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("trench_goblin.hurt3")));
    public static final RegistryObject<SoundEvent> TRENCH_GOBLIN_IDLE_1 = SOUND_EVENTS.register("trench_goblin.idle1", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("trench_goblin.idle1")));
    public static final RegistryObject<SoundEvent> TRENCH_GOBLIN_IDLE_2 = SOUND_EVENTS.register("trench_goblin.idle2", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("trench_goblin.idle2")));
    public static final RegistryObject<SoundEvent> TRENCH_GOBLIN_IDLE_3 = SOUND_EVENTS.register("trench_goblin.idle3", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("trench_goblin.idle3")));
    
    //TRENCH SNIPER
    public static final RegistryObject<SoundEvent> TRENCH_SNIPER_ALERT_1 = SOUND_EVENTS.register("trench_sniper.alert1", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("trench_sniper.alert1")));
    public static final RegistryObject<SoundEvent> TRENCH_SNIPER_ALERT_2 = SOUND_EVENTS.register("trench_sniper.alert2", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("trench_sniper.alert2")));
    public static final RegistryObject<SoundEvent> TRENCH_SNIPER_ALERT_3 = SOUND_EVENTS.register("trench_sniper.alert3", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("trench_sniper.alert3")));
    public static final RegistryObject<SoundEvent> TRENCH_SNIPER_DEATH_1 = SOUND_EVENTS.register("trench_sniper.death1", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("trench_sniper.death1")));
    public static final RegistryObject<SoundEvent> TRENCH_SNIPER_DEATH_2 = SOUND_EVENTS.register("trench_sniper.death2", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("trench_sniper.death2")));
    public static final RegistryObject<SoundEvent> TRENCH_SNIPER_IDLE_1 = SOUND_EVENTS.register("trench_sniper.idle1", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("trench_sniper.idle1")));
    public static final RegistryObject<SoundEvent> TRENCH_SNIPER_IDLE_2 = SOUND_EVENTS.register("trench_sniper.idle2", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("trench_sniper.idle2")));
    public static final RegistryObject<SoundEvent> TRENCH_SNIPER_IDLE_3 = SOUND_EVENTS.register("trench_sniper.idle3", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("trench_sniper.idle3")));
    public static final RegistryObject<SoundEvent> TRENCH_SNIPER_IDLE_4 = SOUND_EVENTS.register("trench_sniper.idle4", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("trench_sniper.idle4")));

    public static void register(IEventBus eventbus){
        SOUND_EVENTS.register(eventbus);
    }
}