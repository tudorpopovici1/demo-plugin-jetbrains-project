package view;

import service.FileStatistics;
import com.intellij.openapi.project.Project;
import service.FileStatistics.FileStatisticsBuilder;
import service.FileStatisticsService;

import java.util.List;
import java.util.Map;

/**
 * @author Tudor Popovici
 *
 * DataAggregator has the responsibility of retrieving all previous information for a file from disk, and creating new statistics based on this data,
 * as well as compare this previous data to the current data.
 */

public class DataAggregator {

    private FileStatistics lastOccurrence;
    private List<FileStatistics> fileStatisticsList;
    private FileStatisticsService fileStatisticsService;

    public DataAggregator(Project project, String fileName) {
        this.fileStatisticsService = FileStatisticsService.getInstance(project);
        this.fileStatisticsList = this.getFileStatisticsList(fileName);
        this.lastOccurrence = this.getLastOccurrence();
        System.out.println("ok");
    }


    /**
     * Method that adds new statistics to the FileStatistics object: new lines, methods and characters.
     * If there is no previous data, do not add anything.
     *
     * @param builder FileStatisticsBuilder - by this stage, the builder will contain the current file information.
     * @return FileStatisticsBuilder - builder containing the storage data on top of the current data.
     */
    public FileStatisticsBuilder collectStorageData(FileStatisticsBuilder builder) {
        if (lastOccurrence != null) {
            int newLines = builder.getLines() - this.getLastOccurrence().getLines();
            builder.withNewLines(newLines);
            int newLength = builder.getFileLength() - this.getLastOccurrence().getFileLength();
            builder.withNewFileLength(newLength);
            int newMethods = builder.getTotalMethods() - this.getLastOccurrence().getTotalMethods();
            builder.withNewMethods(newMethods);
        }
        return builder;
    }


    /**
     * Gets the most recent previous statistics for a file.
     *
     * @return FileStatistics object containing the most recent previous statistics for a file.
     */
    private FileStatistics getLastOccurrence() {
        if (this.fileStatisticsList != null) {
            return this.fileStatisticsList.get(fileStatisticsList.size() - 1);
        }
        return null;
    }

    /**
     * Retrieve the file statistics list from disk, given a file name.
     *
     * @param fileName file name to use for querying.
     * @return  a list of FileStatistics objects.
     */
    private List<FileStatistics> getFileStatisticsList(String fileName) {

        Map<String, List<FileStatistics>> fileStatisticsMap = fileStatisticsService.getFileStatisticsMap();

        if (fileStatisticsMap != null) {
            if (fileStatisticsMap.containsKey(fileName)) {
                return fileStatisticsMap.get(fileName);
            }
        }
        return null;
    }
}
