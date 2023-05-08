public class Parameter {

    public static ParameterData createParameterData(String[] input){
        int rows = Integer.parseInt(input[0]);
        if(rows < 0) throw new RuntimeException("Error: Negative Number of Rows!");

        int columns = Integer.parseInt(input[1]);
        if(columns < 0) throw new RuntimeException("Error: Negative Number of Columns!");

        long maxDuration = Long.parseLong(input[2]);
        if(maxDuration < 0) throw new RuntimeException("Error: Negative MaxDuration!");

        int threshold = Integer.parseInt(input[3]);
        if(threshold < 0) throw new RuntimeException("Error: Negative Threshold!");
        if(threshold > 1000) throw new RuntimeException("Error: Threshold too Big! (max. 1000)");

        long seed = Long.parseLong(input[4]);

        boolean printer = false;
        if(input.length == 6){
            printer = input[5].equals("true");
        }
        return new ParameterData(rows, columns, maxDuration, threshold, seed, printer);
    }
}
