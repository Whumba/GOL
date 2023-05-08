// startTime, runTime and endTime are Timestamps != Duration
public record MetricData(Long startTime,
                         Long runTime,
                         Long endTime,
                         Long maxMemory) {}