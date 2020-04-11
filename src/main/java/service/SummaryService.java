package service;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerAdapter;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.*;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.util.messages.MessageBusConnection;
import data.SummaryData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import view.SummaryView;

import java.util.ArrayList;

public class SummaryService {

    final static Logger logger = Logger.getInstance(SummaryService.class);

    SummaryView view;
    ArrayList<SummaryData> lastSummary = new ArrayList<>();

    public SummaryService(Project project) {
        logger.info("Summary service is starting");
        if (view == null) {
            view = new SummaryView();
        }

        ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
        ToolWindow toolWindow = toolWindowManager.registerToolWindow("Summary_Test", false, ToolWindowAnchor.BOTTOM);
        //toolWindow.setIcon(ICON);
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(view, null, true);
        toolWindow.getContentManager().addContent(content);

        final MessageBusConnection connection = project.getMessageBus().connect(project);
        connection.subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, new FileEditorManagerAdapter() {
            @Override
            public void selectionChanged(@NotNull FileEditorManagerEvent event) {
                updateView (event.getManager().getProject(), event.getNewFile(), false);
            }
        });

        PsiManager.getInstance(project).addPsiTreeChangeListener(new PsiTreeAnyChangeAbstractAdapter() {
            @Override
            protected void onChange(@Nullable PsiFile psiFile) {
                ArrayList<SummaryData> newSummary = new ArrayList<>();
                if (psiFile instanceof PsiJavaFile) {
                    PsiJavaFile javaFile = (PsiJavaFile) psiFile;
                    newSummary = getSummary(javaFile);
                }

                // TO DO: dynamically changes numbers on changes
            }
        });
    }

    public ArrayList<SummaryData> getSummary (PsiJavaFile javaFile) {
        ArrayList<SummaryData> summaries = new ArrayList<>();
        Document doc = FileEditorManager.getInstance(javaFile.getProject()).getSelectedTextEditor().getDocument();

        // Get file info
        String name = javaFile.getName();
        int fileLength = javaFile.getTextLength();
        int noLines = doc.getLineCount();

        // Setup method counters
        int methodCounter = 0;
        int privateMethodCounter = 0;
        int publicMethodCounter = 0;
        int staticMethodCounter = 0;
        int finalMethodCounter = 0;

        // Setup field counters
        int fieldCounter = 0;
        int privateFieldCounter = 0;
        int publicFieldCounter = 0;
        int staticFieldCounter = 0;
        int finalFieldCounter = 0;

        PsiClass[] classes = javaFile.getClasses();
        for (PsiClass psiClass: classes) {
            PsiField[] fields = psiClass.getAllFields();
            PsiMethod[] methods = psiClass.getMethods();
            for (PsiMethod method: methods) {
                methodCounter++;
                PsiModifierList modifiers = method.getModifierList();
                if (modifiers.hasExplicitModifier("public")) { publicMethodCounter++; }
                if (modifiers.hasExplicitModifier("private")) { privateMethodCounter++; }
                if (modifiers.hasExplicitModifier("static")) { staticMethodCounter++; }
                if (modifiers.hasExplicitModifier("final")) { finalMethodCounter++; }
            }
            for (PsiField field: fields) {
                fieldCounter++;
                PsiModifierList modifiers = field.getModifierList();
                if (modifiers.hasExplicitModifier("public")) { publicFieldCounter++; }
                if (modifiers.hasExplicitModifier("private")) { privateFieldCounter++; }
                if (modifiers.hasExplicitModifier("static")) { staticFieldCounter++; }
                if (modifiers.hasExplicitModifier("final")) { finalFieldCounter++; }
            }
        }
        summaries.add(new SummaryData("Name", name));
        summaries.add(new SummaryData("No of lines", noLines + ""));
        summaries.add(new SummaryData("File length", fileLength + ""));
        summaries.add(new SummaryData("No of methods", methodCounter + ""));
        summaries.add(new SummaryData("No of public methods", publicMethodCounter + ""));
        summaries.add(new SummaryData("No of private methods", privateMethodCounter + ""));
        summaries.add(new SummaryData("No of static methods", staticMethodCounter + ""));
        summaries.add(new SummaryData("No of final methods", finalMethodCounter + ""));
        summaries.add(new SummaryData("No of fields", fieldCounter + ""));
        summaries.add(new SummaryData("No of public fields", publicFieldCounter + ""));
        summaries.add(new SummaryData("No of private fields", privateFieldCounter + ""));
        summaries.add(new SummaryData("No of static fields", staticFieldCounter + ""));
        summaries.add(new SummaryData("No of final fields", finalFieldCounter + ""));
        return summaries;
    }

    public void updateView(Project project, VirtualFile file, Boolean activate) {
        ArrayList<SummaryData> summaries = new ArrayList<>();
        ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow("Summary_Test");

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
}
