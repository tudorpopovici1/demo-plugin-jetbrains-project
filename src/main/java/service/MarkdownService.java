package service;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiTreeAnyChangeAbstractAdapter;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import data.MDStatistics;
import data.SummaryData;
import org.intellij.plugins.markdown.lang.MarkdownFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import util.DataConverter;
import view.MDView;
import view.SummaryView;

import java.util.*;

/**
 * @author Ceren Ugurlu
 *
 * Service for MD files plugin
 */
public class MarkdownService {

    final static Logger logger = Logger.getInstance(SummaryService.class);

    SummaryView view;
    MDView mdView;

    /**
     * Class constructor
     * @param project current project
     */
    public MarkdownService(Project project) {
        logger.info("Markdown service is starting");
        if (view == null) {
            view = new SummaryView();
        }
        if (mdView == null) {
            mdView = new MDView();
        }

        // statistics window
        ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
        ToolWindow toolWindow = toolWindowManager.registerToolWindow(
                "MD Statistics", false, ToolWindowAnchor.BOTTOM);
        toolWindow.setIcon(IconLoader.getIcon("/icons/md.png"));

        // md files window
        ToolWindow mdWindow = toolWindowManager.registerToolWindow(
                "MD Files", false, ToolWindowAnchor.BOTTOM);
        mdWindow.setIcon(IconLoader.getIcon("/icons/md.png"));

        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(view, null, true);
        toolWindow.getContentManager().addContent(content);

        Content mdContent = contentFactory.createContent(mdView, null, true);
        mdWindow.getContentManager().addContent(mdContent);

        // update view after vfs change
        project.getMessageBus().connect().subscribe(VirtualFileManager.VFS_CHANGES, new BulkFileListener() {
            @Override
            public void after(@NotNull List<? extends VFileEvent> events) {
                Collection<VirtualFile> virtualFiles =
                        FileTypeIndex.getFiles(MarkdownFileType.INSTANCE, GlobalSearchScope.projectScope(project));
                updateView(project, virtualFiles);
            }
        });

        // update view when there is file change
        PsiManager.getInstance(project).addPsiTreeChangeListener(new PsiTreeAnyChangeAbstractAdapter() {
            @Override
            protected void onChange(@Nullable PsiFile psiFile) {
                Collection<VirtualFile> virtualFiles =
                        FileTypeIndex.getFiles(MarkdownFileType.INSTANCE, GlobalSearchScope.projectScope(project));
                updateView(project, virtualFiles);
            }
        });
    }

    /**
     * This method executes the data gathering logic and refactors the data
     * to prepare it for being passed to he view.
     * @return a list of SummaryData entries
     */
    public ArrayList<SummaryData> getStatistics (Project project, Collection<VirtualFile> virtualFiles) {
        MDStatistics mdStatistics = new MDStatistics();
        mdStatistics.updateStatistics(project, virtualFiles);

        // Convert data to summary format.
        return DataConverter.mdStatisticsToSummaryData(mdStatistics);
    }

    /**
     * Update the view.
     * @param project the currently open project object
     * @param virtualFiles collection of virtual files
     */
    public void updateView(Project project, Collection<VirtualFile> virtualFiles) {
        ArrayList<SummaryData> summaries = getStatistics(project, virtualFiles);
        view.updateModel(summaries);

        MDStatistics mdStatistics = new MDStatistics();
        mdStatistics.updateStatistics(project, virtualFiles);
        Map<String, List<List<String>>> map = mdStatistics.getValues();
        mdView.updateModel(map);
    }

    public static MarkdownService getInstance(@NotNull Project project) {
        return ServiceManager.getService(project, MarkdownService.class);
    }
}
