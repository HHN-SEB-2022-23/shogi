package de.hhn.shogi.gamelogic.util;

public class Vec2 {
    private final int X;
    private final int Y;

    public Vec2 (int x, int y) {
        this.X = x;
        this.Y = y;
    }

    // Create Vec2 from name, for Example {0, 0} is "A1"
    public Vec2(String name) {
        if (!isValidName(name))
            throw new IllegalArgumentException("Name is not name of a valid spot on the Field");
        name = name.toLowerCase();
        Y = (int)name.charAt(0) - (int)'a';
        X = (int)name.charAt(1) - (int)'1';
    }

    // Returns name of the Vector, x = 3 and y = 4 is "E4"
    public String getName() {
        String returnValue = "";
        returnValue += (char)((int)'a' + Y);
        returnValue += (char)((int)'1' + X);
        return returnValue;
    }

    // Returns name of the Vector, x = 3 and y = 4 is "E4"
    public static String getName(int x, int y) {
        return new Vec2(x, y).getName();
    }

    public int getX() {
        return X;
    }

    public int getY() {
        return Y;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Vec2 vec2)
            return vec2.getX() == X && vec2.getY() == Y;
        return false;
    }

    // Return true if the name is valid like "C2" or "a9"
    private static boolean isValidName(String name) {
        if (name.length() == 2) {
            name = name.toLowerCase();
            int c1 = (int)name.charAt(0) - (int)'a';
            int c2 = (int)name.charAt(1) - (int)'1';
            return c1 < 9 && c1 >= 0 && c2 < 9 && c2 >= 0;
        }
        return false;
    }

    //Returns the Horizontal Offset between 2 points
    public static int xDiff(Vec2 start, Vec2 finish) {
        return finish.getX() - start.getX();
    }

    //Returns the Vertical Offset between 2 points
    public static int yDiff(Vec2 start, Vec2 finish) {
        return finish.getY() - start.getY();
    }
}