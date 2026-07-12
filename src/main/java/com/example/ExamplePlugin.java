package com.example;

import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GroundItemSpawned;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import java.util.concurrent.locks.ReentrantLock;

@PluginDescriptor(
    name = "Dynamic Toggle Script",
    description = "Switches between two feature states based on ground item spawns"
)
public class ExamplePlugin extends Plugin {
    public static final String DISABLE_ID = "AttackingBuddy";
    public static final String ENABLE_ID = "LooterBuddy";

    private boolean isFeatureEnabled;
    private final ReentrantLock lock = new ReentrantLock();

    @Override
    protected void startUp() throws Exception {
        lock.lock();
        try {
            isFeatureEnabled = false;
        } finally {
            lock.unlock();
        }
    }

    @Override
    protected void shutDown() throws Exception {
        lock.lock();
        try {
            isFeatureEnabled = false;
        } finally {
            lock.unlock();
        }
    }

    @Subscribe
    public void onGroundItemSpawned(GroundItemSpawned event) {
        if (client.getGameState() == GameState.LOGGED_IN) {
            lock.lock();
            try {
                // Check for specific ground items and toggle features accordingly
                if (event.getItem().getId() == 12345 && event.getQuantity() >= 1) { // Replace 12345 with the actual ID of the first feature
                    isFeatureEnabled = !isFeatureEnabled;
                    toggleFeature();
                }
            } finally {
                lock.unlock();
            }
        }
    }

    private void toggleFeature() {
        if (lock.tryLock()) {
            try {
                if (isFeatureEnabled) {
                    // Enable LooterBuddy and disable AttackingBuddy
                    client.addChatMessage(Client.CHAT_MESSAGE_GAME, "Enabling LooterBuddy and disabling AttackingBuddy");
                    // Call the API method to disable AttackingBuddy first
                    // TODO: Replace with actual method to disable feature
                    client.getGameService().disableFeature(DISABLE_ID);
                    // Then call the API method to enable LooterBuddy
                    // TODO: Replace with actual method to enable feature
                    client.getGameService().enableFeature(ENABLE_ID);
                } else {
                    // Disable LooterBuddy and enable AttackingBuddy
                    client.addChatMessage(Client.CHAT_MESSAGE_GAME, "Disabling LooterBuddy and enabling AttackingBuddy");
                    // Call the API method to disable LooterBuddy first
                    // TODO: Replace with actual method to disable feature
                    client.getGameService().disableFeature(ENABLE_ID);
                    // Then call the API method to enable AttackingBuddy
                    // TODO: Replace with actual method to enable feature
                    client.getGameService().enableFeature(DISABLE_ID);
                }
            } finally {
                lock.unlock();
            }
        }
    }
}
