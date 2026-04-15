package org.pneditor.editor.commands;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.Set;
import org.pneditor.petrinet.Element;
import org.pneditor.petrinet.PetriNet;
import org.pneditor.petrinet.Subnet;
import org.pneditor.util.Command;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class PasteCommand implements Command {

    private Subnet subnet;
    private Set<Element> elements;

    public PasteCommand(Set<Element> elements, Subnet currentSubnet, PetriNet petriNet) {
        this.subnet = currentSubnet;
        this.elements = elements;
        petriNet.getNodeLabelGenerator().setLabelsToPastedContent(elements);

        Point translation = calculateTranslatioToCenter(elements, currentSubnet);
        for (Element element : elements) {
            element.moveBy(translation.x, translation.y);
        }
    }

    public void execute() {
        subnet.addAll(elements);
    }

    public void undo() {
        subnet.removeAll(elements);
    }

    public void redo() {
        execute();
    }

    @Override
    public String toString() {
        return "Paste";
    }

    private Point calculateTranslatioToCenter(Set<Element> elements, Subnet currentSubnet) {
        Point viewTranslation = currentSubnet.getViewTranslation();
        Subnet tempSubnet = new Subnet();
        tempSubnet.addAll(elements);
        Rectangle bounds = tempSubnet.getBounds();

        Point result = new Point();
        result.translate(Math.round(-(float) bounds.getCenterX()), Math.round(-(float) bounds.getCenterY()));
        result.translate(-viewTranslation.x, -viewTranslation.y);
        return result;
    }

}
