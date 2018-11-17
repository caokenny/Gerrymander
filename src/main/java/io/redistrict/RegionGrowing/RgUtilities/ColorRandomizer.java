package io.redistrict.RegionGrowing.RgUtilities;

import java.awt.*;
import java.util.*;
import java.util.stream.IntStream;

public class ColorRandomizer {
    public  Set<Color> pickRandomColors(int numColors)
    {
        Set<Color> result = new HashSet<>();
        IntStream.range(0,numColors).forEach(num -> result.add(generateRandomColor())
        );
        return result;
    }
    private Color generateRandomColor() {
        Random random = new Random();
        int r = random.nextInt(256);
        int b = random.nextInt(256);
        int g = random.nextInt(256);
        return new Color(r,g,b);
    }
}
