package com.example;

import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GroundItemSpawned;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import java.util.concurrent.locks.ReentrantLock;

@PluginDescriptor(
    name = "Dynamic Toggle Script",
    description = "Switches between two feature states based on ground item spawns",
    enabledByDefault = false
)
public class ExamplePlugin extends Plugin {
    public static final String DISABLE_ID = "AttackingBuddy";
    public static final String ENABLE_ID = "LooterBuddy";

    private boolean isFeatureEnabled;
    private final ReentrantLock toggleLock = new ReentrantLock();
    @Inject
    private Client client;

    @Override
    protected void startUp() throws Exception {
        log.info("Toggle Script started!");
        toggleLock.lock();
        try {
            isFeatureEnabled = false;
        } finally {
            toggleLock.unlock();
        }
    }

    @Override
    protected void shutDown() throws Exception {
        log.info("Toggle Script stopped!");
        toggleLock.lock();
        try {
            isFeatureEnabled = false;
        } finally {
            toggleLock.unlock();
        }
    }

    @Subscribe
    public void onGroundItemSpawned(GroundItemSpawned event) {
        if (client.getGameState() != GameState.LOGGED_IN) {
            return;
        }

        toggleLock.lock();
        try {
            // Logic check: Trigger on any item spawn if quantity is 1 or more
            if (event.getQuantity() >= 1) {
                isFeatureEnabled = !isFeatureEnabled;
                toggleFeature();
            }
        } finally {
            toggleLock.unlock();
        }
    }

    private void toggleFeature() {
        toggleLock.lock();
        try {
            if (isFeatureEnabled) {
                // Enable LooterBuddy and disable AttackingBuddy
                client.addChatMessage(Client.CHAT_MESSAGE_GAME, "Enabling LooterBuddy and disabling AttackingBuddy");
                // Call the API method to disable AttackingBuddy first
                client.getGameService().disableFeature(DISABLE_ID);
                // Then call the API method to enable LooterBuddy
                client.getGameService().enableFeature(ENABLE_ID);
            } else {
                // Disable LooterBuddy and enable AttackingBuddy
                client.addChatMessage(Client.CHAT_MESSAGE_GAME, "Disabling LooterBuddy and enabling AttackingBuddy");
                // Call the API method to disable LooterBuddy first
                client.getGameService().disableFeature(ENABLE_ID);
                // Then call the API method to enable AttackingBuddy
                client.getGameService().enableFeature(DISABLE_ID);
            }
        } finally {
            toggleLock.unlock();
        }
    }
}
