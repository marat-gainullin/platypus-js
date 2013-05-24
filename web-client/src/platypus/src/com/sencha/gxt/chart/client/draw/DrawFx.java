/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.chart.client.draw;

import java.util.List;

import com.sencha.gxt.chart.client.draw.path.CurveTo;
import com.sencha.gxt.chart.client.draw.path.MoveTo;
import com.sencha.gxt.chart.client.draw.path.PathCommand;
import com.sencha.gxt.chart.client.draw.path.PathSprite;
import com.sencha.gxt.chart.client.draw.sprite.RectangleSprite;
import com.sencha.gxt.chart.client.draw.sprite.Sprite;
import com.sencha.gxt.core.client.util.PreciseRectangle;
import com.sencha.gxt.fx.client.animation.Animator;

/**
 * Class that provides utility functions for creating sprite animations.
 */
public class DrawFx {

  /**
   * Returns an {@link Animator} that will animate the given {@link PathSprite}
   * to the given list of {@link PathCommand}s.
   * 
   * @param origin the path sprite
   * @param commands the target path commands
   * @return the animator
   */
  public static Animator createCommandsAnimator(final PathSprite origin, List<PathCommand> commands) {
    List<PathSprite> sourceDelta = PathSprite.findDelta(origin, commands);
    final PathSprite source = sourceDelta.get(0);
    final PathSprite delta = sourceDelta.get(1);
    return new Animator() {
      @Override
      protected void onUpdate(double progress) {
        PathSprite start = new PathSprite(source);
        for (int i = 0; i < start.size(); i++) {
          if (start.getCommand(i) instanceof MoveTo) {
            MoveTo move1 = (MoveTo) start.getCommand(i);
            MoveTo move2 = (MoveTo) delta.getCommand(i);
            move1.setX(move1.getX() + (move2.getX() * progress));
            move1.setY(move1.getY() + (move2.getY() * progress));
          } else if (start.getCommand(i) instanceof CurveTo) {
            CurveTo curve1 = (CurveTo) start.getCommand(i);
            CurveTo curve2 = (CurveTo) delta.getCommand(i);
            curve1.setX(curve1.getX() + (curve2.getX() * progress));
            curve1.setY(curve1.getY() + (curve2.getY() * progress));
            curve1.setX1(curve1.getX1() + (curve2.getX1() * progress));
            curve1.setY1(curve1.getY1() + (curve2.getY1() * progress));
            curve1.setX2(curve1.getX2() + (curve2.getX2() * progress));
            curve1.setY2(curve1.getY2() + (curve2.getY2() * progress));
          }
        }
        origin.setCommands(start.getCommands());
        origin.redraw();
      }
    };
  }

  /**
   * Returns an {@link Animator} that will animate the given {@link Sprite} to
   * the given degrees of rotation.
   * 
   * @param sprite the sprite to be animated
   * @param degrees the target degrees of rotation
   * @return the animator
   */
  public static Animator createDegreesAnimator(final Sprite sprite, double degrees) {
    final Rotation rot = sprite.getRotation();
    final double deltaDegrees = degrees - rot.getDegrees();
    return new Animator() {
      @Override
      protected void onUpdate(double progress) {
        sprite.setRotation(rot.getDegrees() + (deltaDegrees * progress));
        sprite.redraw();
      }
    };
  }

  /**
   * Returns an {@link Animator} that will animate the given {@link Sprite} to
   * the given opacity.
   * 
   * @param sprite the sprite to be animated
   * @param opacity the target opacity
   * @return the animator
   */
  public static Animator createOpacityAnimator(final Sprite sprite, double opacity) {
    final double origin;
    if (Double.isNaN(sprite.getOpacity())) {
      origin = 1;
    } else {
      origin = sprite.getOpacity();
    }
    final double delta = opacity - origin;
    return new Animator() {
      @Override
      protected void onUpdate(double progress) {
        sprite.setOpacity(origin + (delta * progress));
        sprite.redraw();
      }
    };
  }

