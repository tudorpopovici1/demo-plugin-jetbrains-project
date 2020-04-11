package actions;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.tree.IElementType;
import org.intellij.plugins.markdown.lang.MarkdownElementType;
import org.intellij.plugins.markdown.lang.MarkdownFileType;
import org.intellij.plugins.markdown.lang.psi.impl.MarkdownFile;
import org.intellij.plugins.markdown.lang.psi.impl.MarkdownLinkDestinationImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import static org.intellij.plugins.markdown.lang.MarkdownElementTypes.*;
import static org.intellij.markdown.flavours.gfm.GFMTokenTypes.*;
import static org.intellij.plugins.markdown.lang.MarkdownTokenTypes.ATX_HEADER;
import static org.intellij.plugins.markdown.lang.MarkdownTokenTypes.TEXT;

import com.intellij.openapi.diagnostic.Logger;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Class for the report of markdown files statistics summary
 */
public class MarkdownAction extends AnAction {

    private static final Logger log = Logger.getInstance(MarkdownAction.class); //logger for debugging

    /**
     * This default constructor is used by the IntelliJ Platform framework to
     * instantiate this class based on plugin.xml declarations. Only needed in MarkdownAction
     * class because a second constructor is overridden.
     * @see AnAction#AnAction()
     */
    public MarkdownAction() {
        super();
    }

    /**
     * This constructor is used to support dynamically added menu actions.
     * It sets the text, description to be displayed for the menu item.
     * Otherwise, the default AnAction constructor is used by the IntelliJ Platform.
     * @param text  The text to be displayed as a menu item.
     * @param description  The description of the menu item.
     * @param icon  The icon to be used with the menu item.
     */
    public MarkdownAction(@Nullable String text, @Nullable String description, @Nullable Icon icon) {
        super(text, description, icon);
    }

    // variables to store
    // TODO : find a better way to store these numbers
    int noOfLinks = 0;
    int noOfFiles = 0;
    int noOfFIlesWithLinks = 0;
    int noOfRepoLinks = 0;
    int noOfUrls = 0;
    int noOfLines = 0;
    int noOfHeaders = 0;
    boolean linkFound = false;
    List<String> repoReferences = new ArrayList<>();
    List<String> urls = new ArrayList<>();

    /**
     * @param event
     */
    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Project currentProject = event.getProject();

        Collection<VirtualFile> virtualFiles =
                FileTypeIndex.getFiles(MarkdownFileType.INSTANCE, GlobalSearchScope.projectScope(currentProject));

        noOfLinks = 0;
        noOfFiles = 0;
        noOfFIlesWithLinks = 0;
        noOfRepoLinks = 0;
        noOfUrls = 0;
        noOfLines = 0;
        noOfHeaders = 0;
        repoReferences = new ArrayList<>();
        urls = new ArrayList<>();

        // gets each md file and gathers statistics
        for (VirtualFile virtualFile : virtualFiles) {

            linkFound = false;
            noOfFiles++;

            MarkdownFile psiFile = (MarkdownFile)PsiManager.getInstance(currentProject).findFile(virtualFile);

            noOfLines += psiFile.getText().split("\r\n|\r|\n").length;

            psiFile.accept(new PsiRecursiveElementVisitor() {
                @Override
                public void visitElement(PsiElement element) {
                    IElementType elemType = element.getNode().getElementType();

                    //finds links by looking at right element types in a markdown file
                    if ((element.getClass().equals(ASTWrapperPsiElement.class) && elemType == AUTOLINK)
                            || (element.getClass().equals(MarkdownLinkDestinationImpl.class) && elemType == LINK_DESTINATION)
                            || (element.getClass().equals(LeafPsiElement.class)
                            && (elemType == MarkdownElementType.platformType(GFM_AUTOLINK)
                            && element.getParent().getNode().getElementType() != LINK_DESTINATION))) {
                        linkFound = true;
                        noOfLinks++;

                        if((element.getClass().equals(MarkdownLinkDestinationImpl.class) && elemType == LINK_DESTINATION)) {
                            if(element.getFirstChild().getNode().getElementType()==TEXT){
                                repoReferences.add(element.getText());
                                noOfRepoLinks++;
                            } else {
                                urls.add(element.getText());
                                noOfUrls++;
                            }
                        } else {
                            urls.add(element.getText());
                            noOfUrls++;
                        }

                    }

                    // counts headers
                    if(elemType == ATX_HEADER) {
                        noOfHeaders++;
                    }

                    super.visitElement(element);
                }
            });

            // counts numbers of files with links
            if (linkFound) {
                noOfFIlesWithLinks++;
            }
        }

        String msg = "No of markdown files : " + noOfFiles + "\nTotal no of lines: " + noOfLines +
                "\nNo of headers : " + noOfHeaders + "\nNo of files with links : " + noOfFIlesWithLinks +
                "\nTotal no of links : " + noOfLinks +"\nNo of reference links to repo : " + noOfRepoLinks +
                "\nNo of urls : " + noOfUrls;

        msg+="\nRepo references:";
        for(String ref : repoReferences) {
            msg+="\n-" + ref;
        }

        msg+="\nUrl links:";
        for(String url : urls) {
            msg+="\n-" + url;
        }

        Messages.showMessageDialog(currentProject, msg, "Markdown Files Report", Messages.getInformationIcon());
    }

    @Override
    public void update(AnActionEvent e) {
        Project project = e.getProject();
        e.getPresentation().setEnabledAndVisible(project != null);
    }
}
