package com.tanks.game.utils;

/**
 * Created by cohenort on 12/05/2016.
 */
public class mathUtil1 {


    public static double getAngle(int x, int y) {

        return (180 / Math.PI) * Math.atan2(y, x)
                - 180; //note the atan2 call, the order of paramers is y then x
    }

}
