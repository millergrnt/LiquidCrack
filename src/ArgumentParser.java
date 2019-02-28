
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

/**
 * Parse the commandline arguments for the supplied program
 * Author: Grant Miller <gem1086.rit.edu>
 * File: ArgumentParser.java
 * Date: 25 February 2019
 * Version: 0.1
 */
public class ArgumentParser {

    // Instance variables
    private HashMap<String, Argument> argShortList;
    private HashMap<String, Argument> argLongList;
    private ArrayList<Argument> requiredArgs;


    /**
     * ArgumentParser constructor
     */
    ArgumentParser() {
        this.argShortList = new HashMap<>();
        this.argLongList = new HashMap<>();
        this.requiredArgs = new ArrayList<>();
    }

    /**
     * Adds a new argument to the argument parser
     * @param type Type of argument
     * @param argFullName The full name of the argument (output)
     * @param argShortName The short name of the argument (o)
     */
    public void addArgument(Argtype type, String argFullName, String argShortName) {

        // Create and add the new argument
        Argument arg = new Argument(type, argFullName, argShortName);
        this.argLongList.put(argFullName, arg);
        this.argShortList.put(argShortName, arg);
    }


    /**
     * Adds a new argument to the argument parser
     * @param type Type of argument
     * @param argFullName The full name of the argument (output)
     * @param argShortName The short name of the argument (o)
     * @param description Description of the argument
     */
    public void addArgument(Argtype type, String argFullName, String argShortName, String description) {

        // Create and add the new argument
        Argument arg = new Argument(type, argFullName, argShortName, description);
        this.argLongList.put(argFullName, arg);
        this.argShortList.put(argShortName, arg);
    }


    /**
     * Prints the usage of this program
     */
    public void usage() {

        // Get the main class of this program
        final Properties properties = System.getProperties();
        String mainClassName = properties.getProperty("sun.java.command").split(" ")[0];

        // If this program has no required arguments then don't print them on top
        // of usage message
        if(this.requiredArgs.size() == 0)
            System.out.println(String.format("java %s ", mainClassName));

        else {
            System.out.print(String.format("java %s ", mainClassName));

            // Iterate over required arguments and add them to the top line
            for(Argument requiredArg : this.requiredArgs) {

                System.out.print(String.format("-%s/--%s ",
                        requiredArg.getArgShortName(), requiredArg.getArgFullName()));
            }

            System.out.println();
        }

        // Iterate over the list of arguments
        for(String argKey : this.argLongList.keySet()) {

            // Get the argument and print the argument names
            Argument arg = this.argLongList.get(argKey);
            System.out.print(String.format(
                    "\t-%s, --%s\t\t",
                    arg.getArgFullName(), arg.getArgShortName()));

            // Print the description
            int i = 1;
            for(char c : arg.getDescription().toCharArray()) {

                // if the description is too long try and make it nice
                if(i % 30 == 0) {
                    System.out.print(String.format("\n\t\t\t\t\t\t%c", c));
                    i++;
                } else {
                    System.out.print(c);
                    i++;
                }
            }

            System.out.println();
        }

        System.out.println();
    }


