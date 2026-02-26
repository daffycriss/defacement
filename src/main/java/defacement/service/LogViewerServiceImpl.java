package defacement.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LogViewerServiceImpl implements LogViewerService {

    private static final Logger logger = LoggerFactory.getLogger(LogViewerServiceImpl.class);

    @Value("${logging.file.name:logs/defacement-app.log}")
    private String logFilePath;

    private List<String> readLastLines(int lines) {
        try {
            List<String> allLines = Files.readAllLines(Paths.get(logFilePath));
            int from = Math.max(0, allLines.size() - lines);
            return allLines.subList(from, allLines.size());
        } catch (IOException e) {
            logger.error("Failed to read log file [{}]: {}", logFilePath, e.getMessage());
            return List.of("Log file not found or unreadable.");
        }
    }

    @Override
    public List<String> getRecentLogs(int lines) {
        return readLastLines(lines);
    }

    @Override
    public List<String> searchLogs(String keyword, int lines) {
        try {
            List<String> allLines = Files.readAllLines(Paths.get(logFilePath));
            List<String> filtered = allLines.stream()
                    .filter(line -> line.toLowerCase().contains(keyword.toLowerCase()))
                    .collect(Collectors.toList());
            int from = Math.max(0, filtered.size() - lines);
            return filtered.subList(from, filtered.size());
        } catch (IOException e) {
            logger.error("Failed to search log file: {}", e.getMessage());
            return List.of("Log file not found or unreadable.");
        }
    }

    @Override
    public List<String> getLogsByLevel(String level, int lines) {
        return searchLogs(" " + level.toUpperCase() + " ", lines);
    }
}