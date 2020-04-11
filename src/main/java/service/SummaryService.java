package service;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerAdapter;
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
import data.FileStatistics;
import data.MethodStatistics;
import data.SummaryData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import util.DataConverter;
import view.SummaryView;

import java.util.ArrayList;
import java.util.Objects;

public class SummaryService {

    final static Logger logger = Logger.getInstance(SummaryService.class);

    SummaryView view;

    public SummaryService(Project project) {
        logger.info("Summary service is starting");
        if (view == null) {
            view = new SummaryView();
        }

        ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
        ToolWindow toolWindow = toolWindowManager.registerToolWindow(
                "Statistics", false, ToolWindowAnchor.BOTTOM);

        toolWindow.setIcon(IconLoader.getIcon("/icons/icon.png"));
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(view, null, true);
        toolWindow.getContentManager().addContent(content);

        // update view when new file is selected
        final MessageBusConnection connection = project.getMessageBus().connect(project);
        connection.subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, new FileEditorManagerAdapter() {
            @Override
            public void selectionChanged(@NotNull FileEditorManagerEvent event) {
                updateView (event.getManager().getProject(), event.getNewFile(), false);
            }
        });

        // update view when file is changed
        PsiManager.getInstance(project).addPsiTreeChangeListener(new PsiTreeAnyChangeAbstractAdapter() {
            @Override
            protected void onChange(@Nullable PsiFile psiFile) {
                if (psiFile instanceof PsiJavaFile) {
                    updateView(psiFile.getProject(), psiFile.getVirtualFile(), false);
                }
            }
        });
    }

    /**
     * This method executes the data gathering logic and refactors the data
     * to prepare it for being passed to he view.
     * @param psiFile the psi object
     * @return a list of SummaryData entries
     */
    public ArrayList<SummaryData> getSummary (PsiJavaFile psiFile) {

        // Get document object and generate statistics object.
        Document document = Objects.requireNonNull(FileEditorManager.getInstance(psiFile.getProject())
                .getSelectedTextEditor()).getDocument();
        FileStatistics myFileStatistics = new FileStatistics(psiFile.getName(), document);

        // Execute data gathering logic.
        visitMethods(psiFile, myFileStatistics);

        // Convert data to summary format.
        return DataConverter.fileStatisticsToSummaryData(myFileStatistics);
    }

    /**
     * Update the view.
     * @param project the currently open project object
     * @param file the file object referring to the document in the currently open editor
     * @param activate check whether the table should be activated or not
     */
    public void updateView(Project project, VirtualFile file, Boolean activate) {
        ArrayList<SummaryData> summaries = new ArrayList<>();
        ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow("Statistics");

        if (file != null) {
            PsiFile psiFile = PsiManager.getInstance(project).findFile(file);
            if (psiFile instanceof PsiJavaFile) {
                PsiJavaFile javaFile = (PsiJavaFile)psiFile;
                summaries = getSummary(javaFile);
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
     * and update the statistics input object with results.
     * @param psiFile the Psi object representing the current file.
     * @param stats the statistics object to fill with data during scan.
     */
    private void visitMethods(PsiFile psiFile, FileStatistics stats) {

        PsiClass[] classes = ((PsiJavaFile) psiFile).getClasses();
        for (PsiClass psiClass: classes) {

            PsiMethod[] methods = psiClass.getMethods();
            for (PsiMethod method: methods) {

                MethodStatistics methodStatistics = new MethodStatistics(method);
                method.accept(new JavaRecursiveMethodVisitor(methodStatistics));
                stats.addMethod(methodStatistics);
            }
        }
    }
}
