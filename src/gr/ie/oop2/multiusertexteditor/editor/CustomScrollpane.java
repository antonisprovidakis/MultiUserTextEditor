/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.ie.oop2.multiusertexteditor.editor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JScrollPane;

/**
 *
 * @author Anton
 */
public class CustomScrollpane extends JScrollPane {

    public CustomScrollpane(Component view) {
        super(view);

        setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        getVerticalScrollBar().setPreferredSize(new Dimension(10, 0));
        getHorizontalScrollBar().setPreferredSize(new Dimension(0, 10));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        
        g.setColor(Color.red);
        
//        g.drawLine(0, 0, 200, 200);
        g.drawRect(0, 0, getWidth(), getHeight());
    }
    
    

}
