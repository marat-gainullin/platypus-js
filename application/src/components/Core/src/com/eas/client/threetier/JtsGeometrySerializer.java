/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier;

import com.bearsoft.rowset.exceptions.RowsetException;
import com.bearsoft.rowset.metadata.DataTypeInfo;
import com.bearsoft.rowset.serial.CustomSerializer;
import com.eas.proto.ProtoWriter;
import com.eas.proto.dom.ProtoDOMBuilder;
import com.eas.proto.dom.ProtoNode;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.InStream;
import com.vividsolutions.jts.io.WKBReader;
import com.vividsolutions.jts.io.WKBWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author mg
 */
public class JtsGeometrySerializer implements CustomSerializer {

    protected static final int TAG_GEOMETRY_SRID_VALUE = 1;
    protected static final int TAG_GEOMETRY_BODY = 2;

    protected class InStreamImpl implements InStream {

        protected InputStream stream;

        public InStreamImpl(InputStream aStream) {
            super();
            stream = aStream;
        }

        @Override
        public void read(byte[] bytes) throws IOException {
            stream.read(bytes);
        }
    }
    protected WKBWriter writer = new WKBWriter();
    protected WKBReader reader = new WKBReader();

    @Override
    public byte[] serialize(Object aValue, DataTypeInfo aTypeInfo) throws RowsetException {
        if (aValue instanceof Geometry) {
            try {
                Geometry gValue = (Geometry) aValue;
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                ProtoWriter gWriter = new ProtoWriter(out);
                gWriter.put(TAG_GEOMETRY_SRID_VALUE, gValue.getSRID());
                gWriter.put(TAG_GEOMETRY_BODY, writer.write(gValue));
                gWriter.flush();
                return out.toByteArray();
            } catch (Exception ex) {
                throw new RowsetException(ex);
            }
        }
        return null;
    }

    @Override
    public Object deserialize(byte[] bytes, int aOffset, int aDataSize, DataTypeInfo aTypeInfo) throws RowsetException {
        try {
            ProtoNode node = ProtoDOMBuilder.buildDOM(bytes, aOffset, aDataSize);
            int srid = node.getChild(TAG_GEOMETRY_SRID_VALUE).getInt();
            ProtoNode bodyNode = node.getChild(TAG_GEOMETRY_BODY);
            ByteArrayInputStream in = new ByteArrayInputStream(bodyNode.getData(), bodyNode.getOffset(), bodyNode.getSize());
            Geometry geom = reader.read(new InStreamImpl(in));
            geom.setSRID(srid);
            return geom;
        } catch (Exception ex) {
            throw new RowsetException(ex);
        }
    }
}
