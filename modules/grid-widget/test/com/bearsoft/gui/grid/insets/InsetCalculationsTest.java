/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bearsoft.gui.grid.insets;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Gala
 */
public class InsetCalculationsTest {

    @Test
    public void zeroBothInsetZeroContentTest()
    {
        LinearInset inset = new LinearInset(0, 0);
        InsetPart part0 = inset.toInnerSpace(0, 0);
        assertEquals(part0.getKind(), InsetPart.PartKind.CONTENT);
        assertEquals(part0.getValue(), 0);
        InsetPart part1 = inset.toInnerSpace(1, 0);
        assertEquals(part1.getKind(), InsetPart.PartKind.CONTENT);
        assertEquals(part1.getValue(), 1);
        InsetPart part2 = inset.toInnerSpace(2, 0);
        assertEquals(part2.getKind(), InsetPart.PartKind.CONTENT);
        assertEquals(part2.getValue(), 2);

        LinearInset inset1 = new LinearInset(0, 0);
        InsetPart part10 = inset1.toInnerSpace(0, 10);
        assertEquals(part10.getKind(), InsetPart.PartKind.CONTENT);
        assertEquals(part10.getValue(), 0);
        InsetPart part11 = inset1.toInnerSpace(1, 3);
        assertEquals(part11.getKind(), InsetPart.PartKind.CONTENT);
        assertEquals(part11.getValue(), 1);
        InsetPart part12 = inset1.toInnerSpace(2, 7);
        assertEquals(part12.getKind(), InsetPart.PartKind.CONTENT);
        assertEquals(part12.getValue(), 2);
    }

    @Test
    public void valueLeftInsetZeroContentTest()
    {
        LinearInset inset = new LinearInset(3, 0);
        InsetPart part0 = inset.toInnerSpace(0, 0);
        assertEquals(part0.getKind(), InsetPart.PartKind.BEFORE);
        assertEquals(part0.getValue(), 0);
        InsetPart part1 = inset.toInnerSpace(1, 0);
        assertEquals(part1.getKind(), InsetPart.PartKind.BEFORE);
        assertEquals(part1.getValue(), 1);
        InsetPart part2 = inset.toInnerSpace(2, 0);
        assertEquals(part2.getKind(), InsetPart.PartKind.BEFORE);
        assertEquals(part2.getValue(), 2);
    }

    @Test
    public void valueRightInsetZeroContentTest()
    {
        LinearInset inset = new LinearInset(0, 3);
        InsetPart part0 = inset.toInnerSpace(0, 0);
        assertEquals(part0.getKind(), InsetPart.PartKind.AFTER);
        assertEquals(part0.getValue(), 0);
        InsetPart part1 = inset.toInnerSpace(1, 0);
        assertEquals(part1.getKind(), InsetPart.PartKind.AFTER);
        assertEquals(part1.getValue(), 1);
        InsetPart part2 = inset.toInnerSpace(2, 0);
        assertEquals(part2.getKind(), InsetPart.PartKind.AFTER);
        assertEquals(part2.getValue(), 2);
    }

    @Test
    public void valueBothInsetZeroContentTest()
    {
        LinearInset inset = new LinearInset(3, 3);
        InsetPart part00 = inset.toInnerSpace(0, 0);
        assertEquals(part00.getKind(), InsetPart.PartKind.BEFORE);
        assertEquals(part00.getValue(), 0);
        InsetPart part01 = inset.toInnerSpace(1, 0);
        assertEquals(part01.getKind(), InsetPart.PartKind.BEFORE);
        assertEquals(part01.getValue(), 1);
        InsetPart part02 = inset.toInnerSpace(2, 0);
        assertEquals(part02.getKind(), InsetPart.PartKind.BEFORE);
        assertEquals(part02.getValue(), 2);
        InsetPart part10 = inset.toInnerSpace(3, 0);
        assertEquals(part10.getKind(), InsetPart.PartKind.AFTER);
        assertEquals(part10.getValue(), 0);
        InsetPart part11 = inset.toInnerSpace(4, 0);
        assertEquals(part11.getKind(), InsetPart.PartKind.AFTER);
        assertEquals(part11.getValue(), 1);
        InsetPart part12 = inset.toInnerSpace(5, 0);
        assertEquals(part12.getKind(), InsetPart.PartKind.AFTER);
        assertEquals(part12.getValue(), 2);
    }

