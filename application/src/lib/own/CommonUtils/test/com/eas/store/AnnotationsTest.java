/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.store;

import java.util.AbstractMap;
import java.util.TreeMap;
import com.eas.xml.dom.XmlDom2String;
import java.util.Map.Entry;
import java.util.HashMap;
import java.util.Map;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.w3c.dom.Document;
import static org.junit.Assert.*;

/**
 *
 * @author mg
 */
public class AnnotationsTest {

    protected static class SerialSubject {

        private boolean bval;
        private String sval;

        @Serial
        public boolean isBval() {
            return bval;
        }

        @Serial
        public void setBval(boolean bval) {
            this.bval = bval;
        }

        @Serial
        public String getSval() {
            return sval;
        }

        @Serial
        public void setSval(String sval) {
            this.sval = sval;
        }
    }

    @Test
    public void serialTest() {
        SerialSubject ss = new SerialSubject();
        ss.setBval(true);
        ss.setSval("sample");
        Document doc = Object2Dom.transform(ss, "serialTest");
        SerialSubject res = new SerialSubject();
        Object2Dom.transform(res, doc);
        assertEquals(ss.getSval(), res.getSval());
        assertTrue(ss.isBval());
        assertTrue(res.isBval());
    }

    @Test
    public void serialNullTest() {
        SerialSubject ss = new SerialSubject();
        ss.setBval(true);
        ss.setSval(null);
        Document doc = Object2Dom.transform(ss, "serialTest");
        SerialSubject res = new SerialSubject();
        Object2Dom.transform(res, doc);
        assertNull(res.getSval());
        assertNull(ss.getSval());
        assertTrue(ss.isBval());
        assertTrue(res.isBval());
    }

    protected static class Base {

        private String bdata;

        public Base()
        {
            super();
        }

        @Serial
        public String getBdata() {
            return bdata;
        }

        @Serial
        public void setBdata(String bdata) {
            this.bdata = bdata;
        }
    }

    protected static class Middle extends Base {

        private String mdata;

        public Middle()
        {
            super();
        }

        @Serial
        public String getMdata() {
            return mdata;
        }

        @Serial
        public void setMdata(String mdata) {
            this.mdata = mdata;
        }
    }

    protected static class Deep extends Middle {

        private String ddata;

        public Deep()
        {
            super();
        }

        @Serial
        public String getDdata() {
            return ddata;
        }

        @Serial
        public void setDdata(String ddata) {
            this.ddata = ddata;
        }
    }

    protected static class ClassedSerialSubject implements PropertiesSimpleFactory {

        private int simple;
        private Base b1;
        private Base b2;
        private Base b3;

        @Serial
        public int getSimple() {
            return simple;
        }

        @Serial
        public void setSimple(int simple) {
            this.simple = simple;
        }

        @ClassedSerial(propertyClassHint = "type")
        public Base getB1() {
            return b1;
        }

        @ClassedSerial(propertyClassHint = "type")
        public void setB1(Base b1) {
            this.b1 = b1;
        }

        @ClassedSerial(propertyClassHint = "type")
        public Base getB2() {
            return b2;
        }

        @ClassedSerial(propertyClassHint = "type")
        public void setB2(Base b2) {
            this.b2 = b2;
        }

        @ClassedSerial(propertyClassHint = "type")
        public Base getB3() {
            return b3;
        }

        @ClassedSerial(propertyClassHint = "type")
        public void setB3(Base b3) {
            this.b3 = b3;
        }

        public Object createPropertyObjectInstance(String aSimpleClassName) {
            if (Base.class.getSimpleName().equals(aSimpleClassName)) {
                return new Base();
            } else if (Middle.class.getSimpleName().equals(aSimpleClassName)) {
                return new Middle();
            } else if (Deep.class.getSimpleName().equals(aSimpleClassName)) {
                return new Deep();
            } else {
                return null;
            }
        }
    }

    @Test
    public void classedSerialTest() {
        ClassedSerialSubject ss = new ClassedSerialSubject();
        ss.setSimple(67);
        Base base = new Base();
        base.setBdata("sample bdata");
        ss.setB1(base);
        Deep deep = new Deep();
        deep.setBdata("sample bdata1");
        deep.setMdata("sample mdata");
        deep.setDdata("sample ddata");
        ss.setB2(deep);
        Middle middle = new Middle();
        middle.setBdata("sample bdata2");
        middle.setMdata("sample mdata1");
        ss.setB3(middle);
        Document doc = Object2Dom.transform(ss, ClassedSerialSubject.class.getSimpleName());
        //String serialized = XmlDom2String.transform(doc);

        ClassedSerialSubject res = new ClassedSerialSubject();
        Object2Dom.transform(res, doc);
        assertEquals(ss.getSimple(), res.getSimple());
        assertEquals(ss.getB1().getBdata(), res.getB1().getBdata());
        assertTrue(res.getB2() instanceof Deep);
        assertEquals(((Deep) ss.getB2()).getBdata(), ((Deep) res.getB2()).getBdata());
        assertEquals(((Deep) ss.getB2()).getMdata(), ((Deep) res.getB2()).getMdata());
        assertEquals(((Deep) ss.getB2()).getDdata(), ((Deep) res.getB2()).getDdata());
        assertTrue(res.getB3() instanceof Middle);
        assertEquals(((Middle) ss.getB3()).getBdata(), ((Middle) res.getB3()).getBdata());
        assertEquals(((Middle) ss.getB3()).getMdata(), ((Middle) res.getB3()).getMdata());
    }

