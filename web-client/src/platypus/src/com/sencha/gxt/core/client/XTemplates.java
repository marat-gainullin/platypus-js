/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.core.client;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.sencha.gxt.core.client.XTemplates.FormatterFactories;
import com.sencha.gxt.core.client.XTemplates.FormatterFactory;
import com.sencha.gxt.core.client.XTemplates.FormatterFactoryMethod;
import com.sencha.gxt.core.client.XTemplates.TemplateCondition;

/**
 * A tag interface for declaring methods that accept objects and convert them into HTML based
 * on an associated template. Methods return {@link SafeHtml} instances which can be used in many
 * GWT and GXT widgets to render content. XTemplates support a variety of features to make it easy
 * to generate content, including:
 * 
 * <ul>
 * <li>Reading properties and nested properties</li>
 * <li>Formatting data into strings</li>
 * <li>Collection and Array iterating, applying sub templates to each item</li>
 * <li>Conditional support, including the ability to call other Java methods that return boolean</li>
 * <li>Basic math and expression support</li>
 * </ul>
 * 
 * <br>
 * <b>
 * Declaring a Template
 * </b>
 * <p>
 * Templates are declared by creating a new interface that extends {@code XTemplate}, or by 
 * extending another interface that extends {@code XTemplate}. Instances can then be created by 
 * invoking {@code GWT.create} on the template class, and then methods can be invoked.
 * </p>
 * <p>
 * Template method content is created in one of two ways: either by declaring the template content
 * in the {@code @XTemplate} annotation, or by creating a new html file and pointing the 
 * {@code @XTemplate} annotation at that file.
 * </p>
 * <pre><code>
public interface SampleXTemplates extends XTemplates {
  {@literal @}XTemplate("&lt;div>Hello, {name}!&lt;/div>")
  SafeHtml hello(String name);

  {@literal @}XTemplate(source="hello.html")
  SafeHtml helloExternal(String name);
}

private SampleXTemplates tpl = GWT.create(SampleXTemplates.class);
</pre></code>
 * <p>
 * In this example, {@code tpl.hello("test")} would return a {@code SafeHtml} instance representing
 * the html
 * <pre><code>&lt;div>Hello, test!&lt;div></pre></code>
 * while {@code tpl.hello("test")} would have its content depend on the template defined in the file 
 * {@code hello.html} in the same package.
 * 
 * <ul>
 * <li>
 * <b>Reading properties</b>
 * <p>
 * This is the data used for reference in each code sample:
 * </p>
 * <pre></code>
public class Kid {
  public Kid(String name, int age) ...

  public String getName() ...
  public int getAge() ...
}
public class Person {
  public Person(String name, String company, String product, String location) ...

  public String getName() ...
  public String getCompany() ...
  public String getProduct() ...
  public String getLocation() ...
  public List&lt;Kid> getKids() ...

  public void setKids(List&lt;Kid> kids) ...
}

final Person person = new Person("Darrell Meyer", "Sencha Inc", "GXT", "Washington, DC");

List&lt;Kid> kids = new ArrayList&lt;Kid>();
kids.add(new Kid("Alec", 4));
kids.add(new Kid("Lia", 2));
kids.add(new Kid("Andrew", 1));

person.setKids(kids);
</pre></code>
 * <p>
 * Properties are read by naming the object or property to read in {@code { ... }} brackets in the
 * template body. In the main template (not in a {@code &lt;tpl for="...">} tag) these will be
 * scoped based on the arguments to the template. If there is only one argument, the argument may 
 * be named, or properties may be referred to directly. If there are multiple arguments, they must
 * be named. 
 * </p>
 * <pre><code>
interface ReadingPropertiesTemplates extends XTemplates {
  {@literal @}XTemplate("Hello, {name}, from {person.location}") // either works
  SafeHtml oneArgument(Person person);
  
  {@literal @}XTemplate("Hello, {one.name}, {two.name}, and {three.name}!")
  SafeHtml twoArguments(Kid one, Kid two, Kid three);
}
ReadingPropertiesTemplates tpl = GWT.create(ReadingPropertiesTemplates.class);
SafeHtml parentContent = tpl.oneArgument(person);
SafeHtml childrenContent = tpl.twoArguments(person.get(0), person.get(1), person.get(2));
</pre></code>
 * <p>
 * Nested properties are read by separating each getter or method call with a '{@code .}'. 
 * Properties are most often read from getter methods, but can also be read from zero-arg methods
 * by using their full name. As a result of this, the size of the list retrieved from 
 * {@code Person.getKids()} can be expressed as {@code kids.size}:
 * </p>
 * <pre><code>
interface PersonWithKidsTemplate extends XTemplates {
  {@literal @}XTemplate("{name} has {kids.size} children")
  SafeHtml renderChildCount(Person p);
}
PersonWithKidsTemplate tpl = GWT.create(PersonWithKidsTemplate.class);
SafeHtml content = tpl.renderChildCount(person);
</pre></code>
 * </li>
 * <li>
 * <b>Formatting data into strings</b>
 * <p>
 * <br>
 * Values can be formatted using the following syntax:
 * 
 * <br>
 * <ul>
 * <li>{@code {value:formatName}} - no format param</li>
 * <li>{@code {value:formatName(format)}} - with format param</li></li>
 * </ul>
 * <br>
 * Built-in formats:
 * <ul>
 * <li>{@code date(format)} - format syntax uses GWT DateTimeFomat (example:
 * {@code {mydate:date("m/d/yyyy")})}</li>
 * <li>{@code number(format)} - format syntax uses GWT NumberFormat (example:
 * {@code {mynumber:number("0000.0000")})}</li>
 * <li>{@code currency} - no parameters</li>
 * <li>{@code scientific} - no parameters</li>
 * <li>{@code decimal} - no parameters</li>
 * 
 * </ul>
 * <br>
 * Additional formatters can be created and registered when creating an XTemplate. They can be
 * declared on any class - when an XTemplates type is declared, it, and all parents will be checked
 * for formatters to use. Formatters can be redeclared when a type is extended - the type closest to
 * the final type will override other formatters.
 * 
 * </p>
 * </li>
 * <li>
 * <b>Collections and Arrays</b>
 * <p>
 * <p>
 * <br>
 * The <b><tt>tpl</tt></b> tag and the <b><tt>for</tt></b> operator are used to
 * process the provided data object:
 * <ul>
 * <li>If the value specified in <tt>for</tt> is an array, it will auto-fill,
 * repeating the template block inside the <tt>tpl</tt> tag for each item in the
 * array.</li>
 * <li>If <tt>for="."</tt> is specified, the data object provided is examined.</li>
 * <li>While processing an array, the special variable <tt>{#}</tt> will provide
 * the current array index + 1 (starts at 1, not 0).</li>
 * </ul>
 * </p>
 * 
 * <pre><code>
&lt;tpl <b>for</b>=".">...&lt;/tpl>       // loop through array at root node
&lt;tpl <b>for</b>="foo">...&lt;/tpl>     // loop through array at foo node
&lt;tpl <b>for</b>="foo.bar">...&lt;/tpl> // loop through array at foo.bar node
 * </code></pre>
 * Using the sample data above:
 * 
 * <pre><code>
interface KidListTemplate extends XTemplates {
  {@literal @}XTemplate("&lt;p>Kids: " +
             "&lt;tpl <b>for</b>='.'>" +       // process the data.kids node
             "&lt;p>{#}. {name}&lt;/p>" +  // use current array index to autonumber
             "&lt;/tpl>&lt;/p>")
  SafeHtml listKids(List&lt;Kid> kids);
}
KidListTemplate tpl = GWT.create(KidListTemplate.class);
SafeHtml content = tpl.listKids(person.getKids()); // pass the kids property of the data object
 * </code></pre>
 * <p>
 * An example illustrating how the <b><tt>for</tt></b> property can be leveraged
 * to access specified members of the provided data object to populate the
 * template:
 * </p>
 * 
 * <pre><code>
interface NestedListTemplate extends XTemplates {
  {@literal @}XTemplate("&lt;p>Name: {name}&lt;/p>" +
             "&lt;p>Title: {title}&lt;/p>" +
             "&lt;p>Company: {company}&lt;/p>" +
             "&lt;p>Kids: " +
             "&lt;tpl <b>for='kids'</b>>" +     // interrogate the kids property within the data
               "&lt;p>{name}&lt;/p>" +
             "&lt;/tpl>&lt;/p>")
   SafeHtml renderPerson(Person person);
}
SafeHtml content = tpl.renderPerson(person);
 * </code></pre>
 * <p>
 * 
 * When processing a sub-template, for example while looping through a child
 * array, you can access the parent object's members via the <b><tt>parent</tt>
 * </b> object:
 * </p>
 * 
 * <pre><code>
interface ParentChildTemplate extends XTemplates {
  {@literal @}XTemplate("&lt;p>Name: {name}&lt;/p>" +
             "&lt;p>Kids: " +
             "&lt;tpl for=\"kids\">" +
               "&lt;tpl if=\"age > 1\">" +
                 "&lt;p>{name}&lt;/p>" +
                 "&lt;p>Dad: {<b>parent</b>.name}&lt;/p>" +
               "&lt;/tpl>" +
             "&lt;/tpl>&lt;/p>")
  SafeHtml renderParentAndChildren(Person person);
}
SafeHtml content = tpl.renderParentAndChildren(person);
 * </code></pre>
 * </p>
 * </li>
 * <li>
 * <b>Conditional support</b>
 * <p>
 * <br>
 * The <b><tt>tpl</tt></b> tag and the <b><tt>if</tt></b> operator are used to
 * provide conditional checks for deciding whether or not to render specific
 * parts of the template. Notes:<div class="sub-desc">
 * <ul>
 * <li>If quotes are to be used in the conditionals, they must either be different quotes than were
 * used to define the if, or must be escaped.</li>
 * <li>Greater-than (&gt;) symbols must be escaped within conditionals as &amp;gt;</li>
 * <li>There is no <tt>else</tt> operator &mdash; if needed, two opposite
 * <tt>if</tt> statements should be used.</li>
 * </ul>
 * 
 * <pre><code>
&lt;tpl if="age &gt; 1 &amp;&amp; age &lt; 10">Child&lt;/tpl>
&lt;tpl if="age >= 10 && age < 18">Teenager&lt;/tpl>
&lt;tpl <b>if</b>="id==\'download\'">...&lt;/tpl>
&lt;tpl <b>if</b>="needsIcon">&lt;img src="{icon}" class="{iconCls}"/>&lt;/tpl>
// no good:
&lt;tpl if="name == "Jack"">Hello&lt;/tpl>
// encode &#34; if it is part of the condition, e.g.
&lt;tpl if="name == &#38;quot;Jack&#38;quot;">Hello&lt;/tpl>
 * </code></pre>
 * Using the sample data above:
 * 
 * <pre><code>
interface OlderChildListTemplate extends XTemplates {
  {@literal @}XTemplate("&lt;p>Name: {name}&lt;/p>" +
             "&lt;p>Kids: " +
             "&lt;tpl for=\"kids\">" +   // either double quotes (escaped)
               "&lt;tpl if='age > 1'>" + // or single are allowed
                 "&lt;p>{name}&lt;/p>" + // (no need to escape either in an external file)
               "&lt;/tpl>" +
             "&lt;/tpl>&lt;/p>")
  SafeHtml renderFamilyOf(Person person);
);
SafeHtml content = tpl.renderFamilyOf(person);
 * </code></pre>
 * </div>
 * </li>
 * <li>
 * <b>Basic math and expressions</b>
 * <p>
 * <br>
 * The following basic math operators may be applied directly on numeric data
 * values:
 * </p>
 * 
 * <pre>
 * + - * /
 * </pre>
 * 
 * To be returned as part of the template, expressions must be contained within {@code {[ ... ]}}
 * brackets.
 * 
 * <br>
 * For example:
 * 
 * <pre><code>
interface FutureAgesTemplate extends XTemplates {
  {@literal @}XTemplate("&lt;p>Name: {name}&lt;/p>" +
            "&lt;p>Kids: " +
            "&lt;tpl for=\"kids\">" +
              "&lt;tpl if=\"age &amp;gt; 1\">" +  // <-- Note that the &gt; is encoded
                "&lt;p>{#}: {name}&lt;/p>" +  // <-- Auto-number each item
                "&lt;p>In 5 Years: {[age+5]}&lt;/p>" +  // <-- Basic math
                "&lt;p>Dad: {parent.name}&lt;/p>" +
              "&lt;/tpl>" +
            "&lt;/tpl>&lt;/p>")
  SafeHtml renderFutureChildAges(Person person);
);
SafeHtml content = tpl.renderFutureChildAges(person);
</code></pre>
 * </li>
 * 
 */
