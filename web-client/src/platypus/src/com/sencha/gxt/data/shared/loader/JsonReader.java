/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.data.shared.loader;

import com.google.gwt.http.client.RequestBuilder;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;
import com.google.web.bindery.autobean.shared.Splittable;
import com.google.web.bindery.autobean.shared.impl.StringQuoter;
import com.sencha.gxt.data.client.loader.HttpProxy;

/**
 * A <code>DataReader</code> implementation that reads JSON data and builds it
 * into the given {@link AutoBean} type, using other types from the given
 * factory.
 * 
 * <p />
 * Subclasses can override {@link #createReturnData(Object, Object)} to control
 * what object is returned by the reader.
 * <p />
 * Although a <code>JsonReader</code> is frequently used to convert JSON data
 * from a server to AutoBeans that can be displayed in a widget, the underlying
 * concepts may be easier to understand by considering a simple example that
 * converts JSON data into beans and retrieves the bean's properties:
 * 
 * <pre>
 * public class MyTest implements EntryPoint {
 * 
 *   // A Java String containing JSON data. We've simplified the source of
 *   // the JSON data for the purposes of the example. Typically it would be
 *   // the result of invoking a web service, using classes like {@link RequestBuilder},
 *   // {@link HttpProxy} and {@link ListLoader}. For a complete example, see
 *   // <code>JsonGridExample</code>.
 *   private static final String SAMPLE_JSON = "{ \"cityList\" : [ { \"name\": \"Tokyo\" , \"population\" : 32450000 }, { \"name\" : \"New York\" , \"population\" : 19750000 } ] }";
 * 
 *   // A representation of the JSON root object.
 *   // The JSON root object contains a list of City objects.
 *   // The AutoBean framework creates a bean from this interface.
 *   public interface JsonRootObject {
 * 
 *     // Returns a list of the City beans contained in the JSON root object.
 *     // List&lt;City&gt; matches the type of the root object in the JSON data.
 *     // CityList matches the name of the "cityList" field in the JSON data.
 *     List&lt;City&gt; getCityList();
 * 
 *   }
 * 
 *   // A representation of the name, population object in the JSON data.
 *   // A list of these objects is contained in JsonRootObject.
 *   // The AutoBean framework creates a bean from this interface.
 *   public interface City {
 * 
 *     // Returns the name of the city.
 *     // String matches the type of the name field in the JSON data.
 *     // Name matches the name of the "name" field in the JSON data.
 *     String getName();
 * 
 *     // Returns the population of the city.
 *     // Integer matches the type of the population field in the JSON data.
 *     // Population matches the name of the "population" field in the JSON data.
 *     Integer getPopulation();
 * 
 *   }
 * 
 *   // The AutoBean framework provides a factory that can
 *   // create AutoBeans, just by extending AutoBeanFactory.
 *   public interface JsonRootObjectAutoBeanFactory extends AutoBeanFactory {
 * 
 *     // Returns the JSON root object (a list of cities).
 *     // JsonRootObject type parameter matches root JSON type.
 *     // The method name is arbitrary (no corresponding name in the JSON data).
 *     AutoBean&lt;JsonRootObject&gt; jsonRootObject();
 * 
 *   }
 * 
 *   // Creates a CityList from JSON data in one step.
 *   // To convert from JSON data, extend a JsonReader and override
 *   // createReturnData to return the desired type.
 *   public class CityListReader extends JsonReader&lt;ListLoadResult&lt;City&gt;, JsonRootObject&gt; {
 * 
 *     public CityListReader(AutoBeanFactory factory, Class&lt;JsonRootObject&gt; rootBeanType) {
 *       super(factory, rootBeanType);
 *     }
 * 
 *     protected ListLoadResult&lt;City&gt; createReturnData(Object loadConfig, JsonRootObject incomingData) {
 *       return new ListLoadResultBean&lt;City&gt;(incomingData.getCityList());
 *     }
 *   }
 * 
 *   public void onModuleLoad() {
 *     JsonRootObjectAutoBeanFactory factory = GWT.create(JsonRootObjectAutoBeanFactory.class);
 *     CityListReader reader = new CityListReader(factory, JsonRootObject.class);
 *     ListLoadResult&lt;City&gt; cities = reader.read(null, SAMPLE_JSON);
 *     List&lt;City&gt; cityList = cities.getData();
 *     for (City city : cityList) {
 *       System.out.println("name=" + city.getName() + ", population=" + city.getPopulation());
 *     }
 *   }
 * }
 * </pre>
 * See <a href="http://www.json.org">http://www.json.org</a> for a brief (yet
 * nearly complete) description of JSON.
 * <p/>
 * 
 * @param <Result> the desired return data to use from the {@link DataReader},
 *          usually {@link ListLoadResult}
 * @param <Base> the base type of data to be read from the incoming data
 */
public class JsonReader<Result, Base> extends AbstractAutoBeanReader<Result, Base, String> {

  /**
   * Creates a JsonReader capable of converting JSON data into one or more
   * AutoBeans.
   * 
   * @param factory AutoBeanFactory instance capable of building all of the
   *          required classes
   * @param rootBeanType AutoBean based type to represent the root object in the
   *          JSON data
   */
  public JsonReader(AutoBeanFactory factory, Class<Base> rootBeanType) {
    super(factory, rootBeanType);
  }

  @Override
  protected Splittable readSplittable(Object loadConfig, String data) {
    return StringQuoter.split(data);
  }
}
