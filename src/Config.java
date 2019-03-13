import java.io.*;
import java.util.*;

/**
 * Responsible for storing configuration data that will be loaded/saved via
 * genetic methods or other UI panels. Config is a HashMap, with each entry
 * in the map corresponding to a JazzGen class.
 */
public class Config extends HashMap<String, Object> implements Serializable {
    final static File defaultFn = new File("default.opt");

    public void saveToFile(File fn) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fn));
        oos.writeObject(this);
        oos.close();
    }
    
    public void saveDefault() throws IOException {
        saveToFile(defaultFn);
    }
    
    public static Config loadFromFile(File fn) throws FileNotFoundException, IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fn));
        Config config = (Config)ois.readObject();
        ois.close();
        return config;
    }
    
    public static Config loadDefault() {
        try {
            return loadFromFile(defaultFn);
        }
        catch(FileNotFoundException e) { // def. options file doesn't exist yet
            return new Config();
        }
        catch(Exception e) {
            System.out.println("WARNING: Unable to load init options file. Using default options.");
            return new Config();
        }
    }
}