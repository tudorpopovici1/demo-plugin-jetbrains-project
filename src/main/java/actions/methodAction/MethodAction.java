package actions.methodAction;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Objects;

/**
 * @author Irem Ugurlu
 * @author Tommaso Brandirali
 * An action that displays a set of statistics about methods in the currently open editor.
 */
public class MethodAction extends AnAction {

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

        // Get current file, return if no file open.
        Document currentDoc = Objects.requireNonNull(FileEditorManager.getInstance(currentProject)
                .getSelectedTextEditor()).getDocument();
        VirtualFile currentFile = FileDocumentManager.getInstance().getFile(currentDoc);
        if (currentFile == null) {
            Messages.showErrorDialog(
                    "Please open a file to see the Summary Report.",
                    "Summary Report"
            );
            return;
        }

        // Get file info and generate statistics object.
        String filePath = currentFile.getPath();
        String fileName = currentFile.getName();
        int docLength = currentDoc.getTextLength();
        int docLines = currentDoc.getLineCount();
        FileStatistics myFileStatistics = new FileStatistics();

        // Get Psi file
        PsiFile psiFile = PsiDocumentManager.getInstance(currentProject).getPsiFile(currentDoc);
        // Check if file is Java file, else display warning
        if (psiFile instanceof PsiJavaFile) {

            visitMethods(psiFile, myFileStatistics);

            // Build string from list of methods
            StringBuilder methodsStringBuilder = new StringBuilder();
            ArrayList<MethodStatistics> methodsList = myFileStatistics.getMyMethods();
            for (int i = 0; i < methodsList.size(); i++) {
                MethodStatistics method = methodsList.get(i);
                methodsStringBuilder.append("Name: ");
                methodsStringBuilder.append(method.getName());
                methodsStringBuilder.append(",   Complexity: ");
                methodsStringBuilder.append(method.getComplexity());
                if (i != methodsList.size() - 1) {
                    methodsStringBuilder.append(";\n");
                }
            }

            // Build final popup text string
            String dlgMsg = "file name: " + fileName + "\n" +
                    "file path: " + filePath + "\n" +
                    "file length: " + docLength + "\n" +
                    "lines of code: " + docLines + "\n" +
                    "number of methods: " + myFileStatistics.getTotalMethods() + "\n" +
                    "number of public methods: " + myFileStatistics.getMyPublicMethods() + "\n" +
                    "number of private methods: " + myFileStatistics.getMyPrivateMethods() + "\n\n" +
                    "Methods:\n" + methodsStringBuilder.toString();
            Messages.showMessageDialog(currentProject,
                    dlgMsg,
                    "Summary Report",
                    Messages.getInformationIcon());

        } else {

            // Build final popup text string
            String dlgMsg = "file name: " + fileName + "\n" +
                    "file path: " + filePath + "\n" +
                    "file length: " + docLength + "\n" +
                    "lines of code: " + docLines + "\n\n" +
                    "We're sorry, but method statistics are currently only available for Java files :(";
            Messages.showMessageDialog(currentProject,
                    dlgMsg,
                    "Summary Report",
                    Messages.getInformationIcon());
        }
    }

    /**
     * Helper method to get current document's methods from the psi,
     * and update the statistics input object with results.
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