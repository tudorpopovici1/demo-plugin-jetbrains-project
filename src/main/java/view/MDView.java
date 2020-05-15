package view;

import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.treeStructure.Tree;
import util.LinkStatistics;

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
    public void updateModel(Map<String, List<List<LinkStatistics>>> files) {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) tree.getModel().getRoot();
        root.removeAllChildren();
        for (String fileName : files.keySet()){
            DefaultMutableTreeNode file = new DefaultMutableTreeNode(fileName);
            DefaultMutableTreeNode urlLinks = new DefaultMutableTreeNode("URL Links");
            DefaultMutableTreeNode references = new DefaultMutableTreeNode("References");
            List<List<LinkStatistics>> list = files.get(fileName);
            List<LinkStatistics> urlList = list.get(0);
            List<LinkStatistics> refList = list.get(1);
            for (LinkStatistics urlLink : urlList) {
                DefaultMutableTreeNode newUrl = new DefaultMutableTreeNode(urlLink.getLink() + " --- This link is " + urlLink.validityToString());
                urlLinks.add(newUrl);
            }
            for (LinkStatistics refLink : refList) {
                DefaultMutableTreeNode newUrl = new DefaultMutableTreeNode(refLink.getLink() + " --- This link is " + refLink.validityToString());
                references.add(newUrl);
            }
            file.add(urlLinks);
            file.add(references);
            root.add(file);
        }
        ((DefaultTreeModel)tree.getModel()).reload();
    }
}