    @Test
    public void zeroBothInsetValueContentTest()
    {
        int contentSize = 2;
        LinearInset inset = new LinearInset(0, 0);
        
        InsetPart part0 = inset.toInnerSpace(0, contentSize);
        assertEquals(part0.getKind(), InsetPart.PartKind.CONTENT);
        assertEquals(part0.getValue(), 0);
        InsetPart part1 = inset.toInnerSpace(1, contentSize);
        assertEquals(part1.getKind(), InsetPart.PartKind.CONTENT);
        assertEquals(part1.getValue(), 1);
    }

    @Test
    public void valueLeftInsetValueContentTest()
    {
        int contentSize = 2;
        LinearInset inset = new LinearInset(3, 0);
        InsetPart part0 = inset.toInnerSpace(0, contentSize);
        assertEquals(part0.getKind(), InsetPart.PartKind.BEFORE);
        assertEquals(part0.getValue(), 0);
        InsetPart part1 = inset.toInnerSpace(1, contentSize);
        assertEquals(part1.getKind(), InsetPart.PartKind.BEFORE);
        assertEquals(part1.getValue(), 1);
        InsetPart part2 = inset.toInnerSpace(2, contentSize);
        assertEquals(part2.getKind(), InsetPart.PartKind.BEFORE);
        assertEquals(part2.getValue(), 2);
        InsetPart part3 = inset.toInnerSpace(3, contentSize);
        assertEquals(part3.getKind(), InsetPart.PartKind.CONTENT);
        assertEquals(part3.getValue(), 0);
        InsetPart part4 = inset.toInnerSpace(4, contentSize);
        assertEquals(part4.getKind(), InsetPart.PartKind.CONTENT);
        assertEquals(part4.getValue(), 1);
    }

    @Test
    public void valueRightInsetValueContentTest()
    {
        int contentSize = 2;
        LinearInset inset = new LinearInset(0, 3);
        InsetPart part0 = inset.toInnerSpace(0, contentSize);
        assertEquals(part0.getKind(), InsetPart.PartKind.CONTENT);
        assertEquals(part0.getValue(), 0);
        InsetPart part1 = inset.toInnerSpace(1, contentSize);
        assertEquals(part1.getKind(), InsetPart.PartKind.CONTENT);
        assertEquals(part1.getValue(), 1);
        InsetPart part2 = inset.toInnerSpace(2, contentSize);
        assertEquals(part2.getKind(), InsetPart.PartKind.AFTER);
        assertEquals(part2.getValue(), 0);
        InsetPart part3 = inset.toInnerSpace(3, contentSize);
        assertEquals(part3.getKind(), InsetPart.PartKind.AFTER);
        assertEquals(part3.getValue(), 1);
        InsetPart part4 = inset.toInnerSpace(4, contentSize);
        assertEquals(part4.getKind(), InsetPart.PartKind.AFTER);
        assertEquals(part4.getValue(), 2);
    }

    @Test
    public void valueBothInsetValueContentTest()
    {
        int contentSize = 2;
        LinearInset inset = new LinearInset(3, 3);
        InsetPart part0 = inset.toInnerSpace(0, contentSize);
        assertEquals(part0.getKind(), InsetPart.PartKind.BEFORE);
        assertEquals(part0.getValue(), 0);
        InsetPart part1 = inset.toInnerSpace(1, contentSize);
        assertEquals(part1.getKind(), InsetPart.PartKind.BEFORE);
        assertEquals(part1.getValue(), 1);
        InsetPart part2 = inset.toInnerSpace(2, contentSize);
        assertEquals(part2.getKind(), InsetPart.PartKind.BEFORE);
        assertEquals(part2.getValue(), 2);
        InsetPart part3 = inset.toInnerSpace(3, contentSize);
        assertEquals(part3.getKind(), InsetPart.PartKind.CONTENT);
        assertEquals(part3.getValue(), 0);
        InsetPart part4 = inset.toInnerSpace(4, contentSize);
        assertEquals(part4.getKind(), InsetPart.PartKind.CONTENT);
        assertEquals(part4.getValue(), 1);
        InsetPart part5 = inset.toInnerSpace(5, contentSize);
        assertEquals(part5.getKind(), InsetPart.PartKind.AFTER);
        assertEquals(part5.getValue(), 0);
        InsetPart part6 = inset.toInnerSpace(6, contentSize);
        assertEquals(part6.getKind(), InsetPart.PartKind.AFTER);
        assertEquals(part6.getValue(), 1);
        InsetPart part7 = inset.toInnerSpace(7, contentSize);
        assertEquals(part7.getKind(), InsetPart.PartKind.AFTER);
        assertEquals(part7.getValue(), 2);
    }
}
