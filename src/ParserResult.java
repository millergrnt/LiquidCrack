import java.util.ArrayList;
import java.util.HashMap;

/**
 * Results of the Argument Parser object
 * Author: Grant Miller <gem1086@rit.edu>
 * File: ParserResult.java
 * Date: 25 February 2019
 * Version: 0.1
 */
public class ParserResult {

    // Instance variables
    private ArrayList<String> extraArgs;
    private HashMap<String, Argument> argMap;

    /**
     * Results of the parsing
     * @param extraArgs The extra strings found in the process of parsing
     * @param argMap Argument map
     */
    public ParserResult(ArrayList<String> extraArgs, HashMap<String, Argument> argMap) {
        this.extraArgs = extraArgs;
        this.argMap = argMap;
    }


    /**
     * extraArgs getter
     * @return Extra arguments found on the command line
     */
    public ArrayList<String> getExtraArgs() {
        return extraArgs;
    }


    /**
     * argMap getter
     * @return Hashmap of the argument objects
     */
    public HashMap<String, Argument> getArgMap() {
        return argMap;
    }
}
