package gr.ie.oop2.multiusertexteditor.model;

import gr.ie.oop2.multiusertexteditor.editor.Comment;
import gr.ie.oop2.multiusertexteditor.editor.Proposal;
import java.util.ArrayList;
import java.util.Observable;

/**
 *
 * @author Anton
 */
public class MultiUserEditorDataModel extends Observable {

    private StringBuffer data;
    private ArrayList mediator;

    public static final int CHAR_INSERTED = 1;
    public static final int CHAR_DELETED = 2;
    public static final int INDEX_POSITION_CHANGED = 3;
    public static final int COMMENT_COMP_INSERTED = 4;
    public static final int PROPOSAL_INSERTED = 5;
    public static final int COMMENT_COMP_DELETED = 6;
    public static final int COMMENT_ADDED = 7;

    public MultiUserEditorDataModel() {

        data = new StringBuffer();

        mediator = new ArrayList(3);

        // fill with dummy values
        mediator.add(0);
        mediator.add(0);
        mediator.add(0);
        mediator.add(0);
    }

    public String getData() {
        return data.toString();
    }

    public void writeCharacter(int index, char character, String userName) {
        data.insert(index, character);

        mediator.set(0, CHAR_INSERTED);
        mediator.set(1, index);
        mediator.set(2, character);
        mediator.set(3, userName);

        setChanged();
        notifyObservers(mediator);
    }

    public void deleteCharecter(int index, String userName) {
        data.deleteCharAt(index);
        mediator.set(0, CHAR_DELETED);
        mediator.set(1, index);
        mediator.set(2, userName);

        setChanged();
        notifyObservers(mediator);
    }

    public void updateIndex(int index, String userName) {
        mediator.set(0, INDEX_POSITION_CHANGED);
        mediator.set(1, index);
        mediator.set(2, userName);

        setChanged();
        notifyObservers(mediator);
    }

    public void sendInsertCommentComp(Comment comment) {
        mediator.set(0, COMMENT_COMP_INSERTED);
        mediator.set(1, comment);

        setChanged();
        notifyObservers(mediator);
    }

    public void sendRemoveCommentComp(int compIndex) {
        mediator.set(0, COMMENT_COMP_DELETED);
        mediator.set(1, compIndex);

        setChanged();
        notifyObservers(mediator);
    }

//    public void sendAddComment(DefaultMutableTreeNode otherSelectedNode,String newText, int compIndex) {
//        mediator.set(0, COMMENT_ADDED);
//        mediator.set(1, otherSelectedNode);
//        mediator.set(2, newText);
//        mediator.set(3, compIndex);
//
//        setChanged();
//        notifyObservers(mediator);
//    }
    public void sendProposalComp(Proposal proposal, String userName, String text) {
        mediator.set(0, PROPOSAL_INSERTED);
        mediator.set(1, proposal);
        mediator.set(2, userName);
        mediator.set(3, text);

        setChanged();
        notifyObservers(mediator);
    }
    
}
