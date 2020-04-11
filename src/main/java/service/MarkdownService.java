package service;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import data.MDStatistics;
import data.SummaryData;
import org.jetbrains.annotations.NotNull;
import util.DataConverter;
import view.SummaryView;

import java.util.ArrayList;
import java.util.Collection;

public class MarkdownService {

    final static Logger logger = Logger.getInstance(SummaryService.class);

    SummaryView view;

    public MarkdownService(Project project) {
        logger.info("Markdown service is starting");
        if (view == null) {
            view = new SummaryView();
        }

        ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
        ToolWindow toolWindow = toolWindowManager.registerToolWindow(
                "MD Files", false, ToolWindowAnchor.BOTTOM);

        toolWindow.setIcon(IconLoader.getIcon("/icons/md.png"));
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(view, null, true);
        toolWindow.getContentManager().addContent(content);
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
        ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow("MD Files");
        ArrayList<SummaryData> summaries = getStatistics(project, virtualFiles);
        view.updateModel(summaries);
        toolWindow.activate(null);
    }

    public static MarkdownService getInstance(@NotNull Project project) {
        return ServiceManager.getService(project, MarkdownService.class);
    }
}