@FormatterFactories({
    @FormatterFactory(factory = DateTimeFormat.class, methods = @FormatterFactoryMethod(name = "date", defaultArgs = "\"M/d/y\"")),
    @FormatterFactory(factory = NumberFormat.class, methods = {
        @FormatterFactoryMethod(name = "scientific", method = "getScientificFormat"),
        @FormatterFactoryMethod(name = "decimal", method = "getDecimalFormat"),
        @FormatterFactoryMethod(name = "currency", method = "getCurrencyFormat"),
        @FormatterFactoryMethod(name = "number", method = "getFormat", defaultArgs = "\"#\""),
        @FormatterFactoryMethod(name = "percentage", method = "getPercentFormat")
    })})
@TemplateCondition(methodName = "equals", type = Object.class)
public interface XTemplates {

  /**
   * Indicates the string that should be used when generating the template.
   * Either an html string can be specified (like
   * {@link com.google.gwt.safehtml.client.SafeHtmlTemplates}, or a source file
   * may be specified.
   * 
   */
  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.METHOD)
  @Documented
  public @interface XTemplate {
    /**
     * The template itself, defined inline
     */
    String value() default "";

    /**
     * The filename of the html file containing the template. The path is
     * relative to the current package.
     */
    String source() default "";
  }

  /**
   * A simple interface to facilitate creation of custom formatters. Instances should be created
   * by a static factory method, as defined by {@link FormatterFactories}.
   *
   * @param <T> the type of data the formatter is intended to handle
   */
  public interface Formatter<T> {
    /**
     * Formats the given data so it can be drawn in the template as a plain string.
     * 
     * @param data the parameter passed in from the template. Will never be null unless {@link FormatterFactory#acceptsNull()} is true
     * @return a string to render in the template
     */
    String format(T data);
  }

  /**
   * Collection of {@link FormatterFactory} instances.
   */
  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.TYPE)
  public @interface FormatterFactories {
    FormatterFactory[] value();
  }
  /**
   * Declares that a class may act as a factory to provide formatter instances.
   * Either the name attribute must be populated with the name that this factory
   * will use in an XTemplate and the method getFormat will be called, or the
   * methods must indicate a name and a methodName to be invoked on the factory.
   * 
   */
  @Target({})
  @Retention(RetentionPolicy.RUNTIME)
  public @interface FormatterFactory {
    Class<?> factory();

    /**
     * The name of the formatter being registered. If blank, then {@link #methods()} is assumed to
     * contain factory methods instead.
     * 
     * @return name of the formatter being registered.
     */
    String name() default "";

    /**
     * Allows methods to be defined on a formatter factory with a name other that {@code getFormat},
     * and allowed more than one factory method to be defined.
     * 
     * @return one or more factory methods
     */
    FormatterFactoryMethod[] methods() default {};

    /**
     * True if the formatter can handle null values. If false, a null value will be rendered directly
     * as an empty string.
     * 
     * @return false if null values should be rendered as an empty string and not formatted
     */
    boolean acceptsNull() default false;
  }
  /**
   * Allows methods to be called on a factory other than 'getFormat'. More than
   * one instance of this may be provided to a {@link FormatterFactory}
   * annotation to register multiple method within the same factory.
   * 
   */
  @Target({})
  @Retention(RetentionPolicy.RUNTIME)
  public @interface FormatterFactoryMethod {
    /**
     * The name of the formatter being registered. This name will be what is referenced
     * from the XTemplate source.
     * 
     * @return the name of the formatter as it will be used in xtemplate source
     */
    String name();

    /**
     * The name of the method to invoke to produce a formatter. Defaults to {@code getFormat}
     * @return the name of the static method to invoke to produce a formatter
     */
    String method() default "getFormat";

    /**
     * Default arguments to pass into the format method if none are specified in the XTemplate
     * source.
     * 
     * @return the default arguments to pass into the formatter factory method
     */
    String defaultArgs() default "";

  }

  /**
   * Defines a single method on an object that should be accessible from within XTemplate
   * conditional statements.
   * 
   * By default, {@link Object#equals(Object)} is defined as :equals in an XTemplate, so this is
   * legal:
   * <pre><code>
interface TemplateUsingEquals extends XTemplates {
  {@literal @}XTemplate("&lt;tpl if='o1:equals(o2)'>true&lt;/tpl>")
  SafeHtml tpl(Object o1, Object o2);
}
</pre></code>
   *
   */
  @Target(ElementType.TYPE)
  @Retention(RetentionPolicy.RUNTIME)
  public @interface TemplateCondition {
    /**
     * The name of the method to define. Defaults to the {@link #methodName()}.
     * 
     * @return the name of the method as it will be used in XTemplate source, or "" if the same 
     * as {@link #methodName()}
     */
    String name() default "";
    
    /**
     * @return the type the condition method is defined on.
     */
    Class<?> type();

    /**
     * The name of the method to invoke from a compiled template. This method must exist on
     * {@link #type()} or a superclass.
     * @return the method to invoke on {@link #type()}
     */
    String methodName();
  }

  /**
   * Holds a collection of {@link TemplateCondition} instances, allowing more than one to be
   * declared at a time.
   */
  @Target(ElementType.TYPE)
  @Retention(RetentionPolicy.RUNTIME)
  public @interface TemplateConditions {
    TemplateCondition[] value();
  }

}
