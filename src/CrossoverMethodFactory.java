/**
 * Lists all current crossover methods and creates crossover objects.
 */
public class CrossoverMethodFactory {
    static String[] methodList = new String[] { "single point", "double point", "uniform", "average", "random mask" };

    public static String[] methods() {
        return methodList;
    }
    
    public static CrossoverMethod getMethod(String methodName, Config c) {
        for(int i = 0; i < methodList.length; i++) {
            if(methodList[i].equals(methodName)) return getMethod(i, c);
        }
        return getMethod(0, c); // invalid
    }
    
    public static CrossoverMethod getMethod(int methodIndex, Config c) {
        switch(methodIndex) {
            case 0: return new CrossoverSinglePoint();
            case 1: return new CrossoverDoublePoint();
            case 2: return new CrossoverUniform();
            case 3: return new CrossoverAverage(c);
            case 4: return new CrossoverRandomMask(c);
            default: return new CrossoverSinglePoint(); // invalid
        }
    }
}