package net.runelite.client.plugins.safers.rooftopagility;

import net.runelite.api.Client;
import net.runelite.api.ItemID;
import net.runelite.api.Skill;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.WidgetItemOverlay;

import javax.inject.Inject;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class NomAgilityFoodOverlay extends WidgetItemOverlay {
    private final ItemManager itemManager;
    private final NomAgilityPlugin plugin;
    private final NomAgilityConfig config;
    private final Client client;

    @Inject
    public NomAgilityFoodOverlay(ItemManager itemManager, NomAgilityPlugin plugin, NomAgilityConfig config, Client client) {
        this.itemManager = itemManager;
        this.plugin = plugin;
        this.config = config;
        this.client = client;
        showOnInventory();
    }

    private boolean showingUntilFull = false;
	private List<Integer> pies = Arrays.asList(ItemID.SUMMER_PIE,ItemID.HALF_A_SUMMER_PIE);
    @Override
    public void renderItemOverlay(Graphics2D graphics2D, int itemId, WidgetItem widgetItem) {
		if (plugin.isDisableOverlays()) return;
		if (plugin.isShowingMarkOfGrace()) return;

		int realAgil = client.getRealSkillLevel(Skill.AGILITY);
		int currAgil = client.getBoostedSkillLevel(Skill.AGILITY);

		if (pies.contains(itemId) && (currAgil-realAgil) < config.minimumBoost()) {
			highlightFoodboost(graphics2D, widgetItem);
		}

        int currHP = client.getBoostedSkillLevel(Skill.HITPOINTS);
        int maxHP = client.getRealSkillLevel(Skill.HITPOINTS);

		int foodHeal = Food.heals(itemId, maxHP);
        if (foodHeal >= 1)
		{
			if (maxHP - currHP >= foodHeal ||
				currHP <= 5
			) {
				highlightFood(graphics2D, widgetItem);
			}
		}
    }
    private void highlightFoodboost(Graphics2D graphics2D, WidgetItem widgetItem) {
        Rectangle bounds = widgetItem.getCanvasBounds();
        if (bounds == null) return;
        SquareOverlay.drawRandomBounds(graphics2D, bounds, plugin.square(), new Color(0,0,255));
    }
    private void highlightFood(Graphics2D graphics2D, WidgetItem widgetItem) {
        Rectangle bounds = widgetItem.getCanvasBounds();
        if (bounds == null) return;
        SquareOverlay.drawRandomBounds(graphics2D, bounds, plugin.square(), config.getOverlayColor());
    }
}
