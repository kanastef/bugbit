package de.bugplus.examples.development;

import de.bugplus.development.*;
import de.bugplus.specification.BugplusLibrary;
import de.bugplus.specification.BugplusProgramSpecification;

import java.util.LinkedList;
import java.util.List;

public class Challenge27B {

    public static void main(String[] args) {
        BugplusLibrary myFunctionLibrary = BugplusLibrary.getInstance();

        BugplusNEGImplementation negImpl = BugplusNEGImplementation.getInstance();
        myFunctionLibrary.addSpecification(negImpl.getSpecification());

        //reverse the direction fo the three starting bits(002, 003, 004), no matter what state they start in
        //we have two blue balls and one red ball
        BugplusProgramSpecification challenge27BSpec = BugplusProgramSpecification.getInstance("ch27B_Test", 0, 2, myFunctionLibrary);
        BugplusProgramImplementation challenge27BImpl = challenge27BSpec.addImplementation();

        LinkedList<String> ch27Bbugs = new LinkedList<String>();
        for (int i = 1; i <= 4; i++) {
            String bugID = "!_00" + i;
            ch27Bbugs.add(bugID);
            challenge27BImpl.addBug("!", bugID);

            //connect data out with data in for each bug
            challenge27BImpl.addDataFlow(bugID, bugID, 0);
        }
        //0 is left, 1 is right

        //the first ball always drops into the first bug
        challenge27BImpl.connectControlInInterface(ch27Bbugs.get(0));

        challenge27BImpl.addControlFlow(ch27Bbugs.get(0), 0, ch27Bbugs.get(1));
        challenge27BImpl.addControlFlow(ch27Bbugs.get(0), 1, ch27Bbugs.get(2));

        challenge27BImpl.addControlFlow(ch27Bbugs.get(1), 0, ch27Bbugs.get(0));
        challenge27BImpl.addControlFlow(ch27Bbugs.get(1), 1, ch27Bbugs.get(0));

        challenge27BImpl.addControlFlow(ch27Bbugs.get(2), 0, ch27Bbugs.get(3));
        challenge27BImpl.addControlFlow(ch27Bbugs.get(2), 1, ch27Bbugs.get(3));

        challenge27BImpl.addControlFlow(ch27Bbugs.get(3), 0, ch27Bbugs.get(3));
        challenge27BImpl.addControlFlow(ch27Bbugs.get(3), 1, ch27Bbugs.get(3));

        //the program stops when the red ball goes either to the left or right of the fourth bit
        challenge27BImpl.connectControlOutInterface(ch27Bbugs.get(3), 1, 0);
        challenge27BImpl.connectControlOutInterface(ch27Bbugs.get(3), 0, 1);

        BugplusInstance ch27BInstance = challenge27BImpl.instantiate();
        BugplusProgramInstanceImpl ch27BTest = ch27BInstance.getInstanceImpl();
        ch27BTest.getBugs().get("!_001").setInternalState(1); //the bit we have placed
        // ourselves on the board must always start flipped to the right
        ch27BTest.getBugs().get("!_002").setInternalState(0);
        ch27BTest.getBugs().get("!_003").setInternalState(0);
        ch27BTest.getBugs().get("!_004").setInternalState(1);

        BugplusThread newThread = BugplusThread.getInstance();
        newThread.connectInstance(ch27BInstance);


        newThread.start();

        for (
                String s : ch27Bbugs) {
            System.out.println("Internal State " + s + ": \t" + ch27BTest.getBugs().get(s).getInternalState());
            System.out.println("Call Counter " + s + ": \t" + ch27BTest.getBugs().get(s).getCallCounter() + "\n");
        }
    }
}
