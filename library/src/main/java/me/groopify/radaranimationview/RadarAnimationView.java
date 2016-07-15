package me.groopify.radaranimationview;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RadarAnimationView extends View {

  private final static int DEFAULT_OUTER_RINGS_SPACING = 25;
  private final static int DEFAULT_INNER_RING_RADIOUS = 40;
  private final static int DEFAULT_NUMBER_OF_RINGS = 4;
  private final static int DEFAULT_TEXT_SIZE = 30;

  private int outerRingsSpacingPx = DEFAULT_OUTER_RINGS_SPACING;
  private int innerRingRadiousPx = DEFAULT_INNER_RING_RADIOUS;
  private int numberOfRings = DEFAULT_NUMBER_OF_RINGS;
  private float textSizePx = DEFAULT_TEXT_SIZE;

  private int viewHeight;
  private int viewWidth;
  private int minDistViewSize;
  private int maxDistViewSize;
  private int traslationX;
  private int traslationY;

  private int animationProgress = 0;

  private Paint radarLineSectionPaint;
  private Paint radarFillSectionPaint;
  private RectF radarShadowBoundsRectF;
  private Paint innerLoadingGradientPaint;
  private RectF innerLoadingViewBoundsRecF;
  private Paint shadowPaint;
  private Paint friendPointPaint;
  public List<RadarPoint> radarPoints;
  private Paint textPaint;
  private boolean shouldDrawView;

  public class RadarPoint {
    public int radius;
    public int angle;
    public boolean isDiscovered = false;

    public RadarPoint(int radius, int angle, boolean isDiscovered) {
      this.radius = radius;
      this.angle = angle;
      this.isDiscovered = isDiscovered;
    }
  }

  public RadarAnimationView(Context context) {
    super(context);
    init(null);
  }

  public RadarAnimationView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(attrs);
  }

  public RadarAnimationView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(attrs);
  }

  public void setAnimationProgress(int animationProgress) {
    this.animationProgress = animationProgress;
  }

  @Override public void onScreenStateChanged(int screenState) {
    super.onScreenStateChanged(screenState);
    if (screenState == SCREEN_STATE_ON) {
      shouldDrawView = true;
    } else if (screenState == SCREEN_STATE_OFF) {
      shouldDrawView = false;
    }
  }

  @Override protected void onWindowVisibilityChanged(int visibility) {
    super.onWindowVisibilityChanged(visibility);
    if (visibility == VISIBLE) {
      shouldDrawView = true;
    } else {
      shouldDrawView = false;
    }
  }

  @Override protected void onVisibilityChanged(View changedView, int visibility) {
    super.onVisibilityChanged(changedView, visibility);
    if (visibility == VISIBLE) {
      shouldDrawView = true;
    } else {
      shouldDrawView = false;
    }
  }

  public void setNumberOfItemsToDiscover(int numberOfItemsToDiscover) {
    radarPoints.clear();
    for (int i = 0; i < numberOfItemsToDiscover; i++) {
      int randomAngle = new Random().nextInt(360);
      int ringPosition = (i % numberOfRings) + 1;
      int y = ringPosition * outerRingsSpacingPx + innerRingRadiousPx;
      radarPoints.add(new RadarPoint(y, randomAngle, false));
    }
  }

  public void beginAnimation(Animator.AnimatorListener listener) {
    ObjectAnimator animation = ObjectAnimator.ofInt(this, "animationProgress", 0, 360);
    animation.setDuration(3000);
    animation.setInterpolator(new AccelerateDecelerateInterpolator());
    animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      @Override public void onAnimationUpdate(ValueAnimator animation) {
        invalidate();
      }
    });
    if (listener != null) {
      animation.addListener(listener);
    }
    animation.start();
  }

  public void setCounterTypeface(Typeface typeface) {
    textPaint.setTypeface(typeface);
  }

  public void setCounterTextSizeDp(int textSize) {
    textSizePx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, textSize,
        getResources().getDisplayMetrics());
    textPaint.setTextSize(textSizePx);
  }

  private void init(AttributeSet attrs) {
    // Disable HW acceleration
    setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    radarPoints = new ArrayList<>();

    outerRingsSpacingPx =
        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_OUTER_RINGS_SPACING,
            getResources().getDisplayMetrics());
    innerRingRadiousPx =
        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_INNER_RING_RADIOUS,
            getResources().getDisplayMetrics());

    radarLineSectionPaint = new Paint();
    radarLineSectionPaint.setStyle(Paint.Style.STROKE);
    radarLineSectionPaint.setStrokeWidth(3);
    radarLineSectionPaint.setColor(Color.GRAY);
    radarLineSectionPaint.setAntiAlias(true);

    friendPointPaint = new Paint();
    friendPointPaint.setStyle(Paint.Style.FILL);
    friendPointPaint.setColor(Color.WHITE);
    friendPointPaint.setShadowLayer(5.0f, 0.0f, 1.0f, Color.DKGRAY);
    friendPointPaint.setAntiAlias(true);

    radarFillSectionPaint = new Paint();
    radarFillSectionPaint.setColor(Color.argb(150, 100, 100, 100));
    radarFillSectionPaint.setAntiAlias(true);
    radarFillSectionPaint.setStyle(Paint.Style.STROKE);
    radarFillSectionPaint.setStrokeWidth(numberOfRings * outerRingsSpacingPx);
    radarFillSectionPaint.setAntiAlias(true);

    innerLoadingGradientPaint = new Paint();
    innerLoadingGradientPaint.setStrokeWidth(10);
    innerLoadingGradientPaint.setAntiAlias(true);
    innerLoadingGradientPaint.setStrokeCap(Paint.Cap.ROUND);
    innerLoadingGradientPaint.setStyle(Paint.Style.STROKE);
    innerLoadingGradientPaint.setAntiAlias(true);

    textPaint = new Paint();
    textPaint.setColor(Color.WHITE);
    textPaint.setTextSize(50);
    textPaint.setAntiAlias(true);

    shadowPaint = new Paint();
    shadowPaint.setStrokeWidth(10);
    shadowPaint.setColor(Color.BLACK);
    shadowPaint.setAntiAlias(true);
    shadowPaint.setStrokeCap(Paint.Cap.ROUND);
    shadowPaint.setStyle(Paint.Style.STROKE);
    shadowPaint.setAntiAlias(true);
    shouldDrawView = true;
  }

  @Override protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);

    this.viewHeight = getMeasuredHeight();
    this.viewWidth = getMeasuredWidth();
    this.minDistViewSize = Math.min(getMeasuredWidth(), getMeasuredHeight());
    this.maxDistViewSize = Math.max(getMeasuredWidth(), getMeasuredHeight());

    if (viewWidth <= viewHeight) {
      this.traslationX = 0;
      this.traslationY = (maxDistViewSize - minDistViewSize) / 2;
    } else {
      this.traslationX = (maxDistViewSize - minDistViewSize) / 2;
      this.traslationY = 0;
    }
    configurePaintParams();
  }

  private void configurePaintParams() {
    int delta = (numberOfRings * outerRingsSpacingPx + innerRingRadiousPx * 2) / 2;
    radarShadowBoundsRectF =
        new RectF(viewWidth / 2 - delta, viewHeight / 2 - delta, viewWidth / 2 + delta,
            viewHeight / 2 + delta);

    innerLoadingViewBoundsRecF =
        new RectF(viewWidth / 2 - innerRingRadiousPx, viewHeight / 2 - innerRingRadiousPx,
            viewWidth / 2 + innerRingRadiousPx, viewHeight / 2 + innerRingRadiousPx);

    int colors[] = {
        Color.parseColor("#b5615e"), Color.parseColor("#b5a15c"), Color.parseColor("#93bb5c"),
        Color.parseColor("#b5615e")
    };
    float[] positions = { 0f, .25f, .50f, .95f };
    innerLoadingGradientPaint.setShader(
        new SweepGradient(viewWidth / 2, viewHeight / 2, colors, positions));

    shadowPaint.setShadowLayer(10.0f, 0.0f, 2.0f, Color.BLACK);

    int colors2[] = { Color.TRANSPARENT, Color.GRAY };
    float[] positions2 = { 0f, .90f, };
    radarFillSectionPaint.setShader(
        new SweepGradient(viewWidth / 2, viewHeight / 2, colors2, positions2));
  }

  @Override protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    if (shouldDrawView) {
      // Center in canvas
      canvas.translate(traslationX, traslationY);
      // Rotate -90 degrees to begin on top
      canvas.rotate(-90, viewWidth / 2, viewHeight / 2);
      drawOuterRings(canvas);
      drawRadarShadow(canvas);
      drawInnerLoadingView(canvas);
      drawFriendsPoints(canvas);
    }
  }

  private void drawInnerLoadingView(Canvas canvas) {
    canvas.drawArc(innerLoadingViewBoundsRecF, 0, animationProgress, false, shadowPaint);
    canvas.drawArc(innerLoadingViewBoundsRecF, 0, animationProgress, false,
        innerLoadingGradientPaint);
  }

  private void drawRadarShadow(Canvas canvas) {
    canvas.rotate(animationProgress - 360, viewWidth / 2, viewHeight / 2);
    canvas.drawArc(radarShadowBoundsRectF, 0, 360, false, radarFillSectionPaint);
    canvas.rotate(-animationProgress + 360, viewWidth / 2, viewHeight / 2);
  }

  private void drawOuterRings(Canvas canvas) {
    for (int i = 1; i <= numberOfRings; i++) {
      canvas.drawCircle(viewWidth / 2, viewHeight / 2,
          innerRingRadiousPx + outerRingsSpacingPx * (i), radarLineSectionPaint);
    }
  }

  private void drawFriendsPoints(Canvas canvas) {
    canvas.translate(viewWidth / 2, viewHeight / 2);
    int discoveredItems = 0;
    for (RadarPoint friendsPoint : radarPoints) {
      if (animationProgress > friendsPoint.angle) {
        friendsPoint.isDiscovered = true;
      }
      if (friendsPoint.isDiscovered) {
        discoveredItems++;
        // rotate 90 degrees to fit initial offset
        canvas.rotate(friendsPoint.angle - 90);
        canvas.drawCircle(0, friendsPoint.radius, 8, friendPointPaint);
        canvas.rotate(-friendsPoint.angle + 90);
      }
    }
    drawNumberOfFriends(canvas, discoveredItems);
    canvas.translate(-viewWidth / 2, -viewHeight / 2);
  }

  private void drawNumberOfFriends(Canvas canvas, int discoveredItems) {

    canvas.rotate(90);
    if (discoveredItems > 0) {
      String discoveredItemsText = discoveredItems < 10 ? "" + discoveredItems : "9+";
      Rect bounds = new Rect();
      textPaint.getTextBounds(discoveredItemsText, 0, 1, bounds);
      canvas.translate(-bounds.width() / 2, bounds.height() / 2);
      canvas.drawText(discoveredItems + "", 0, 0, textPaint);
      canvas.translate(bounds.width() / 2, -bounds.height() / 2);
    }
    canvas.rotate(-90);
  }
}
