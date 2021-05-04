package pedigree;

import java.util.ArrayList;

/**
 * Entry point for the simulation. Call with command-line arguments: <code> java ... pedigree.Runner n Tmax </code>
 *
 * @author Sandrine Bédard et Robin Legault
 */
public class Runner {

    public static void main(String[] args) throws Exception{

        if (args.length != 2) {
            throw new IllegalArgumentException("Give parameters as command-line arguments: java ... pedigree.Runner n Tmax");
        }

        // Arguments entered in command-line
        int n = Integer.parseInt(args[0]);
        int Tmax = Integer.parseInt(args[1]);

        // Handling exceptions
        if (n <= 0) { throw new IllegalArgumentException("Illegal number of founders (n). Must be a positive integer."); }
        if (Tmax <= 0) { throw new IllegalArgumentException("Illegal maximum time (Tmax). Must be a positive integer."); }

        // A) Simulation
        Simulation S = new Simulation();
        S.simulate(n, Tmax);

        // B) Coalescence of ancestral lines
        Coalescence C = new Coalescence(S);
        Coalescence.CoalescencePoints cpMen = C.getCpMen();
        Coalescence.CoalescencePoints cpWomen = C.getCpWomen();

        // C) Empirical study
        // Data for population study
        ArrayList popHist = S.getPopulationHistory();
        ArrayList timeHist = S.getTimeHistory();

        // Data for coalescence study (men and women)
        ArrayList cpMenTimes = cpMen.getTime();
        ArrayList cpWomenTimes = cpWomen.getTime();
        ArrayList cpMenN = cpMen.getN();
        ArrayList cpWomenN = cpWomen.getN();

        // D) Showing results on terminal
        System.out.print('\n');
        System.out.println("----------------------------- Population History ----------------------------------------");
        System.out.print('\n');
        for (int i = 0; i < popHist.size(); i++) {
            System.out.println("[n = " + popHist.get(i) + ", t = " + timeHist.get(i) + "]");
        }
        System.out.print('\n');
        System.out.println("------------------------------ Paternal Ancestral Lines ---------------------------------");
        System.out.print('\n');

        for (int i = 0; i < cpMenN.size(); i++) {
            System.out.println("[n = " + cpMenN.get(i) + ", t = " + cpMenTimes.get(i) + "]");
        }
        System.out.print('\n');
        System.out.println("------------------------------ Maternal Ancestral Lines ---------------------------------");
        System.out.print('\n');

        for (int i = 0; i < cpWomenN.size(); i++) {
            System.out.println("[n = " + cpWomenN.get(i) + ", t " + cpWomenTimes.get(i) + "]");
        }
    }
}
