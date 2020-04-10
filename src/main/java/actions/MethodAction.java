package actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.pom.Navigatable;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;

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

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {

        // Get context objects
        Project currentProject = event.getProject();
        Document currentDoc = FileEditorManager.getInstance(currentProject).getSelectedTextEditor().getDocument();
        VirtualFile currentFile = FileDocumentManager.getInstance().getFile(currentDoc);

        // Get file info
        String filePath = currentFile.getPath();
        String fileName = currentFile.getName();
        int docLength = currentDoc.getTextLength();
        int docLines = currentDoc.getLineCount();

        // Get Psi file
        PsiFile psiFile = PsiDocumentManager.getInstance(currentProject).getPsiFile(currentDoc);
        // Check if file is Java file, else display warning
        if (psiFile instanceof PsiJavaFile) {

            // Setup statistics counters
            int methodCounter = 0;
            int privateMethodCounter = 0;
            int publicMethodCounter = 0;
            ArrayList<String> methodNames = new ArrayList<>();

            PsiClass[] classes = ((PsiJavaFile) psiFile).getClasses();
            for (PsiClass psiClass: classes) {

                PsiMethod[] methods = psiClass.getMethods();
                for (PsiMethod method: methods) {

                    methodCounter++;
                    String methodName = method.getName();
                    methodNames.add(methodName);
                    PsiModifierList modifiers = method.getModifierList();
                    if (modifiers.hasExplicitModifier("public")) { publicMethodCounter++; }
                    if (modifiers.hasExplicitModifier("private")) { privateMethodCounter++; }
                }
            }

            // Build string from list of methods
            StringBuilder methodsStringBuilder = new StringBuilder();
            for (int i = 0; i < methodNames.size(); i++) {
                methodsStringBuilder.append("\t");
                methodsStringBuilder.append(methodNames.get(i));
                if (i != methodNames.size() - 1) {
                    methodsStringBuilder.append(",\n");
                }
            }

            // Build final popup text string
            StringBuffer dlgMsg = new StringBuffer(
                            "file name: " + fileName + "\n" +
                            "file path: " +  filePath + "\n" +
                            "file length: " + docLength + "\n" +
                            "lines of code: " + docLines + "\n" +
                            "number of methods: " + methodCounter + "\n" +
                            "number of public methods: " + publicMethodCounter + "\n" +
                            "number of private methods: " + privateMethodCounter + "\n" +
                            "method names:\n" + methodsStringBuilder.toString()
            );

            // If an element is selected in the editor, add info about it.
            Navigatable nav = event.getData(CommonDataKeys.NAVIGATABLE);
            if (nav != null) {
                dlgMsg.append(String.format("\n\nSelected Element: %s", nav.toString()));
            }
            Messages.showMessageDialog(currentProject,
                    dlgMsg.toString(),
                    "Summary Report",
                    Messages.getInformationIcon());

        } else {

            // Build final popup text string
            StringBuffer dlgMsg = new StringBuffer(
                            "file name: " + fileName + "\n" +
                            "file path: " +  filePath + "\n" +
                            "file length: " + docLength + "\n" +
                            "lines of code: " + docLines + "\n\n" +
                            "We're sorry, but method statistics are currently only available for Java files :(");
            Messages.showMessageDialog(currentProject,
                    dlgMsg.toString(),
                    "Summary Report",
                    Messages.getInformationIcon());
        }
    }

    @Override
    public void update(AnActionEvent e) {
        Project project = e.getProject();
        e.getPresentation().setEnabledAndVisible(project != null);
    }
}