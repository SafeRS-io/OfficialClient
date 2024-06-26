package net.runelite.client.plugins.safers.SetupBuilder;

import net.runelite.api.Client;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.PluginPanel;

import javax.inject.Inject;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.List;

public class SetupBuilderPanel extends PluginPanel {
    private JButton fetchButton;
    private JButton copyButton;
    private JTextArea compiledIdsArea;
    private JPanel equipmentPanel;
    private JScrollPane equipmentScrollPane;
    private JPanel equipmentContainer;
    private List<JCheckBox> equipmentCheckboxes;

    private JTextField locationField;
    private JButton locationButton;

    @Inject
    private ItemManager itemManager;
    @Inject
    private Client client;

    public SetupBuilderPanel(SetupBuilderPlugin plugin) {
        super();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        JPanel titlePanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Welcome to SafeRS", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 15));
        titlePanel.add(titleLabel, BorderLayout.CENTER); // Add label to the center of the title panel

        add(titlePanel); // Add title panel to the main panel
        add(Box.createRigidArea(new Dimension(0, 10))); // Adjust the height (10) as needed

        // Item Fetcher Section
        JPanel itemFetcherPanel = new JPanel(new BorderLayout());
        JLabel itemFetcherTitle = new JLabel("SafeRS Item Fetcher", JLabel.CENTER);
        itemFetcherTitle.setFont(new Font("Arial", Font.BOLD, 12));
        itemFetcherPanel.add(itemFetcherTitle, BorderLayout.NORTH);
        add(Box.createRigidArea(new Dimension(0, 5))); // Adjust the height (10) as needed

        equipmentCheckboxes = new ArrayList<>();
        equipmentContainer = new JPanel(new BorderLayout());

        fetchButton = new JButton("Fetch Equipped Items");
        fetchButton.addActionListener(e -> plugin.fetchAndDisplayEquippedItems());
        equipmentContainer.add(fetchButton, BorderLayout.NORTH);

        equipmentPanel = new JPanel();
        equipmentPanel.setLayout(new BoxLayout(equipmentPanel, BoxLayout.Y_AXIS));
        equipmentScrollPane = new JScrollPane(equipmentPanel);
        equipmentScrollPane.setPreferredSize(new Dimension(100, 0));
        equipmentContainer.add(equipmentScrollPane, BorderLayout.CENTER);
        itemFetcherPanel.add(equipmentContainer, BorderLayout.CENTER);

        JPanel southPanel = new JPanel(new BorderLayout());
        copyButton = new JButton("Copy Items");
        copyButton.addActionListener(e -> copySelectedItemsString());
        southPanel.add(copyButton, BorderLayout.NORTH);

        compiledIdsArea = new JTextArea();
        compiledIdsArea.setEditable(false);
        southPanel.add(new JScrollPane(compiledIdsArea), BorderLayout.CENTER);
        compiledIdsArea.setPreferredSize(new Dimension(100, 25));
        itemFetcherPanel.add(southPanel, BorderLayout.SOUTH);

        add(itemFetcherPanel);

        // Location Fetcher Section
        JPanel locationFetcherPanel = new JPanel(new BorderLayout());
        JLabel locationFetcherTitle = new JLabel("SafeRS Location Fetcher", JLabel.CENTER);
        locationFetcherTitle.setFont(new Font("Arial", Font.BOLD, 12));
        locationFetcherPanel.add(locationFetcherTitle, BorderLayout.NORTH);
        add(Box.createRigidArea(new Dimension(0, 5))); // Adjust the height (10) as needed

        locationField = new JTextField();
        locationField.setEditable(false);
        locationFetcherPanel.add(locationField, BorderLayout.CENTER);

        locationButton = new JButton("Get Current Location");
        locationButton.addActionListener(e -> plugin.fetchAndDisplayPlayerLocation());
        locationFetcherPanel.add(locationButton, BorderLayout.SOUTH);

        add(locationFetcherPanel);
    }

    public void updateLocationField(String location) {
        locationField.setText(location);
    }

    private void updateEquipmentScrollPaneHeight() {
        int newHeight = equipmentCheckboxes.size() * 30;
        equipmentScrollPane.setPreferredSize(new Dimension(100, newHeight));
        equipmentContainer.revalidate();
    }

    public void updateEquipmentList(String equippedItems) {
        equipmentPanel.removeAll();
        equipmentCheckboxes.clear();

        String[] items = equippedItems.split("\n");
        for (String item : items) {
            String[] parts = item.split(" - ");
            if (parts.length == 2) {
                int itemId = Integer.parseInt(parts[0]);
                String itemName = parts[1];
                JCheckBox checkBox = new JCheckBox(itemName + " (" + itemId + ")");
                checkBox.setActionCommand(String.valueOf(itemId));
                equipmentPanel.add(checkBox);
                equipmentCheckboxes.add(checkBox);
            }
        }
        updateEquipmentScrollPaneHeight();
        equipmentPanel.revalidate();
        equipmentPanel.repaint();
    }

    private void copySelectedItemsString() {
        String selectedIds = getSelectedItemsString();
        StringSelection stringSelection = new StringSelection(selectedIds);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
        compiledIdsArea.setText(selectedIds);
    }

    private String getSelectedItemsString() {
        StringBuilder compiledIds = new StringBuilder();
        for (JCheckBox checkBox : equipmentCheckboxes) {
            if (checkBox.isSelected()) {
                if (compiledIds.length() > 0) {
                    compiledIds.append(", ");
                }
                compiledIds.append(checkBox.getActionCommand());
            }
        }
        return compiledIds.toString();
    }
}
