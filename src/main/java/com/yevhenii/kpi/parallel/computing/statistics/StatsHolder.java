package com.yevhenii.kpi.parallel.computing.statistics;

import java.util.Map;

public class StatsHolder {
    private String tag;
    private int runs;
    private Map<String, Double> statistics;

    public StatsHolder() {
    }

    public StatsHolder(String tag, int runs, Map<String, Double> statistics) {
        this.tag = tag;
        this.runs = runs;
        this.statistics = statistics;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getRuns() {
        return runs;
    }

    public void setRuns(int runs) {
        this.runs = runs;
    }

    public Map<String, Double> getStatistics() {
        return statistics;
    }

    public void setStatistics(Map<String, Double> statistics) {
        this.statistics = statistics;
    }
}
