import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class Process {
	public static ProcessData evolveForDuration(MetricData metricData, GenerationData generationZero, long maxDuration, boolean printer){
		MetricData newMetricData;
		// Uses Number of Columns to create Boundary Information for Threads
		MultiData multiData = Multi.createMultiData(generationZero.nColumns());

		// Create Objects of the static nested Classes for MultiThreading
		Coordinator coordinator = new Coordinator(metricData.startTime(), maxDuration, generationZero.cells(), printer);
		BarrierRun barrierRun = new BarrierRun(coordinator);
		CyclicBarrier barrier = new CyclicBarrier(multiData.nThreads(), barrierRun);
		ProcessThread[] threads = createThreads(multiData, coordinator, barrier);

		// Initializing-Phase END
		newMetricData = Metric.newRunTime(metricData);
		// Running-Phase START

		startThreads(threads);
		waitForThreads(threads);

		// Running-Phase END
		newMetricData = Metric.newEndTime(newMetricData);
		// Finishing-Phase START

		return evaluateProcess(newMetricData, multiData, coordinator);
	}

	private static ProcessThread[] createThreads(MultiData multiData, Coordinator coordinator, CyclicBarrier barrier){
		ProcessThread[] threads = new ProcessThread[multiData.nThreads()];
		for(int i = 0; i < multiData.nThreads(); i++){
			threads[i] = new ProcessThread(coordinator, barrier, multiData.threadBounds()[i]);
		}
		return threads;
	}

	private static void startThreads(ProcessThread[] threads){
		for (ProcessThread pt : threads) {
			pt.start();
		}
	}

	private static void waitForThreads(ProcessThread[] threads){
		for (ProcessThread pt : threads) {
			try {
				pt.join();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
	}

	// Reads all the MUTABLE Data from the Coordinator and combines them with MetaData
	private static ProcessData evaluateProcess(MetricData metricData, MultiData multiData, Coordinator coordinator){
		MetricData resMetricData =
				(metricData.maxMemory() < coordinator.getMaxMemory()) ?
				Metric.newMaxMemory(metricData, coordinator.getMaxMemory()) :
						metricData;
		return new ProcessData(
				resMetricData,
				multiData,
				Generation.createNewFromArray(coordinator.getGeneration(), coordinator.getCurrentCells()));
	}

	// Responsible for Data MUTATION in Coordinator-Object
	// Commands the Coordinator to MUTATE his Data
	private static class BarrierRun implements Runnable{
		private Coordinator coordinator;

		public BarrierRun(Coordinator coordinator){
			this.coordinator = coordinator;
		}

		@Override
		public void run(){
			coordinator.incrementGeneration();
			coordinator.switchEven();
			coordinator.checkPrinter();
			coordinator.checkMaxMemory();
			coordinator.checkDurationForRunning();
		}
	}

	// Gets Data from the Coordinator (Coordinator holds MUTABLE Data!!!)
	// Each Thread processes a designated set of columns defined by bounds[]
	// Checks in on barrier after every evolveOnceMulti()
	private static class ProcessThread extends Thread{
		private Coordinator coordinator;
		private CyclicBarrier barrier;
		private final int[] BOUNDS;

		private ProcessThread(Coordinator coordinator, CyclicBarrier barrier, int[] bounds){
			this.coordinator = coordinator;
			this.barrier = barrier;
			this.BOUNDS = bounds;
		}
		
		@Override
		public void run(){
			try{
				while(coordinator.isRunning()){
					if(coordinator.isEven())
						evolveOnceWithBounds(coordinator.getCellsEven(), coordinator.getCellsOdd());
					else
						evolveOnceWithBounds(coordinator.getCellsOdd(), coordinator.getCellsEven());
					barrier.await();
				}
			} catch (InterruptedException e){
				throw new RuntimeException(e);
			} catch (BrokenBarrierException e){
				throw new RuntimeException(e);
			}
		}

		// Enforces the Rules of Game of Life
		// Method is MUTABLE !!!
		// Evolves bounds[0] to bounds[1] Columns of cellsSource by manipulating cellsResult
		private void evolveOnceWithBounds(boolean[][] cellsSource, boolean[][] cellsResult){
			// Loop over Columns the Thread is Bound too
			for(int x = BOUNDS[0]; x < BOUNDS[1] + 1; x++){
				// Loop over Rows
				for(int y = 0; y < cellsSource[0].length; y++){
					int aN = countAliveNeighbours(cellsSource, x, y);
					// Rules of Game of Life
					cellsResult[x][y] = (cellsSource[x][y]) ? ((aN == 2) || (aN == 3)) : (aN == 3);
				}
			}
		}

		private static int countAliveNeighbours(boolean[][] cells, int x, int y){
			int res = 0;
			// Loop over possible X-Coordinates
			for(int xPos = x - 1; xPos < x + 2; xPos++){
				// cols = cells.length
				int tmpX = checkTorus(cells.length, xPos);
				// Loop over possible Y-Coordinates
				for(int yPos = y - 1; yPos < y + 2; yPos++){
					// Don't count self
					if(xPos == x && yPos == y) continue;
					// rows = cells[0].length
					int tmpY = checkTorus(cells[0].length, yPos);
					if(cells[tmpX][tmpY]) res++;
				}
			}
			return res;
		}

		// Makes sure a given Coordinate acts like being Part of a Torus
		private static int checkTorus(int max, int pos){
			int res = pos;
			if(pos < 0) res = max - 1;
			if(pos == max) res = 0;
			return res;
		}
	}

	// Holds all MUTABLE data necessary for Threads
	private static class Coordinator{
		private final long START_TIME;
		private final long MAX_DURATION;
		private final boolean PRINTER;
		private long maxMemory;
		private boolean[][] cellsEven;
		private boolean[][] cellsOdd;
		private boolean running;
		private boolean even;
		private int generation;

		private Coordinator(long startTime, long maxDuration, boolean[][] cellsSource, boolean printer){
			this.START_TIME = startTime;
			this.MAX_DURATION = maxDuration;
			maxMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
			this.cellsEven = cloneCells(cellsSource);
			this.cellsOdd = new boolean[cellsSource.length][cellsSource[0].length];
			this.PRINTER = printer;
			running = true;
			even = true;
			generation = 0;
		}

		private boolean[][] cloneCells(boolean[][] cells) {
			boolean[][] res = new boolean[cells.length][cells[0].length];
			// Loop over Columns
			for (int i = 0; i < cells.length; i++) {
				System.arraycopy(cells[i], 0, res[i], 0, cells[i].length);
			}
			return res;
		}

		private void checkMaxMemory(){
			long currentMemoryUsage = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
			if(currentMemoryUsage > maxMemory) maxMemory = currentMemoryUsage;
		}

		private long getMaxMemory(){
			return maxMemory;
		}

		private boolean[][] getCellsEven(){
			return cellsEven;
		}

		private boolean[][] getCellsOdd(){
			return cellsOdd;
		}

		private boolean[][] getCurrentCells(){
			if(even) return cellsEven;
			else return cellsOdd;
		}

		private boolean isRunning(){
			return running;
		}

		private void checkDurationForRunning(){
			long currentTime = System.currentTimeMillis();
			if(currentTime - START_TIME >= MAX_DURATION) running = false;
		}

		private boolean isEven(){
			return even;
		}

		private void switchEven(){
			even = !even;
		}

		private void incrementGeneration(){
			generation++;
		}

		private int getGeneration(){
			return generation;
		}

		private void checkPrinter(){
			if(PRINTER) {
				boolean[][] cells = getCurrentCells();

				System.out.println();
				System.out.println("Generation: " + generation);
				// loop over rows
				for (int y = 0; y < cells[0].length; y++) {
					// loop over columns
					for (int x = 0; x < cells.length; x++) {
						if (cells[x][y]) {
							System.out.print("1 ");
						} else {
							System.out.print("0 ");
						}
					}
					System.out.println();
				}
			}
		}
	}
}