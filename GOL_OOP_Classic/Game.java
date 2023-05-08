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

    private long[] timestamps;
    private long maxMemory;

    public Game(int rows, int columns, long maxDuration, int threshold, long seed, boolean printer){
        this.timestamps = new long[3];
        markTimestamp(0);

        this.rows = rows;
        this.columns = columns;
        this.maxDuration = maxDuration;
        this.threshold = threshold;
        this.seed = seed;
        this.printer = printer;

        this.field = new Cell[columns][rows];
        this.generation = 0;
        this.running = true;

        this.cyclicBarriers = new CyclicBarrier[3];
        this.cyclicBarriers[0] = new CyclicBarrier((rows * columns), new Initializing_Runnable(this));
        this.cyclicBarriers[1] = new CyclicBarrier((rows * columns), new Running_Runnable(this));
        this.cyclicBarriers[2] = new CyclicBarrier((rows * columns), new Finishing_Runnable(this));

        createRandomizedCells();

        this.maxMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
    }

    private void createRandomizedCells(){
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
                this.field[xPosition][yPosition] = new Cell(this, xPosition, yPosition, tmpAlive);
            }
        }
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
