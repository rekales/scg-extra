package net.zincstudios.scgextra.sounds;

import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.zincstudios.scgextra.SCGExtra;
import net.minecraftforge.eventbus.api.IEventBus;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, SCGExtra.MOD_ID);

//Whaler
    public static final RegistryObject<SoundEvent> WHALE_SLAM_1 = SOUND_EVENTS.register("armored_whale_slam_1", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("armored_whale_slam_1")));

    public static final RegistryObject<SoundEvent> WHALE_SLAM_2 = SOUND_EVENTS.register("armored_whale_slam_2", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("armored_whale_slam_2")));

    public static final RegistryObject<SoundEvent> WHALE_SPLASH_1 = SOUND_EVENTS.register("armored_whale_splash_1", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("armored_whale_splash_1")));

    public static final RegistryObject<SoundEvent> WHALE_SPLASH_2 = SOUND_EVENTS.register("armored_whale_splash_2", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("armored_whale_splash_2")));

    public static final RegistryObject<SoundEvent> GUARDIAN_STATUE_CHARGE = SOUND_EVENTS.register("guardian_statue_charge", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("guardian_statue_charge")));

//RRC
    //Drone
    public static final RegistryObject<SoundEvent> RRC_DRONE_DEATH_1 = SOUND_EVENTS.register("drone.death1", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("drone.death1")));
    public static final RegistryObject<SoundEvent> RRC_DRONE_DEATH_2 = SOUND_EVENTS.register("drone.death2", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("drone.death2")));
    public static final RegistryObject<SoundEvent> RRC_DRONE_HURT_1 = SOUND_EVENTS.register("drone.hurt1", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("drone.hurt1")));
    public static final RegistryObject<SoundEvent> RRC_DRONE_HURT_2 = SOUND_EVENTS.register("drone.hurt2", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("drone.hurt2")));
    public static final RegistryObject<SoundEvent> RRC_DRONE_HURT_3 = SOUND_EVENTS.register("drone.hurt3", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("drone.hurt3")));
    public static final RegistryObject<SoundEvent> RRC_DRONE_HURT_4 = SOUND_EVENTS.register("drone.hurt4", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("drone.hurt4")));
    public static final RegistryObject<SoundEvent> RRC_DRONE_HURT_5 = SOUND_EVENTS.register("drone.hurt5", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("drone.hurt5")));
    public static final RegistryObject<SoundEvent> RRC_DRONE_HURT_6 = SOUND_EVENTS.register("drone.hurt6", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("drone.hurt6")));
    public static final RegistryObject<SoundEvent> RRC_DRONE_HURT_7 = SOUND_EVENTS.register("drone.hurt7", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("drone.hurt7")));
    public static final RegistryObject<SoundEvent> RRC_DRONE_HURT_8 = SOUND_EVENTS.register("drone.hurt8", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("drone.hurt8")));
    public static final RegistryObject<SoundEvent> RRC_DRONE_IDLE = SOUND_EVENTS.register("drone.idle", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("drone.idle")));
    public static final RegistryObject<SoundEvent> RRC_DRONE_WALK = SOUND_EVENTS.register("drone.walk", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("drone.walk")));
    
    //Spring Junkie
    public static final RegistryObject<SoundEvent> RRC_SPRING_JUNKIE_DEATH_1 = SOUND_EVENTS.register("spring_junkie.death1", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("spring_junkie.death1")));
    public static final RegistryObject<SoundEvent> RRC_SPRING_JUNKIE_DEATH_2 = SOUND_EVENTS.register("spring_junkie.death2", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("spring_junkie.death2")));
    public static final RegistryObject<SoundEvent> RRC_SPRING_JUNKIE_DEATH_3 = SOUND_EVENTS.register("spring_junkie.death3", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("spring_junkie.death3")));
    public static final RegistryObject<SoundEvent> RRC_SPRING_JUNKIE_LAUGH_1 = SOUND_EVENTS.register("spring_junkie.laugh1", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("spring_junkie.laugh1")));
    public static final RegistryObject<SoundEvent> RRC_SPRING_JUNKIE_LAUGH_2 = SOUND_EVENTS.register("spring_junkie.laugh2", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("spring_junkie.laugh2")));
    public static final RegistryObject<SoundEvent> RRC_SPRING_JUNKIE_RUN_1 = SOUND_EVENTS.register("spring_junkie.run1", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("spring_junkie.run1")));
    public static final RegistryObject<SoundEvent> RRC_SPRING_JUNKIE_RUN_2 = SOUND_EVENTS.register("spring_junkie.run2", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("spring_junkie.run2")));
    public static final RegistryObject<SoundEvent> RRC_SPRING_JUNKIE_SCREAM = SOUND_EVENTS.register("spring_junkie.scream", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("spring_junkie.scream")));
    public static final RegistryObject<SoundEvent> RRC_SPRING_JUNKIE_WALK_1 = SOUND_EVENTS.register("spring_junkie.walk1", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("spring_junkie.walk1")));
    public static final RegistryObject<SoundEvent> RRC_SPRING_JUNKIE_WALK_2 = SOUND_EVENTS.register("spring_junkie.walk2", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("spring_junkie.walk2")));
    public static final RegistryObject<SoundEvent> RRC_SPRING_JUNKIE_WALK_3 = SOUND_EVENTS.register("spring_junkie.walk3", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("spring_junkie.walk3")));
    public static final RegistryObject<SoundEvent> RRC_SPRING_JUNKIE_WALK_4 = SOUND_EVENTS.register("spring_junkie.walk4", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("spring_junkie.walk4")));
    
    //Scout
    public static final RegistryObject<SoundEvent> RRC_SCOUT_DEATH_1 = SOUND_EVENTS.register("scout.death1", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("scout.death1")));
    public static final RegistryObject<SoundEvent> RRC_SCOUT_DEATH_2 = SOUND_EVENTS.register("scout.death2", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("scout.death2")));
    public static final RegistryObject<SoundEvent> RRC_SCOUT_HURT_1 = SOUND_EVENTS.register("scout.hurt1", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("scout.hurt1")));
    public static final RegistryObject<SoundEvent> RRC_SCOUT_HURT_2 = SOUND_EVENTS.register("scout.hurt2", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("scout.hurt2")));
    public static final RegistryObject<SoundEvent> RRC_SCOUT_HURT_3 = SOUND_EVENTS.register("scout.hurt3", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("scout.hurt3")));
    public static final RegistryObject<SoundEvent> RRC_SCOUT_HURT_4 = SOUND_EVENTS.register("scout.hurt4", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("scout.hurt4")));
    public static final RegistryObject<SoundEvent> RRC_SCOUT_HURT_5 = SOUND_EVENTS.register("scout.hurt5", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("scout.hurt5")));
    public static final RegistryObject<SoundEvent> RRC_SCOUT_IDLE_1 = SOUND_EVENTS.register("scout.idle1", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("scout.idle1")));
    public static final RegistryObject<SoundEvent> RRC_SCOUT_IDLE_2 = SOUND_EVENTS.register("scout.idle2", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("scout.idle2")));
    public static final RegistryObject<SoundEvent> RRC_SCOUT_WALK_1 = SOUND_EVENTS.register("scout.walk1", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("scout.walk1")));
    public static final RegistryObject<SoundEvent> RRC_SCOUT_WALK_2 = SOUND_EVENTS.register("scout.walk2", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("scout.walk2")));
    
    //Tallman
    public static final RegistryObject<SoundEvent> RRC_TALLMAN_DEATH_1 = SOUND_EVENTS.register("tallman.death1", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("tallman.death1")));
    public static final RegistryObject<SoundEvent> RRC_TALLMAN_DEATH_2 = SOUND_EVENTS.register("tallman.death2", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("tallman.death2")));
    public static final RegistryObject<SoundEvent> RRC_TALLMAN_HURT_1 = SOUND_EVENTS.register("tallman.hurt1", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("tallman.hurt1")));
    public static final RegistryObject<SoundEvent> RRC_TALLMAN_HURT_2 = SOUND_EVENTS.register("tallman.hurt2", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("tallman.hurt2")));
    public static final RegistryObject<SoundEvent> RRC_TALLMAN_HURT_3 = SOUND_EVENTS.register("tallman.hurt3", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("tallman.hurt3")));
    public static final RegistryObject<SoundEvent> RRC_TALLMAN_HURT_4 = SOUND_EVENTS.register("tallman.hurt4", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("tallman.hurt4")));
    public static final RegistryObject<SoundEvent> RRC_TALLMAN_HURT_5 = SOUND_EVENTS.register("tallman.hurt5", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("tallman.hurt5")));
    public static final RegistryObject<SoundEvent> RRC_TALLMAN_HURT_6 = SOUND_EVENTS.register("tallman.hurt6", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("tallman.hurt6")));
    public static final RegistryObject<SoundEvent> RRC_TALLMAN_IDLE_1 = SOUND_EVENTS.register("tallman.idle1", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("tallman.idle1")));
    public static final RegistryObject<SoundEvent> RRC_TALLMAN_IDLE_2 = SOUND_EVENTS.register("tallman.idle2", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("tallman.idle2")));
    public static final RegistryObject<SoundEvent> RRC_TALLMAN_WALK_1 = SOUND_EVENTS.register("tallman.walk1", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("tallman.walk1")));
    public static final RegistryObject<SoundEvent> RRC_TALLMAN_WALK_2 = SOUND_EVENTS.register("tallman.walk2", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("tallman.walk2")));
    
    //Oppressor
    public static final RegistryObject<SoundEvent> RRC_OPPRESSOR_ALERT = SOUND_EVENTS.register("oppressor.alert", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("oppressor.alert")));
    public static final RegistryObject<SoundEvent> RRC_OPPRESSOR_DEATH_1 = SOUND_EVENTS.register("oppressor.death_1", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("oppressor.death_1")));
    public static final RegistryObject<SoundEvent> RRC_OPPRESSOR_DEATH_2 = SOUND_EVENTS.register("oppressor.death_2", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("oppressor.death_2")));
    public static final RegistryObject<SoundEvent> RRC_OPPRESSOR_HURT_1 = SOUND_EVENTS.register("oppressor.hurt_1", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("oppressor.hurt_1")));
    public static final RegistryObject<SoundEvent> RRC_OPPRESSOR_HURT_2 = SOUND_EVENTS.register("oppressor.hurt_2", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("oppressor.hurt_2")));
    public static final RegistryObject<SoundEvent> RRC_OPPRESSOR_IDLE_1 = SOUND_EVENTS.register("oppressor.idle_1", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("oppressor.idle_1")));
    public static final RegistryObject<SoundEvent> RRC_OPPRESSOR_IDLE_2 = SOUND_EVENTS.register("oppressor.idle_2", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("oppressor.idle_2")));
    public static final RegistryObject<SoundEvent> RRC_OPPRESSOR_IDLE_3 = SOUND_EVENTS.register("oppressor.idle_3", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("oppressor.idle_3")));
    
    //Scrapguard
    public static final RegistryObject<SoundEvent> RRC_SCRAPGUARD_PUNCH = SOUND_EVENTS.register("scrapguard.punch", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("scrapguard.punch")));

    //Copper Knight
    public static final RegistryObject<SoundEvent> RRC_COPPER_KNIGHT_DEAD = SOUND_EVENTS.register("knight.dead", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("knight.dead")));
    public static final RegistryObject<SoundEvent> RRC_COPPER_KNIGHT_HURT_1 = SOUND_EVENTS.register("knight.hurt_1", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("knight.hurt_1")));
    public static final RegistryObject<SoundEvent> RRC_COPPER_KNIGHT_HURT_2 = SOUND_EVENTS.register("knight.hurt_2", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("knight.hurt_2")));
    public static final RegistryObject<SoundEvent> RRC_COPPER_KNIGHT_HURT_3 = SOUND_EVENTS.register("knight.hurt_3", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("knight.hurt_3")));
    public static final RegistryObject<SoundEvent> RRC_COPPER_KNIGHT_IDLE_1 = SOUND_EVENTS.register("knight.idle_1", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("knight.idle_1")));
    public static final RegistryObject<SoundEvent> RRC_COPPER_KNIGHT_IDLE_2 = SOUND_EVENTS.register("knight.idle_2", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("knight.idle_2")));
    public static final RegistryObject<SoundEvent> RRC_COPPER_KNIGHT_IDLE_3 = SOUND_EVENTS.register("knight.idle_3", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("knight.idle_3")));

    //Flaming Head
    public static final RegistryObject<SoundEvent> RRC_FLAMING_HEAD_IDLE_1 = SOUND_EVENTS.register("flaming_head.idle_1", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("flaming_head.idle_1")));
    public static final RegistryObject<SoundEvent> RRC_FLAMING_HEAD_IDLE_2 = SOUND_EVENTS.register("flaming_head.idle_2", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("flaming_head.idle_2")));
    public static final RegistryObject<SoundEvent> RRC_FLAMING_HEAD_IDLE_3 = SOUND_EVENTS.register("flaming_head.idle_3", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("flaming_head.idle_3")));
    public static final RegistryObject<SoundEvent> RRC_FLAMING_HEAD_IDLE_4 = SOUND_EVENTS.register("flaming_head.idle_4", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("flaming_head.idle_4")));
    public static final RegistryObject<SoundEvent> RRC_FLAMING_HEAD_IDLE_5 = SOUND_EVENTS.register("flaming_head.idle_5", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("flaming_head.idle_5")));
    public static final RegistryObject<SoundEvent> RRC_FLAMING_HEAD_DEAD_1 = SOUND_EVENTS.register("flaming_head.dead_1", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("flaming_head.dead_1")));
    public static final RegistryObject<SoundEvent> RRC_FLAMING_HEAD_DEAD_2 = SOUND_EVENTS.register("flaming_head.dead_2", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("flaming_head.dead_2")));
    public static final RegistryObject<SoundEvent> RRC_FLAMING_HEAD_SPIN_1 = SOUND_EVENTS.register("flaming_head.spin_1", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("flaming_head.spin_1")));
    public static final RegistryObject<SoundEvent> RRC_FLAMING_HEAD_SPIN_2 = SOUND_EVENTS.register("flaming_head.spin_2", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("flaming_head.spin_2")));
    public static final RegistryObject<SoundEvent> RRC_FLAMING_HEAD_SPIN_3 = SOUND_EVENTS.register("flaming_head.spin_3", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("flaming_head.spin_3")));
    public static final RegistryObject<SoundEvent> RRC_FLAMING_HEAD_STUN_1 = SOUND_EVENTS.register("flaming_head.stun_1", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("flaming_head.stun_1")));
    public static final RegistryObject<SoundEvent> RRC_FLAMING_HEAD_STUN_2 = SOUND_EVENTS.register("flaming_head.stun_2", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("flaming_head.stun_2")));
       
    //Arc Psycho
    public static final RegistryObject<SoundEvent> RRC_ARC_PSYCHO_DEAD_1 = SOUND_EVENTS.register("arc_psycho.dead_1", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("arc_psycho.dead_1")));
    public static final RegistryObject<SoundEvent> RRC_ARC_PSYCHO_DEAD_2 = SOUND_EVENTS.register("arc_psycho.dead_2", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("arc_psycho.dead_2")));
    public static final RegistryObject<SoundEvent> RRC_ARC_PSYCHO_HURT_1 = SOUND_EVENTS.register("arc_psycho.hurt_1", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("arc_psycho.hurt_1")));
    public static final RegistryObject<SoundEvent> RRC_ARC_PSYCHO_HURT_2 = SOUND_EVENTS.register("arc_psycho.hurt_2", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("arc_psycho.hurt_2")));
    public static final RegistryObject<SoundEvent> RRC_ARC_PSYCHO_HURT_3 = SOUND_EVENTS.register("arc_psycho.hurt_3", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("arc_psycho.hurt_3")));
    public static final RegistryObject<SoundEvent> RRC_ARC_PSYCHO_IDLE_1 = SOUND_EVENTS.register("arc_psycho.idle_1", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("arc_psycho.idle_1")));
    public static final RegistryObject<SoundEvent> RRC_ARC_PSYCHO_IDLE_2 = SOUND_EVENTS.register("arc_psycho.idle_2", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("arc_psycho.idle_2")));
    public static final RegistryObject<SoundEvent> RRC_ARC_PSYCHO_IDLE_3 = SOUND_EVENTS.register("arc_psycho.idle_3", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("arc_psycho.idle_3")));

//FAC
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
//FAC END
    public static void register(IEventBus eventbus){
        SOUND_EVENTS.register(eventbus);
    }
}