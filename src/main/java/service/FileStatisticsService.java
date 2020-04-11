package service;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.*;
import com.intellij.openapi.components.State;
import com.intellij.openapi.diagnostic.Logger;
import data.FileStatistics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Tudor Popovici
 *
 * Service that has the responsibility of storing data on disk in an XML format.
 */

@State(name="FileStatisticsService",
       storages = {@Storage("file_statistics.xml")})
public class FileStatisticsService implements PersistentStateComponent<FileStatisticsService.State> {

    final static Logger logger = Logger.getInstance(SummaryService.class);

    public State state = new State();

    public static class State {
        public Map<String, Map<String, List<FileStatistics>>> projectMap = new HashMap<>();
    }

    public void setProjectMap(@NotNull Map<String, Map<String, List<FileStatistics>>> projectMap) {
        state.projectMap = projectMap;
    }

    public Map<String, Map<String, List<FileStatistics>>> getProjectMap() {
        return state.projectMap;
    }

    @Nullable
    @Override
    public State getState() {
        logger.info("FileStatisticsService: Saving state");
        return state;
    }

    @Override
    public void loadState(@NotNull State state) {
        this.state = state;
    }

    @Override
    public void noStateLoaded() {
        logger.info("FileStatisticsService: No state has been loaded");
    }

    /**
     * Saves on disk statistics per project and per file.
     * @param projectName name of the project - to be used as a key - unique within the xml file
     * @param fileStatistics object containing all of the information to be stored on disk
     */
    public void saveStatistics(String projectName, FileStatistics fileStatistics) {

        Map<String, Map<String, List<FileStatistics>>> projectMap = this.getProjectMap();
        String fileName = fileStatistics.getName();

        // Project already exists in memory
        if (projectMap.containsKey(projectName)) {
            Map<String, List<FileStatistics>> fileStatisticsMap = projectMap.get(projectName);
            if (fileStatisticsMap.containsKey(fileName)) {
                List<FileStatistics> fileStatisticsList = fileStatisticsMap.get(fileName);
                fileStatisticsList.add(fileStatistics);
            } else {
                List<FileStatistics> fileStatisticsList = new ArrayList<>();
                fileStatisticsList.add(fileStatistics);
                fileStatisticsMap.put(fileName, fileStatisticsList);
            }
        } else {
            Map<String, List<FileStatistics>> newFileStatisticsMap = new HashMap<>();
            List<FileStatistics> fileStatisticsList = new ArrayList<>();
            fileStatisticsList.add(fileStatistics);
            newFileStatisticsMap.put(fileName, fileStatisticsList);
            projectMap.put(projectName, newFileStatisticsMap);
        }
        this.setProjectMap(projectMap);
    }

    /**
     * Wrapper function that returns a FileStatisticsService instance
     * @return FileStatisticsService instance
     */
    public static FileStatisticsService getInstance() {
        return ApplicationManager.getApplication().getComponent(FileStatisticsService.class);
    }
}
