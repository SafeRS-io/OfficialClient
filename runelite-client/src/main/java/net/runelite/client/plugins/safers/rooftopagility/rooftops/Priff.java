package net.runelite.client.plugins.safers.rooftopagility.rooftops;


import net.runelite.api.coords.WorldPoint;

import java.util.Arrays;
import java.util.List;

public class Priff extends Base {
    public Priff() {
        init();
    }

    @Override
    public String toString() {
        return "Priff";
    }

    @Override
    public void init() {
        stageList.add(new RooftopStage("Ladder","Climb",new WorldPoint(3254,6109,0),36221,Arrays.asList(new WorldPoint(3251,6109,0),new WorldPoint(3252,6109,0),new WorldPoint(3253,6109,0),new WorldPoint(3251,6108,0),new WorldPoint(3252,6108,0),new WorldPoint(3253,6108,0),new WorldPoint(3251,6108,0))));

        stageList.add(new RooftopStage("Tightrope","Cross",new WorldPoint(3257,6105,2),36225, Arrays.asList(new WorldPoint(3255,6109,2),new WorldPoint(3256,6109,2),new WorldPoint(3257, 6109, 2),new WorldPoint(3258, 6109, 2),new WorldPoint(3259, 6109, 2),new WorldPoint(3259, 6107, 2),new WorldPoint(3259, 6106, 2),new WorldPoint(3259, 6105, 2),new WorldPoint(3259, 6104, 2),new WorldPoint(3259, 6103, 2))));

        stageList.add(new RooftopStage("Chimney","Jump",new WorldPoint(3273,6107,2),36227, Arrays.asList(new WorldPoint(3272,6105,2),new WorldPoint(3273,6105,2),new WorldPoint(  3274,6105,2),new WorldPoint(3275,6105,2),new WorldPoint(3276,6105,2))));

        stageList.add(new RooftopStage("Roof edge","Jump",new WorldPoint(3269,6116,2),36228, Arrays.asList(new WorldPoint(3269,6112,2),new WorldPoint(3269,6113,2),new WorldPoint(3269,6114,2),new WorldPoint(3269,6115,2))));

        stageList.add(new RooftopStage("Dark hole","Enter",new WorldPoint(3269,6118,0),36229, List.of(new WorldPoint(3269, 6117, 0))));

        stageList.add(new RooftopStage("Ladder","Climb",new WorldPoint(2270,3393,0),36231, Arrays.asList(new WorldPoint(2269,3389,0),new WorldPoint(2269,3390,0),new WorldPoint(2269,3391,0),new WorldPoint(2269,3392,0),new WorldPoint(2270,3390,0),new WorldPoint(2270,3389,0),new WorldPoint(2270,3392,0))));

        stageList.add(new RooftopStage("Rope bridge","Cross",new WorldPoint(2264,3390,2),36233, Arrays.asList(new WorldPoint(2269,3393,2),new WorldPoint(2268,3392,2),new WorldPoint(2267,3392,2),new WorldPoint(2266,3392,2),new WorldPoint(2265,3392,2),new WorldPoint(2265,3391,2),new WorldPoint(2265,3390,2))));

        stageList.add(new RooftopStage("Tightrope","Cross",new WorldPoint(2254,3390,2),36234, Arrays.asList(new WorldPoint(2253,3390,2),new WorldPoint(2257,3390,2),new WorldPoint(2256,3390,2),new WorldPoint(2255,3390,2))));

        stageList.add(new RooftopStage("Rope bridge","Cross",new WorldPoint(2246,3399,2),36235, Arrays.asList(new WorldPoint(2247,3397,2),new WorldPoint(2247,3398,2),new WorldPoint(2246,3398,2))));

        stageList.add(new RooftopStage("Tightrope","Cross",new WorldPoint(2244,3409,2),36236, Arrays.asList(new WorldPoint(2246,3406,2),new WorldPoint(2245,3406,2),new WorldPoint(2244,3406,2))));

        stageList.add(new RooftopStage("Tightrope","Cross",new WorldPoint(2253,3418,2),36237, Arrays.asList(new WorldPoint(2250,3416,2),new WorldPoint(2250,3417,2),new WorldPoint(2250,3418,2),new WorldPoint(2249,3419,2),new WorldPoint(2249,3418,2),new WorldPoint(2251,3418,2),new WorldPoint(2252,3418,2))));
        stageList.add(new RooftopStage("Dark hole","Enter",new WorldPoint(2258,3432,2),36238, Arrays.asList(new WorldPoint(2260,3425,0),new WorldPoint(2260,3426,0),new WorldPoint(2259,3426,0),new WorldPoint(2259,3427,0),new WorldPoint(2258,3426,0),new WorldPoint(2258,3427,0),new WorldPoint(2258,3428,0),new WorldPoint(2258,3429,0),new WorldPoint(2258,3430,0),new WorldPoint(2258,3431,0),new WorldPoint(2257,3430,0),new WorldPoint(2257,3431,0),new WorldPoint(2257,3432,0))));

    }

    @Override
    public int getLevel() {
        return 70;
    }

    @Override
    public WorldPoint getLocation() {
        return new WorldPoint(3254,6109,0);
    }
}
