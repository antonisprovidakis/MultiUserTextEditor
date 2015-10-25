package gr.ie.oop2.multiusertexteditor.editor;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.Date;
import javax.swing.JLabel;

/**
 *
 * @author Anton
 */
public class OnlineUserBadge extends JLabel {

    private Color color;

    private String name;
    private Date dateOnline;
//    private BalloonTip userBadgeBalloon;
    private JLabel infoLabel;

    public OnlineUserBadge(Color color, String name) {
        this.color = color;
        this.name = name;

        dateOnline = new Date();
        setPreferredSize(new Dimension(10, 10));

        String data = "<html>Username: <b><font color=\"" + color + "\">"
                + name + "</font></b><br>"
                + "Date Online: <b>" + dateOnline + "</b></html>";

        setToolTipText(data);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

//        userBadgeBalloon = new BalloonTip(this, new JLabel("label text"), new MinimalBalloonStyle(Color.yellow, 5), true);
//        userBadgeBalloon.setCloseButton(BalloonTip.getDefaultCloseButton(), false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(color);
        g.fillOval(0, 0, getWidth(), getHeight());
    }
}
