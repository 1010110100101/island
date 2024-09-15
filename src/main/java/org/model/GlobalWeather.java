package org.model;

public class GlobalWeather {

    public static int getSunAndRain() {
        return sunAndRain;
    }

    public static void setSunAndRain(int sunAndRain) {
        GlobalWeather.sunAndRain = sunAndRain;
    }

    public static void addSunAndRain() {
        if(sunAndRain < 10)
            sunAndRain++;
    }

    public static void reduceSunAndRain() {
        if(sunAndRain > 0)
            sunAndRain--;
    }

    private static int sunAndRain = 3;
}
