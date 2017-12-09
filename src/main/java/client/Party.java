package client;

/**
 * This class is used as a structure holding basic information about a party
 *
 * @author Aleksander Lasecki
 */
class Party {
    String name;
    int left, max;

    /**
     * Class constructor
     *
     * @param name  The name of the party
     * @param left  How many slots are available in the party
     * @param max   How many slots are in the party (available + taken)
     */
    Party(String name, int left, int max) {
        this.name = name;
        this.left = left;
        this.max = max;
    }
}
