package pedigree;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Class representing the ancestors.
 *
 * @author Sandrine Bédard et Robin Legault
 */
public class Coalescence {

    private PQ<PA> currentPop; // current pedigree.Sim population
    private Set<Integer> ancestors = new HashSet<>();

    private CoalescencePoints cpMen;
    private CoalescencePoints cpWomen;
    private int ancestralLines; // number of ancestral lines

    /**
     * Inner class.
     * Stores coalescence points as a time and number of ancestral lines.
     */
    public class CoalescencePoints {

        ArrayList<Integer> time;
        ArrayList<Integer> n; // number of ancestral lines

        /**
         * Constructors
         */
        public CoalescencePoints() {
            this.time = new ArrayList<>();
            this.n = new ArrayList<>();
        }

        /**
         * Adds a new coalescence point in the structure
         * @param time moment when coalescence is registered
         * @param n number of ancestral lines
         */
        public void add(int time, int n) {
            this.time.add(time);
            this.n.add(n);
        }

        /**
         * Getters
         */
        public ArrayList<Integer> getTime() { return time; }

        public ArrayList<Integer> getN() { return n; }
    }

    /**
     * Constructor
     */
    public Coalescence(Simulation S) {
        this.cpMen = new CoalescencePoints();
        this.cpWomen = new CoalescencePoints();
        cpMen = buildCoalescence(S.getMenArray());
        cpWomen = buildCoalescence(S.getWomenArray());
    }

    /**
     * Getters
     */
    public CoalescencePoints getCpMen() { return cpMen; }

    public CoalescencePoints getCpWomen() { return cpWomen; }

    public int getAncestralLines() { return ancestralLines; }

    /**
     * Registers a coalescence point.
     */
    private void coalescencePoint(Sim youngest, CoalescencePoints cp) {
        ancestralLines--;
        cp.add((int) youngest.getBirthTime(), ancestralLines);
    }

    /**
     * Builds the coalescence.
     * @param pop current pedigree.Sim population (male or female)
     */
    private CoalescencePoints buildCoalescence(PA[] pop) {
        // Initialization
        CoalescencePoints cp = new CoalescencePoints();
        this.currentPop = new PQ<>(2, PQ.Type.MAX);

        // Translate the sim population into ancestors
        for (int i = 0; i < pop.length; i++) {
            PA ancestor = pop[i];
            ancestors.add(ancestor.getIndent());
            currentPop.insert(ancestor);
        }
        this.ancestralLines = ancestors.size();

        // Creates a max heap sorted by birth date
        currentPop.heapify(pop);

        while (!currentPop.isEmpty()) {
            PA youngest = new PA(currentPop.delete());
            ancestors.remove(youngest.getIndent());

            if (!youngest.isFounder()) {
                PA parent;
                if(youngest.isMale()){ // Paternal line
                    parent = new PA(youngest.getFather());
                }
                else{ // Maternal line
                    parent = new PA(youngest.getMother());
                }

                // Check if youngest's parent is in ancestors
                // If so, add the coalescence point
                if (ancestors.contains(parent.getIndent())) {
                    coalescencePoint(youngest, cp);
                } else {
                    // If not, add youngest's parent in structures
                    currentPop.insert(parent);
                    ancestors.add(parent.getIndent());
                }
            }
        }
        cp.add(0, ancestralLines); // Minimal number of lines reached
        return cp;
    }
}
