package actions.methodAction;

import com.intellij.psi.PsiModifierList;
import com.sun.istack.NotNull;

import java.util.ArrayList;

/**
 * @author Tommaso Brandirali
 *
 * A data class to hold partial and final values
 * of statistics about the whole file.
 * All values refer to the document in the currently open editor.
 */
public class FileStatistics {

    /**
     * Total number of methods.
     */
    @NotNull
    private int myTotalMethods;

    public int getTotalMethods() {
        return myTotalMethods;
    }

    /**
     * Number of private methods.
     */
    @NotNull
    private int myPrivateMethods;

    public int getMyPrivateMethods() {
        return myPrivateMethods;
    }

    /**
     * Number of public methods.
     */
    @NotNull
    private int myPublicMethods;

    public int getMyPublicMethods() {
        return myPublicMethods;
    }

    /**
     * The list of MethodStatistics objects holding data about the specific methods.
     */
    @NotNull
    private final ArrayList<MethodStatistics> myMethods;

    public ArrayList<MethodStatistics> getMyMethods() {
        return myMethods;
    }

    public void addMethod(MethodStatistics methodStatistics) {

        myMethods.add(methodStatistics);
        myTotalMethods++;

        PsiModifierList modifiers = methodStatistics.getMethod().getModifierList();
        if (modifiers.hasExplicitModifier("public")) {
            myPublicMethods++;
        }
        if (modifiers.hasExplicitModifier("private")) {
            myPrivateMethods++;
        }
    }

    /**
     * Construct an instance of FileStatistics.
     */
    public FileStatistics() {

        this.myTotalMethods = 0;
        this.myPrivateMethods = 0;
        this.myPublicMethods = 0;
        this.myMethods = new ArrayList<>();
    }
}
