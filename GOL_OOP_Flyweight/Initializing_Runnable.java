public class Initializing_Runnable implements Runnable{

    private Game game;

    public Initializing_Runnable(Game game){
        this.game = game;
    }

    @Override
    public void run(){
        game.checkPrinter();
        // Initializing-Phase END
        game.markTimestamp(1);
        // Running-Phase START
        game.checkDurationForRunning();
    }
}
