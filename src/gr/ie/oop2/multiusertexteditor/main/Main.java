package gr.ie.oop2.multiusertexteditor.main;

import gr.ie.oop2.multiusertexteditor.model.MultiUserEditorDataModel;
import gr.ie.oop2.multiusertexteditor.views.EditorUserView;

/**
 *
 * @author Anton
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        MultiUserEditorDataModel model = new MultiUserEditorDataModel();
        EditorUserView user1 = new EditorUserView(model, "Anton");
        user1.setVisible(true);
//        EditorUserView user2 = new EditorUserView(model, "Esmeralda");
//        user2.setVisible(true);
    }

}
