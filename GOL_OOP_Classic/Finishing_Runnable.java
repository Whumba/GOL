public class Finishing_Runnable implements Runnable{

    private Game game;

    public Finishing_Runnable(Game game){
        this.game = game;
    }

    @Override
    public void run(){
        // Running-Phase END
        game.markTimestamp(2);
        // Finishing-Phase START
        game.printResult();
    }
}
