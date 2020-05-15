package service;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.*;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.util.messages.MessageBusConnection;
import actions.DataAggregator;
import service.FileStatistics.FileStatisticsBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import util.SummaryView;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Service for Summary Report plugin
 */
public class SummaryService {

    private final static Logger logger = Logger.getInstance(SummaryService.class);

    private SummaryView view;
    private ArrayList<SummaryData> lastSummary = new ArrayList<>();
    private Project project;

    /**
     * Class constructor
     * @param project current project
     */
    public SummaryService(Project project) {
        logger.info("Summary service is starting");

        view = new SummaryView();
        this.project = project;

        ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
        ToolWindow toolWindow = toolWindowManager.registerToolWindow(
                "Statistics", false, ToolWindowAnchor.BOTTOM);

        toolWindow.setIcon(IconLoader.getIcon("/icon.png"));
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(view, null, true);
        toolWindow.getContentManager().addContent(content);

        // update view when new file is selected
        final MessageBusConnection connection = project.getMessageBus().connect(project);
        connection.subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, new FileEditorManagerListener() {
            @Override
            public void selectionChanged(@NotNull FileEditorManagerEvent event) {
                updateView(event.getManager().getProject(), event.getNewFile(), false, true);
            }
        });

        // update view when file is changed
        PsiManager.getInstance(project).addPsiTreeChangeListener(new PsiTreeAnyChangeAbstractAdapter() {
            @Override
            protected void onChange(@Nullable PsiFile psiFile) {
                if (psiFile instanceof PsiJavaFile) {
                    updateView(psiFile.getProject(), psiFile.getVirtualFile(), false, true);
                }
            }
        });
    }

    /**
     * This method executes the data gathering logic and refactors the data
     * to prepare it for being passed to the view.
     * @return a list of SummaryData entries
     */
    private ArrayList<SummaryData> getSummary(Boolean fileChanged) {

        // Convert data to summary format.
        //return DataConverter.fileStatisticsToSummaryData(this.buildFileStatistics(psiFile, fileChanged));
        return new ArrayList<>();
    }

    /**
     * Higher-level function that builds the FileStatistics object
     *
     * @param psiFile file for which to collect statistics
     * @return FileStatistics object
     */
    public FileStatistics buildFileStatistics(PsiFile psiFile, Boolean fileChanged) {
        FileStatisticsBuilder builder = this.buildWithCurrentData(psiFile);
        if (fileChanged) {
            return builder.build();
        }
        return buildWithStorageData(builder, psiFile.getName()).build();
    }

    /**
     * Helper function that performs the first part of building the FileStatistics object
     * This function adds to the FileStatistics object the 'current' fetched statistics
     *
     * @param psiFile file for which to collect 'current' statistics
     * @return FileStatistics builder object containing 'current' statistics
     */
    private FileStatisticsBuilder buildWithCurrentData(PsiFile psiFile) {

        FileStatisticsBuilder builder = new FileStatisticsBuilder();

        // Get document object and generate statistics object.
        Document document = Objects.requireNonNull(FileEditorManager.getInstance(psiFile.getProject())
                .getSelectedTextEditor()).getDocument();
        builder.addName(psiFile.getName())
                .withDocument(document)
                .withLines(document.getLineCount())
                .withFileLength(document.getTextLength());

        // Execute data gathering logic.
        return visitMethods(psiFile, builder);
    }


    /**
     * Helper function that performs the second part of building the FileStatistics object
     * This function adds to the FileStatistics object the aggregated statistics using disk data.
     *
     * @param builder FileStatisticsBuilder object on top of which to add the new storage statistics
     * @param name name of the file for which to aggregate the data
     * @return a new FileStatisticsBuilder object containing the 'storage' statistics.
     */
    private FileStatisticsBuilder buildWithStorageData(FileStatisticsBuilder builder, String name) {
        DataAggregator dataAggregator = new DataAggregator(project, name);
        return dataAggregator.collectStorageData(builder);
    }

    /*    *//**
     * Saves the file statistics of a JAVA file on disk.
     *
     * @param file file object for which to store the statistics on disk.
     *//*
    public void save(VirtualFile file) {
        final FileStatisticsService fileStatisticsService = FileStatisticsService.getInstance(this.project);
        if (file != null) {
            PsiFile psiFile = PsiManager.getInstance(project).findFile(file);
            if (psiFile instanceof PsiJavaFile) {
                fileStatisticsService.saveStatistics(this.buildFileStatistics(psiFile, false));
            } else {
                // TODO: Save statistics for other types of files
            }
        }

    }*/

    /**
     * Update the view.
     * @param project the currently open project object
     * @param file the file object referring to the document in the currently open editor
     * @param activate check whether the table should be activated or not
     */
    public void updateView(Project project, VirtualFile file, Boolean activate, Boolean fileChanged) {
        ArrayList<SummaryData> summaries = new ArrayList<>();
        ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow("Statistics");

        if (file != null) {
            PsiFile psiFile = PsiManager.getInstance(project).findFile(file);
            if (true) {
                //PsiJavaFile javaFile = (PsiJavaFile)psiFile;
                //summaries = getSummary(psiFile, fileChanged);
                summaries = new ArrayList<>();
                view.updateModel(summaries);
                if (activate) {
                    toolWindow.activate(null);
                }
            } else {
                Messages.showErrorDialog(
                        "Non-java files are not supported.",
                        "Summary Report"
                );
                view.updateModel(summaries);
                toolWindow.hide(null);
            }
        } else {
            view.updateModel(summaries);
            toolWindow.hide(null);
        }
    }

    public static SummaryService getInstance(@NotNull Project project) {
        return ServiceManager.getService(project, SummaryService.class);
    }

    /**
     * Helper method to get current document's methods from the psi,
     * and update the statistics builder object with results.
     *
     * @param psiFile the Psi object representing the current file.
     * @param builder the builder for the statistics object to fill with data during scan.
     */
    private FileStatisticsBuilder visitMethods(PsiFile psiFile, FileStatisticsBuilder builder) {

        return builder;
    }
}

