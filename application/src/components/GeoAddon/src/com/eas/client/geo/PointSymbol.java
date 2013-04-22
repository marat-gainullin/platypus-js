/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.geo;

/**
 *
 * @author pk
 */
public enum PointSymbol
{
    CIRCLE(0, "Circle"), SQUARE(1, "Square"), CROSS(2, "Cross"), X(3, "X"),
    TRIANGLE(4, "Triangle"), STAR(5, "Star");
    private final int number;
    private final String name;

    private PointSymbol(int number, String name)
    {
        this.number = number;
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public int getNumber()
    {
        return number;
    }

    public PointSymbol getPointSymbol(String name)
    {
        for (PointSymbol ps : values())
            if (ps.name.equals(name))
                return ps;
        return null;
    }

    public PointSymbol getPointSymbol(int number)
    {
        for (PointSymbol ps : values())
            if (ps.number == number)
                return ps;
        return null;
    }
}
