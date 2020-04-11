package view;

import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.treeStructure.Tree;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 * Class creating MD method plugin view
 */
public class MDView extends JPanel {

    Tree tree;

    /**
     * Constructor of class
     */
    public MDView() {
        super(new BorderLayout());
        DefaultMutableTreeNode mdFiles = new DefaultMutableTreeNode("Markdown Files");
        tree = new Tree(new DefaultTreeModel(mdFiles));
        JBScrollPane scrollPane = new JBScrollPane(tree);
        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Updating tree view
     * @param files map including file names and their references and links
     */
    public void updateModel(Map<String, List<List<String>>> files) {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) tree.getModel().getRoot();
        root.removeAllChildren();
        for (String fileName : files.keySet()){
            DefaultMutableTreeNode file = new DefaultMutableTreeNode(fileName);
            DefaultMutableTreeNode urlLinks = new DefaultMutableTreeNode("URL Links");
            DefaultMutableTreeNode references = new DefaultMutableTreeNode("References");
            List<List<String>> list = files.get(fileName);
            List<String> urlList = list.get(0);
            List<String> refList = list.get(1);
            for (String url : urlList) {
                DefaultMutableTreeNode newUrl = new DefaultMutableTreeNode(url);
                urlLinks.add(newUrl);
            }
            for (String ref : refList) {
                DefaultMutableTreeNode newUrl = new DefaultMutableTreeNode(ref);
                references.add(newUrl);
            }
            file.add(urlLinks);
            file.add(references);
            root.add(file);
        }
        ((DefaultTreeModel)tree.getModel()).reload();
    }
}