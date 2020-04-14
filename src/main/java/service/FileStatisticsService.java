package service;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.*;
import com.intellij.openapi.components.State;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
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
        public Map<String, List<FileStatistics>> fileStatisticsMap = new HashMap<>();
    }

    public void setFileStatisticsMap(@NotNull Map<String, List<FileStatistics>> fileStatisticsMap) {
        state.fileStatisticsMap = fileStatisticsMap;
    }

    public Map<String, List<FileStatistics>> getFileStatisticsMap() {
        return state.fileStatisticsMap;
    }

    @Nullable
    @Override
    public State getState() {
        System.out.println("FileStatisticsService: Saving state");
        // logger.info("FileStatisticsService: Saving state");
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
     *
     * @param fileStatistics object containing all of the information to be stored on disk
     */
    public void saveStatistics(FileStatistics fileStatistics) {

        Map<String, List<FileStatistics>> fileStatisticsMap = this.getFileStatisticsMap();
        String fileName = fileStatistics.getName();

        // File already exists in memory
        if (fileStatisticsMap.containsKey(fileName)) {
            fileStatisticsMap.get(fileName).add(fileStatistics);
        } else {
            List<FileStatistics> fileStatisticsList = new ArrayList<>();
            fileStatisticsList.add(fileStatistics);
            fileStatisticsMap.put(fileName, fileStatisticsList);
        }
        this.setFileStatisticsMap(fileStatisticsMap);
    }


    /**
     * Wrapper function that returns a FileStatisticsService instance
     * @return FileStatisticsService instance
     */
    public static FileStatisticsService getInstance(Project currentProject) {
        // return currentProject.getComponent(FileStatisticsService.class);
        //return ApplicationManager.getApplication().getComponent(FileStatisticsService.class);
        return ServiceManager.getService(currentProject, FileStatisticsService.class);
    }
}
