import java.util.concurrent.BrokenBarrierException;

public class Cell extends Thread{

    private Game game;
    private int x,y;
    private boolean cellAlive;
    private boolean cellAliveNextGen;
    private Cell[] neighbours;

    public Cell(Game game, int x, int y, boolean alive){
        this.game = game;
        this.x = x;
        this.y = y;
        this.cellAlive = alive;
        this.cellAliveNextGen = alive;
        this.neighbours = new Cell[8];
        this.start();
    }

    @Override
    public void run(){
        try{
            // Initializing-Phase
            game.getCyclicBarrier(0).await();
            detectNeighbours();
            game.getCyclicBarrier(0).await();
            // Running-Phase
            while(game.isRunning()){
                calculateCellAliveNextGen();
                game.getCyclicBarrier(1).await();
                updateCellAlive();
                game.getCyclicBarrier(1).await();
            }
            // Finishing-Phase
            game.getCyclicBarrier(2).await();
        } catch (InterruptedException e){
            throw new RuntimeException(e);
        } catch (BrokenBarrierException e){
            throw new RuntimeException(e);
        }
    }

    public boolean isCellAlive(){
        return this.cellAlive;
    }

    // Used to store a Reference of every neighbouring Cell in neighbours
    private void detectNeighbours(){
        int tmpIndexNeighbours = 0;
        for(int xPos = x - 1; xPos < x + 2; xPos++){
            int tmpX = checkTorus(game.getColumns(), xPos);
            for (int yPos = y - 1; yPos < y + 2; yPos++){
                if(xPos == x && yPos == y) continue;
                int tmpY = checkTorus(game.getRows(), yPos);
                // Stores the neighbour in the Array neighbours
                neighbours[tmpIndexNeighbours] = game.getCellFromField(tmpX, tmpY);
                tmpIndexNeighbours++;
            }
        }
    }

    // Makes sure a given Coordinate acts like being Part of a Torus
    private int checkTorus(int max, int pos){
        int res = pos;
        if(pos < 0) res = max - 1;
        if(pos == max) res = 0;
        return res;
    }

    private int countAliveNeighbours(){
        int res = 0;
        for(Cell cell: neighbours){
            if(cell.isCellAlive()) res++;
        }
        return res;
    }

    private void calculateCellAliveNextGen(){
        int aliveNeighbours = countAliveNeighbours();
        cellAliveNextGen = cellAlive ? ((aliveNeighbours == 2) || (aliveNeighbours == 3)) : (aliveNeighbours == 3);
    }

    private void updateCellAlive(){
        cellAlive = cellAliveNextGen;
    }
}