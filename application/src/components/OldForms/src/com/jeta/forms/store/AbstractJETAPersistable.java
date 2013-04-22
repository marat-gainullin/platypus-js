package com.jeta.forms.store;

import com.jeta.forms.gui.beans.JETABean;
import com.jeta.forms.gui.form.GridComponent;
import java.io.IOException;
import java.util.HashMap;

public abstract class AbstractJETAPersistable implements JETAPersistable {

    static final long serialVersionUID = -3204919974884667986L;
    /**
     * The version of this class
     */
    public static final int VERSION = 1;

    /**
     * Externalizable Implementation
     */
    @Override
    public void readExternal(java.io.ObjectInput in) throws ClassNotFoundException, IOException {
        if (in instanceof JETAObjectInput) {
            read((JETAObjectInput) in);
        } else {
            read(new JavaExternalizableObjectInput(in));
        }
    }

    /**
     * Externalizable Implementation
     */
    @Override
    public void writeExternal(java.io.ObjectOutput out) throws IOException {
        if (out instanceof JETAObjectOutput) {
            write((JETAObjectOutput) out);
        } else {
            write(new JavaExternalizableObjectOutput(out));
        }
    }

    @Override
    public void resolveReferences(JETABean jbean, HashMap<Long, GridComponent> aAllComps, String propName) {
    }
}
