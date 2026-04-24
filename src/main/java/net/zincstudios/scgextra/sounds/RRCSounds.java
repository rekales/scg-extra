package net.zincstudios.scgextra.sounds;

import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.zincstudios.scgextra.SCGExtra;

public class RRCSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, SCGExtra.MOD_ID);
    
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

    public static void register(IEventBus eventbus){
        SOUND_EVENTS.register(eventbus);
    }
}