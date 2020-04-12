package util;

import com.intellij.openapi.editor.Document;
import data.FileStatistics;
import data.MethodStatistics;
import data.SummaryData;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * A utility to convert data between different representations.
 */
public class DataConverter {

    private static final String DECIMAL_ROUNDING_FORMAT = "#.##";

    /**
     * Convert from a FileStatistics object used during the data gathering stage for methods
     * to a list of SummaryData entries to be passed to the view.
     * @param stats the input data object
     * @return a list of SummaryData entries.
     */
    public static ArrayList<SummaryData> fileStatisticsToSummaryData(FileStatistics stats) {

        ArrayList<SummaryData> summaries = new ArrayList<>();
        DataConverter.addMiscellaneousStatistics(summaries, stats);
        DataConverter.addCyclomaticComplexities(summaries, stats);
        DataConverter.addNewDeletedStatistics(summaries, stats);

        return summaries;
    }

    private static void addMiscellaneousStatistics(ArrayList<SummaryData> summaries, FileStatistics stats) {
        DecimalFormat df = new DecimalFormat(DECIMAL_ROUNDING_FORMAT);
        summaries.add(new SummaryData("Name", stats.getName()));
        summaries.add(new SummaryData("No of lines", stats.getLines() + ""));
        summaries.add(new SummaryData("File length", stats.getFileLength() + ""));
        summaries.add(new SummaryData("No of methods", stats.getTotalMethods() + ""));
        summaries.add(new SummaryData("No of public methods", stats.getPublicMethods() + ""));
        summaries.add(new SummaryData("No of private methods", stats.getPrivateMethods() + ""));
        summaries.add(new SummaryData("No of static methods", stats.getStaticMethods() + ""));
        summaries.add(new SummaryData("Average cyclomatic complexity",
                df.format(stats.getAverageComplexity()) + ""));
    }

    private static void addNewDeletedStatistics(ArrayList<SummaryData> summaries, FileStatistics stats) {
        if (stats.getNewLines() < 0) {
            summaries.add(new SummaryData("No of deleted lines (since action last run on this file)", -stats.getNewLines() + ""));

        } else {
            summaries.add(new SummaryData("No of new lines (since action last run on this file)", stats.getNewLines() + ""));
        }
        if (stats.getNewFileLength() < 0) {
            summaries.add(new SummaryData("No of deleted characters (since action last run on this file)", -stats.getNewFileLength() + ""));
        } else {
            summaries.add(new SummaryData("No of new characters (since action last run on this file)", stats.getNewFileLength() + ""));
        }
        if (stats.getNewMethods() < 0) {
            summaries.add(new SummaryData("No of deleted methods (since action last run on this file)", -stats.getNewMethods() + ""));
        } else {
            summaries.add(new SummaryData("No of new methods (since action last run on this file)", stats.getNewMethods() + ""));
        }
    }

    private static void addCyclomaticComplexities(ArrayList<SummaryData> summaries, FileStatistics stats) {
        if (stats.getMethods() != null) {
            for (MethodStatistics methodStatistics : stats.getMethods()) {
                summaries.add(new SummaryData("Cyclomatic complexity for: " + methodStatistics.getName(), methodStatistics.getComplexity() + ""));
            }
        }
    }
}
