package net.displayphoenix.image.components;

import net.displayphoenix.Application;
import net.displayphoenix.image.interfaces.ISettingComponent;
import net.displayphoenix.image.tools.FontSetting;
import net.displayphoenix.image.tools.Setting;

import javax.swing.*;
import java.awt.*;

public class FontSettingComponent extends JPanel implements ISettingComponent<Font> {

    private JComboBox<String> fontList;
    private FontSetting setting;

    public FontSettingComponent(FontSetting setting) {
        this.setting = setting;
        setForeground(Application.getTheme().getColorTheme().getSecondaryColor());
        setBackground(Application.getTheme().getColorTheme().getAccentColor());
        setOpaque(false);
        String[] fontNames = new String[setting.getFonts().length];
        int arialIndex = 0;
        for (int i = 0; i < setting.getFonts().length; i++) {
            String name = setting.getFonts()[i].getFontName();
            fontNames[i] = name;
            if (name.equalsIgnoreCase("Arial")) {
                arialIndex = i;
            }
        }
        this.fontList = new JComboBox(fontNames);
        this.fontList.setSelectedIndex(arialIndex);
        add(this.fontList);
    }

    @Override
    public Font getValue() {
        for (Font font : this.setting.getFonts()) {
            if (font.getFontName().equalsIgnoreCase((String) this.fontList.getSelectedItem())) {
                return font;
            }
        }
        return null;
    }

    @Override
    public Setting getSetting() {
        return this.setting;
    }
}
