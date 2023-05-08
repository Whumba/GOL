import java.util.concurrent.BrokenBarrierException;

public class EvolverThread extends Thread{
    private Game game;
    // boundaries[0] first column of interval
    // boundaries[1] last column of interval
    private int[] boundaries;

    public EvolverThread(Game game, int[] boundaries){
        this.game = game;
        this.boundaries = boundaries;
    }

    @Override
    public void run(){
        try {
            // Initializing-Phase
            detectCellsNeighbours();
            game.getCyclicBarrier(0).await();
            // Running-Phase
            while(game.isRunning()){
                calculateCellsAliveNextGen();
                game.getCyclicBarrier(1).await();
                updateCellsAlive();
                game.getCyclicBarrier(1).await();
            }
            // Finishing-Phase
            game.getCyclicBarrier(2).await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (BrokenBarrierException e) {
            throw new RuntimeException(e);
        }
    }

    // Messages Cells to perform detectNeighbours()
    private void detectCellsNeighbours(){
        // Loop over Columns the Thread is Bound too
        for(int x = boundaries[0]; x <= boundaries[1]; x++){
            // Loop over Rows
            for(int y = 0; y < game.getRows(); y++){
                game.getCellFromField(x, y).detectNeighbours();
            }
        }
    }

    // Messages Cells to perform calculateCellAliveNextGen()
    private void calculateCellsAliveNextGen(){
        // Loop over Columns the Thread is Bound too
        for(int x = boundaries[0]; x <= boundaries[1]; x++){
            // Loop over Rows
            for(int y = 0; y < game.getRows(); y++){
                game.getCellFromField(x, y).calculateCellAliveNextGen();
            }
        }
    }

    // Messages Cells to perform updateCellAlive()
    private void updateCellsAlive(){
        // Loop over Columns the Thread is Bound too
        for(int x = boundaries[0]; x <= boundaries[1]; x++){
            // Loop over Rows
            for(int y = 0; y < game.getRows(); y++){
                game.getCellFromField(x, y).updateCellAlive();
            }
        }
    }
}
