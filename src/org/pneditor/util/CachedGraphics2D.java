/*
 * Copyright (C) 2008-2010 Martin Riesz <riesz.martin at gmail.com>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.pneditor.util;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.RenderingHints.Key;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class CachedGraphics2D extends Graphics2D {

    private Graphics graphics = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB).getGraphics();
    private Font currentFont = new Font(null);
    private Color currentColor = Color.white;
    private List<Drawable> toBeDrawn = new ArrayList<Drawable>();
    private float currentLineWidth = 1;
    private Rectangle integerBounds = new Rectangle(-1, -1);
    private Rectangle2D realBounds = new Rectangle2D.Float(0, 0, -1, -1);

    public Rectangle getIntegerBounds() {
        Rectangle result = new Rectangle(integerBounds);
        result.width++;
        result.height++;
        return result;
    }

    public Rectangle2D getRealBounds() {
        return realBounds;
    }

    public void applyToGraphics(Graphics2D g) {
        for (Drawable drawable : toBeDrawn) {
            drawable.draw(g);
        }
    }

    private void addPointToBounds(int x, int y) {
        int excess = (int) Math.ceil(currentLineWidth / 2 - 0.5f);
        integerBounds.add(x + excess, y + excess);
        integerBounds.add(x - excess, y - excess);
        integerBounds.add(x + excess, y - excess);
        integerBounds.add(x - excess, y + excess);
        double realExcess = currentLineWidth / 2;
        addPointToRectangle2D(realBounds, x + realExcess, y + realExcess);
        addPointToRectangle2D(realBounds, x - realExcess, y - realExcess);
        addPointToRectangle2D(realBounds, x + realExcess, y - realExcess);
        addPointToRectangle2D(realBounds, x - realExcess, y + realExcess);
    }

    private void addPointToRectangle2D(Rectangle2D rectangle, double x, double y) {
        if (rectangle.getWidth() < 0 || rectangle.getHeight() < 0) {
            rectangle.setRect(x, y, 0, 0);
        } else {
            rectangle.add(x, y);
        }
    }

    private void addRectangleToBounds(int x, int y, int width, int height) {
        addPointToBounds(x, y);
        addPointToBounds(x + width, y + height);
        addPointToBounds(x, y + height);
        addPointToBounds(x + width, y);
    }

    private interface Drawable {

        public void draw(Graphics2D g);
    }

    @Override
    public void drawLine(final int x1, final int y1, final int x2, final int y2) {
        addPointToBounds(x1, y1);
        addPointToBounds(x2, y2);
        toBeDrawn.add(new Drawable() {
            public void draw(Graphics2D g) {
                g.drawLine(x1, y1, x2, y2);
            }
        });
    }

    @Override
    public void fillRect(final int x, final int y, final int width, final int height) {
        addRectangleToBounds(x, y, width - 1, height - 1);
        toBeDrawn.add(new Drawable() {
            public void draw(Graphics2D g) {
                g.fillRect(x, y, width, height);
            }
        });
    }

    @Override
    public void drawPolygon(final int[] xPoints, final int[] yPoints, final int nPoints) {
        for (int i = 0; i < nPoints; i++) {
            addPointToBounds(xPoints[i], yPoints[i]);
        }
        toBeDrawn.add(new Drawable() {
            public void draw(Graphics2D g) {
                g.drawPolygon(xPoints, yPoints, nPoints);
            }
        });
    }

    @Override
    public void fillPolygon(final int[] xPoints, final int[] yPoints, final int nPoints) {
        for (int i = 0; i < nPoints; i++) {
            addPointToBounds(xPoints[i], yPoints[i]);
        }
        toBeDrawn.add(new Drawable() {
            public void draw(Graphics2D g) {
                g.fillPolygon(xPoints, yPoints, nPoints);
            }
        });
    }

    @Override
    public Color getColor() {
        return currentColor;
    }

    @Override
    public void setColor(final Color c) {
        currentColor = c;
        toBeDrawn.add(new Drawable() {
            public void draw(Graphics2D g) {
                g.setColor(c);
            }
        });
    }

    @Override
    public void drawOval(final int x, final int y, final int width, final int height) {
        addRectangleToBounds(x, y, width, height);
        toBeDrawn.add(new Drawable() {
            public void draw(Graphics2D g) {
                g.drawOval(x, y, width, height);
            }
        });
    }

    @Override
    public void fillOval(final int x, final int y, final int width, final int height) {
        addRectangleToBounds(x, y, width - 1, height - 1);
        toBeDrawn.add(new Drawable() {
            public void draw(Graphics2D g) {
                g.fillOval(x, y, width, height);
            }
        });
    }

    @Override
    public void setStroke(final Stroke s) {
        if (s instanceof BasicStroke) {
            BasicStroke stroke = (BasicStroke) s;
            currentLineWidth = stroke.getLineWidth();
        }
        toBeDrawn.add(new Drawable() {
            public void draw(Graphics2D g) {
                g.setStroke(s);
            }
        });
    }

    @Override
    public void drawString(final String str, final int x, final int y) {
        Rectangle stringBounds = getFontMetrics(currentFont).getStringBounds(str, graphics).getBounds();
        addRectangleToBounds(x, y - stringBounds.height, stringBounds.width, stringBounds.height);
        toBeDrawn.add(new Drawable() {
            public void draw(Graphics2D g) {
                g.drawString(str, x, y);
            }
        });
    }

    @Override
    public Font getFont() {
        return currentFont;
    }

    @Override
    public void setFont(final Font font) {
        currentFont = font;
        toBeDrawn.add(new Drawable() {
            public void draw(Graphics2D g) {
                g.setFont(font);
            }
        });
    }

    @Override
    public FontMetrics getFontMetrics(Font f) {
        return graphics.getFontMetrics(f);
    }

    @Override
    public void setRenderingHint(final Key hintKey, final Object hintValue) {
        toBeDrawn.add(new Drawable() {
            public void draw(Graphics2D g) {
                g.setRenderingHint(hintKey, hintValue);
            }
        });
    }

	//########################// NOT SUPPORTED YET //#########################//
    @Override
    public void draw(Shape s) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean drawImage(Image img, AffineTransform xform, ImageObserver obs) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void drawImage(BufferedImage img, BufferedImageOp op, int x, int y) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void drawRenderedImage(RenderedImage img, AffineTransform xform) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void drawRenderableImage(RenderableImage img, AffineTransform xform) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void drawString(String str, float x, float y) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void drawString(AttributedCharacterIterator iterator, int x, int y) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void drawString(AttributedCharacterIterator iterator, float x, float y) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void drawGlyphVector(GlyphVector g, float x, float y) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void fill(Shape s) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean hit(Rectangle rect, Shape s, boolean onStroke) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public GraphicsConfiguration getDeviceConfiguration() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setComposite(Composite comp) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setPaint(Paint paint) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object getRenderingHint(Key hintKey) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setRenderingHints(Map<?, ?> hints) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void addRenderingHints(Map<?, ?> hints) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public RenderingHints getRenderingHints() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void translate(int x, int y) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void translate(double tx, double ty) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void rotate(double theta) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void rotate(double theta, double x, double y) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void scale(double sx, double sy) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void shear(double shx, double shy) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void transform(AffineTransform Tx) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setTransform(AffineTransform Tx) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public AffineTransform getTransform() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Paint getPaint() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Composite getComposite() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setBackground(Color color) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Color getBackground() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Stroke getStroke() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void clip(Shape s) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public FontRenderContext getFontRenderContext() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Graphics create() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setPaintMode() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setXORMode(Color c1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Rectangle getClipBounds() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void clipRect(int x, int y, int width, int height) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setClip(int x, int y, int width, int height) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Shape getClip() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setClip(Shape clip) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void copyArea(int x, int y, int width, int height, int dx, int dy) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void clearRect(int x, int y, int width, int height) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean drawImage(Image img, int x, int y, int width, int height, ImageObserver observer) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean drawImage(Image img, int x, int y, Color bgcolor, ImageObserver observer) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean drawImage(Image img, int x, int y, int width, int height, Color bgcolor, ImageObserver observer) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, ImageObserver observer) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, Color bgcolor, ImageObserver observer) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void dispose() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
