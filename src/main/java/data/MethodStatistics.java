package data;

import com.intellij.psi.PsiMethod;
import com.sun.istack.NotNull;

/**
 * @author Tommaso Brandirali
 *
 * A data class to hold partial and final values,
 * of statistics about a single method.
 */
public class MethodStatistics {

    /**
     * The method object referenced by this instance.
     */
    @NotNull
    private final PsiMethod myMethod;

    public PsiMethod getMethod() {
        return myMethod;
    }

    /**
     * The name of the method.
     */
    @NotNull
    private final String myName;

    public String getName(){
        return myName;
    }

    /**
     * The cyclomatic complexity of
     */
    private int myComplexity;

    public int getComplexity() {
        return myComplexity;
    }

    public void incrementComplexity() {
        myComplexity++;
    }

    /**
     * Constructs an instance of MethodStatistics.
     * @param method the Psi method object referred to by this instance
     */
    public MethodStatistics(PsiMethod method) {

        myMethod = method;
        myName = method.getName();
        myComplexity = 1;
    }
}
