public class Initializing_Runnable implements Runnable{

    private Game game;

    private boolean barrierBrokenBefore;

    public Initializing_Runnable(Game game){
        this.game = game;
        this.barrierBrokenBefore = false;
    }

    @Override
    public void run(){
        if(barrierBrokenBefore) {
            game.checkPrinter();
            // Initializing-Phase END
            game.markTimestamp(1);
            // Running-Phase START
        } else
            barrierBrokenBefore = true;
        game.checkDurationForRunning();
    }
}
