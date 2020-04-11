package data;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.tree.IElementType;
import org.intellij.plugins.markdown.lang.MarkdownElementType;
import org.intellij.plugins.markdown.lang.psi.impl.MarkdownFile;
import org.intellij.plugins.markdown.lang.psi.impl.MarkdownLinkDestinationImpl;
import service.SummaryService;

import java.util.*;

import static org.intellij.markdown.flavours.gfm.GFMTokenTypes.GFM_AUTOLINK;
import static org.intellij.plugins.markdown.lang.MarkdownElementTypes.AUTOLINK;
import static org.intellij.plugins.markdown.lang.MarkdownElementTypes.LINK_DESTINATION;
import static org.intellij.plugins.markdown.lang.MarkdownTokenTypes.ATX_HEADER;
import static org.intellij.plugins.markdown.lang.MarkdownTokenTypes.TEXT;

/**
 * @author Irem Ugurlu
 *
 * A data class to hold partial and final values
 * of statistics about all MD files.
 */
public class MDStatistics {

    final static Logger logger = Logger.getInstance(SummaryService.class);

    /**
     * Total number of links.
     */
    private int noLinks;
    public int getNoLinks() {
        return noLinks;
    }

    /**
     * Number of MD files.
     */
    private int noFiles;
    public int getNoFiles() {
        return noFiles;
    }

    /**
     * Number of files with links.
     */
    private int noFilesLinks;
    public int getNoFilesLinks() {
        return noFilesLinks;
    }

    /**
     * Number of repository links.
     */
    private int noRepoLinks;
    public int getNoRepoLinks() { return noRepoLinks; }

    /**
     * Number of URLs.
     */
    private int noUrls;
    public int getNoUrls() {
        return noUrls;
    }

    /**
     * Total number of lines.
     */
    private int noLines;
    public int getNoLines() {
        return noLines;
    }

    /**
     * Number of headers.
     */
    private int noHeaders;
    public int getNoHeaders() {
        return noHeaders;
    }

    /**
     * The map of files and their references and links
     */
    private Map<String, List<List<String>>> values;
    public Map<String, List<List<String>>> getValues() {
        return values;
    }

    boolean linkFound = false;

    /**
     * Method updating statistics
     * @param currentProject current project
     * @param virtualFiles collection of all virtual files
     */
    public void updateStatistics(Project currentProject, Collection<VirtualFile> virtualFiles) {

        // gets each md file and gathers statistics
        for (VirtualFile virtualFile : virtualFiles) {
            List<String> repoReferences = new ArrayList<>();
            List<String> urls = new ArrayList<>();
            linkFound = false;
            noFiles++;
            String fileName = virtualFile.getName();

            MarkdownFile psiFile = (MarkdownFile)PsiManager.getInstance(currentProject).findFile(virtualFile);

            noLines += psiFile.getText().split("\r\n|\r|\n").length;

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
                        noLinks++;
                        if((element.getClass().equals(MarkdownLinkDestinationImpl.class) && elemType == LINK_DESTINATION)) {
                            if(element.getFirstChild().getNode().getElementType()==TEXT){
                                repoReferences.add(element.getText());
                                noRepoLinks++;
                            } else { urls.add(element.getText()); noUrls++; }
                        } else { urls.add(element.getText());noUrls++; }
                    }

                    // counts headers
                    if(elemType == ATX_HEADER) { noHeaders++; }

                    List<List<String>> links = new ArrayList<>();
                    links.add(urls);
                    links.add(repoReferences);

                    values.put(fileName, links);
                    logger.info(values.toString());
                    super.visitElement(element);
                }
            });

            // counts numbers of files with links
            if (linkFound) { noFilesLinks++; }
        }
    }

    /**
     * Construct an instance of MDStatistics.
     */
    public MDStatistics() {
        noLinks = 0;
        noFiles = 0;
        noFilesLinks = 0;
        noRepoLinks = 0;
        noUrls = 0;
        noLines = 0;
        noHeaders = 0;
        values = new HashMap<>();
    }
}
