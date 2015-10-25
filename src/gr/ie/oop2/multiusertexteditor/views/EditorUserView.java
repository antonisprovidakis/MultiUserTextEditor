package gr.ie.oop2.multiusertexteditor.views;

import gr.ie.oop2.multiusertexteditor.editor.MultiUserTextEditor;
import gr.ie.oop2.multiusertexteditor.model.MultiUserEditorDataModel;
import javax.swing.JFrame;

/**
 *
 * @author Anton
 */
public class EditorUserView extends JFrame {

    private MultiUserTextEditor multiUserTextEditor;

    public EditorUserView(MultiUserEditorDataModel model, String userName) {
        initComponents(model, userName);
    }

    private void initComponents(MultiUserEditorDataModel model, String userName) {
        setTitle("Multi User Editor - User: " + userName);
        setSize(600, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        multiUserTextEditor = new MultiUserTextEditor(model, userName);
        add(multiUserTextEditor);
    }
}
