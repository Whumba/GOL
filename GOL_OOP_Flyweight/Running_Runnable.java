public class Running_Runnable implements Runnable{

    private Game game;
    private int state;

    public Running_Runnable(Game game){
        this.game = game;
        this.state = 0;
    }

    @Override
    public void run(){
        if(state == 0){
            // Broken after Reading-Phase (First half of a Generation)
            game.checkMaxMemory();
            state = 1;
        } else{
            // Broken after Writing-Phase (Second half of a Generation)
            game.incrementGeneration();
            game.checkPrinter();
            game.checkMaxMemory();
            game.checkDurationForRunning();
            state = 0;
        }
    }
}
