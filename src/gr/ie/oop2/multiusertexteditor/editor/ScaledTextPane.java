package gr.ie.oop2.multiusertexteditor.editor;

/**
 * @author Stanislav Lapitsky
 * @version 1.0
 */
import java.awt.*;
import java.awt.geom.*;

import javax.swing.*;
import javax.swing.text.*;

public class ScaledTextPane extends JTextPane {

    public ScaledTextPane(Document doc) {
        super();

        setOpaque(true);
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        setEditorKit(new ScaledEditorKit());

        setDocument(doc);
        setEditable(false);

        getDocument().putProperty("i18n", Boolean.FALSE);
        getDocument().putProperty("ZOOM_FACTOR", new Double(1.5));

        String s = "40%";
        s = s.substring(0, s.length() - 1);
        double scale = new Double(s).doubleValue() / 100;
        getDocument().putProperty("ZOOM_FACTOR", new Double(scale));

    }

    @Override
    public void repaint(int x, int y, int width, int height) {
        super.repaint(0, 0, getWidth(), getHeight());
    }

    private class ScaledEditorKit extends StyledEditorKit {

        @Override
        public ViewFactory getViewFactory() {
            return new StyledViewFactory();
        }

        private class StyledViewFactory implements ViewFactory {

            @Override
            public View create(Element elem) {
                String kind = elem.getName();
                if (kind != null) {
                    if (kind.equals(AbstractDocument.ContentElementName)) {
                        return new WrapLabelView(elem);
                    } else if (kind.equals(AbstractDocument.ParagraphElementName)) {
                        return new ParagraphView(elem);
                    } else if (kind.equals(AbstractDocument.SectionElementName)) {
                        return new ScaledView(elem, View.Y_AXIS);
                    } else if (kind.equals(StyleConstants.ComponentElementName)) {
                        return new ComponentView(elem);
                    } else if (kind.equals(StyleConstants.IconElementName)) {
                        return new IconView(elem);
                    }
                }

                // default to text display
                return new LabelView(elem);
            }

        }
    }
    
    private class WrapLabelView extends LabelView {

        public WrapLabelView(Element elem) {
            super(elem);
        }

        @Override
        public float getMinimumSpan(int axis) {
            switch (axis) {
                case View.X_AXIS:
                    return 0;
                case View.Y_AXIS:
                    return super.getMinimumSpan(axis);
                default:
                    throw new IllegalArgumentException("Invalid axis: " + axis);
            }
        }
    }

//-----------------------------------------------------------------
    private class ScaledView extends BoxView {

        public ScaledView(Element elem, int axis) {
            super(elem, axis);
        }

        public double getZoomFactor() {
            Double scale = (Double) getDocument().getProperty("ZOOM_FACTOR");
            if (scale != null) {
                return scale.doubleValue();
            }

            return 1;
        }

        @Override
        public void paint(Graphics g, Shape allocation) {
            Graphics2D g2d = (Graphics2D) g;
            double zoomFactor = getZoomFactor();
            AffineTransform old = g2d.getTransform();
            g2d.scale(zoomFactor, zoomFactor);
            super.paint(g2d, allocation);
            g2d.setTransform(old);
        }

        @Override
        public float getMinimumSpan(int axis) {
            float f = super.getMinimumSpan(axis);
            f *= getZoomFactor();
            return f;
        }

        @Override
        public float getMaximumSpan(int axis) {
            float f = super.getMaximumSpan(axis);
            f *= getZoomFactor();
            return f;
        }

        @Override
        public float getPreferredSpan(int axis) {
            float f = super.getPreferredSpan(axis);
            f *= getZoomFactor();
            return f;
        }

        @Override
        protected void layout(int width, int height) {
            super.layout(new Double(width / getZoomFactor()).intValue(),
                    new Double(height
                            * getZoomFactor()).intValue());
        }

        @Override
        public Shape modelToView(int pos, Shape a, Position.Bias b) throws BadLocationException {
            double zoomFactor = getZoomFactor();
            Rectangle alloc;
            alloc = a.getBounds();
            Shape s = super.modelToView(pos, alloc, b);
            alloc = s.getBounds();
            alloc.x *= zoomFactor;
            alloc.y *= zoomFactor;
            alloc.width *= zoomFactor;
            alloc.height *= zoomFactor;

            return alloc;
        }

        @Override
        public int viewToModel(float x, float y, Shape a,
                Position.Bias[] bias) {
            double zoomFactor = getZoomFactor();
            Rectangle alloc = a.getBounds();
            x /= zoomFactor;
            y /= zoomFactor;
            alloc.x /= zoomFactor;
            alloc.y /= zoomFactor;
            alloc.width /= zoomFactor;
            alloc.height /= zoomFactor;

            return super.viewToModel(x, y, alloc, bias);
        }
}

}
