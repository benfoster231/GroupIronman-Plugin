package com.looter;

import com.google.inject.Provides;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.ItemComposition;
import net.runelite.api.Tile;
import net.runelite.api.TileItem;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ItemDespawned;
import net.runelite.api.events.ItemSpawned;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@Slf4j
@PluginDescriptor(
	name = "Looter",
	description = "Highlights valuable ground items so they're easy to click and loot",
	tags = {"loot", "looter", "ground", "items", "pickup", "highlight", "drops"}
)
public class LooterPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private LooterConfig config;

	@Inject
	private ItemManager itemManager;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private LooterOverlay overlay;

	// Ground items currently tracked in the scene. CopyOnWrite so the overlay
	// (render thread) can iterate safely while events (client thread) mutate it.
	private final List<LootItem> lootItems = new CopyOnWriteArrayList<>();

	List<LootItem> getLootItems()
	{
		return lootItems;
	}

	@Override
	protected void startUp()
	{
		overlayManager.add(overlay);
		log.debug("Looter started");
	}

	@Override
	protected void shutDown()
	{
		overlayManager.remove(overlay);
		lootItems.clear();
		log.debug("Looter stopped");
	}

	@Subscribe
	public void onItemSpawned(ItemSpawned event)
	{
		final TileItem item = event.getItem();
		final Tile tile = event.getTile();
		final ItemComposition comp = itemManager.getItemComposition(item.getId());

		// Per-item GE price; fall back to store/alch value for untradeables.
		final int gePrice = itemManager.getItemPrice(item.getId());
		final int unitValue = Math.max(gePrice, comp.getPrice());
		final long totalValue = (long) unitValue * item.getQuantity();

		lootItems.add(new LootItem(
			item,
			tile.getWorldLocation(),
			comp.getName(),
			item.getQuantity(),
			totalValue));
	}

	@Subscribe
	public void onItemDespawned(ItemDespawned event)
	{
		final TileItem item = event.getItem();
		lootItems.removeIf(li -> li.getTileItem() == item);
	}

	@Provides
	LooterConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(LooterConfig.class);
	}
}
