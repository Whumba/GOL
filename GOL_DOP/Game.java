public class Game{
	public static GameData run(ParameterData parameterData){
		// Initializing-Phase START
		// Creating MetaData with Start set
		MetricData metricData = Metric.createMetaData();
		// Creating Cell Representation of Generation Zero
		GenerationData generationZero = Generation.createNewRandomized(parameterData);
		// Print Generation Zero
		checkPrinter(parameterData.printer(), generationZero);

		// Process the Evolution of Cells over Generations for maxDuration - Time for Initialization
		ProcessData processData = Process.evolveForDuration(metricData, generationZero, parameterData.maxDuration(), parameterData.printer());

		// Summarize everything in GameData
		GameData gameData = new GameData(parameterData, generationZero, processData);
		printResult(gameData);

		return gameData;
	}

	public static void printResult(GameData gameData){
		System.out.print(
			gameData.parameterData().rows() + "," +
			gameData.parameterData().columns() + "," +
			gameData.parameterData().maxDuration() + "," +
			gameData.parameterData().threshold() + "," +
			gameData.parameterData().seed() + "," +
			gameData.processData().generationData().generation() + "," +
			Metric.durationInitializing(gameData.processData().metricData()) + "," +
			Metric.durationRunning(gameData.processData().metricData()) + "," +
			gameData.processData().metricData().maxMemory()
		);
	}

	private static void checkPrinter(boolean printer, GenerationData generationData){
		if(printer) Generation.print(generationData);
	}
}