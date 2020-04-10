package actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.pom.Navigatable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class MarkdownAction extends AnAction {

    /**
     * This default constructor is used by the IntelliJ Platform framework to
     * instantiate this class based on plugin.xml declarations. Only needed in MarkdownAction
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

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {

        Project currentProject = event.getProject();
        String path = currentProject.getBasePath();
        File directory = new File(path);

        //find only md files
        FilenameFilter textFilter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                String lowercaseName = name.toLowerCase();
                if (lowercaseName.endsWith(".md")) {
                    return true;
                } else {
                    return false;
                }
            }
        };
        long fileCount = directory.list(textFilter).length;
        long lineCount = 0;
        long wordCount = 0;
        File[] files = directory.listFiles(textFilter);
        String msg = "Number of md files: " + fileCount + "\n"
                + "File names: \n";

        // gets the number of lines and words of all md files
        for(File f:files) {
            Path filePath = Paths.get(f.getPath());
            try {
                long count = Files.lines(filePath).count();
                msg += "-" + f.getName() + " (" + count + " lines, ";
                lineCount += count;
            } catch (IOException e) {
                e.printStackTrace();
            }

            try (Scanner sc = new Scanner(new FileInputStream(f))) {
                int count = 0;
                while (sc.hasNext()) {
                    sc.next();
                    count++;
                }
                msg += count + " words)\n";
                wordCount += count;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        msg += "Total number of lines: " + lineCount + "\n" +
                "Total number of words: " + wordCount + "\n";

        Navigatable nav = event.getData(CommonDataKeys.NAVIGATABLE);
        if (nav != null) {
            msg += String.format("\nSelected Element: %s", nav.toString());
        }

        Messages.showMessageDialog(currentProject, msg, "Markdown Files Report", Messages.getInformationIcon());
    }

    @Override
    public void update(AnActionEvent e) {
        Project project = e.getProject();
        e.getPresentation().setEnabledAndVisible(project != null);
    }
}
