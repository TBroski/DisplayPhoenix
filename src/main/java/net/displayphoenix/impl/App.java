package net.displayphoenix.impl;

import net.displayphoenix.Application;
import net.displayphoenix.blockly.Blockly;
import net.displayphoenix.enums.WidgetStyle;
import net.displayphoenix.file.DetailedFile;
import net.displayphoenix.file.FileDialog;
import net.displayphoenix.image.BackgroundCanvasPanel;
import net.displayphoenix.image.CanvasPanel;
import net.displayphoenix.image.DropElementPanel;
import net.displayphoenix.impl.blockly.Blocks;
import net.displayphoenix.impl.elements.EventElement;
import net.displayphoenix.init.ColorInit;
import net.displayphoenix.screen.impl.CircleLoadingSplashScreen;
import net.displayphoenix.ui.ColorTheme;
import net.displayphoenix.ui.Theme;
import net.displayphoenix.util.ImageHelper;
import net.displayphoenix.web.Website;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class App {

    public static Website DOWNLOAD_PAGE = new Website("https://google.com");
    public static Website NODEJS_DOWNLOAD_PAGE = new Website("https://nodejs.org/en/download/");

    public static State STATE = State.STABLE;

    //        Theme theme = new Theme(new ColorTheme(new Color(92, 219, 149), new Color(5, 56, 107), new Color(237, 245, 225)), WidgetStyle.POPPING, new Font(Font.MONOSPACED, Font.PLAIN, 14), 1200, 900);
    public static void main(String args[]) {
        CircleLoadingSplashScreen splashScreen = new CircleLoadingSplashScreen(ImageHelper.getImage("atme/alt_discord_buddy").getImage(), 300, 300, 140, 10, Color.BLUE, ColorInit.TRANSPARENT);
        splashScreen.setLoadingProgress(25);
        Theme theme = new Theme(new ColorTheme(new Color(38, 38, 38), new Color(192, 226, 113), new Color(255, 255, 255), Color.GRAY), WidgetStyle.POPPING, new Font(Font.MONOSPACED, Font.PLAIN, 14), 900, 600);
        System.out.println("Theme created");
        splashScreen.setLoadingProgress(50);
        new EventElement("event");
        System.out.println("Elements created");
        splashScreen.setLoadingProgress(75);
        Blockly.queueFlowControl();
        Blockly.queueLogic();
        Blockly.queueMath();
        Blockly.queueText();
        Blocks.register();
        System.out.println("Blockly created");
        splashScreen.setLoadingProgress(100);
        Application.create("AtMe", ImageHelper.getImage("atme/alt_discord_buddy"), theme, "Alpha 1.0");

        /*LobbyScreen.open();*/
        Application.openWindow(parentFrame -> {
            DropElementPanel canvas = new DropElementPanel(ImageHelper.getImage("atme/discord_buddy"), ImageHelper.getImage("atme/discord_buddy")); //new ImageIcon(ImageIO.read(png.getFile())

            //canvas.addToCanvas(ImageHelper.getImage("atme/discord_buddy"), 0, 20, 20);
            parentFrame.add(canvas);
        });
    }

    public enum State {
        STABLE, BUILDING, RUNNING
    }
}