    @Test
    public void classedSerialNullTest() {
        ClassedSerialSubject ss = new ClassedSerialSubject();
        ss.setSimple(67);
        Base base = new Base();
        base.setBdata("sample bdata");
        ss.setB1(base);
        ss.setB2(null);
        Middle middle = new Middle();
        middle.setBdata("sample bdata2");
        middle.setMdata("sample mdata1");
        ss.setB3(middle);
        Document doc = Object2Dom.transform(ss, ClassedSerialSubject.class.getSimpleName());
        //String serialized = XmlDom2String.transform(doc);

        ClassedSerialSubject res = new ClassedSerialSubject();
        Object2Dom.transform(res, doc);
        assertEquals(ss.getSimple(), res.getSimple());
        assertEquals(ss.getB1().getBdata(), res.getB1().getBdata());
        assertNull(res.getB2());
        assertTrue(res.getB3() instanceof Middle);
        assertEquals(((Middle) ss.getB3()).getBdata(), ((Middle) res.getB3()).getBdata());
        assertEquals(((Middle) ss.getB3()).getMdata(), ((Middle) res.getB3()).getMdata());
    }

    protected static class SerialCollectionSubject {

        private String simple;
        private List<Base> bases;

        @Serial
        public String getSimple() {
            return simple;
        }

        @Serial
        public void setSimple(String simple) {
            this.simple = simple;
        }

        @SerialCollection(deserializeAs = LinkedList.class, elementTagName = "baseElement", elementType = Base.class)
        public List<Base> getBases() {
            return bases;
        }

        @SerialCollection(deserializeAs = LinkedList.class, elementTagName = "baseElement", elementType = Base.class)
        public void setBases(List<Base> bases) {
            this.bases = bases;
        }
    }

    @Test
    public void serialCollectionTest() {
        SerialCollectionSubject ss = new SerialCollectionSubject();
        ss.setSimple("sample simple data");
        ss.setBases(new ArrayList<Base>());
        Base b1 = new Base();
        b1.setBdata("sample bdata1");
        Base b2 = new Base();
        b2.setBdata("sample bdata2");
        Base b3 = new Base();
        b3.setBdata("sample bdata3");
        Base b4 = new Base();
        b4.setBdata("sample bdata4");
        ss.getBases().add(b1);
        ss.getBases().add(b2);
        ss.getBases().add(b3);
        ss.getBases().add(null);
        ss.getBases().add(b4);
        Document doc = Object2Dom.transform(ss, ClassedSerialSubject.class.getSimpleName());
        String serialized = XmlDom2String.transform(doc);

        SerialCollectionSubject res = new SerialCollectionSubject();
        Object2Dom.transform(res, doc);
        assertEquals(ss.getSimple(), res.getSimple());
        assertTrue(ss.getBases() instanceof ArrayList);
        assertTrue(res.getBases() instanceof LinkedList);
        assertEquals(ss.getBases().size(), res.getBases().size());
        for (int i = 0; i < ss.getBases().size(); i++) {
            if (ss.getBases().get(i) != null) {
                assertEquals(ss.getBases().get(i).getBdata(), res.getBases().get(i).getBdata());
            } else {
                assertNull(ss.getBases().get(i));
                assertNull(res.getBases().get(i));
            }
        }
    }

    protected static class ClassedSerialCollectionSubject implements PropertiesSimpleFactory {

        private String simple;
        private List<Base> bases;

        @Serial
        public String getSimple() {
            return simple;
        }

        @Serial
        public void setSimple(String simple) {
            this.simple = simple;
        }

        @ClassedSerialCollection(deserializeAs = LinkedList.class, elementTagName = "onBaseElement", elementClassHint = "type")
        public List<Base> getBases() {
            return bases;
        }

        @ClassedSerialCollection(deserializeAs = LinkedList.class, elementTagName = "onBaseElement", elementClassHint = "type")
        public void setBases(List<Base> bases) {
            this.bases = bases;
        }

