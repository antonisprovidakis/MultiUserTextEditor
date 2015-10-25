package gr.ie.oop2.multiusertexteditor.editor;

import gr.ie.oop2.multiusertexteditor.model.MultiUserEditorDataModel;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTextPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.BoxView;
import javax.swing.text.ComponentView;
import javax.swing.text.Element;
import javax.swing.text.IconView;
import javax.swing.text.LabelView;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.ParagraphView;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;

/**
 *
 * @author Anton
 */
public class MUTextPane extends JTextPane implements Observer, DocumentListener, CaretListener, AncestorListener {

    private Font font = new Font("Times New Roman", Font.PLAIN, 10);
    private FontMetrics fm = getFontMetrics(getFont());

    private SimpleAttributeSet aset;

    private HashMap<String, Color> userMapColor;
    private HashMap<String, Integer> userMapIndex;

    private ArrayList<Color> colorsArraylist;
    private Random random;

    private MultiUserEditorDataModel model;

    private String userName;
    private int index;

    private int delStart = 0;
    private int delReps = 0;
    private boolean isMultTextSelected = false;

    private JPopupMenu menu;

    private JDialog onlineUsersDialog;
    private JDialog radar;

    private ScaledTextPane scaledTextPane;

    public MUTextPane(MultiUserEditorDataModel model, String userName) {
        this.model = model;
        this.userName = userName;

        userMapColor = new HashMap();
        userMapIndex = new HashMap();

        colorsArraylist = new ArrayList<>();
        colorsArraylist.add(Color.RED);
        colorsArraylist.add(Color.GREEN);
        colorsArraylist.add(Color.BLUE);
        colorsArraylist.add(Color.MAGENTA);
        colorsArraylist.add(Color.ORANGE);

        random = new Random();

        aset = new SimpleAttributeSet();

        setEditorKit(new WrapEditorKit());
        setLayout(null);
        setBorder(BorderFactory.createEmptyBorder(8, 20, 0, 20));

        setText(model.getData());

        // create and add right-click menu
        menu = new JPopupMenu();
        menu.add(new JMenuItem(new AbstractAction("Add comments") {

            @Override
            public void actionPerformed(ActionEvent e) {

                // checks if there is any selection
                if (getSelectionStart() != getSelectionEnd()) {
                    try {
                        int start = getSelectionStart();
                        int end = getSelectionEnd();

                        int x1 = modelToView(start).x;
                        int x2 = modelToView(end).x;
                        int y = modelToView(start).y - 7;

                        Comment comment = new Comment(x1, x2, y, start, end, getSelectedText() + " (created by: " + MUTextPane.this.userName + ")");
                        add(comment);

                        repaint();

                        Comment newComment = new Comment(comment);

                        MUTextPane.this.model.deleteObserver(MUTextPane.this);
                        MUTextPane.this.model.sendInsertCommentComp(newComment);
                        MUTextPane.this.model.addObserver(MUTextPane.this);
                    } catch (BadLocationException ex) {
                        Logger.getLogger(MUTextPane.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }));

        menu.add(new StrikeThroughAction());
        setComponentPopupMenu(menu);

        addAncestorListener(this);

        getStyledDocument().addDocumentListener(this);
        addCaretListener(this);
        
        model.addObserver(this);
    }

    public String getUserName() {
        return userName;
    }

    public int getIndex() {
        return index;
    }

    public MultiUserEditorDataModel getModel() {
        return model;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setFont(font);
        int nameSizeInPixels;

        try {
            for (Map.Entry<String, Color> entry : userMapColor.entrySet()) {
                g.setColor(entry.getValue());

                // caret
                int x = modelToView(userMapIndex.get(entry.getKey())).x;
                int y = modelToView(userMapIndex.get(entry.getKey())).y;
                g.drawLine(x, y + 3, x, y + getFont().getSize());

                // name
                String name = entry.getKey();
                nameSizeInPixels = fm.stringWidth(name);
                g.drawString(name, x - (nameSizeInPixels / 2), y + 3);
            }
        } catch (BadLocationException ex) {
            Logger.getLogger(MUTextPane.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // observer method
    // data coming from outside
    @Override
    public void update(Observable o, Object arg) {
        deactivateListeners();

        // insert
        if (((int) ((ArrayList) arg).get(0)) == MultiUserEditorDataModel.CHAR_INSERTED) {
            try {
                int indexToIns = (int) ((ArrayList) arg).get(1);
                char newCharacter = (char) ((ArrayList) arg).get(2);
                String otherUserName = (String) ((ArrayList) arg).get(3);

                StyleConstants.setForeground(aset, userMapColor.get(otherUserName));
                setCharacterAttributes(aset, false);

                getStyledDocument().insertString(indexToIns, Character.toString(newCharacter), aset);

                insertMove(userMapIndex.get(otherUserName));

            } catch (BadLocationException ex) {
                Logger.getLogger(MUTextPane.class.getName()).log(Level.SEVERE, null, ex);
            }
        } // delete
        else if (((int) ((ArrayList) arg).get(0)) == MultiUserEditorDataModel.CHAR_DELETED) {
            try {
                int indexToDel = (int) ((ArrayList) arg).get(1);
                String otherUserName = (String) ((ArrayList) arg).get(2);

                getStyledDocument().remove(indexToDel, 1);

                removeMove(userMapIndex.get(otherUserName));

            } catch (BadLocationException ex) {
                Logger.getLogger(MUTextPane.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        } // update index
        else if (((int) ((ArrayList) arg).get(0)) == MultiUserEditorDataModel.INDEX_POSITION_CHANGED) {
            int otherCaretIndex = (int) ((ArrayList) arg).get(1);
            String otherCaretName = (String) ((ArrayList) arg).get(2);

            setCaretPosition(otherCaretIndex, otherCaretName);

            repaint();
        } else if (((int) ((ArrayList) arg).get(0)) == MultiUserEditorDataModel.COMMENT_COMP_INSERTED) {
            Comment otherComment = (Comment) ((ArrayList) arg).get(1);

            add(otherComment);

            repaint();
        } else if (((int) ((ArrayList) arg).get(0)) == MultiUserEditorDataModel.PROPOSAL_INSERTED) {

            Proposal otherProposal = (Proposal) ((ArrayList) arg).get(1);
            String otherCaretName = (String) ((ArrayList) arg).get(2);
            String extraText = (String) ((ArrayList) arg).get(3);

            StyleConstants.setForeground(aset, userMapColor.get(otherCaretName));
            setCharacterAttributes(aset, false);

            try {
                getStyledDocument().insertString(otherProposal.getEnd() - extraText.length(), extraText, aset);

            } catch (BadLocationException ex) {
                Logger.getLogger(MUTextPane.class.getName()).log(Level.SEVERE, null, ex);
            }

            setSelectionStart(otherProposal.getStart());
            setSelectionEnd(otherProposal.getEnd() - extraText.length());

            StyledEditorKit kit = getStyledEditorKit();
            MutableAttributeSet attr = kit.getInputAttributes();
            boolean strikethrough = !(StyleConstants.isStrikeThrough(attr));
            SimpleAttributeSet sas = new SimpleAttributeSet();
            StyleConstants.setStrikeThrough(sas, strikethrough);
            setCharacterAttributes(sas, false);

            otherProposal.setColor(userMapColor.get(otherCaretName));
            add(otherProposal);
            repaint();

            setCaretPosition(otherProposal.getEnd() + extraText.length());
        } else if (((int) ((ArrayList) arg).get(0)) == MultiUserEditorDataModel.COMMENT_COMP_DELETED) {
            int indexToDel = (int) ((ArrayList) arg).get(1);

            remove(getComponent(indexToDel));
            repaint();

        }
//        else if (((int) ((ArrayList) arg).get(0)) == MultiUserEditorDataModel.COMMENT_ADDED) {
//            DefaultMutableTreeNode otherSelectedNode = (DefaultMutableTreeNode) ((ArrayList) arg).get(1);
//            String newText = (String) ((ArrayList) arg).get(2);
//            int compIndex = (int) ((ArrayList) arg).get(3);
//
//            Comment c = (Comment) getComponent(compIndex);
//
//            DefaultTreeModel treeModel = (DefaultTreeModel) c.getCommentTree().getModel();
//            DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(newText);
//            treeModel.insertNodeInto(newNode, otherSelectedNode, otherSelectedNode.getChildCount());
//            c.getCommentTree().expandPath(new TreePath(otherSelectedNode.getPath()));
//
//            repaint();
//        }

        activateListeners();
    }

    // document listener
    @Override
    public void insertUpdate(DocumentEvent e) {
        try {
            insertMove(getCaretPosition());

            StyleConstants.setForeground(aset, Color.BLACK);
            setCharacterAttributes(aset, false);

            char character = e.getDocument().getText(e.getOffset(), e.getLength()).charAt(0);
            model.deleteObserver(this);
            model.writeCharacter(e.getOffset(), character, userName);
            model.addObserver(this);

            repaint();
        } catch (BadLocationException ex) {
            Logger.getLogger(MUTextPane.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        removeMove(getCaretPosition());

        model.deleteObserver(this);
        if (isMultTextSelected) {
            for (int i = 0; i < delReps; i++) {
                model.deleteCharecter(delStart, userName);
                repaint();
            }
        } else {
            model.deleteCharecter(e.getOffset(), userName);
            repaint();
        }
        model.addObserver(this);
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
    }

    // caret listener
    @Override
    public void caretUpdate(CaretEvent e) {
        model.deleteObserver(this);

        if (e.getDot() < e.getMark()) {
            isMultTextSelected = true;
            delStart = e.getDot();
            delReps = Math.abs(delStart - e.getMark());

            index = e.getDot();
            model.updateIndex(e.getDot(), userName);
        } else if (e.getDot() > e.getMark()) {
            isMultTextSelected = true;
            delStart = e.getMark();
            delReps = Math.abs(delStart - e.getDot());

            index = e.getMark();
            model.updateIndex(e.getMark(), userName);
        } else {
            isMultTextSelected = false;

            index = e.getDot();
            model.updateIndex(e.getDot(), userName);
        }

        model.addObserver(this);
    }

    @Override
    public void ancestorAdded(AncestorEvent event) {
        MultiUserTextEditor parentPanel = (MultiUserTextEditor) SwingUtilities.getAncestorOfClass(MultiUserTextEditor.class, MUTextPane.this);
        JViewport viewPort = parentPanel.getScrollPane().getViewport();

        JFrame parentView = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, MUTextPane.this);

        // start online users dialog
        onlineUsersDialog = new JDialog(parentView);
        onlineUsersDialog.setLayout(new FlowLayout(FlowLayout.RIGHT, 2, 0));
        onlineUsersDialog.setSize(viewPort.getWidth(), 11);
        onlineUsersDialog.setLocation(viewPort.getLocationOnScreen().x, viewPort.getLocationOnScreen().y);
        onlineUsersDialog.setUndecorated(true);
        onlineUsersDialog.setBackground(new Color(255, 0, 0, 0));

        // add local user badge
        onlineUsersDialog.add(new OnlineUserBadge(Color.BLACK, userName));

        parentView.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentMoved(ComponentEvent e) {
                MultiUserTextEditor parentPanel = (MultiUserTextEditor) SwingUtilities.getAncestorOfClass(MultiUserTextEditor.class, MUTextPane.this);
                JViewport vp = parentPanel.getScrollPane().getViewport();

                onlineUsersDialog.setLocation(vp.getLocationOnScreen().x, vp.getLocationOnScreen().y);
                radar.setLocation(vp.getLocationOnScreen().x + vp.getWidth() - radar.getWidth() - 10, vp.getLocationOnScreen().y + onlineUsersDialog.getHeight() + 2);
            }

            @Override
            public void componentResized(ComponentEvent e) {
                MultiUserTextEditor parentPanel = (MultiUserTextEditor) SwingUtilities.getAncestorOfClass(MultiUserTextEditor.class, MUTextPane.this);
                JViewport vp = parentPanel.getScrollPane().getViewport();

                onlineUsersDialog.setSize(vp.getWidth(), 10);
                onlineUsersDialog.revalidate();

                radar.setSize(200, 200);
                radar.setLocation(vp.getLocationOnScreen().x + vp.getWidth() - radar.getWidth() - 10, vp.getLocationOnScreen().y + onlineUsersDialog.getHeight() + 2);
                radar.revalidate();
            }
        });

        onlineUsersDialog.setVisible(true);
        // end online users dialog

        // start radar dialog
        radar = new JDialog(parentView);
        radar.setSize(200, 200);

        radar.setLocation(viewPort.getLocationOnScreen().x + viewPort.getWidth() - radar.getWidth() - 10, viewPort.getLocationOnScreen().y + onlineUsersDialog.getHeight() + 2);
        radar.setUndecorated(true);
        radar.getContentPane().setBackground(Color.YELLOW);

        scaledTextPane = new ScaledTextPane(getStyledDocument());

        radar.add(scaledTextPane);

        radar.setVisible(true);
    }

    @Override
    public void ancestorRemoved(AncestorEvent event) {
    }

    @Override
    public void ancestorMoved(AncestorEvent event) {
    }

    public Color getRandomColor() {
        int r = random.nextInt(colorsArraylist.size());
        Color color = colorsArraylist.get(r);
        colorsArraylist.remove(r);

        return color;
    }

    public void associateUserWithColor(String userName) {
        if (!userMapColor.containsKey(userName)) {
            userMapColor.put(userName, getRandomColor());

            onlineUsersDialog.add(new OnlineUserBadge(userMapColor.get(userName), userName));
            onlineUsersDialog.revalidate();
        }
    }

    public void setCaretPosition(int index, String userName) {
        associateUserWithColor(userName);
        userMapIndex.put(userName, index);
    }

    public void insertMove(int position) {
        if (getComponentCount() != 0) {
            for (Component comp : getComponents()) {
                IMovableEditorComponent mvec = (IMovableEditorComponent) comp;
                try {

                    int x1;
                    int x2;
                    int y;

                    if (position <= mvec.getStart()) {
                        mvec.setStart(mvec.getStart() + 1);
                        mvec.setEnd(mvec.getEnd() + 1);

                        x1 = modelToView(mvec.getStart()).x;
                        x2 = modelToView(mvec.getEnd()).x;
                        y = modelToView(mvec.getStart()).y;

                        //TODO: fix proposal positioning when inserting
                        if (comp instanceof Comment) {
                            comp.setSize(x2 - x1, Comment.COMM_HEIGHT);
                            comp.setLocation(x1, y - 7);
                        } else {
                            comp.setSize(x2 - x1, Proposal.PROP_HEIGHT);
                            comp.setLocation(x1, y - 3);
                        }

                    } else if (position > mvec.getStart() && position < mvec.getEnd()) {
                        mvec.setEnd(mvec.getEnd() + 1);

                        x1 = modelToView(mvec.getStart()).x;
                        x2 = modelToView(mvec.getEnd()).x;
                        y = modelToView(mvec.getStart()).y;
                        //TODO: fix proposal positioning when removing
                        if (comp instanceof Comment) {
                            comp.setSize(x2 - x1, Comment.COMM_HEIGHT);
                            comp.setLocation(x1, y - 7);
                        } else {
                            comp.setSize(x2 - x1, Proposal.PROP_HEIGHT);
                            comp.setLocation(x1, y - 3);
                        }
                    }
                } catch (BadLocationException ex) {
                    Logger.getLogger(MUTextPane.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public void removeMove(int position) {

        if (getComponentCount() != 0) {
            for (Component comp : getComponents()) {
                IMovableEditorComponent mvec = (IMovableEditorComponent) comp;
                try {
                    int x1;
                    int x2;
                    int y;

                    if (position <= mvec.getStart()) {
                        mvec.setStart(mvec.getStart() - 1);
                        mvec.setEnd(mvec.getEnd() - 1);

                        x1 = modelToView(mvec.getStart()).x;
                        x2 = modelToView(mvec.getEnd()).x;
                        y = modelToView(mvec.getStart()).y;

                        if (comp instanceof Comment) {
                            comp.setSize(x2 - x1, Comment.COMM_HEIGHT);
                            comp.setLocation(x1, y - 7);
                        } else {
                            comp.setSize(x2 - x1, Proposal.PROP_HEIGHT);
                            comp.setLocation(x1, y - 3);
                        }

                    } else if (position > mvec.getStart() && position <= mvec.getEnd()) {
                        mvec.setEnd(mvec.getEnd() - 1);

                        x1 = modelToView(mvec.getStart()).x;
                        x2 = modelToView(mvec.getEnd()).x;
                        y = modelToView(mvec.getStart()).y;

                        if (comp instanceof Comment) {
                            comp.setSize(x2 - x1, Comment.COMM_HEIGHT);
                            comp.setLocation(x1, y - 7);
                        } else {
                            comp.setSize(x2 - x1, Proposal.PROP_HEIGHT);
                            comp.setLocation(x1, y - 3);
                        }
                    }
                } catch (BadLocationException ex) {
                    Logger.getLogger(MUTextPane.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public class StrikeThroughAction extends StyledEditorKit.StyledTextAction {

        /**
         * Constructs a new UnderlineAction.
         */
        public StrikeThroughAction() {
            super("Propose change");
        }

        /**
         * Toggles the Strikethrough attribute.
         *
         * @param e the action event
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            JEditorPane editor = getEditor(e);
            if (editor != null) {

                if (editor.getSelectionStart() != editor.getSelectionEnd()) {

                    String newText = " ";
                    String input = JOptionPane.showInputDialog("Enter proposal for selected text:");

                    if (input != null) {
                        newText = newText.concat(input);

                        StyledEditorKit kit = getStyledEditorKit(editor);
                        MutableAttributeSet attr = kit.getInputAttributes();
                        boolean strikethrough = !(StyleConstants.isStrikeThrough(attr));
                        SimpleAttributeSet sas = new SimpleAttributeSet();
                        StyleConstants.setStrikeThrough(sas, strikethrough);
                        setCharacterAttributes(editor, sas, false);

                        try {

                            deactivateListeners();
                            ((StyledDocument) editor.getDocument()).insertString(editor.getSelectionEnd(), newText, null);
                            activateListeners();

                            int start = editor.getSelectionStart();
                            int end = editor.getSelectionEnd();
                            int x1 = modelToView(start).x;
                            int x2 = modelToView(end).x;
                            int y = modelToView(start).y - 3;

                            Proposal proposal = new Proposal(x1, x2, y, start, end, userMapColor.get(MUTextPane.this.userName));
                            add(proposal);
                            repaint();

                            setCaretPosition(getSelectionEnd());

                            Proposal newProposal = new Proposal(proposal);

                            model.deleteObserver(MUTextPane.this);
                            model.sendProposalComp(newProposal, MUTextPane.this.userName, newText);
                            model.addObserver(MUTextPane.this);

                        } catch (BadLocationException ex) {
                            Logger.getLogger(MUTextPane.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        }
    }

    public void activateListeners() {
        getDocument().addDocumentListener(this);
        addCaretListener(this);
    }

    public void deactivateListeners() {
        getDocument().removeDocumentListener(this);
        removeCaretListener(this);
    }

    // private classes
    private class WrapEditorKit extends StyledEditorKit {

        ViewFactory defaultFactory = new WrapColumnFactory();

        @Override
        public ViewFactory getViewFactory() {
            return defaultFactory;
        }

        private class WrapColumnFactory implements ViewFactory {

            @Override
            public View create(Element elem) {
                String kind = elem.getName();
                if (kind != null) {
                    if (kind.equals(AbstractDocument.ContentElementName)) {
                        return new WrapLabelView(elem);
                    } else if (kind.equals(AbstractDocument.ParagraphElementName)) {
                        return new ParagraphView(elem);
                    } else if (kind.equals(AbstractDocument.SectionElementName)) {
                        return new BoxView(elem, View.Y_AXIS);
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
}
