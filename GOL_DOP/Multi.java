public class Multi {
    public static MultiData createMultiData(int columns){
        int nThreads = Runtime.getRuntime().availableProcessors();
        if(nThreads > columns) nThreads = columns;
        int[][] threadBoundaries = new int[nThreads][2];
        int section = columns / nThreads;
        int remainder = columns % nThreads;

        for (int i = 0; i < nThreads; i++) {
            if(i == 0){
                threadBoundaries[0][0] = 0;
                threadBoundaries[0][1] = (remainder > 0) ?  section : section - 1;
            } else {
                threadBoundaries[i][0] = threadBoundaries[i-1][1] + 1;
                threadBoundaries[i][1] = (remainder > i) ? (threadBoundaries[i][0] + section) : (threadBoundaries[i][0] + section - 1);
            }
            //System.out.println("threadBoundaries["+i+"][]: " + threadBoundaries[i][0] +"/"+threadBoundaries[i][1]);
        }
        return new MultiData(nThreads, threadBoundaries);
    }
}
