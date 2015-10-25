package gr.ie.oop2.multiusertexteditor.editor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import javax.swing.JButton;
import javax.swing.JLabel;

/**
 *
 * @author Anton
 */
public class Proposal extends JLabel implements IMovableEditorComponent {

    private JButton rejectButton;
    private JButton acceptButton;

    private int x1;
    private int x2;
    private int y1;
    private int start;
    private int end;
    private Color color;

    private final int ARR_SIZE = 4;

    public final static int PROP_HEIGHT = 7;

    public Proposal(Proposal otherProposal) {
        this.x1 = otherProposal.getX1();
        this.x2 = otherProposal.getX2();
        this.y1 = otherProposal.getY1();
        this.start = otherProposal.getStart();
        this.end = otherProposal.getEnd();
        this.color = otherProposal.getColor();

        initComponents();
    }

    public Proposal(int x1, int x2, int y1, int start, int end, Color color) {
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.start = start;
        this.end = end;
        this.color = color;

        initComponents();
    }

    private void initComponents() {
        setSize(x2 - x1, PROP_HEIGHT);
        setLocation(x1, y1);

        rejectButton = new JButton();
        rejectButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                //TODO: Implement reject code
                System.out.println("reject");

            }
        });

        rejectButton.setOpaque(true);
        rejectButton.setBackground(Color.red);
        rejectButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        rejectButton.setSize(10, getFont().getSize() / 2);
        rejectButton.setLocation(0, 0);
        add(rejectButton);

        acceptButton = new JButton();
        acceptButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                //TODO: Implement accept code
                System.out.println("accept");

            }
        });

        acceptButton.setOpaque(true);
        acceptButton.setBackground(Color.GREEN);
        acceptButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        acceptButton.setSize(10, getFont().getSize() / 2);
        acceptButton.setLocation(getWidth() - acceptButton.getWidth(), 0);
        add(acceptButton);
    }

    public int getX1() {
        return x1;
    }

    public int getX2() {
        return x2;
    }

    public int getY1() {
        return y1;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public void setSize(int width, int height) {
        super.setSize(width, height);
        if (acceptButton != null) {
            acceptButton.setLocation(getWidth() - acceptButton.getWidth(), 0);
        }
    }

    @Override
    public void setLocation(int x, int y) {
        super.setLocation(x, y);
        if (acceptButton != null) {
            acceptButton.setLocation(getWidth() - acceptButton.getWidth(), 0);
        }
    }

    @Override
    public int getStart() {
        return start;
    }

    @Override
    public void setStart(int newStart) {
        start = newStart;
    }

    @Override
    public int getEnd() {
        return end;
    }

    @Override
    public void setEnd(int newEnd) {
        end = newEnd;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // draw curved line
//        g.drawLine(0, 0, getWidth(), 0); // temp
//        g.drawArc(rejectButton.getWidth(), rejectButton.getHeight(),
//                getWidth() - acceptButton.getWidth(),
//                acceptButton.getHeight(), -180, -180);
        drawArrow(g, rejectButton.getWidth(), getHeight() / 2,
                getWidth() - acceptButton.getWidth(),
                getHeight() / 2);
    }

    void drawArrow(Graphics g1, int x1, int y1, int x2, int y2) {
        Graphics2D g = (Graphics2D) g1.create();

        double dx = x2 - x1, dy = y2 - y1;
        double angle = Math.atan2(dy, dx);
        int len = (int) Math.sqrt(dx * dx + dy * dy);
        AffineTransform at = AffineTransform.getTranslateInstance(x1, y1);
        at.concatenate(AffineTransform.getRotateInstance(angle));
        g.transform(at);

        g.setStroke(new BasicStroke(0.3f));

        g.setColor(color);

        g.drawLine(0, 0, len, 0);
        g.fillPolygon(new int[]{len, len - ARR_SIZE, len - ARR_SIZE, len},
                new int[]{0, -ARR_SIZE, ARR_SIZE, 0}, 4);
    }

}
