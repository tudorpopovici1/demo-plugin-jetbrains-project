package util;

import data.FileStatistics;
import data.MethodStatistics;
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

        } else if (stats.getNewLines() > 0){
            summaries.add(new SummaryData("No of new lines (since action last run on this file)", stats.getNewLines() + ""));
        }
        if (stats.getNewFileLength() < 0) {
            summaries.add(new SummaryData("No of deleted characters (since action last run on this file)", -stats.getNewFileLength() + ""));
        } else  if (stats.getNewFileLength() > 0){
            summaries.add(new SummaryData("No of new characters (since action last run on this file)", stats.getNewFileLength() + ""));
        }
        if (stats.getNewMethods() < 0) {
            summaries.add(new SummaryData("No of deleted methods (since action last run on this file)", -stats.getNewMethods() + ""));
        } else if (stats.getNewMethods() > 0){
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
