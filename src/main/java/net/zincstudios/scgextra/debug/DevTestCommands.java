package net.zincstudios.scgextra.debug;

import com.mojang.brigadier.Command;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.zincstudios.scgextra.entity.cog.COGEntities;
import net.zincstudios.scgextra.entity.cog.centipede.CogCentipedeAttackGoal;
import net.zincstudios.scgextra.entity.cog.centipede.CogCentipedeEntity;

// TODO: needs to be cleaned up at prod
public class DevTestCommands {

    public static void registerCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("scgextra")
                .then(Commands.literal("devtest")
                        .requires(css-> css.hasPermission(2))
                        .then(Commands.literal("summon_centipede_16")
                                .executes(context -> {
                                    CogCentipedeEntity entity = new CogCentipedeEntity(COGEntities.CENTIPEDE.get(), context.getSource().getLevel());
                                    entity.moveTo(context.getSource().getPosition());

                                    entity.goalSelector.getAvailableGoals().removeIf(
                                            goal -> goal.getGoal() instanceof CogCentipedeAttackGoal);
                                    entity.goalSelector.addGoal(3, new CogCentipedeAttackGoal(entity, 120)
                                            .maxRange(16)
                                            .approachDist(4)
                                            .attackInterval(80)
                                    );

                                    boolean spawned =  context.getSource().getLevel().addFreshEntity(entity);

                                    if (spawned) {
                                        context.getSource().sendSuccess(() -> Component.translatable("commands.summon.success", entity.getDisplayName()), true);
                                    } else {
                                        context.getSource().sendFailure(Component.translatable("commands.summon.failed", entity.getDisplayName()));
                                    }

                                    return Command.SINGLE_SUCCESS;
                                })
                        )
                )
        );
    }
}
