package net.runelite.client.plugins.safers.mlm;

import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.annotation.Nullable;
import java.awt.*;

public class MotherlodeSackWidget extends OverlayPanel {
    private final SafeRSMotherlodeConfig config;
    private final Motherlode motherlode;
    private final Client client;
    private final MotherlodeSack sack;
    private final MotherlodeInventory inventory;
    private final PanelComponent panel = new PanelComponent();
    private WorldPoint lastPosition;
    private int lastAnimation = -1;
    private long lastMoveTime;
    private long lastAnimationEndTime;
    private static final int MOVE_TIMEOUT = 4300; // 3000ms after stopping movement
    private static final int ANIM_END_TIMEOUT = 4300; // 100ms after animation ends
    @Nullable
    private Widget widget = null;

    // Variables for tracking player status
    private long lastActivityTime = System.currentTimeMillis();
    private boolean isMining = false;

    public MotherlodeSackWidget(final SafeRSMotherlodeConfig config, final Motherlode motherlode, final Client client) {
        this.config = config;
        this.motherlode = motherlode;
        this.client = client;
        this.sack = motherlode.getSack();
        this.inventory = motherlode.getInventory();
        this.lastPosition = null;
        this.lastMoveTime = System.currentTimeMillis();
        this.lastAnimationEndTime = System.currentTimeMillis();
        setPosition(OverlayPosition.TOP_LEFT);
        setLayer(OverlayLayer.ABOVE_WIDGETS);
        panelComponent.setBorder(new Rectangle(0, 0, 0, 0));
    }

    private void updatePlayerStatus() {
        Player localPlayer = client.getLocalPlayer();
        if (localPlayer != null) {
            int animation = localPlayer.getAnimation();
            isMining = animation != -1; // Assuming mining animation IDs are not -1. Adjust this condition based on actual mining animations.
            if (isMining) {
                lastActivityTime = System.currentTimeMillis();
            }
        }
    }

    @Override
    public Dimension render(final Graphics2D graphics) {
        updatePlayerStatus(); // Update the player status before rendering

        if (!motherlode.inRegion() || !config.showSackNeeded()) return null;

        final int pay_dirt_needed = motherlode.getPayDirtNeeded();
        final int deposits_left = motherlode.getDepositsLeft();

        panelComponent.getChildren().clear();
        panel.getChildren().clear();

        // Add "Motherlode Mine" title and player status
        panel.getChildren().add(TitleComponent.builder()
                .text("SafeRS MLM")
                .color(new Color(0,128,192))
                .build());

        // Existing functionality to display sack information
        final Color color_background = (isPayDirtTotalPerfect(pay_dirt_needed) || pay_dirt_needed == 0 && inventory.countPayDirt() > 0) ? Color.GREEN :
                (sack.isFull() || pay_dirt_needed < 0) ? Color.ORANGE :
                        (sack.shouldBeEmptied()) ? Color.ORANGE : null;
        panel.setBackgroundColor(color_background == null ? null : new Color(color_background.getRed(), color_background.getGreen(), color_background.getBlue(), 255));


        if (config.showSackNeeded()) {
            panel.getChildren().add(LineComponent.builder()
                    .left("Needed:")
                    .leftColor(color_background != null ? Color.WHITE : Color.WHITE)
                    .right(String.valueOf(pay_dirt_needed))
                    .rightColor(color_background != null ? Color.WHITE : Color.PINK)
                    .build());
        }

        panelComponent.getChildren().add(panel);
        return super.render(graphics);
    }

    private boolean isPayDirtTotalPerfect(final int pay_dirt_needed) {
        return (
                this.getTotalPayDirtCount() == sack.getSize() && inventory.countPayDirt() != 0 ||
                        sack.countPayDirt() == sack.getSize() && inventory.countPayDirt() == inventory.getSize() ||
                        sack.countPayDirt() == sack.getSize() && pay_dirt_needed == 0
        );
    }

    private int getTotalPayDirtCount() {
        return inventory.countPayDirt() + sack.countPayDirt();
    }

    public void loadNativeWidget() {
        final Widget widget = client.getWidget(WidgetInfo.MOTHERLODE_MINE);

        if (widget != null) {
            this.widget = widget;
            updateMotherlodeNativeWidget(  config.showSackNeeded() );
        }
    }

    public void onWidgetLoaded(final WidgetLoaded event) {
        if (event.getGroupId() == WidgetInfo.MOTHERLODE_MINE.getGroupId()) {
            loadNativeWidget();
        }
    }

    public void onConfigChanged(final ConfigChanged event) {
        if (event.getGroup().equals(SafeRSMotherlodeConfig.group)) {
            updateMotherlodeNativeWidget( config.showSackNeeded() );
        }
    }

    public void updateMotherlodeNativeWidget(final boolean hidden) {
        if (widget != null) {
            widget.setHidden(hidden);
        }
    }
}
