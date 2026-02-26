package defacement.service;

import java.util.List;

public interface LogViewerService {
    List<String> getRecentLogs(int lines);
    List<String> searchLogs(String keyword, int lines);
    List<String> getLogsByLevel(String level, int lines);
}