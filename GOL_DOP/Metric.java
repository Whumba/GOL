public class Metric {
	public static MetricData createMetaData(){
		return new MetricData(System.currentTimeMillis(), 0L, 0L, (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()));
	}
	
	public static MetricData newRunTime(MetricData metricData){
		return new MetricData(metricData.startTime(), System.currentTimeMillis(), metricData.endTime(), metricData.maxMemory());
	}
	
	public static MetricData newEndTime(MetricData metricData){
		return new MetricData(metricData.startTime(), metricData.runTime(), System.currentTimeMillis(), metricData.maxMemory());
	}

	public static MetricData newMaxMemory(MetricData metricData, long maxMemory){
		return new MetricData(metricData.startTime(), metricData.runTime(), metricData.endTime(), maxMemory);
	}

	public static long durationInitializing(MetricData metricData){
		return metricData.runTime() - metricData.startTime();
	}

	public static long durationRunning(MetricData metricData){
		return metricData.endTime() - metricData.runTime();
	}
}