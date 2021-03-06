package com.patetlex.displayphoenix.canvasly.tools.impl;

import com.patetlex.displayphoenix.canvasly.CanvasPanel;
import com.patetlex.displayphoenix.canvasly.ToolPanel;
import com.patetlex.displayphoenix.canvasly.elements.impl.ImageElement;
import com.patetlex.displayphoenix.canvasly.interfaces.ISettingComponent;
import com.patetlex.displayphoenix.canvasly.tools.Setting;
import com.patetlex.displayphoenix.canvasly.tools.Tool;
import com.patetlex.displayphoenix.file.DetailedFile;
import com.patetlex.displayphoenix.file.FileDialog;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

public class ImageTool extends Tool {

    private static File previousDirectory = new File(System.getProperty("user.home"));

    @Override
    public String getName() {
        return "image";
    }

    @Override
    public ImageIcon getIcon() {
        return getImage("image/image");
    }

    @Override
    public void onLeftClick(ToolPanel toolkit, CanvasPanel canvas, int x, int y, ISettingComponent[] settingComponents) {
/*        File image = openFile(getIcon(), "png");
        try {
            canvas.setElement(canvas.getSelectedLayer(), new ImageElement(ImageIO.read(image), image.getPath()), x, y);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        FileDialog.openFile(new Consumer<DetailedFile>() {
            @Override
            public void accept(DetailedFile file) {
                try {
                    canvas.setElement(canvas.getSelectedLayer(), new ImageElement(ImageIO.read(file.getFile()), file.getFile().getPath()), x, y);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, "png");
    }

    @Override
    public List<Setting> getSettings() {
        return null;
    }

/*    private static File openFile(ImageIcon icon, String... extensions) {
        JFrame parentWindow = new JFrame();
        parentWindow.setIconImage(icon.getImage());
        File[] files = getBasicFileDialog(parentWindow, getFileFiltersForStringArray(extensions), true, false);
        if (files != null) {
            return files[0];
        }
        return null;
    }

    private static java.io.File[] getBasicFileDialog(Window parent, FileFilter[] filters, boolean open, boolean multiSelect) {
        JFileChooser fileGetter = new JFileChooser();
        if (filters != null) {
            for (FileFilter filter : filters) {
                if (filter != null)
                    fileGetter.addChoosableFileFilter(filter);
            }
        }
        fileGetter.setPreferredSize(new Dimension(720, 420));
        fileGetter.setCurrentDirectory(previousDirectory);
        fileGetter.setMultiSelectionEnabled(multiSelect);
        fileGetter.setAcceptAllFileFilterUsed(false);
        int response = open ? fileGetter.showOpenDialog(parent) : fileGetter.showSaveDialog(parent);
        previousDirectory = fileGetter.getCurrentDirectory();
        if (response == JFileChooser.APPROVE_OPTION) {
            if (multiSelect) {
                File[] files = fileGetter.getSelectedFiles();
                if (files != null && files.length > 0)
                    return files;
            } else
                return new File[] { fileGetter.getSelectedFile() };
        }
        return null;
    }

    private static FileFilter[] getFileFiltersForStringArray(String[] filters) {
        if (filters != null) {
            FileFilter[] fileFilters = new FileFilter[filters.length];
            int idx = 0;
            for (String extension : filters) {
                extension = extension.toLowerCase(Locale.ENGLISH);

                if (extension.startsWith("."))
                    extension = extension.replaceFirst("\\.", "");

                String finalExtension = extension;
                fileFilters[idx] = new FileFilter() {
                    @Override
                    public boolean accept(File f) {
                        return f.getName().toLowerCase(Locale.ENGLISH).endsWith("." + finalExtension) || f.isDirectory();
                    }

                    @Override
                    public String getDescription() {
                        return finalExtension.toUpperCase(Locale.ENGLISH) + " files (*." + finalExtension
                                .toLowerCase(Locale.ENGLISH) + ")";
                    }
                };
                idx++;
            }
            return fileFilters;
        }
        return new FileFilter[] {new FileFilter() {
            @Override
            public boolean accept(File f) {
                return true;
            }

            @Override
            public String getDescription() {
                return "Any file";
            }
        }};
    }*/
}
