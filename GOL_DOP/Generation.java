import java.util.Random;

public class Generation {

    public static GenerationData createNewRandomized(ParameterData parameterData){
        return new GenerationData(0, parameterData.rows(), parameterData.columns(), createRandomizedCells(parameterData));
    }

    public static GenerationData createNewFromArray(int generation, boolean[][] cellsArray){
        return new GenerationData(generation, cellsArray[0].length, cellsArray.length, cellsArray);
    }

    public static void print(GenerationData generationData){
        System.out.println();
        System.out.println("Generation: " + generationData.generation());
        // loop over number of rows
        for(int y = 0; y < generationData.nRows(); y++){
            // loop over number of columns
            for(int x = 0; x < generationData.nColumns(); x++){
                if(generationData.cells()[x][y]){
                    System.out.print("1 ");
                } else {
                    System.out.print("0 ");
                }
            }
            System.out.println();
        }
    }

    private static boolean[][] createRandomizedCells(ParameterData parameterData){
        boolean[][] cells = new boolean[parameterData.columns()][parameterData.rows()];
        // Randomizing Cells
        Random prng = new Random(parameterData.seed());
        for(int x = 0; x < parameterData.columns(); x++){
            for(int y = 0; y < parameterData.rows(); y++){
                int prngInt = prng.nextInt(1001);
                cells[x][y] = prngInt < parameterData.threshold();
            }
        }
        return cells;
    }
}
