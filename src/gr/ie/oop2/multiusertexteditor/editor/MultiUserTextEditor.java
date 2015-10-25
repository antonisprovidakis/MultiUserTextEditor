package gr.ie.oop2.multiusertexteditor.editor;

import gr.ie.oop2.multiusertexteditor.model.MultiUserEditorDataModel;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 *
 * @author Anton
 */
public class MultiUserTextEditor extends JPanel {

    private CustomScrollpane scrollPane;
    private MUTextPane muTextPane;

    public MultiUserTextEditor(MultiUserEditorDataModel model, String userName) {
        super(new BorderLayout());

//        scrollPane = new JScrollPane(new MUTextPane(model, userName));
//
//        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
//        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(10, 0));
//        scrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 10));
        muTextPane = new MUTextPane(model, userName);
        scrollPane = new CustomScrollpane(muTextPane);

        add(scrollPane);
    }

    public JScrollPane getScrollPane() {
        return scrollPane;
    }
}