  /**
   * Returns an {@link Animator} that will animate the given
   * {@link RectangleSprite} to the given {@link PreciseRectangle} attributes.
   * 
   * @param sprite the rectangle sprite
   * @param rectangle the target rectangle attributes
   * @return the animator used to run the animation
   */
  public static Animator createRectangleAnimator(final RectangleSprite sprite, PreciseRectangle rectangle) {
    final PreciseRectangle origin = new PreciseRectangle(sprite.getX(), sprite.getY(), sprite.getWidth(),
        sprite.getHeight());
    final PreciseRectangle delta = new PreciseRectangle(rectangle.getX() - sprite.getX(), rectangle.getY()
        - sprite.getY(), rectangle.getWidth() - sprite.getWidth(), rectangle.getHeight() - sprite.getHeight());
    return new Animator() {
      @Override
      protected void onUpdate(double progress) {
        sprite.setX(origin.getX() + delta.getX() * progress);
        sprite.setY(origin.getY() + delta.getY() * progress);
        sprite.setWidth(origin.getWidth() + delta.getWidth() * progress);
        sprite.setHeight(origin.getHeight() + delta.getHeight() * progress);
        sprite.redraw();
      }
    };
  }

  /**
   * Returns an {@link Animator} that will animate the given {@link Sprite} to
   * the given rotation.
   * 
   * @param sprite the sprite to be animated
   * @param x the target rotation on the x-axis
   * @param y the target rotation on the y-axis
   * @param degrees the degrees of rotation
   * @return the animator
   */
  public static Animator createRotationAnimator(final Sprite sprite, double x, double y, double degrees) {
    final Rotation rot = sprite.getRotation();
    final double deltaX = x - rot.getX();
    final double deltaY = y - rot.getY();
    final double deltaDegrees = degrees - rot.getDegrees();
    return new Animator() {
      @Override
      protected void onUpdate(double progress) {
        sprite.setRotation(new Rotation(rot.getX() + (deltaX * progress), rot.getY() + (deltaY * progress),
            rot.getDegrees() + (deltaDegrees * progress)));
        sprite.redraw();
      }
    };
  }

  /**
   * Returns an {@link Animator} that will animate the given {@link Sprite} to
   * the given {@link Scaling}.
   * 
   * @param sprite the sprite to be animated
   * @param scaling the target scaling
   * @return the animator
   */
  public static Animator createScalingAnimator(final Sprite sprite, Scaling scaling) {
    if (sprite.getScaling() == null) {
      sprite.setScaling(1);
    }
    final Scaling origin = sprite.getScaling();
    final Scaling delta = new Scaling(scaling.getX() - origin.getX(), scaling.getY() - origin.getY(),
        scaling.getCenterX() - origin.getCenterX(), scaling.getCenterY() - origin.getCenterY());
    return new Animator() {
      @Override
      protected void onUpdate(double progress) {
        sprite.setScaling(new Scaling(origin.getX() + (delta.getX() * progress), origin.getY()
            + (delta.getY() * progress), origin.getCenterX() + (delta.getCenterX() * progress), origin.getCenterY()
            + (delta.getCenterY() * progress)));
        sprite.redraw();
      }
    };
  }

  /**
   * Returns an {@link Animator} that will animate the given {@link Sprite} to
   * the given stroke width.
   * 
   * @param sprite the sprite to be animated
   * @param strokeWidth the target stroke width
   * @return the animator
   */
  public static Animator createStrokeWidthAnimator(final Sprite sprite, double strokeWidth) {
    final double origin;
    if (Double.isNaN(sprite.getStrokeWidth())) {
      origin = 0;
    } else {
      origin = sprite.getStrokeWidth();
    }
    final double deltaWidth = strokeWidth - origin;
    return new Animator() {
      @Override
      protected void onUpdate(double progress) {
        sprite.setStrokeWidth(origin + (deltaWidth * progress));
        sprite.redraw();
      }
    };
  }

  /**
   * Returns an {@link Animator} that will animate the given {@link Sprite} to
   * the given {@link Translation} coordinates.
   * 
   * @param sprite the sprite to be animated
   * @param x the target translation x-coordinate
   * @param y the target translation y-coordinate
   * @return the animator
   */
  public static Animator createTranslationAnimator(final Sprite sprite, double x, double y) {
    final Translation trans = sprite.getTranslation();
    final double deltaX = x - trans.getX();
    final double deltaY = y - trans.getY();
    return new Animator() {
      @Override
      protected void onUpdate(double progress) {
        sprite.setTranslation(trans.getX() + (deltaX * progress), trans.getY() + (deltaY * progress));
        sprite.redraw();
      }
    };
  }

}
