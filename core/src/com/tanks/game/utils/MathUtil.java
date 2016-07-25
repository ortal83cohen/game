package com.tanks.game.utils;

/**
 * Created by cohenort on 12/05/2016.
 */
public class MathUtil {


    public static double getAngle(float x, float y) {

        return (180 / Math.PI) * Math.atan2(y, x)
                - 180; //note the atan2 call, the order of paramers is y then x
    }

    public static double normalizeRotation(float rotation) {

        rotation = rotation % 360;

        if (rotation < 0)
        {
            rotation += 360;
        }
        return rotation;
    }

}
