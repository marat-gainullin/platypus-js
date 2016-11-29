/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.opc.hda.dcom;

/**
 *
 * @author pk
 */
public enum EditType
{
    OPCHDA_INSERT((short) 1), OPCHDA_REPLACE((short) 2),
    OPCHDA_INSERTREPLACE((short) 3), OPCHDA_DELETE((short) 4);
    private final short id;

    private EditType(short id)
    {
        this.id = id;
    }

    public short getId()
    {
        return id;
    }

    public static EditType getEditType(short id)
    {
        for (EditType t : values())
            if (t.getId() == id)
                return t;
        throw new IllegalArgumentException("Unknown edit type " + id); //NOI18N
    }
}
