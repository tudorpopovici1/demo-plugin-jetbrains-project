package actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import org.intellij.plugins.markdown.lang.MarkdownFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import service.MarkdownService;

import javax.swing.*;
import java.util.Collection;

/**
 * @author Irem Ugurlu
 * @author Ceren Ugurlu
 * An action that displays a set of statistics about methods in the currently open editor.
 */
public class MarkdownAction extends AnAction {

    /**
     * This default constructor is used by the IntelliJ Platform framework to
     * instantiate this class based on plugin.xml declarations. Only needed in PopupDialogAction
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
                    "Please open a project to see the Markdown Files Report.",
                    "Markdown Files Report");
            return;
        }

        // Get virtual files
        Collection<VirtualFile> virtualFiles =
                FileTypeIndex.getFiles(MarkdownFileType.INSTANCE, GlobalSearchScope.projectScope(currentProject));


        // Update markdown service view.
        MarkdownService markdownService = MarkdownService.getInstance(currentProject);
        markdownService.updateView(currentProject, virtualFiles);
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