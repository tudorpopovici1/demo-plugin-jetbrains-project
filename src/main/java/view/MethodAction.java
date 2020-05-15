package view;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import service.FileStatisticsService;
import service.SummaryService;

import javax.swing.*;
import java.util.*;


/**
 * @author Irem Ugurlu
 * @author Tommaso Brandirali
 * An action that displays a set of statistics about methods in the currently open editor.
 */
public class MethodAction extends AnAction {


    private static final int COMPLEXITY_LEVEL_MILD_WARNING = 7;
    private static final int COMPLEXITY_LEVEL_SERIOUS_WARNING = 10;

    /**
     * This default constructor is used by the IntelliJ Platform framework to
     * instantiate this class based on plugin.xml declarations. Only needed in PopupDialogAction
     * class because a second constructor is overridden.
     * @see AnAction#AnAction()
     */
    public MethodAction() {
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
    public MethodAction(@Nullable String text, @Nullable String description, @Nullable Icon icon) {
        super(text, description, icon);
    }

    /**
     * This method executes the plugin logic and displays the results in a popup window.
     * @param event the event object
     */
    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {

        // Get project, return warning message if no project open.
        Project currentProject = event.getProject();
        if (currentProject == null) {
            Messages.showErrorDialog(
                    "Please open a project to see the Summary Report.",
                    "Summary Report");
            return;
        }

        // Return warning if no file open.
        if (FileEditorManager.getInstance(currentProject).getSelectedEditor() == null) {
            Messages.showErrorDialog(
                    "Please open a file to see the Summary Report.",
                    "Summary Report"
            );
            return;
        }

        // Get current file.
        Document currentDoc = Objects.requireNonNull(FileEditorManager.getInstance(currentProject)
                .getSelectedTextEditor()).getDocument();
        VirtualFile currentFile = FileDocumentManager.getInstance().getFile(currentDoc);

        // Update summary service view and save statistics of the file on disk.
        SummaryService summaryService = SummaryService.getInstance(currentProject);
        summaryService.updateView(currentProject, currentFile, true, false);
        summaryService.save(currentFile);
    }

    /**
     * This method sets the visibility of the action.
     * @param e the event object
     */
    @Override
    public void update(AnActionEvent e) {
        Project project = e.getProject();
        e.getPresentation().setEnabledAndVisible(project != null);
    }
}