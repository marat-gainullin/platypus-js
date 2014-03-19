/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.scripts;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Andrew
 */
public class PlatypusScriptedResourceTest {

    public PlatypusScriptedResourceTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of loadText method, of class PlatypusScriptedResource.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testLoadText() throws Exception {
        System.out.println("load Encoded Text");
        String aResourceId = "http://www.yournavigation.org/api/1.0/gosmore.php?format=geojson&flat=56.337000126006174&flon=38.11655044555664&tlat=56.33157601311051&tlon=38.118953704833984&v=motorcar&fast=0&layer=mapnik&instructions=1";
        String aEncodingName = "UTF-8";
        String expResult = "{\n"
                + "  \"type\": \"LineString\",\n"
                + "  \"crs\": {\n"
                + "    \"type\": \"name\",\n"
                + "    \"properties\": {\n"
                + "      \"name\": \"urn:ogc:def:crs:OGC:1.3:CRS84\"\n"
                + "    }\n"
                + "  },\n"
                + "  \"coordinates\":\n"
                + "  [\n"
                + "[38.114376, 56.337274]\n"
                + ",[38.114576, 56.337762]\n"
                + ",[38.115434, 56.33888]\n"
                + ",[38.116335, 56.339736]\n"
                + ",[38.117537, 56.340046]\n"
                + ",[38.118864, 56.341617]\n"
                + ",[38.119797, 56.341637]\n"
                + ",[38.120301, 56.34172]\n"
                + ",[38.120806, 56.341946]\n"
                + ",[38.12117, 56.342005]\n"
                + ",[38.121567, 56.341999]\n"
                + ",[38.122158, 56.342088]\n"
                + ",[38.122662, 56.341993]\n"
                + ",[38.123263, 56.341987]\n"
                + ",[38.123949, 56.342083]\n"
                + ",[38.124636, 56.342255]\n"
                + ",[38.125267, 56.342441]\n"
                + ",[38.125925, 56.341556]\n"
                + ",[38.126747, 56.340449]\n"
                + ",[38.126959, 56.340201]\n"
                + ",[38.127464, 56.33961]\n"
                + ",[38.128826, 56.338418]\n"
                + ",[38.129145, 56.338068]\n"
                + ",[38.12937, 56.337692]\n"
                + ",[38.129452, 56.337481]\n"
                + ",[38.130522, 56.334599]\n"
                + ",[38.130677, 56.3342]\n"
                + ",[38.130901, 56.333562]\n"
                + ",[38.131102, 56.332807]\n"
                + ",[38.131179, 56.332414]\n"
                + ",[38.131219, 56.332229]\n"
                + ",[38.131539, 56.33069]\n"
                + ",[38.131746, 56.329389]\n"
                + ",[38.131764, 56.329269]\n"
                + ",[38.131837, 56.328854]\n"
                + ",[38.131939, 56.328303]\n"
                + ",[38.131976, 56.328162]\n"
                + ",[38.132228, 56.327512]\n"
                + ",[38.132526, 56.32706]\n"
                + ",[38.132603, 56.326921]\n"
                + ",[38.132742, 56.326723]\n"
                + ",[38.133092, 56.326214]\n"
                + ",[38.133206, 56.326031]\n"
                + ",[38.133309, 56.325858]\n"
                + ",[38.133693, 56.325276]\n"
                + ",[38.133744, 56.325196]\n"
                + ",[38.134017, 56.324814]\n"
                + ",[38.13415, 56.324594]\n"
                + ",[38.13342, 56.324443]\n"
                + ",[38.132903, 56.324343]\n"
                + ",[38.132801, 56.324323]\n"
                + ",[38.132448, 56.324235]\n"
                + ",[38.132267, 56.324106]\n"
                + ",[38.132219, 56.324071]\n"
                + ",[38.132122, 56.323861]\n"
                + ",[38.132138, 56.322911]\n"
                + ",[38.132056, 56.322088]\n"
                + ",[38.132071, 56.321643]\n"
                + ",[38.132051, 56.321442]\n"
                + ",[38.132029, 56.321232]\n"
                + ",[38.13194, 56.320078]\n"
                + ",[38.131821, 56.319104]\n"
                + ",[38.13155, 56.31734]\n"
                + ",[38.131521, 56.316971]\n"
                + ",[38.131532, 56.316852]\n"
                + ",[38.131545, 56.316718]\n"
                + ",[38.131606, 56.315786]\n"
                + ",[38.131658, 56.315329]\n"
                + ",[38.131543, 56.313931]\n"
                + ",[38.131462, 56.313622]\n"
                + ",[38.131349, 56.313543]\n"
                + ",[38.131147, 56.313534]\n"
                + ",[38.129753, 56.314068]\n"
                + ",[38.129052, 56.314268]\n"
                + ",[38.128297, 56.314319]\n"
                + ",[38.127533, 56.314303]\n"
                + ",[38.12584, 56.313999]\n"
                + ",[38.124391, 56.315834]\n"
                + ",[38.122891, 56.317731]\n"
                + ",[38.121759, 56.319424]\n"
                + ",[38.121065, 56.320544]\n"
                + ",[38.118474, 56.32039]\n"
                + ",[38.11839, 56.321011]\n"
                + ",[38.118325, 56.322385]\n"
                + ",[38.118564, 56.323278]\n"
                + ",[38.118777, 56.32361]\n"
                + ",[38.118719, 56.323777]\n"
                + ",[38.117834, 56.325151]\n"
                + ",[38.11731, 56.325912]\n"
                + ",[38.11706, 56.326275]\n"
                + ",[38.116989, 56.326472]\n"
                + ",[38.115449, 56.328471]\n"
                + ",[38.114591, 56.32951]\n"
                + ",[38.114353, 56.329597]\n"
                + ",[38.115995, 56.330082]\n"
                + ",[38.117508, 56.330484]\n"
                + ",[38.118742, 56.330812]\n"
                + ",[38.119491, 56.331046]\n"
                + "  ],  \"properties\": {\n"
                + "    \"distance\": \"7.293988\",\n"
                + "    \"description\": \"Continue on 2-я Тверская улица. Follow the road for 0.6 mi.<br>Turn right into Сергиев Посад — Калязин — Рыбинск — Череповец. Follow the road for 0.1 mi.<br>Continue on Новоугличское шоссе. Follow the road for 1.1 mi.<br>Turn right into улица 1-й Ударной Армии. Follow the road for 0.8 mi.<br>Turn right into Кукуевская набережная. Follow the road for 0.1 mi.<br>Continue on улица 8 Марта. Follow the road for 0.2 mi.<br>Turn right into улица Фаворского. Follow the road for 0.6 mi.<br>Turn right into Садовая улица. Follow the road for 0.2 mi.<br>Continue on Гражданская улица. Follow the road for 0.5 mi.<br>Turn right into Деулинский переулок. Follow the road for 0.2 mi.<br>Continue on fini.<br>\",\n"
                + "    \"traveltime\": \"235\"\n"
                + "    }\n"
                + "}\n";
        String result = PlatypusScriptedResource.loadText(aResourceId, aEncodingName);
        assertEquals(expResult, result);
    }

}
