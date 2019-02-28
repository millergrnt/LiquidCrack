import java.util.ArrayList;

/**
 * Cracker class does the work to try and match hash(es)
 * Author: Grant Miller <gem1086@rit.edu>
 * File: Cracker.java
 * Date: 28 February 2019
 * Version: 0.1
 */
public class Cracker extends Thread {

    // Instance variables
    private ArrayList<String> hashes;
    private Integer numCompleted;

    /**
     * Cracker constructor
     */
    Cracker() {
        this.hashes = new ArrayList<>();
    }


    /**
     * Adds a hash to this worker's
     * @param hash Hash to add the
     */
    public void addHash(String hash) { this.hashes.add(hash); }


    /**
     * Gets the number of hashes completed
     * @return Number of hashes completed so far by this thread
     */
    public Integer getNumCompleted() { return this.numCompleted; }


    /**
     * Will iterate over the supplied hashes and try to crack them
     */
    @Override
    public void run() {

        while(this.hashes.size() > 0) {

            synchronized (System.out) {
                System.out.println(String.format(
                        "Cracking %s", this.hashes.get(0)));
            }

            synchronized (this.numCompleted) {
                this.numCompleted++;
                notify();
            }
            this.hashes.remove(0);
        }
    }
}
