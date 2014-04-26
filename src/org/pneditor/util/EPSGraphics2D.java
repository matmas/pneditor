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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.text.AttributedCharacterIterator;
import java.util.Map;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class EPSGraphics2D extends Graphics2D {

    private static final String APP_NAME = "PNEditor";
    private final Graphics graphics = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB).getGraphics();
    private Font currentFont = new Font(null);
    private ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
    private PrintStream out = new PrintStream(arrayOutputStream);
    private CachedGraphics2D cachedGraphics = new CachedGraphics2D();
    private Color currentColor = Color.black;

    public void writeToFile(File file) throws FileNotFoundException {
        PrintStream fileOut = new PrintStream(file);
        fileOut.println("%!PS-Adobe-3.0 EPSF-3.0");
        fileOut.println("%%Creator: " + APP_NAME);
        fileOut.println("%%Pages: 1");
        fileOut.println("%%Orientation: Portrait");
        Rectangle2D bounds = xy(cachedGraphics.getRealBounds());
        fileOut.println("%%BoundingBox: "
                + (long) Math.floor(bounds.getMinX()) + " "
                + (long) Math.floor(bounds.getMinY()) + " "
                + (long) Math.ceil(bounds.getMaxX()) + " "
                + (long) Math.ceil(bounds.getMaxY()));
        fileOut.println("%%HiResBoundingBox: "
                + bounds.getMinX() + " "
                + bounds.getMinY() + " "
                + bounds.getMaxX() + " "
                + bounds.getMaxY());
        fileOut.println("%%EndComments");
        fileOut.println("%%Page: 1 1");
        fileOut.println("0 0 0 setrgbcolor");
        fileOut.println("[] 0 setdash");
        fileOut.println("1 setlinewidth");
        fileOut.println("0 setlinejoin");
        fileOut.println("0 setlinecap");
        fileOut.println("gsave [1 0 0 1 0 0] concat");
        fileOut.println("/Times-Bold findfont");
        fileOut.println("12 scalefont");
        fileOut.println("setfont");
        fileOut.print(arrayOutputStream.toString());
        fileOut.println("grestore");
        fileOut.println("showpage");
        fileOut.println("%%EOF");
        fileOut.close();
    }

    private double x(double x) {
        return x;
    }

    private double y(double y) {
        return -y;
    }

    private Rectangle2D xy(Rectangle2D rectangle) {
        Rectangle2D result = new Rectangle2D.Double();
        double x1 = x(rectangle.getX());
        double y1 = y(rectangle.getY());
        double x2 = x(rectangle.getMaxX());
        double y2 = y(rectangle.getMaxY());
        result.setFrameFromDiagonal(x1, y1, x2, y2);
        return result;
    }

    private void newPath() {
        out.println("newpath");
    }

    private void closePath() {
        out.println("closepath");
    }

    private void stroke() {
        out.println("stroke");
    }

    private void fill() {
        out.println("fill");
    }

    private void moveTo(double x, double y) {
        out.println(x(x) + " " + y(y) + " moveto");
    }

    private void lineTo(double x, double y) {
        out.println(x(x) + " " + y(y) + " lineto");
    }

    private void curveTo(double x1, double y1, double x2, double y2, double x3, double y3) {
        out.println(x(x1) + " " + y(y1) + " " + x(x2) + " " + y(y2) + " " + x(x3) + " " + y(y3) + " curveto");
    }

    private void circle(double centerX, double centerY, double radius) {
        out.println(x(centerX) + " " + y(centerY) + " " + radius + " 0 360 arc");
    }

    private void setColor(double red, double green, double blue) {
        out.println(red + " " + green + " " + blue + " setrgbcolor");
    }

    @Override
    public void drawLine(int x1, int y1, int x2, int y2) {
        cachedGraphics.drawLine(x1, y1, x2, y2);
        out.println();
        out.println("% begin drawLine");
        newPath();
        moveTo(x1, y1);
        lineTo(x2, y2);
        stroke();
        out.println("% end drawLine");
        out.println();
    }

    private void makeRectanglePath(int x, int y, int width, int height) {
        newPath();
        moveTo(x, y);
        lineTo(x + width, y);
        lineTo(x + width, y + height);
        lineTo(x, y + height);
        closePath();
    }

    @Override
    public void drawRect(int x, int y, int width, int height) {
        cachedGraphics.drawRect(x, y, width, height);
        out.println();
        out.println("% begin drawRect");
        makeRectanglePath(x, y, width + 1, height + 1);
        stroke();
        out.println("% end drawRect");
        out.println();
    }

    @Override
    public void fillRect(int x, int y, int width, int height) {
        cachedGraphics.fillRect(x, y, width, height);
        out.println();
        out.println("% begin fillRect");
        makeRectanglePath(x, y, width, height);
        fill();
        out.println("% end fillRect");
        out.println();
    }

    private void makePolygonPath(int[] xPoints, int[] yPoints, int nPoints) {
        newPath();
        moveTo(xPoints[0], yPoints[0]);
        for (int i = 1; i < nPoints; i++) {
            lineTo(xPoints[i], yPoints[i]);
        }
        closePath();
    }

    @Override
    public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        cachedGraphics.drawPolygon(xPoints, yPoints, nPoints);
        out.println();
        out.println("% begin drawPolygon");
        makePolygonPath(xPoints, yPoints, nPoints);
        stroke();
        out.println("% end drawPolygon");
        out.println();
    }

    @Override
    public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        cachedGraphics.fillPolygon(xPoints, yPoints, nPoints);
        out.println();
        out.println("% begin fillPolygon");
        makePolygonPath(xPoints, yPoints, nPoints);
        fill();
        out.println("% end fillPolygon");
        out.println();
    }

    @Override
    public Color getColor() {
        return currentColor;
    }

    @Override
    public void setColor(Color c) {
        double red = (double) c.getRed() / 255;
        double green = (double) c.getGreen() / 255;
        double blue = (double) c.getBlue() / 255;
        setColor(red, green, blue);
        currentColor = c;
    }

    private void makeOvalPath(int x, int y, int width, int height) {
        newPath();
        if (width == height) {
            double radius = (double) width / 2;
            double centerX = (double) x + radius;
            double centerY = (double) y + radius;
            circle(centerX, centerY, radius);
            closePath();
        } else {
            final double kappa = 0.5522847498;
            double l_horizontal = (double) (kappa * width / 2);
            double l_vertical = (double) (kappa * height / 2);
            double halfWidth = (double) width / 2;
            double halfHeight = (double) height / 2;
            moveTo(x + halfWidth, y);
            curveTo(x + halfWidth + l_horizontal, y,
                    x + width, y + l_vertical,
                    x + width, y + halfHeight);
            curveTo(x + width, y + halfHeight + l_vertical,
                    x + halfWidth + l_horizontal, y + height,
                    x + halfWidth, y + height);
            curveTo(x + l_horizontal, y + height,
                    x, y + halfHeight + l_vertical,
                    x, y + halfHeight);
            curveTo(x, y + l_vertical,
                    x + l_horizontal, y,
                    x + halfWidth, y);
            closePath();
        }
    }

    @Override
    public void drawOval(int x, int y, int width, int height) {
        cachedGraphics.drawOval(x, y, width, height);
        out.println();
        out.println("% begin drawOval");
        makeOvalPath(x, y, width + 1, height + 1);
        stroke();
        out.println("% end drawOval");
        out.println();
    }

    @Override
    public void fillOval(int x, int y, int width, int height) {
        cachedGraphics.fillOval(x, y, width, height);
        out.println();
        out.println("% begin fillOval");
        makeOvalPath(x, y, width, height);
        fill();
        out.println("% end fillOval");
        out.println();
    }

    @Override
    public void setStroke(Stroke s) {
        cachedGraphics.setStroke(s);
        if (s instanceof BasicStroke) {
            BasicStroke stroke = (BasicStroke) s;
            double currentLineWidth = stroke.getLineWidth();
            float[] dashArray = stroke.getDashArray();
            float dashPhase = stroke.getDashPhase();
            int lineCap = stroke.getEndCap();
            int lineJoin = stroke.getLineJoin();
            out.println(currentLineWidth + " setlinewidth");
            out.println(lineCap + " setlinecap");
            out.println(lineJoin + " setlinejoin");
            if (dashArray != null) {
                out.print("[");
                for (float d : dashArray) {
                    out.print(d + " ");
                }
                out.println("] " + dashPhase + " setdash");
            } else {
                out.println("[] 0 setdash");
            }
        } else {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    @Override
    public void drawString(String str, int x, int y) {
        cachedGraphics.drawString(str, x, y);
        str = str.replace("(", "\\(").replace(")", "\\)");
        moveTo(x, y);
        out.println("(" + str + ") show");
        FontMetrics fontMetrics = getFontMetrics();
    }

    @Override
    public Font getFont() {
        return currentFont;
    }

    @Override
    public FontMetrics getFontMetrics(Font f) {
        return graphics.getFontMetrics(f);
    }

    @Override
    public void setFont(Font font) {
        cachedGraphics.setFont(font);
        out.println("/Times-Bold findfont");
//		System.out.println("/" + font.getFamily() + "-" + font.getStyle() + " findfont");
        out.println(font.getSize() + (2 * font.getSize() / 12) + " scalefont");
        out.println("setfont");
        currentFont = font;
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
    public void setRenderingHint(Key hintKey, Object hintValue) {
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
