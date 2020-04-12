package data;

import com.intellij.openapi.editor.Document;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.PsiType;
import com.sun.istack.NotNull;

import java.util.ArrayList;
import java.util.Objects;

/**
 * @author Tommaso Brandirali and Tudor Popovici
 *
 *
 * A data class to hold values of statistics about the whole file.
 * By using a builder, we can collect both 'current' and 'storage' statistics.
 * All values stored refer to the document in the currently open editor.
 */
public class FileStatistics {

    public static class FileStatisticsBuilder {

        private float averageComplexity;
        private int constructors;
        private Document document;
        private int fileLength;
        private int lines;
        private String name;
        private int publicMethods = 0;
        private int privateMethods = 0;
        private int staticMethods = 0;
        private int totalMethods = 0;
        private int voidMethods = 0;
        private int newLines;
        private int newMethods;
        private int newFileLength;
        @NotNull
        private ArrayList<MethodStatistics> methods;

        public FileStatisticsBuilder withNewFileLength(int fileLength) {
            this.newFileLength = fileLength;
            return this;
        }

        public FileStatisticsBuilder withNewLines(int newLines) {
            this.newLines = newLines;
            return this;
        }

        public FileStatisticsBuilder withNewMethods(int newMethods) {
            this.newMethods = newMethods;
            return this;
        }

        public FileStatisticsBuilder withDocument(Document document) {
            this.document = document;
            return this;
        }

        public FileStatisticsBuilder withFileLength(int fileLength) {
            this.fileLength = fileLength;
            return this;
        }

        public FileStatisticsBuilder withLines(int lines) {
            this.lines = lines;
            return this;
        }

        public FileStatisticsBuilder addName(String name) {
            this.name = name;
            return this;
        }

        public FileStatisticsBuilder calculateAverageComplexity() {
            int sumComplexities = this.methods.stream().mapToInt(MethodStatistics::getComplexity).sum();
            this.averageComplexity = (float) sumComplexities / this.totalMethods;
            return this;
        }

        public FileStatistics build() {
            FileStatistics fileStatistics = new FileStatistics();
            fileStatistics.averageComplexity = this.averageComplexity;
            fileStatistics.publicMethods = this.publicMethods;
            fileStatistics.privateMethods = this.privateMethods;
            fileStatistics.lines = this.lines;
            fileStatistics.fileLength = this.fileLength;
            fileStatistics.totalMethods = this.totalMethods;
            fileStatistics.document = this.document;
            fileStatistics.constructors = this.constructors;
            fileStatistics.name = this.name;
            fileStatistics.voidMethods = this.voidMethods;
            fileStatistics.staticMethods = this.staticMethods;
            fileStatistics.methods = this.methods;
            fileStatistics.newLines = this.newLines;
            fileStatistics.newMethods = this.newMethods;
            fileStatistics.newFileLength = this.newFileLength;
            return fileStatistics;
        }

        public void addMethod(MethodStatistics methodStatistics) {

            if (this.methods == null) {
                this.methods = new ArrayList<>();
            }

            this.methods.add(methodStatistics);
            this.totalMethods++;

            PsiModifierList modifiers = methodStatistics.getMethod().getModifierList();
            if (modifiers.hasExplicitModifier(PsiModifier.PUBLIC)) {
                this.publicMethods++;
            }
            if (modifiers.hasExplicitModifier(PsiModifier.PRIVATE)) {
                this.privateMethods++;
            }
            if (modifiers.hasExplicitModifier(PsiModifier.STATIC)) {
                this.staticMethods++;
            }
            if (Objects.equals(methodStatistics.getMethod().getReturnType(), PsiType.VOID)) {
                this.voidMethods++;
            }
            if (methodStatistics.getMethod().isConstructor()) {
                this.constructors++;
            }
        }

        public int getTotalMethods() {
            return totalMethods;
        }

        public int getFileLength() {
            return fileLength;
        }

        public int getLines() {
            return lines;
        }
    }

    private float averageComplexity;
    private int constructors;
    private Document document;
    private int fileLength;
    private int lines;
    private String name;
    private int publicMethods;
    private int privateMethods;
    private int staticMethods;
    private int totalMethods;
    private int voidMethods;
    private int newLines;
    private int newMethods;
    private int newFileLength;

    @NotNull
    private ArrayList<MethodStatistics> methods;

    private FileStatistics() {}

    public int getNewLines() {
        return newLines;
    }

    public void setNewLines(int newLines) {
        this.newLines = newLines;
    }

    public int getNewMethods() {
        return newMethods;
    }

    public void setNewMethods(int newMethods) {
        this.newMethods = newMethods;
    }

    public int getNewFileLength() {
        return newFileLength;
    }

    public void setNewFileLength(int newFileLength) {
        this.newFileLength = newFileLength;
    }

    public float getAverageComplexity() {
        return averageComplexity;
    }

    public void setAverageComplexity(float averageComplexity) {
        this.averageComplexity = averageComplexity;
    }

    public int getConstructors() {
        return constructors;
    }

    public void setConstructors(int constructors) {
        this.constructors = constructors;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public int getFileLength() {
        return fileLength;
    }

    public void setFileLength(int fileLength) {
        this.fileLength = fileLength;
    }

    public int getLines() {
        return lines;
    }

    public void setLines(int lines) {
        this.lines = lines;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPublicMethods() {
        return publicMethods;
    }

    public void setPublicMethods(int publicMethods) {
        this.publicMethods = publicMethods;
    }

    public int getPrivateMethods() {
        return privateMethods;
    }

    public void setPrivateMethods(int privateMethods) {
        this.privateMethods = privateMethods;
    }

    public int getStaticMethods() {
        return staticMethods;
    }

    public void setStaticMethods(int staticMethods) {
        this.staticMethods = staticMethods;
    }

    public int getTotalMethods() {
        return totalMethods;
    }

    public void setTotalMethods(int totalMethods) {
        this.totalMethods = totalMethods;
    }

    public int getVoidMethods() {
        return voidMethods;
    }

    public void setVoidMethods(int voidMethods) {
        this.voidMethods = voidMethods;
    }

    public ArrayList<MethodStatistics> getMethods() {
        return methods;
    }

    public void setMethods(ArrayList<MethodStatistics> methods) {
        if (methods != null && methods.size()!= 0)
            this.methods = methods;
    }
}
