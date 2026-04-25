package qituo.minigc.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import qituo.minigc.config.GameConfig;

public class GameCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("minigc")
                .then(CommandManager.literal("gamemode")
                        .then(CommandManager.literal("enable")
                                .executes(context -> {
                                    if (!GameConfig.isGameActive()) {
                                        GameConfig.gameModeSwitchEnabled = true;
                                        GameConfig.setGameActive(true);
                                        GameConfig.saveConfig();
                                        context.getSource().sendFeedback(() -> Text.translatable("minigc.command.gamemode.enable.success"), true);
                                        return 1;
                                    } else {
                                        context.getSource().sendFeedback(() -> Text.translatable("minigc.command.error.game_active"), false);
                                        return 0;
                                    }
                                })
                        )
                        .then(CommandManager.literal("disable")
                                .executes(context -> {
                                    if (!GameConfig.isGameActive()) {
                                        GameConfig.gameModeSwitchEnabled = false;
                                        GameConfig.setGameActive(false);
                                        GameConfig.saveConfig();
                                        context.getSource().sendFeedback(() -> Text.translatable("minigc.command.gamemode.disable.success"), true);
                                        return 1;
                                    } else {
                                        context.getSource().sendFeedback(() -> Text.translatable("minigc.command.error.game_active"), false);
                                        return 0;
                                    }
                                })
                        )
                        .then(CommandManager.literal("interval")
                                .then(CommandManager.argument("seconds", IntegerArgumentType.integer(1, 300))
                                        .executes(context -> {
                                            int seconds = context.getArgument("seconds", Integer.class);
                                            GameConfig.switchIntervalSeconds = seconds;
                                            GameConfig.saveConfig();
                                            context.getSource().sendFeedback(() -> Text.translatable("minigc.command.gamemode.interval.success", seconds), true);
                                            return 1;
                                        })
                                )
                        )
                )
                .then(CommandManager.literal("status")
                        .executes(context -> {
                            context.getSource().sendFeedback(() -> Text.translatable("minigc.command.status",
                                    GameConfig.gameModeSwitchEnabled ? Text.translatable("minigc.command.enabled") : Text.translatable("minigc.command.disabled"),
                                    GameConfig.switchIntervalSeconds,
                                    GameConfig.isGameActive() ? Text.translatable("minigc.command.active") : Text.translatable("minigc.command.inactive")
                            ), false);
                            return 1;
                        })
                )
        );
    }
}