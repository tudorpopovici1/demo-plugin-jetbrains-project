package data;

import com.intellij.psi.PsiMethod;
import com.intellij.util.xmlb.annotations.Transient;
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
    @Transient
    private PsiMethod method;

    public PsiMethod getMethod() {
        return method;
    }

    /**
     * The name of the method.
     */
    @NotNull
    private String name;

    public String getName(){
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * The cyclomatic complexity of
     */
    private int complexity;

    public int getComplexity() {
        return complexity;
    }

    public void setComplexity(int complexity) {
        this.complexity = complexity;
    }

    public void incrementComplexity() {
        complexity++;
    }

    /**
     * Empty constructor of MethodStatistics
     */
    public MethodStatistics() {}

    /**
     * Constructs an instance of MethodStatistics.
     * @param method the Psi method object referred to by this instance
     */
    public MethodStatistics(PsiMethod method) {

        this.method = method;
        this.name = method.getName();
        this.complexity = 1;
    }

}
