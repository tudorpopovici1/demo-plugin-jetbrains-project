package util;

import com.intellij.psi.*;
import com.intellij.psi.tree.TokenSet;
import com.intellij.psi.util.PsiTreeUtil;
import util.MethodStatistics;

/**
 * @author Tommaso Brandirali
 *
 * An implementation of JavaRecursiveElementWalkingVisitor to compute the cyclomatic complexity of a method.
 * The cyclomatic complexity is defined as (1 + [number of choice points]).
 */
public class JavaRecursiveMethodVisitor extends PsiRecursiveElementWalkingVisitor {

    /**
     * The list of classes to be considered as representing some form of conditional statement,
     * in the context of computing the cyclomatic complexity.
     */
    private final Class[] CONDITIONAL_STATEMENTS = {
        PsiIfStatement.class,
        PsiForeachStatement.class,
        PsiForStatement.class,
        PsiWhileStatement.class,
        PsiConditionalExpression.class,
        PsiTryStatement.class
    };

    /**
     * The set of operators implementing boolean logic which impact the cyclomatic complexity
     * if found in an if statement's condition.
     */
    private final TokenSet BOOLEAN_OPERATIONS = TokenSet.create(
            JavaTokenType.OR, JavaTokenType.AND,
            JavaTokenType.OROR, JavaTokenType.ANDAND);

    /**
     * The statistics object to be updated through the walk.
     */
    private final MethodStatistics myMethodStatistics;

    /**
     * Constructs an instance of JavaRecursiveMethodVisitor.
     * @param methodStatistics the statistics object to be used
     */
    public JavaRecursiveMethodVisitor(MethodStatistics methodStatistics) {
        super();
        myMethodStatistics = methodStatistics;
    }

    /**
     * This method implements the logic to be executed on every node of the tree.
     * Specifically, it increments the cyclomatic complexity in case the element is within
     * the predefined set of conditional statements. Additionally, it increments the complexity
     * in case logical operators (such as && and ||) are found in the condition of an if statement.
     * @param element the node of the Psi tree currently being visited
     */
    @Override
    public void visitElement(PsiElement element) {

        // Increment complexity if element is a form of condition statement.
        if (isInstanceOfConditional(element)) {
            myMethodStatistics.incrementComplexity();
        }

        // Increment complexity for each logical operator found in if statement's condition.
        if (element instanceof PsiIfStatement) {
            PsiExpression condition = ((PsiIfStatement) element).getCondition();
            PsiJavaToken[] binaryExpressionsWithinCondition = PsiTreeUtil.getChildrenOfType(
                    condition, PsiJavaToken.class);
            if (binaryExpressionsWithinCondition != null) {
                for (PsiJavaToken binaryExpression : binaryExpressionsWithinCondition) {
                    if (BOOLEAN_OPERATIONS.contains(binaryExpression.getTokenType())) {
                        myMethodStatistics.incrementComplexity();
                    }
                }
            }
        }

        // Continue walking...
        super.visitElement(element);
    }

    /**
     * Helper function to check whether an object is an instance of one of the classes
     * considered to be conditional operators.
     * @param element the element to be checked
     * @return true if the element is an instance of a conditional operation, false otherwise.
     */
    private boolean isInstanceOfConditional(PsiElement element) {

        for (Class c: CONDITIONAL_STATEMENTS) {
            if (c.isInstance(element)) return true;
        }
        return false;
    }
}
