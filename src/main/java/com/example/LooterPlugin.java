package com.example;

import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.Subscribe;
import javax.inject.Inject;

public class LooterPlugin extends Plugin {
    @Inject
    private Client client;

    @Override
    protected void startUp() throws Exception {
        // Initialization code if needed
    }

    @Override
    protected void shutDown() throws Exception {
        // Cleanup code if needed
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        GameObject[] groundItems = client.getGroundItems();
        for (GameObject item : groundItems) {
            if (item != null && isLootable(item)) {
                highlightItem(item);
                clickItem(item);
                break; // Only loot one item per tick
            }
        }
    }

    private boolean isLootable(GameObject item) {
        // Logic to determine if the item is loottable
        return true; // Placeholder logic
    }

    private void highlightItem(GameObject item) {
        // Highlighting logic (already implemented)
    }

    private void clickItem(GameObject item) {
        // Clicking logic
        client.getMouseManager().setDestination(item.getLocalLocation());
        client.getKeyboardManager().pressKey('E'); // Assuming 'E' is the key to loot items
        client.getKeyboardManager().releaseKey('E');
    }
}
