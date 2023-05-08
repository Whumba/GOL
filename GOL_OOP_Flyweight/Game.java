import java.util.Random;
import java.util.concurrent.CyclicBarrier;

public class Game{

    private int rows;
    private int columns;
    private long maxDuration;
    private int threshold;
    private long seed;
    private boolean printer;

    private Cell[][] field;
    private int generation;
    private boolean running;
    private CyclicBarrier[] cyclicBarriers;
    private EvolverThread[] evolverThreads;

    private long[] timestamps;
    private long maxMemory;

    public Game(int rows, int columns, long maxDuration, int threshold, long seed, boolean printer){
        this.timestamps = new long[3];
        // Initializing-Phase START
        markTimestamp(0);

        this.rows = rows;
        this.columns = columns;
        this.maxDuration = maxDuration;
        this.threshold = threshold;
        this.seed = seed;
        this.printer = printer;

        this.field = createRandomizedCells();
        this.generation = 0;
        this.running = true;

        evolverThreads = createEvolverThreads();

        this.cyclicBarriers = new CyclicBarrier[3];
        this.cyclicBarriers[0] = new CyclicBarrier(evolverThreads.length, new Initializing_Runnable(this));
        this.cyclicBarriers[1] = new CyclicBarrier(evolverThreads.length, new Running_Runnable(this));
        this.cyclicBarriers[2] = new CyclicBarrier(evolverThreads.length, new Finishing_Runnable(this));

        this.maxMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        startEvolverThreads();
    }

    private Cell[][] createRandomizedCells(){
        Cell[][] cells = new Cell[columns][rows];
        Random prng = new Random(this.seed);
        // Loop over Columns
        for(int xPosition = 0; xPosition < this.columns; xPosition++){
            // Loop over Rows
            for(int yPosition = 0; yPosition < this.rows; yPosition++){
                // Create randomized boolean for Cell.life
                int prngInt = prng.nextInt(1001);
                // Check Threshold
                boolean tmpAlive = prngInt < this.threshold;
                // Create new Cell
                cells[xPosition][yPosition] = new Cell(this, xPosition, yPosition, tmpAlive);
            }
        }
        return cells;
    }

    private EvolverThread[] createEvolverThreads(){
        int[][] threadBoundaries = calculateThreadBoundaries();
        EvolverThread[] threads = new EvolverThread[threadBoundaries.length];
        for(int i = 0; i < threadBoundaries.length; i++){
            threads[i] = new EvolverThread(this, threadBoundaries[i]);
        }
        return threads;
    }

    private void startEvolverThreads(){
        for(EvolverThread et : evolverThreads){
            et.start();
        }
    }

    // Determines the Interval of Columns every Thread has to manage
    // threadBoundaries[n][0] --> Interval start of Thread n
    // threadBoundaries[n][1] --> Interval end of Thread n
    private int[][] calculateThreadBoundaries(){
        int nThreads = Runtime.getRuntime().availableProcessors();
        if(nThreads > columns) nThreads = columns;
        int[][] threadBoundaries = new int[nThreads][2];
        int section = columns / nThreads;
        int remainder = columns % nThreads;

        // Loop over number of Threads
        for (int i = 0; i < nThreads; i++) {
            if(i == 0){
                threadBoundaries[0][0] = 0;
                threadBoundaries[0][1] = (remainder > 0) ?  section : section - 1;
            } else {
                threadBoundaries[i][0] = threadBoundaries[i-1][1] + 1;
                threadBoundaries[i][1] = (remainder > i) ? (threadBoundaries[i][0] + section) : (threadBoundaries[i][0] + section - 1);
            }
        }
        return threadBoundaries;
    }

    public int getRows(){
        return this.rows;
    }

    public int getColumns(){
        return this.columns;
    }

    private long currentDuration(){
        return System.currentTimeMillis() - this.timestamps[0];
    }

    public void checkDurationForRunning(){
        if(currentDuration() >= this.maxDuration){
            this.running = false;
        }
    }

    public Cell getCellFromField(int x, int y){
        return this.field[x][y];
    }

    public void incrementGeneration(){
        this.generation++;
    }

    public boolean isRunning(){
        return this.running;
    }

    // Getter Method for Barriers
    // key: 0 -> Barrier of Initializing-Phase
    // key: 1 -> Barrier of Running-Phase
    // key: 2 -> Barrier of Finishing-Phase
    public CyclicBarrier getCyclicBarrier(int key){
        return this.cyclicBarriers[key];
    }

    // Setter Method for Timestamps
    // key: 0 -> Start of Initializing-Phase
    // key: 1 -> Switch from Initializing-Phase to Running-Phase
    // key: 2 -> Switch from Running-Phase to Finishing-Phase
    public void markTimestamp(int key){
        this.timestamps[key] = System.currentTimeMillis();
    }

    public void checkMaxMemory(){
        long usedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        if (usedMemory > this.maxMemory){
            this.maxMemory = usedMemory;
        }
    }

    public void printResult(){
        long durationInitialization = this.timestamps[1] - this.timestamps[0];
        long durationRunning = this.timestamps[2] - this.timestamps[1];

        System.out.print(
                this.rows + "," +
                this.columns + "," +
                this.maxDuration + "," +
                this.threshold + "," +
                this.seed + "," +
                this.generation + "," +
                durationInitialization + "," +
                durationRunning + "," +
                this.maxMemory
        );
    }

    public void checkPrinter(){
        if(this.printer){
            printField();
        }
    }

    // Prints the generation on Console
    private void printField(){
        System.out.println();
        System.out.println("Generation: " + generation);
        // loop over number of rows
        for(int y = 0; y < rows; y++){
            // loop over number of columns
            for(int x = 0; x < columns; x++){
                if(field[x][y].isCellAlive()){
                    System.out.print("1 ");
                } else {
                    System.out.print("0 ");
                }
            }
            System.out.println();
        }
    }
}