    /**
     * Parse the command string based on the Argument objects
     * in this parsing object
     * @param argString The arguments string
     * @param retLongNameMap True if callee wants the long named map, false if short named map
     * @exception IllegalArgumentException thrown if an argument supplied is not found
     *                                      or if an argument supplied is supplied more than once
     */
    public ParserResult parseArgs(String[] argString, boolean retLongNameMap) {

        Argument prevArg = null;
        ArrayList<String> extraArgs = new ArrayList<>();

        // Iterate over the strings in the command line
        for(String s : argString) {

            // Check if this is an argument
            if(s.charAt(0) == '-') {

                // Strip any leading -'s
                while(s.charAt(0) == '-')
                    s = s.substring(1);

                // try and get this by full name
                if(!this.argLongList.containsKey(s)) {

                    // check if this key exists as the short hand
                    if(!this.argShortList.containsKey(s)) {

                        // If the argument is not in the short list
                        // or long list throw an exception
                        System.err.println(String.format(
                                "Unknown argument (%s) supplied", s));
                        System.exit(1);
                    } else {

                        // If this argument is a flag set it to true
                        if(this.argShortList.get(s).getType() == Argtype.FLAG) {
                            this.argShortList.get(s).setVal(true);
                            continue;
                        }

                        // Set the previous arg to the recent argument
                        prevArg = this.argShortList.get(s);
                    }
                } else {

                    // If this argument is a flag set it to true
                    if(this.argLongList.get(s).getType() == Argtype.FLAG) {
                        this.argLongList.get(s).setVal(true);
                        continue;
                    }

                    // Set the previous arg to the recent argument
                    prevArg = this.argLongList.get(s);
                }
            } else {

                // This is not a flag, handle it
                if(prevArg != null) {

                    // arguments should only be supplied once
                    if(prevArg.getVal() != null) {
                        throw new IllegalArgumentException();
                    }

                    // Set the value of the argument then reset prevArg back to null
                    if(prevArg.getType() == Argtype.INTEGER) {

                        try {
                            // parse the int
                            prevArg.setVal(Integer.parseInt(s));
                        } catch (NumberFormatException e) {
                            // Set the value to null if a number is not supplied
                            prevArg.setVal(null);
                        }
                    } else

                        // Just save the string
                        prevArg.setVal(s);

                    // Save the newly updated argument
                    this.argShortList.put(prevArg.getArgShortName(), prevArg);
                    this.argLongList.put(prevArg.getArgFullName(), prevArg);

                    // Set previous argument to null
                    prevArg = null;
                } else

                    // Otherwise add the unknown argument to extraArgs
                    extraArgs.add(s);
            }
        }

        // Check if all required args have a value
        for(Argument reqArg : this.requiredArgs) {

            if(reqArg.getVal() == null) {
                this.usage();
                System.exit(1);
            }
        }

        if(retLongNameMap)

            // returns a new ParserResult with the long list
            return new ParserResult(extraArgs, this.argLongList);
        else

            // returns a new ParserResult with the short list
            return new ParserResult(extraArgs, this.argShortList);
    }


    /**
     * Gets the argument from the key provided
     * @param key Name of the argument desired
     * @return Argument if it exists, null otherwise
     */
    public Argument getArg(String key) {

        if(this.argLongList.containsKey(key))
            return this.argLongList.get(key);
        else if(this.argShortList.containsKey(key))
            return this.argShortList.get(key);
        else
            return null;
    }


    /**
     * Marks an argument as required (moving flag to top)
     * @param key Name of the argument to set
     */
    public void setRequired(String key) {

        Argument arg = this.getArg(key);
        arg.setRequired(true);
        this.requiredArgs.add(arg);
    }


    /**
     * Used for debugging
     */
    public static void main(String[] args) {

        String[] myArgs = {"-o", "output.txt", "--input", "3", "--useTLS"};


        System.out.println("TEST 1 - should work normally");
        // TEST 1, test empty parser and empty arg list
        ArgumentParser parser = new ArgumentParser();
        parser.usage();
        ParserResult res1 = parser.parseArgs(new String[]{}, true);


        System.out.println("TEST 2 - should work normally");
        // TEST 2, add normal arguments
        parser.addArgument(Argtype.STRING, "output", "o",
                "Output of the program will be saved to this file");
        parser.addArgument(Argtype.INTEGER, "input", "i",
                "input number");
        parser.addArgument(Argtype.FLAG, "useTLS", "t");
        parser.usage();
        parser.getArg("useTLS").setDescription("Forces program to use TLS");
        parser.usage();


        System.out.println("TEST 3 - should error out");
        // TEST 3, required flag which shouldn't throw an error
        String[] myArgs2 = {"-o", "output.txt", "--input", "3", "--output", "someOtherFile.txt"};

        ArgumentParser parser2 = new ArgumentParser();
        parser2.usage();
        parser2.addArgument(Argtype.STRING, "output", "o",
                "Output of the program will be saved to this file");
        parser2.addArgument(Argtype.INTEGER, "input", "i",
                "input number");
        parser2.addArgument(Argtype.FLAG, "useTLS", "t");
        parser2.setRequired("useTLS");
        parser2.usage();

        // exclude required value to try and throw an exception (which it shouldn't
        // because useTLS is a boolean which defaults to false so it is not null
        ParserResult res = parser2.parseArgs(myArgs2, true);


        System.out.println("TEST 4 - should error out");
        // TEST 4, required string which SHOULD throw an error
        String[] myArgs3 = {"--input", "3"};

        ArgumentParser parser3 = new ArgumentParser();
        parser3.usage();
        parser3.addArgument(Argtype.STRING, "output", "o",
                "Output of the program will be saved to this file");
        parser3.addArgument(Argtype.INTEGER, "input", "i",
                "input number");
        parser3.addArgument(Argtype.FLAG, "useTLS", "t");

        // adding output to the required list and not supplying one should
        parser3.setRequired("output");
        parser3.usage();

        ParserResult res2 = parser3.parseArgs(myArgs3, true);

        res.getExtraArgs();
    }
}
