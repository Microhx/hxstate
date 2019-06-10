package com.xing.viewstate;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.widget.FrameLayout;
import androidx.annotation.IdRes;
import androidx.annotation.IntDef;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * author: Java
 *
 * 纵然万劫不复，纵然相思入骨，我依然待你眉眼如初，岁月如故。
 *
 * date : 2019/6/4
 *
 * version : 1.0.1
 *
 * desc :
 */
public class HxStateView extends FrameLayout {
  private static final String TAG = HxStateView.class.getSimpleName();

  public static final int LOADING = 0;
  public static final int NO_DATA = 1;
  public static final int ERROR = 2;
  public static final int SUCCESS = 3;

  @IntDef({ LOADING, NO_DATA, ERROR, SUCCESS })
  @Retention(RetentionPolicy.SOURCE) @interface Status {}

  /**
   * 保底默认配置 view配置
   */
  private View mEmptyView;
  private View mLoadingView;
  private View mContentView;
  private View mErrorView;

  /**
   * 全局的view 配置
   */
  private static int mGlobalEmptyViewId;
  private static int mGlobalLoadingViewId;
  private static int mGlobalErrorVieId;

  /**
   * 自定义View 配置
   */
  private int mCustomEmptyViewId;
  private int mCustomLoadingViewId;
  private int mCustomErrorViewId;

  /**
   * 重试布局Id
   */
  private int mRetryResId ;

  /**
   * 重试监听器
   */
  private OnLayoutRetryListener mRetryListener;


  public HxStateView(@NonNull Context context) {
    this(context, null);
  }

  public HxStateView(@NonNull Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public HxStateView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    initAttrs(context, attrs);
    initViews();
  }

  private void initAttrs(Context context, AttributeSet attrs) {
    TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.HxStateView);
    mCustomEmptyViewId = array.getResourceId(R.styleable.HxStateView_hx_empty_layout_id, 0);
    mCustomErrorViewId = array.getResourceId(R.styleable.HxStateView_hx_error_layout_id, 0);
    mCustomLoadingViewId = array.getResourceId(R.styleable.HxStateView_hx_loading_layout_id, 0);
    array.recycle();
  }

  private void initViews() {
    View.inflate(getContext(), R.layout.hx_state_view_layout, this);
  }

  public void checkEmptyView() {
    if (null == mEmptyView) {
      ViewStub viewStub = findViewById(R.id.id_view_stub_empty);
      if (mCustomEmptyViewId > 0) {
        viewStub.setLayoutResource(mCustomEmptyViewId);
      } else if (mGlobalEmptyViewId > 0) {
        viewStub.setLayoutResource(mGlobalEmptyViewId);
      }

      mEmptyView = viewStub.inflate();
    }
  }

  private void checkLoadingView() {
    if (null == mLoadingView) {
      ViewStub viewStub = findViewById(R.id.id_view_stub_loading);
      if (mCustomLoadingViewId > 0) {
        viewStub.setLayoutResource(mCustomLoadingViewId);
      } else if (mGlobalLoadingViewId > 0) {
        viewStub.setLayoutResource(mGlobalLoadingViewId);
      }

      mLoadingView = viewStub.inflate();
    }

  }

  private void checkErrorView() {
    if (null == mErrorView) {
      ViewStub viewStub = findViewById(R.id.id_view_stub_error);
      if (mCustomErrorViewId > 0) {
        viewStub.setLayoutResource(mCustomErrorViewId);
      } else if (mGlobalErrorVieId > 0) {
        viewStub.setLayoutResource(mGlobalErrorVieId);
      }
      mErrorView = viewStub.inflate();
      registerRetryListener();
    }
  }


  public View getContentView() {
    if (null == mContentView) {
      Log.e(TAG, "contentView is null");
    }

    return mContentView;
  }

  public void addContentView(@LayoutRes int layoutId) {
    mContentView = View.inflate(getContext(), layoutId, null);
    addView(mContentView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    showContentLayout();
  }

  public void showEmpty() {
    hideAllViews(mLoadingView, mErrorView, mContentView);
    checkEmptyView();
    mEmptyView.setVisibility(VISIBLE);
  }

  public void showError() {
    hideAllViews(mEmptyView, mLoadingView, mContentView);
    checkErrorView();
    mErrorView.setVisibility(VISIBLE);
  }


  private void registerRetryListener() {
    if(null == mErrorView || mRetryResId <= 0) {
      return;
    }

    View targetView = mErrorView.findViewById(mRetryResId);
    if(null == targetView) {
      Log.w(TAG,"please check the register id : " + mRetryResId +
          ", the id does not exist in " + mErrorView);
      return;
    }

    targetView.setOnClickListener(new OnClickListener() {
      @Override public void onClick(View v) {
        if(null != mRetryListener) mRetryListener.onRetry();
      }
    });
  }

  /**
   * 注册重试Id
   */
  public void registerRetryId(@IdRes int registerId, OnLayoutRetryListener listener) {
        this.mRetryResId = registerId;
        this.mRetryListener = listener;
  }


  public void showLoading() {
    hideAllViews(mEmptyView, mErrorView, mContentView);
    checkLoadingView();
    mLoadingView.setVisibility(VISIBLE);
  }


  public void showContentLayout() {
    if (null == mContentView) {
      Log.e(TAG, "content layout is null, please check it again.");
      return;
    }

    hideAllViews(mEmptyView, mLoadingView, mErrorView);

    if (null != mContentView && mContentView.getVisibility() != VISIBLE) {
      mContentView.setVisibility(VISIBLE);
    }
  }

  private void hideAllViews(View... views) {
    for (View v : views) {
      if (null != v && v.getVisibility() != GONE) {
        v.setVisibility(GONE);
      }
    }
  }

  /**
   * 全局构建者 统一模式构建
   */
  public static class Builder {

    //加载布局Id
    private int mLoadingLayoutId;

    //为空时全局加载Id
    private int mEmptyLayoutId;

    // 错误时全局加载Id
    private int mErrorLayoutId;

    public Builder setLoadingLayoutId(int loadingLayoutId) {
      this.mLoadingLayoutId = loadingLayoutId;
      return this;
    }

    public Builder setEmptyLayoutId(int emptyLayoutId) {
      this.mEmptyLayoutId = emptyLayoutId;
      return this;
    }

    public Builder setErrorLayoutId(int errorLayoutId) {
      this.mErrorLayoutId = errorLayoutId;
      return this;
    }

    public void build() {
      mGlobalLoadingViewId = mLoadingLayoutId;
      mGlobalEmptyViewId = mEmptyLayoutId;
      mGlobalErrorVieId = mErrorLayoutId;
    }
  }

}
