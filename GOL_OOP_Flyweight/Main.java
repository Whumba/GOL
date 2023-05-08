public class Main{

    public static void main(String[] args){

        // Typecasting of Startparameters
        if(args.length != 0){
            int rows = Integer.parseInt(args[0]);
            int cols = Integer.parseInt(args[1]);
            long maxDuration = Long.parseLong(args[2]);
            int threshold = Integer.parseInt(args[3]);
            long seed = Long.parseLong(args[4]);
            boolean printer = false;
            if(args.length == 6){
                printer = args[5].equals("true");
            }
            // Creating a new GOL which starts itself
            new Game(rows, cols, maxDuration, threshold, seed, printer);
        }
    }
}
