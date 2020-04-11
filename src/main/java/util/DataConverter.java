package util;

import com.intellij.openapi.editor.Document;
import data.FileStatistics;
import data.MDStatistics;
import data.SummaryData;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * A utility to convert data between different representations.
 */
public class DataConverter {

    private static final String DECIMAL_ROUNDING_FORMAT = "#.##";

    /**
     * Method Summary Action
     * Convert from a FileStatistics object used during the data gathering stage for methods
     * to a list of SummaryData entries to be passed to the view.
     * @param stats the input data object
     * @return a list of SummaryData entries.
     */
    public static ArrayList<SummaryData> fileStatisticsToSummaryData(FileStatistics stats) {

        Document document = stats.getDocument();
        ArrayList<SummaryData> summaries = new ArrayList<>();
        DecimalFormat df = new DecimalFormat(DECIMAL_ROUNDING_FORMAT);

        summaries.add(new SummaryData("Name", stats.getName()));
        summaries.add(new SummaryData("No of lines", document.getLineCount() + ""));
        summaries.add(new SummaryData("File length", document.getTextLength() + ""));
        summaries.add(new SummaryData("No of methods", stats.getTotalMethods() + ""));
        summaries.add(new SummaryData("No of public methods", stats.getPublicMethods() + ""));
        summaries.add(new SummaryData("No of private methods", stats.getPrivateMethods() + ""));
        summaries.add(new SummaryData("No of static methods", stats.getStaticMethods() + ""));
        summaries.add(new SummaryData("Average cyclomatic complexity",
                df.format(stats.getAvgComplexity()) + ""));

        return summaries;
    }

    /**
     * Markdown Files Action
     * Convert MDStatistics object used during the data gathering stage for MD files
     * to a list of SummaryData entries to be passed to the view.
     * @param stats the input data object
     * @return a list of SummaryData entries.
     */
    public static ArrayList<SummaryData> mdStatisticsToSummaryData(MDStatistics stats) {
        ArrayList<SummaryData> summaries = new ArrayList<>();
        summaries.add(new SummaryData("No of markdown files", stats.getNoFiles() + ""));
        summaries.add(new SummaryData("Total no of lines", stats.getNoLines() + ""));
        summaries.add(new SummaryData("No of headers", stats.getNoHeaders() + ""));
        summaries.add(new SummaryData("No of files with links", stats.getNoFilesLinks() + ""));
        summaries.add(new SummaryData("Total no of links", stats.getNoLinks() + ""));
        summaries.add(new SummaryData("No of reference links to repo", stats.getNoRepoLinks() + ""));
        summaries.add(new SummaryData("No of urls", stats.getNoUrls() + ""));
        return summaries;
    }
}
