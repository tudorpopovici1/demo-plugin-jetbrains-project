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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;

/**
 * @author Irem Ugurlu
 * @author Tommaso Brandirali
 * An action that displays a set of statistics about methods in the currently open editor.
 */
public class MethodAction extends AnAction {

    private static final String DECIMAL_ROUNDING_FORMAT = "#.##";
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

        // Get psi file.
        PsiFile psiFile = PsiDocumentManager.getInstance(currentProject).getPsiFile(currentDoc);

        // Check: if file is Java file scan methods, else display warning.
        if (psiFile instanceof PsiJavaFile) {

            FileStatistics myFileStatistics = new FileStatistics();
            visitMethods(psiFile, myFileStatistics);
            String messageText = buildResultText(currentFile, currentDoc, myFileStatistics);

            Messages.showMessageDialog(currentProject,
                    messageText,
                    "Summary Report",
                    Messages.getInformationIcon());
        } else {

            String messageText = buildNonJavaText(currentFile, currentDoc);
            Messages.showMessageDialog(currentProject,
                    messageText,
                    "Summary Report",
                    Messages.getInformationIcon());
        }
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

    /**
     * Build the string for the text that will be displayed in
     * the plugin's popup if the plugin executes successfully.
     * @param currentFile the file object
     * @param currentDoc the document object
     * @param myFileStatistics the statistics object containing result data.
     * @return the String to be displayed in the plugin's popup
     */
    private String buildResultText(VirtualFile currentFile, Document currentDoc, FileStatistics myFileStatistics) {

        // Build string from list of methods.
        StringBuilder methodsStringBuilder = new StringBuilder();
        ArrayList<MethodStatistics> methodsList = myFileStatistics.getMyMethods();
        for (int i = 0; i < methodsList.size(); i++) {
            MethodStatistics method = methodsList.get(i);
            methodsStringBuilder.append("Name: ");
            methodsStringBuilder.append(method.getName());
            methodsStringBuilder.append(",   Complexity: ");
            methodsStringBuilder.append(method.getComplexity());
            if (i != methodsList.size() - 1) {
                methodsStringBuilder.append(";");
            }
            methodsStringBuilder.append("\n");
        }

        // Build final popup text string.
        StringBuilder messageText = new StringBuilder("file name: " + currentFile.getName() + "\n" +
                "file path: " + currentFile.getPath() + "\n" +
                "file length: " + currentDoc.getTextLength() + "\n" +
                "lines of code: " + currentDoc.getLineCount() + "\n\n" +
                "total methods: " + myFileStatistics.getTotalMethods() + "\n" +
                "public methods: " + myFileStatistics.getPublicMethods() + "\n" +
                "private methods: " + myFileStatistics.getPrivateMethods() + "\n" +
                "void methods: " + myFileStatistics.getVoidMethods() + "\n" +
                "constructors: " + myFileStatistics.getConstructors() + "\n\n" +
                "Methods:\n" + methodsStringBuilder.toString() + "\n");

        // Add display average complexity.
        DecimalFormat df = new DecimalFormat(DECIMAL_ROUNDING_FORMAT);
        float avgComplexity = myFileStatistics.getAvgComplexity();
        messageText.append("Average complexity: ");
        messageText.append(df.format(avgComplexity));

        // If average complexity above thresholds: display warning.
        if (avgComplexity >= COMPLEXITY_LEVEL_SERIOUS_WARNING) {
            messageText.append("WARNING: Your average complexity is dangerously high.\n" +
                    "This means your software will be hard to test properly and prone to bugs.\n" +
                    "We strongly advise you to refactor your code to decrease the complexity of methods.");
        } else if (avgComplexity >= COMPLEXITY_LEVEL_MILD_WARNING) {
            messageText.append("WARNING: Your average complexity is high.\n" +
                    "To improve code quality we advise you to refactor your code to decrease it.");
        }

        return messageText.toString();
    }

    /**
     * Build the string for the text that will be displayed
     * in the plugin's popup if the file is not a Java file.
     * @param currentFile the file object
     * @param currentDoc the document object
     * @return the String to be displayed in the plugin's popup
     */
    private String buildNonJavaText(VirtualFile currentFile, Document currentDoc) {

        return "file name: " + currentFile.getName() + "\n" +
                "file path: " + currentFile.getPath() + "\n" +
                "file length: " + currentDoc.getTextLength() + "\n" +
                "lines of code: " + currentDoc.getLineCount() + "\n\n" +
                "We're sorry, but method statistics are currently only available for Java files :(";
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