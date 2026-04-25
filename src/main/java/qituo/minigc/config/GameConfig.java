package qituo.minigc.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import qituo.minigc.MiniGameCollection;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class GameConfig {
    private static final Path configPath = FabricLoader.getInstance().getConfigDir().resolve("minigc").resolve("minigc-common.properties");

    public static boolean gameModeSwitchEnabled = false;
    public static int switchIntervalSeconds = 30;
    public static boolean isGameActive = false;

    public static void loadConfig() {
        try {
            Files.createDirectories(configPath.getParent());

            if (!Files.exists(configPath)) {
                saveConfig();
                MiniGameCollection.LOGGER.info("Created default config file.");
                return;
            }

            Properties props = new Properties();
            props.load(Files.newInputStream(configPath));

            if (props.containsKey("gameModeSwitchEnabled")) {
                gameModeSwitchEnabled = Boolean.parseBoolean(props.getProperty("gameModeSwitchEnabled"));
            }
            if (props.containsKey("switchIntervalSeconds")) {
                try {
                    switchIntervalSeconds = Integer.parseInt(props.getProperty("switchIntervalSeconds"));
                } catch (NumberFormatException e) {
                    MiniGameCollection.LOGGER.warn("Invalid switchIntervalSeconds value, using default.");
                    switchIntervalSeconds = 30;
                }
            }

            MiniGameCollection.LOGGER.info("Config loaded successfully.");
        } catch (Exception e) {
            MiniGameCollection.LOGGER.error("Failed to load config: {}", e.getMessage(), e);
        }
    }

    public static void saveConfig() {
        try {
            Files.createDirectories(configPath.getParent());

            Properties props = new Properties();

            props.setProperty("gameModeSwitchEnabled", String.valueOf(gameModeSwitchEnabled));
            props.setProperty("switchIntervalSeconds", String.valueOf(switchIntervalSeconds));

            try (OutputStream output = Files.newOutputStream(configPath)) {
                props.store(output, """
                    Mini Game Collection Configuration
                    This file was automatically generated.
                    
                    Configuration options:
                    gameModeSwitchEnabled: Enable or disable the game mode switching feature (true/false)
                    switchIntervalSeconds: How often to switch game modes (1-300 seconds)
                    """);
            }

            MiniGameCollection.LOGGER.info("Config saved successfully.");
        } catch (IOException e) {
            MiniGameCollection.LOGGER.error("Failed to save config: {}", e.getMessage(), e);
        }
    }

    public static Screen createConfigScreen(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.translatable("minigc.config.title"))
                .setSavingRunnable(GameConfig::saveConfig);

        ConfigCategory generalCategory = builder.getOrCreateCategory(Text.translatable("minigc.config.category.general"));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        generalCategory.addEntry(entryBuilder.startBooleanToggle(
                Text.translatable("minigc.config.game_mode_switch.enabled"),
                gameModeSwitchEnabled
        ).setDefaultValue(false)
                .setTooltip(Text.translatable("minigc.config.game_mode_switch.enabled.tooltip"))
                .setSaveConsumer(value -> {
                    if (!isGameActive) {
                        gameModeSwitchEnabled = value;
                    }
                })
                .build());

        generalCategory.addEntry(entryBuilder.startIntField(
                Text.translatable("minigc.config.game_mode_switch.interval"),
                switchIntervalSeconds
        ).setDefaultValue(30)
                .setMin(1)
                .setMax(300)
                .setTooltip(Text.translatable("minigc.config.game_mode_switch.interval.tooltip"))
                .setSaveConsumer(value -> switchIntervalSeconds = value)
                .build());

        return builder.build();
    }

    public static void setGameActive(boolean active) {
        isGameActive = active;
    }

    public static boolean isGameActive() {
        return isGameActive;
    }
}