        public Object createPropertyObjectInstance(String aSimpleClassName) {
            if (Base.class.getSimpleName().equals(aSimpleClassName)) {
                return new Base();
            } else if (Middle.class.getSimpleName().equals(aSimpleClassName)) {
                return new Middle();
            } else if (Deep.class.getSimpleName().equals(aSimpleClassName)) {
                return new Deep();
            } else {
                return null;
            }
        }
    }

    @Test
    public void classedSerialCollectionTest() {
        ClassedSerialCollectionSubject ss = new ClassedSerialCollectionSubject();
        ss.setSimple("sample simple data");
        ss.setBases(new ArrayList<Base>());
        Base b1 = new Base();
        b1.setBdata("sample bdata1");
        Middle b2 = new Middle();
        b2.setBdata("sample bdata2");
        b2.setMdata("sample mdata1");
        Base b3 = new Base();
        b3.setBdata("sample bdata3");
        Deep b4 = new Deep();
        b4.setBdata("sample bdata4");
        b4.setMdata("sample mdata2");
        b4.setDdata("sample ddata2");

        ss.getBases().add(b1);
        ss.getBases().add(b2);
        ss.getBases().add(b3);
        ss.getBases().add(null);
        ss.getBases().add(b4);
        Document doc = Object2Dom.transform(ss, ClassedSerialSubject.class.getSimpleName());
        String serialized = XmlDom2String.transform(doc);

        ClassedSerialCollectionSubject res = new ClassedSerialCollectionSubject();
        Object2Dom.transform(res, doc);
        assertEquals(ss.getSimple(), res.getSimple());
        assertTrue(ss.getBases() instanceof ArrayList);
        assertTrue(res.getBases() instanceof LinkedList);
        assertEquals(ss.getBases().size(), res.getBases().size());

        assertTrue(res.getBases().get(0) instanceof Base);
        assertEquals(res.getBases().get(0).getBdata(), ss.getBases().get(0).getBdata());

        assertTrue(res.getBases().get(1) instanceof Middle);
        Middle lm = (Middle) ss.getBases().get(1);
        Middle rm = (Middle) res.getBases().get(1);
        assertEquals(lm.getBdata(), rm.getBdata());
        assertEquals(lm.getMdata(), rm.getMdata());

        assertTrue(res.getBases().get(2) instanceof Base);
        assertNull(res.getBases().get(3));
        assertTrue(res.getBases().get(4) instanceof Deep);
        Deep ld = (Deep) ss.getBases().get(4);
        Deep rd = (Deep) res.getBases().get(4);
        assertEquals(ld.getBdata(), rd.getBdata());
        assertEquals(ld.getMdata(), rd.getMdata());
        assertEquals(ld.getDdata(), rd.getDdata());
    }

    protected static class SerialMapSubject {

        private String simple;
        private Map<String, Base> bases;

        @Serial
        public String getSimple() {
            return simple;
        }

        @Serial
        public void setSimple(String simple) {
            this.simple = simple;
        }

        @SerialMap(deserializeAs = HashMap.class, elementTagName = "entry", keyType = String.class, elementType = Base.class)
        public Map<String, Base> getBases() {
            return bases;
        }

        @SerialMap(deserializeAs = HashMap.class, elementTagName = "entry", keyType = String.class, elementType = Base.class)
        public void setBases(Map<String, Base> bases) {
            this.bases = bases;
        }
    }

    @Test
    public void serialMapTest() {
        SerialMapSubject ss = new SerialMapSubject();
        ss.setSimple("sample simple data");
        ss.setBases(new HashMap<String, Base>());
        Base b1 = new Base();
        b1.setBdata("sample bdata1");
        Base b2 = new Base();
        b2.setBdata("sample bdata2");
        Base b3 = new Base();
        b3.setBdata("sample bdata3");
        ss.getBases().put("k1", b1);
        ss.getBases().put("k2", b2);
        ss.getBases().put("k_null", null);
        ss.getBases().put("k3", b3);
        Document doc = Object2Dom.transform(ss, SerialMapSubject.class.getSimpleName());
        String serialized = XmlDom2String.transform(doc);

        SerialMapSubject res = new SerialMapSubject();
        Object2Dom.transform(res, doc);
        assertEquals(ss.getSimple(), res.getSimple());
        Entry[] ssArray = ss.getBases().entrySet().toArray(new Entry[0]);
        Entry[] resArray = res.getBases().entrySet().toArray(new Entry[0]);
        assertEquals(ssArray.length, resArray.length);
        boolean nullPresent = false;
        for (int i = 0; i < ssArray.length; i++) {
            Entry<String, Base> le = (Entry<String, Base>) ssArray[i];
            Entry<String, Base> re = (Entry<String, Base>) resArray[i];
            assertEquals(le.getKey(), re.getKey());
            Base lb = le.getValue();
            Base rb = re.getValue();
            if ("k_null".equals(re.getKey())) {
                assertNull(lb);
                assertNull(rb);
                nullPresent = true;
            } else {
                assertEquals(lb.getBdata(), rb.getBdata());
            }
        }
        assertTrue(nullPresent);
    }

