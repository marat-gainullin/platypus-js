/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.core.rebind;

import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.TreeLogger.Type;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.sencha.gxt.core.rebind.XTemplateParser.ContentChunk.ContentType;

/**
 * Parses out the contents of a given XTemplate, with a result intended for use
 * in one or more SafeHtmlTemplates interfaces, to handle the html replacement
 * issues.
 * 
 * 
 */
public class XTemplateParser {

  private static final Pattern INVOKE_PATTERN = Pattern.compile("\\{\\[([^\\]]+)\\]\\}");
  private static final Pattern PARAM_PATTERN = Pattern.compile("\\{((?:[a-zA-Z_0-9\\.]+|#)(?:\\:[^\\}\\(]+(?:\\([^\\)]+\\))?)?)\\}");

  private static final Pattern TAG_PATTERN = Pattern.compile("<tpl([^>]+)>");
  private static final Pattern TAG_CLOSE_PATTERN = Pattern.compile("</tpl>");

  private static final Pattern NON_LITERAL_PATTERN = Pattern.compile(PARAM_PATTERN.pattern() + "|"
      + TAG_PATTERN.pattern() + "|" + TAG_CLOSE_PATTERN.pattern() + "|" + INVOKE_PATTERN);

  private static final Pattern ATTR_PATTERN = Pattern.compile("(if|for)=(?:\"([^\">]+)\"|\\'([^\\'>]+)\\')");
  private final TreeLogger logger;

  public XTemplateParser(TreeLogger logger) {
    this.logger = logger;
  }

  public TemplateModel parse(String template) throws UnableToCompleteException {
    // look for parameters or tags (Consider combining into one pattern)
    TemplateModel model = new TemplateModel();
    Stack<ContainerTemplateChunk> stack = new Stack<ContainerTemplateChunk>();
    stack.push(model);
    Matcher m = NON_LITERAL_PATTERN.matcher(template);
    int lastMatchEnd = 0;
    while (m.find()) {
      // range of the current non-literal
      int begin = m.start(), end = m.end();
      String currentMatch = template.substring(begin, end);

      // if there was content since the last non-literal chunk, track it
      if (lastMatchEnd < begin) {
        ContentChunk c = literal(template.substring(lastMatchEnd, begin));
        stack.peek().children.add(c);
        log(c);
      }

      // move the last match pointer
      lastMatchEnd = end;

      // tpl tag starting
      Matcher tagOpenMatch = TAG_PATTERN.matcher(currentMatch);
      if (tagOpenMatch.matches()) {
        ControlChunk c = new ControlChunk();
        c.controls = new HashMap<String, String>();
        String attrs = tagOpenMatch.group(1).trim();
        Matcher attrMatcher = ATTR_PATTERN.matcher(attrs);
        while (attrMatcher.find()) {
          // should be if or for
          String key = attrMatcher.group(1);
          // must be html-decoded
          String encodedValue = attrMatcher.group(2) == null ? attrMatcher.group(3) : attrMatcher.group(2);
          String value = StringEscapeUtils.unescapeXml(encodedValue);
          c.controls.put(key, value);
        }
        stack.peek().children.add(c);
        stack.push(c);
        log(c);
        continue;
      }

      // tpl tag ending
      Matcher tagCloseMatch = TAG_CLOSE_PATTERN.matcher(currentMatch);
      if (tagCloseMatch.matches()) {
        TemplateChunk c;
        try {
          c = stack.pop();
        } catch (EmptyStackException ex) {
          logger.log(Type.ERROR, "Too many </tpl> tags");
          throw new UnableToCompleteException();
        }
        log(c);
        continue;
      }

      // reference (code)
      Matcher codeMatch = INVOKE_PATTERN.matcher(currentMatch);
      if (codeMatch.matches()) {
        ContentChunk c = new ContentChunk();
        c.type = ContentType.CODE;
        c.content = codeMatch.group(1);
        stack.peek().children.add(c);
        log(c);
        continue;
      }

      // reference (param)
      Matcher paramMatch = PARAM_PATTERN.matcher(currentMatch);
      if (paramMatch.matches()) {
        ContentChunk c = new ContentChunk();
        c.type = ContentType.REFERENCE;
        c.content = paramMatch.group(1);
        stack.peek().children.add(c);
        log(c);
        continue;
      }
    }
    // handle trailing content
    if (lastMatchEnd < template.length()) {
      ContentChunk c = literal(template.substring(lastMatchEnd));
      log(c);
      model.children.add(c);
    }
    if (model != stack.peek()) {
      logger.log(Type.ERROR, "Too few </tpl> tags");
      throw new UnableToCompleteException();
    }
    return model;
  }

  private ContentChunk literal(String match) {
    // Get the content, and crop out whitespace between tags
    // TODO provide a way to turn this functionality on and off
    ContentChunk c = new ContentChunk();
    c.content = match.replaceAll("\\>\\s+\\<", "><");
    c.type = ContentType.LITERAL;
    return c;
  }

  private void log(TemplateChunk c) {
    if (c instanceof ContentChunk) {
      logger.log(Type.DEBUG, "xtemplate[" + ((ContentChunk) c).type + "]:\t" + ((ContentChunk) c).content);
    } else if (c instanceof ContainerTemplateChunk) {
      ContainerTemplateChunk cont = (ContainerTemplateChunk) c;
      if (cont.children.size() == 0) {
        logger.log(Type.DEBUG, "xtemplate[container start]");
      } else {
        logger.log(Type.DEBUG, "xtemplate[container end]");
      }
    }
  }

  public static class TemplateModel extends ContainerTemplateChunk {
    @Override
    public String toString() {
      return "Root of template";
    }
  }
  public static class ControlChunk extends ContainerTemplateChunk {
    public Map<String, String> controls;

    @Override
    public String toString() {
      return "<tpl> tag: " + controls;
    }
  }
  public static class ContentChunk extends TemplateChunk {
    public enum ContentType {
      LITERAL, REFERENCE, CODE
    }

    public ContentType type;
    public String content;

    @Override
    public String toString() {
      return content;
    }
  }
  public abstract static class TemplateChunk {

  }
  public abstract static class ContainerTemplateChunk extends TemplateChunk {
    public final List<TemplateChunk> children = new LinkedList<TemplateChunk>();
  }
}
