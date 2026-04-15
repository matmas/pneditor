package org.pneditor.editor.canvas;

import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.JScrollBar;
import org.pneditor.editor.PNEditor;
import org.pneditor.util.Point;

/**
 *
 * @author matmas
 */
public class ScrollingFeature implements Feature, MouseListener, MouseMotionListener, AdjustmentListener {

    private Canvas canvas;

    public ScrollingFeature(Canvas canvas) {
        this.canvas = canvas;
        canvas.addMouseListener(this);
        canvas.addMouseMotionListener(this);
    }

    private int prevDragX;
    private int prevDragY;
    private boolean scrolling;

    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON2
                || e.getButton() == MouseEvent.BUTTON1 && e.isControlDown()) {
            prevDragX = e.getX();
            prevDragY = e.getY();
            scrolling = true;
        }
    }

    public void mouseDragged(MouseEvent e) {
        if (scrolling) {
            doTheScrolling(e.getX(), e.getY());
            prevDragX = e.getX();
            prevDragY = e.getY();
        }
    }

    public void mouseReleased(MouseEvent e) {
        if (scrolling) {
            doTheScrolling(e.getX(), e.getY());
            scrolling = false;
        }
    }

    private void doTheScrolling(int mouseX, int mouseY) {
        Point viewTranslation = canvas.getViewTranslation();
        canvas.setViewTranslation(viewTranslation.getTranslated(mouseX - prevDragX, mouseY - prevDragY));
        canvas.repaint();
    }

    public void drawForeground(Graphics g) {
    }

    public void drawBackground(Graphics g) {
    }

    public void drawMainLayer(Graphics g) {
        Rectangle petriNetBounds = PNEditor.getRoot().getDocument().petriNet.getCurrentSubnet().getBounds();
        Rectangle canvasBounds = canvas.getBounds();

        JScrollBar horizontalScrollBar = PNEditor.getRoot().getDrawingBoard().getHorizontalScrollBar();
        JScrollBar verticalScrollBar = PNEditor.getRoot().getDrawingBoard().getVerticalScrollBar();

        canvasBounds.translate(-canvas.getViewTranslation().getX(), -canvas.getViewTranslation().getY()); // to account for translation
        petriNetBounds.translate(canvas.getWidth() / 2, canvas.getHeight() / 2); // [0, 0] is in center

        // Union of the two rectangles:
        if (!petriNetBounds.isEmpty()) {
            petriNetBounds.add(canvasBounds);
        }

        horizontalScrollBar.setEnabled(false);
        horizontalScrollBar.setMinimum(petriNetBounds.x);
        horizontalScrollBar.setMaximum(petriNetBounds.x + petriNetBounds.width);
        horizontalScrollBar.setVisibleAmount(canvasBounds.width);
        horizontalScrollBar.setValue(-canvas.getViewTranslation().getX());
        horizontalScrollBar.setEnabled(true);

        verticalScrollBar.setEnabled(false);
        verticalScrollBar.setMinimum(petriNetBounds.y);
        verticalScrollBar.setMaximum(petriNetBounds.y + petriNetBounds.height);
        verticalScrollBar.setVisibleAmount(canvasBounds.height);
        verticalScrollBar.setValue(-canvas.getViewTranslation().getY());
        verticalScrollBar.setEnabled(true);
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void adjustmentValueChanged(AdjustmentEvent e) {
        int value = e.getValue();
        JScrollBar scrollBar = (JScrollBar) e.getSource();
        if (!scrolling && scrollBar.isEnabled()) {
            Point viewTranslation = canvas.getViewTranslation();
            if (e.getSource() == PNEditor.getRoot().getDrawingBoard().getHorizontalScrollBar()) {
                viewTranslation = new Point(-value, viewTranslation.getY());
            }
            if (e.getSource() == PNEditor.getRoot().getDrawingBoard().getVerticalScrollBar()) {
                viewTranslation = new Point(viewTranslation.getX(), -value);
            }
            canvas.setViewTranslation(viewTranslation);
            canvas.repaint();
        }
    }

    @Override
    public void mouseDragged(int x, int y) {
    }

    @Override
    public void mouseReleased(int x, int y) {
    }

    @Override
    public void mouseMoved(int x, int y) {
    }

    @Override
    public void setHoverEffects(int x, int y) {
    }

    @Override
    public void setCursor(int x, int y) {
        if (scrolling) {
            canvas.alternativeCursor = Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);
        }
    }
}
