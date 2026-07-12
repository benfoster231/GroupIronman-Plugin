package com.example;

import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GroundItemSpawned;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@PluginDescriptor(
    name = "Dynamic Toggle Script",
    description = "Switches between two feature states based on ground item spawns"
)
public class ExamplePlugin extends Plugin {
    public static final String DISABLE_ID = "AttackingBuddy";
    public static final String ENABLE_ID = "LooterBuddy";

    private boolean isFeatureEnabled;

    @Override
    protected void startUp() throws Exception {
        isFeatureEnabled = false;
    }

    @Override
    protected void shutDown() throws Exception {
        isFeatureEnabled = false;
    }

    @Subscribe
    public void onGroundItemSpawned(GroundItemSpawned event) {
        if (client.getGameState() == GameState.LOGGED_IN) {
            // Check for specific ground items and toggle features accordingly
            if (event.getItem().getId() == 12345) { // Replace 12345 with the actual ID of the first feature
                isFeatureEnabled = !isFeatureEnabled;
                toggleFeature();
            }
        }
    }

    private void toggleFeature() {
        if (isFeatureEnabled) {
            // Enable LooterBuddy
            client.addChatMessage(Client.CHAT_MESSAGE_GAME, "Enabling LooterBuddy");
        } else {
            // Disable LooterBuddy and enable AttackingBuddy
            client.addChatMessage(Client.CHAT_MESSAGE_GAME, "Disabling LooterBuddy and enabling AttackingBuddy");
        }
    }
}
