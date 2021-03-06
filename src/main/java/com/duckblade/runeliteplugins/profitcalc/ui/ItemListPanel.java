package com.duckblade.runeliteplugins.profitcalc.ui;

import com.duckblade.runeliteplugins.profitcalc.CalcItemStack;
import com.duckblade.runeliteplugins.profitcalc.ProfitCalcPlugin;
import lombok.Getter;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.util.ImageUtil;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class ItemListPanel extends JPanel {

    @Getter
    private boolean inputMode = false;

    @Getter
    private final ProfitCalcPanel calcPanel;

    private final JLabel headerLabel;
    private final JLabel totalLabel;
    private final JPanel headerPanel;

    private final ArrayList<ItemStackPanel> items;
    private final JPanel itemPanel;
    private final JButton addButton;

    public ItemListPanel(ProfitCalcPanel parent) {
        this.calcPanel = parent;

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createLineBorder(ColorScheme.LIGHT_GRAY_COLOR, 1, false));
        setBackground(ColorScheme.DARK_GRAY_COLOR);

        headerPanel = new JPanel();
        headerPanel.setLayout(new GridLayout(2, 1));
        headerPanel.setBackground(ColorScheme.DARK_GRAY_COLOR);
        add(headerPanel, BorderLayout.NORTH);

        headerLabel = new JLabel(inputMode ? "Inputs" : "Outputs", JLabel.CENTER);
        Font f = headerLabel.getFont();
        headerLabel.setFont(f.deriveFont(f.getSize() * 1.2f));
        headerLabel.setForeground(Color.white);
        headerPanel.add(headerLabel);

        totalLabel = new JLabel("Total: 0gp", JLabel.CENTER);
        totalLabel.setForeground(Color.white);
        headerPanel.add(totalLabel);

        items = new ArrayList<>();
        itemPanel = new JPanel();
        itemPanel.setBackground(ColorScheme.DARK_GRAY_COLOR);
        itemPanel.setLayout(new BoxLayout(itemPanel, BoxLayout.Y_AXIS));
        add(itemPanel, BorderLayout.CENTER);

        addButton = new JButton(new ImageIcon(ImageUtil.getResourceStreamFromClass(ProfitCalcPlugin.class, "/add.png")));
        addButton.setBackground(ColorScheme.DARK_GRAY_COLOR);
        addButton.setToolTipText("Add " + (inputMode ? "Input" : "Output"));
        addButton.addActionListener(e -> triggerSearch());
        add(addButton, BorderLayout.SOUTH);
    }

    public void triggerSearch() {
        calcPanel.getPlugin().getCalcItemSearch().triggerSearch(this.inputMode);
    }

    public void setInputMode(boolean inputMode) {
        this.inputMode = inputMode;
        SwingUtilities.invokeLater(() -> {
            headerLabel.setText(this.inputMode ? "Inputs" : "Outputs");
            addButton.setToolTipText("Add " + (this.inputMode ? "Input" : "Output"));
        });
    }

    public void addItemFromSearch(CalcItemStack itemStack) {
        ItemStackPanel toAdd = new ItemStackPanel(this, itemStack);
        items.add(toAdd);
        recalculate();
    }

    public void removeItem(ItemStackPanel toRemove) {
        items.remove(toRemove);
        recalculate();
    }

    public void reset() {
        items.clear();
        recalculate();
    }

    public void recalculate() {
        SwingUtilities.invokeLater(() -> {
            itemPanel.removeAll();
            items.forEach(itemPanel::add);

            float value = getValue();
            totalLabel.setText("Total: " + ProfitCalcPlugin.DECIMAL_FORMAT.format(value) + "gp");

            repaint();
            revalidate();

            calcPanel.recalculate();
        });
    }

    public float getValue() {
        return (float) items.stream()
                .mapToDouble(ItemStackPanel::getValue)
                .sum();
    }

}