    protected static class ClassedSerialMapSubject implements PropertiesSimpleFactory {

        private String simple;
        private Map<String, Base> bases;

        @Serial
        public String getSimple() {
            return simple;
        }

        @Serial
        public void setSimple(String simple) {
            this.simple = simple;
        }

        @ClassedSerialMap(deserializeAs = TreeMap.class, elementTagName = "entry", keyType = String.class, elementClassHint = "type")
        public Map<String, Base> getBases() {
            return bases;
        }

        @ClassedSerialMap(deserializeAs = TreeMap.class, elementTagName = "entry", keyType = String.class, elementClassHint = "type")
        public void setBases(Map<String, Base> bases) {
            this.bases = bases;
        }

        public Object createPropertyObjectInstance(String aSimpleClassName) {
            if (Base.class.getSimpleName().equals(aSimpleClassName)) {
                return new Base();
            } else if (Middle.class.getSimpleName().equals(aSimpleClassName)) {
                return new Middle();
            } else if (Deep.class.getSimpleName().equals(aSimpleClassName)) {
                return new Deep();
            } else {
                return null;
            }
        }
    }

    @Test
    public void classedSerialMapTest() {
        ClassedSerialMapSubject ss = new ClassedSerialMapSubject();
        ss.setSimple("sample simple data");
        ss.setBases(new HashMap<String, Base>());
        Base b1 = new Base();
        b1.setBdata("sample bdata1");
        Deep b2 = new Deep();
        b2.setBdata("sample bdata2");
        b2.setMdata("sample mdata");
        b2.setDdata("sample ddata");
        Base b3 = new Base();
        b3.setBdata("sample bdata3");
        ss.getBases().put("k1", b1);
        ss.getBases().put("k2", b2);
        ss.getBases().put("k_null", null);
        ss.getBases().put("k3", b3);
        Document doc = Object2Dom.transform(ss, ClassedSerialMapSubject.class.getSimpleName());
        String serialized = XmlDom2String.transform(doc);

        ClassedSerialMapSubject res = new ClassedSerialMapSubject();
        Object2Dom.transform(res, doc);
        assertEquals(ss.getSimple(), res.getSimple());
        assertTrue(ss.getBases() instanceof HashMap);
        assertTrue(res.getBases() instanceof TreeMap);
        assertTrue(res.getBases().get("k1") instanceof Base);
        assertTrue(res.getBases().get("k2") instanceof Deep);
        assertNull(res.getBases().get("k_null"));
        assertTrue(res.getBases().get("k3") instanceof Base);
        Entry[] ssArray = new Entry[4];
        ssArray[0] = new AbstractMap.SimpleEntry("k1", ss.getBases().get("k1"));
        ssArray[1] = new AbstractMap.SimpleEntry("k2", ss.getBases().get("k2"));
        ssArray[2] = new AbstractMap.SimpleEntry("k3", ss.getBases().get("k3"));
        ssArray[3] = new AbstractMap.SimpleEntry("k_null", ss.getBases().get("k_null"));

        Entry[] resArray = res.getBases().entrySet().toArray(new Entry[0]);
        assertEquals(ssArray.length, resArray.length);
        assertEquals(4, resArray.length);

        {
            Entry<String, Base> le = (Entry<String, Base>) ssArray[0];
            Entry<String, Base> re = (Entry<String, Base>) resArray[0];
            assertEquals(le.getKey(), re.getKey());
            Base lb = le.getValue();
            Base rb = re.getValue();
            assertEquals(lb.getBdata(), rb.getBdata());
        }

        {
            Entry<String, Base> le = (Entry<String, Base>) ssArray[1];
            Entry<String, Base> re = (Entry<String, Base>) resArray[1];
            assertEquals(le.getKey(), re.getKey());
            Base lb = le.getValue();
            Base rb = re.getValue();
            assertEquals(lb.getBdata(), rb.getBdata());
        }

        {
            Entry<String, Base> le = (Entry<String, Base>) ssArray[2];
            Entry<String, Base> re = (Entry<String, Base>) resArray[2];
            assertEquals(le.getKey(), re.getKey());
            Base lb = le.getValue();
            Base rb = re.getValue();
            assertEquals(lb.getBdata(), rb.getBdata());
        }

        {
            Entry<String, Base> le = (Entry<String, Base>) ssArray[3];
            Entry<String, Base> re = (Entry<String, Base>) resArray[3];
            assertEquals(le.getKey(), re.getKey());
            assertNull(le.getValue());
            assertNull(re.getValue());
        }
    }
}
