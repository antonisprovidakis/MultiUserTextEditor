package gr.ie.oop2.multiusertexteditor.editor;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import net.java.balloontip.BalloonTip;
import net.java.balloontip.styles.MinimalBalloonStyle;

/**
 *
 * @author Anton
 */
public class Comment extends JLabel implements IMovableEditorComponent {

    private int x1;
    private int x2;
    private int y1;
    private int start;
    private int end;
    private String rootText;

//    private Color color;
    private BalloonTip commentBalloon;
    private JPopupMenu treeMenu;
    private JPopupMenu commentMenu;
    private JTree commentTree;

    public final static int COMM_HEIGHT = 10;

    //TODO: create a copy Constructor
    public Comment(Comment otherComment) {
        this.x1 = otherComment.getX1();
        this.x2 = otherComment.getX2();
        this.y1 = otherComment.getY1();
        this.start = otherComment.getStart();
        this.end = otherComment.getEnd();
        this.rootText = otherComment.getRootText();

        initComponents();
    }

    public Comment(int x1, int x2, int y1, int start, int end, String rootText/*, Color color*/) {
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.start = start;
        this.end = end;
        this.rootText = rootText;
//        this.color = color;

        initComponents();
    }

    private void initComponents() {
        ImageIcon commentIcon = new ImageIcon(getClass().getResource("/gr/ie/oop2/multiusertexteditor/icons/comment_icon2.png"));
        setIcon(commentIcon);

        setHorizontalAlignment(SwingConstants.RIGHT);

        setSize(x2 - x1, COMM_HEIGHT);
        setLocation(x1, y1);

        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!commentBalloon.isVisible()) {
                    commentBalloon.setVisible(true);
                }
            }
        });

        commentMenu = new JPopupMenu();
        commentMenu.add(new JMenuItem(new AbstractAction("Delete Comments") {

            @Override
            public void actionPerformed(ActionEvent e) {

                MUTextPane parent = (MUTextPane) SwingUtilities.getAncestorOfClass(MUTextPane.class, Comment.this);

                int compIndex = 0;

                for (int i = 0; i < parent.getComponentCount(); i++) {
                    if (parent.getComponent(i) == Comment.this) {
                        compIndex = i;
                    }
                }

                commentBalloon.setVisible(false);
                parent.remove(Comment.this);
                parent.repaint();

                parent.getModel().deleteObserver(parent);
                parent.getModel().sendRemoveCommentComp(compIndex);
                parent.getModel().addObserver(parent);

            }
        }));
        setComponentPopupMenu(commentMenu);

        DefaultMutableTreeNode treeRoot = new DefaultMutableTreeNode(rootText);
        treeRoot.setAllowsChildren(true);

        commentTree = new JTree(treeRoot);
        commentTree.setEditable(true);

        ImageIcon treeIcon = new ImageIcon(getClass().getResource("/gr/ie/oop2/multiusertexteditor/icons/tree_icon.png"));
        DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
        renderer.setLeafIcon(treeIcon);
        renderer.setClosedIcon(treeIcon);
        renderer.setOpenIcon(treeIcon);
        commentTree.setCellRenderer(renderer);

        treeMenu = new JPopupMenu();
        treeMenu.add(new JMenuItem(new AbstractAction("Add Comment") {
            @Override
            public void actionPerformed(ActionEvent e) {

                String newText = " ";
                String input = JOptionPane.showInputDialog("Enter your comment:");

                if (input != null) {
                    if (!input.trim().equals("")) {
                        newText = newText.concat(input);

                        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) commentTree.getLastSelectedPathComponent();
                        DefaultTreeModel model = (DefaultTreeModel) commentTree.getModel();
                        DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(newText);
                        model.insertNodeInto(newNode, selectedNode, selectedNode.getChildCount());
                        commentTree.expandPath(new TreePath(selectedNode.getPath()));

//                        DefaultMutableTreeNode otherSelectedNode = (DefaultMutableTreeNode) commentTree.getLastSelectedPathComponent();
//
//                        MUTextPane parent = (MUTextPane) SwingUtilities.getAncestorOfClass(MUTextPane.class, Comment.this);
//
//                        int compIndex = 0;
//
//                        for (int i = 0; i < parent.getComponentCount(); i++) {
//                            if (parent.getComponent(i) == Comment.this) {
//                                compIndex = i;
//                            }
//                        }
//                        
//                        
//                        parent.getModel().deleteObserver(parent);
//                        parent.getModel().sendAddComment(otherSelectedNode, newText, compIndex);
//                        parent.getModel().addObserver(parent);
                    }
                }
            }
        }));

        commentTree.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent e) {
                int r = Comment.this.commentTree.getRowForLocation(e.getX(), e.getY());

                if (r >= 0 && r < Comment.this.commentTree.getRowCount()) {
                    Comment.this.commentTree.setSelectionRow(r);
                } else {
                    Comment.this.commentTree.clearSelection();
                }

                if (e.isPopupTrigger() && e.getComponent() instanceof JTree) {
                    JPopupMenu popup = Comment.this.treeMenu;
                    popup.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });

        JScrollPane treeScrollpane = new JScrollPane(commentTree);
        treeScrollpane.setPreferredSize(new Dimension(220, 200));

        commentBalloon = new BalloonTip(this, treeScrollpane, new MinimalBalloonStyle(Color.yellow, 5), true);
        commentBalloon.setCloseButton(BalloonTip.getDefaultCloseButton(), false);
        commentBalloon.setVisible(true);
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

    public String getRootText() {
        return rootText;
    }

    public BalloonTip getCommentBalloon() {
        return commentBalloon;
    }

    public JTree getCommentTree() {
        return commentTree;
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

//        g.setColor(color);
        g.setColor(Color.ORANGE);

        // draw line
        g.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
        g.drawLine(0, getHeight() - 2, getWidth(), getHeight() - 2);
    }

}
