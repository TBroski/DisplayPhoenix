package net.displayphoenix.ui.widget;

import net.displayphoenix.Application;
import net.displayphoenix.ui.ColorTheme;
import net.displayphoenix.ui.animation.Clipper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;

/**
 * @author TBroski
 */
public class RoundedButton extends JButton {

    private Clipper clipper = new Clipper(this, 0.005F, 1F, 0F).smooth(); //0.005F

    private ColorTheme colorTheme;
    private Color hoverColor;
    private Color borderColor;
    private int borderWidth;

    public RoundedButton() {
        super();
        this.colorTheme = Application.getTheme().getColorTheme();
        init();
    }

    public RoundedButton(String text) {
        super(text);
        this.colorTheme = Application.getTheme().getColorTheme();
        init();
    }

    public RoundedButton setBorder(Color color) {
        this.borderColor = color;
        return this;
    }

    public RoundedButton setBorder(Color color, int borderWidth) {
        this.borderColor = color;
        this.borderWidth = borderWidth;
        return this;
    }

    public RoundedButton setHoverColor(Color color) {
        this.hoverColor = color;
        return this;
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(this.borderColor);
        g.fillRoundRect(0,0,this.getWidth(), this.getHeight(), 15,15);
        g.setColor(this.colorTheme.getSecondaryColor());
        g.fillRoundRect(this.borderWidth / 2,this.borderWidth / 2, this.getWidth() - this.borderWidth, this.getHeight() - this.borderWidth, 15,15);
        ((Graphics2D) g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, clipper.getCurrentValue()));
        g.setColor(this.hoverColor);
        g.fillRoundRect(this.borderWidth / 2,this.borderWidth / 2, this.getWidth() - this.borderWidth, this.getHeight() - this.borderWidth, 15,15);
        ((Graphics2D) g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
        g.setColor(this.getForeground());
        Rectangle2D bounds = g.getFont().getStringBounds(this.getText(), new FontRenderContext(g.getFont().getTransform(), true, true));
        g.drawString(this.getText(), (int) (this.getWidth() - bounds.getWidth()) / 2, (int) ((this.getHeight()) / 2 + (bounds.getHeight() / 4)));
    }

    private void init() {
        this.setForeground(this.colorTheme.getAccentColor());
        this.hoverColor = this.colorTheme.getAccentColor().darker().darker();
        this.borderColor = this.colorTheme.getAccentColor().darker().darker().darker().darker().darker();
        this.borderWidth = 4;
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));
        this.setContentAreaFilled(false);
        this.setBorderPainted(false);
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                clipper.increment();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                clipper.decrement();
            }
        });
    }

}