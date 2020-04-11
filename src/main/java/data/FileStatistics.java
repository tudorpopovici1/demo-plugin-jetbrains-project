package data;

import com.intellij.openapi.editor.Document;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.PsiType;
import com.sun.istack.NotNull;

import java.util.ArrayList;
import java.util.Objects;

/**
 * @author Tommaso Brandirali
 *
 * A data class to hold partial and final values
 * of statistics about the whole file.
 * All values refer to the document in the currently open editor.
 */
public class FileStatistics {

    /**
     * The name of the file referred to by this instance.
     */
    private final String myName;

    public String getName() {
        return myName;
    }

    /**
     * The Document object referred to by this instance.
     */
    private final Document myDocument;

    public Document getDocument() {
        return myDocument;
    }

    /**
     * Total number of methods.
     */
    private int myTotalMethods;

    public int getTotalMethods() {
        return myTotalMethods;
    }

    /**
     * Number of private methods.
     */
    private int myPrivateMethods;

    public int getPrivateMethods() {
        return myPrivateMethods;
    }

    /**
     * Number of public methods.
     */
    private int myPublicMethods;

    public int getPublicMethods() {
        return myPublicMethods;
    }

    /**
     * Number of static methods.
     */
    private int myStaticMethods;

    public int getStaticMethods() {
        return myStaticMethods;
    }

    /**
     * Number of void methods.
     */
    private int myVoidMethods;

    public int getVoidMethods() {
        return myVoidMethods;
    }

    /**
     * Number of constructors.
     */
    private int myConstructors;

    public int getConstructors() {
        return myConstructors;
    }

    public float getAvgComplexity() {
        int sumComplexities = myMethods.stream().mapToInt(MethodStatistics::getComplexity).sum();
        return (float) sumComplexities / myTotalMethods;
    }

    /**
     * The list of MethodStatistics objects holding data about the specific methods.
     */
    @NotNull
    private final ArrayList<MethodStatistics> myMethods;

    public ArrayList<MethodStatistics> getMethods() {
        return myMethods;
    }

    public void addMethod(MethodStatistics methodStatistics) {

        myMethods.add(methodStatistics);
        myTotalMethods++;

        PsiModifierList modifiers = methodStatistics.getMethod().getModifierList();
        if (modifiers.hasExplicitModifier(PsiModifier.PUBLIC)) {
            myPublicMethods++;
        }
        if (modifiers.hasExplicitModifier(PsiModifier.PRIVATE)) {
            myPrivateMethods++;
        }
        if (modifiers.hasExplicitModifier(PsiModifier.STATIC)) {
            myStaticMethods++;
        }
        if (Objects.equals(methodStatistics.getMethod().getReturnType(), PsiType.VOID)) {
            myVoidMethods++;
        }
        if (methodStatistics.getMethod().isConstructor()) {
            myConstructors++;
        }
    }

    /**
     * Construct an instance of FileStatistics.
     */
    public FileStatistics(String name, Document doc) {

        myName = name;
        myDocument = doc;
        myTotalMethods = 0;
        myPrivateMethods = 0;
        myPublicMethods = 0;
        myStaticMethods = 0;
        myVoidMethods = 0;
        myConstructors = 0;
        myMethods = new ArrayList<>();
    }
}
