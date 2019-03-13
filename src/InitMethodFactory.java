/**
 * Lists all current initialisation methods and returns InitMethod objects.
 */
public class InitMethodFactory {
    static String[] methodList = new String[] { "random scale", "random chromatic", "chaotic", "1/f" };

    public static String[] methods() {
        return methodList;
    }
    
    public static InitMethod getMethod(String methodName, Config c) {
        for(int i = 0; i < methodList.length; i++) {
            if(methodList[i].equals(methodName)) return getMethod(i, c);
        }
        return getMethod(0, c); // invalid
    }
    
    public static InitMethod getMethod(int methodIndex, Config c) {
        switch(methodIndex) {
            case 0: return new InitRandomScale();
			case 1: return new InitRandomChromatic();
            case 2: return new InitChaotic();
            case 3: return new InitOneOverF(c);
            default: return new InitRandomScale(); // invalid
        }
    }
}