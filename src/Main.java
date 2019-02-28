import java.io.File;
import java.util.ArrayList;

/**
 * Main class for the cracker
 * Author: Grant Miller <gem1086@rit.edu>
 * File: Main.java
 * Version: 0.1
 * Date: 28 February 2019
 */
public class Main {

    /**
     * Gets the hashes from the input file and dumps them into the extraArgs
     * string array
     * @param extraArgs List of extra arguments in the program
     * @param filePath Path of the file containing the hashes
     * @return A new String[] filled with hashes to crack
     */
    private static ArrayList<String> getHashes(ArrayList<String> extraArgs, File filePath) {

        // TODO implement this function
        return extraArgs;
    }


    /**
     * Collects the number of completed hashes from each worker
     * @param crackers List of worker
     * @return Number of completed hashes
     */
    private static Integer collectNumCompleted(ArrayList<Cracker> crackers) {

        int numCompleted = 0;
        for(Cracker cracker : crackers)
            numCompleted += cracker.getNumCompleted();

        return numCompleted;
    }


    /**
     * Main method of the program
     * @param args Commandline arguments to process
     */
    public static void main(String[] args) {

        // Create arg parser and get number of cores desired to use
        ArgumentParser parser = new ArgumentParser();
        parser.addArgument(Argtype.INTEGER, "numWorker", "w",
                "Number of worker to use. Default is the number of cores available.");
        parser.addArgument(Argtype.STRING, "inputFile", "i",
                "File holding a series of hashes (one per line).");
        parser.addArgument(Argtype.STRING, "hashMethod", "m",
                "Method of hashing used on the hashes");

        // Hash method is required
        parser.setRequired("hashMethod");

        ParserResult res = parser.parseArgs(args, true);

        // Check if one or more hashes is supplied through an input file
        // or via the commandline
        if(res.getArgMap().get("inputFile").getVal() == null &&
            res.getExtraArgs().size() == 0) {

            System.err.println(
                    "Program requires either a hash or series " +
                    "of hashes on the commandline or an input file");
            System.exit(1);
        }

        // If an inputFile is supplied then load those hashes into the extra
        // args array
        if(res.getArgMap().get("inputFile").getVal() != null) {

            File f = new File((String)res.getArgMap().get("inputFile").getVal());
            getHashes(res.getExtraArgs(), f);
        }

        ArrayList<Cracker> crackers = new ArrayList<>();

        // Get the available number of processors
        int cores = Runtime.getRuntime().availableProcessors();
        Integer numWorkers = (Integer)res.getArgMap().get("numWorker").getVal();

        // If numWorkers is not supplied use as many workers as there are cores
        if(numWorkers == null)
            numWorkers = cores;

        // Create the correct number of crackers
        while(numWorkers-- > 0)
            crackers.add(new Cracker());

        // All extra supplied arguments should be hashes
        ArrayList<String> hashes = res.getExtraArgs();

        // Iterate over hashes and dish them out to the crackers
        for(int i = 0; i < hashes.size(); i++)
            crackers.get(i % crackers.size()).addHash(hashes.get(i));

        // Start the cracker threads
        for(Cracker cracker : crackers)
            cracker.start();

        // Alert user of completion
        int numCompleted = 0;
        while(numCompleted < hashes.size()) {

            float completion = (float)numCompleted / (float)hashes.size();
            synchronized (System.out) {
                System.out.println(String.format("Completion: %.2f", completion));
            }

            wait();
            collectNumCompleted(crackers);
        }


    }
}
