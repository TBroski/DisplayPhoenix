package net.displayphoenix.image.tools.impl;

import net.displayphoenix.file.DetailedFile;
import net.displayphoenix.file.FileDialog;
import net.displayphoenix.image.CanvasPanel;
import net.displayphoenix.image.ToolPanel;
import net.displayphoenix.image.elements.impl.ImageElement;
import net.displayphoenix.image.interfaces.ISettingComponent;
import net.displayphoenix.image.tools.Setting;
import net.displayphoenix.image.tools.Tool;
import net.displayphoenix.util.ImageHelper;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.IOException;
import java.util.List;

public class ImageTool extends Tool {
    @Override
    public ImageIcon getIcon() {
        return ImageHelper.getImage("image/image");
    }

    @Override
    public void onLeftClick(ToolPanel toolkit, CanvasPanel canvas, int x, int y, ISettingComponent[] settingComponents) {
        DetailedFile image = FileDialog.openFile(null, "png");
        try {
            canvas.setElement(canvas.getSelectedLayer(), new ImageElement(new ImageIcon(ImageIO.read(image.getFile()))), x, y);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Setting> getSettings() {
        return null;
    }

}
