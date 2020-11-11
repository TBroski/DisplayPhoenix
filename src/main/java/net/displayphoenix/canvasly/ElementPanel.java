package net.displayphoenix.canvasly;

import net.displayphoenix.Application;
import net.displayphoenix.canvasly.elements.Element;
import net.displayphoenix.canvasly.elements.Layer;
import net.displayphoenix.canvasly.elements.StaticElement;
import net.displayphoenix.util.ComponentHelper;
import net.displayphoenix.util.PanelHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;

public class ElementPanel extends JPanel {

    public ElementPanel(CanvasPanel canvas, StaticElement... elements) {
        setOpaque(true);
        setBackground(Application.getTheme().getColorTheme().getPrimaryColor());
        setForeground(canvas.getForeground());

        JList<StaticElement> elementList = ComponentHelper.createJList(new ElementRenderer(), Arrays.asList(elements.clone()));
        elementList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (e.getClickCount() == 2) {
                    StaticElement element = elementList.getSelectedValue();
                    Element clone = element.getElement().clone();
                    canvas.addStaticElement(canvas.getSelectedLayer(), new StaticElement(clone, element.getX(), element.getY(), element.getProperties()));
                }
            }
        });
        elementList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        elementList.setOpaque(false);
        ComponentHelper.addScrollPane(elementList);
        JPanel elementListPanel = PanelHelper.join(elementList);
        elementListPanel.setBackground(canvas.getBackground());
        elementListPanel.setForeground(canvas.getForeground());
        elementListPanel.setOpaque(false);
        add(elementListPanel);
    }

    protected Layer getLayerToAdd(CanvasPanel canvas) {
        return canvas.getSelectedLayer();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());
    }

    private static class ElementRenderer implements ListCellRenderer<StaticElement> {

        @Override
        public Component getListCellRendererComponent(JList<? extends StaticElement> list, StaticElement value, int index, boolean isSelected, boolean cellHasFocus) {
            ElementComponent elementComponent = new ElementComponent(value);
            elementComponent.setPreferredSize(new Dimension(125, 125));
            elementComponent.setOpaque(isSelected);
            elementComponent.setBackground(Application.getTheme().getColorTheme().getAccentColor());
            return elementComponent;
        }
    }

    private static class ElementComponent extends JPanel {

        private CanvasPanel canvasToDraw;
        private Element element;

        public ElementComponent(StaticElement element) {
            this.element = element.getElement();
            this.canvasToDraw = new CanvasPanel(this.getWidth(), this.getHeight());
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            this.canvasToDraw.setPreferredSize(new Dimension(this.element.getWidth(this.canvasToDraw, g), this.element.getHeight(this.canvasToDraw, g)));
            ((Graphics2D) g).translate(getWidth() / 2F, getHeight() / 2F);
            ((Graphics2D) g).translate(-this.element.getWidth(this.canvasToDraw, g) / 2F, -this.element.getHeight(this.canvasToDraw, g) / 2F);
            this.element.draw(this.canvasToDraw, g);
        }
    }
}