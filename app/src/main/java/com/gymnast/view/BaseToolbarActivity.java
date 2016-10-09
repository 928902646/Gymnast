package com.gymnast.view;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import butterknife.BindView;
import com.gymnast.R;
import com.makeramen.roundedimageview.RoundedImageView;
import java.util.ArrayList;
public abstract class BaseToolbarActivity extends BaseActivity {
  @BindView(R.id.toolbar) protected Toolbar mToolbar;
  @BindView(R.id.toolbar_title) protected TextView mToolbarTitle;
  @BindView(R.id.toolbar_head) protected RoundedImageView toolbarHead;
  @BindView(R.id.app_bar_layout) protected AppBarLayout mAppBarLayout;
  protected ActionBarHelper mActionBarHelper;
  /**
   * Initialize the toolbar in the layout
   * @param savedInstanceState savedInstanceState
   */
  @Override protected void initToolbar(Bundle savedInstanceState) {
    this.initToolbarHelper();
  }
  /**
   * init the toolbar
   */
  protected void initToolbarHelper() {
    if (this.mToolbar == null || this.mAppBarLayout == null) return;
    this.setSupportActionBar(this.mToolbar);
    this.mActionBarHelper = this.createActionBarHelper();
    this.mActionBarHelper.init();
    if (Build.VERSION.SDK_INT >= 21) {
      this.mAppBarLayout.setElevation(6.6f);
    }
  }
  /**
   * @param item The menu item that was selected.
   * @return boolean Return false to allow normal menu processing to
   * proceed, true to consume it here.
   * @see #onCreateOptionsMenu
   */
  @Override public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      this.onBackPressed();
      return true;
    } else {
      return super.onOptionsItemSelected(item);
    }
  }
  protected void showBack() {
    if (this.mActionBarHelper != null) this.mActionBarHelper.setDisplayHomeAsUpEnabled(true);
  }
  public Toolbar getToolbar() {
    return mToolbar;
  }
  public void setTitle(String title) {
    if (mToolbarTitle != null) {
      mToolbarTitle.setText(title);
    }
  }
  public void setTitle(int title) {
    if (mToolbarTitle != null) {
      mToolbarTitle.setText(title);
    }
  }
  @Override protected void onTitleChanged(CharSequence title, int color) {
    super.onTitleChanged(title, color);
    if (this.mToolbar != null) {
      this.mToolbar.setTitle("");
    }
    if (mToolbarTitle != null) {
      mToolbarTitle.setText(title);
      //mToolbarTitle.setTextColor(color);
    }
  }
  /**
   * set the AppBarLayout alpha
   * @param alpha alpha
   */
  protected void setAppBarLayoutAlpha(float alpha) {
    this.mAppBarLayout.setAlpha(alpha);
  }
  /**
   * set the AppBarLayout visibility
   * @param visibility visibility
   */
  protected void setAppBarLayoutVisibility(boolean visibility) {
    if (visibility) {
      this.mAppBarLayout.setVisibility(View.VISIBLE);
    } else {
      this.mAppBarLayout.setVisibility(View.GONE);
    }
  }
  public View getToolbarLogoIcon(Toolbar toolbar) {
    //check if contentDescription previously was set
    boolean hadContentDescription = android.text.TextUtils.isEmpty(toolbar.getLogoDescription());
    String contentDescription = String.valueOf(
        !hadContentDescription ? toolbar.getLogoDescription() : "logoContentDescription");
    toolbar.setLogoDescription(contentDescription);
    ArrayList<View> potentialViews = new ArrayList<View>();
    //find the view based on it's content description, set programatically or with android:contentDescription
    toolbar.findViewsWithText(potentialViews, contentDescription,
        View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION);
    //Nav icon is always instantiated at this point because calling setLogoDescription ensures its existence
    View logoIcon = null;
    if (potentialViews.size() > 0) {
      logoIcon = potentialViews.get(0);
    }
    //Clear content description if not previously present
    if (hadContentDescription) {
      toolbar.setLogoDescription(null);
    }
    return logoIcon;
  }
  /**
   * Create a compatible helper that will manipulate the action bar if available.
   */
  public ActionBarHelper createActionBarHelper() {
    return new ActionBarHelper();
  }
  public class ActionBarHelper {
    private final ActionBar mActionBar;
    public CharSequence mDrawerTitle;
    public CharSequence mTitle;
    public ActionBarHelper() {
      this.mActionBar = getSupportActionBar();
    }
    public void init() {
      if (this.mActionBar == null) return;
      this.mActionBar.setDisplayHomeAsUpEnabled(true);
      this.mActionBar.setDisplayShowHomeEnabled(false);
      this.mTitle = mDrawerTitle = getTitle();
    }
    public void onDrawerClosed() {
      if (this.mActionBar == null) return;
      //this.mActionBar.setTitle(this.mTitle);
    }
    public void onDrawerOpened() {
      if (this.mActionBar == null) return;
      //this.mActionBar.setTitle(this.mDrawerTitle);
    }
    public void setTitle(CharSequence title) {
      this.mTitle = title;
    }
    public void setDrawerTitle(CharSequence drawerTitle) {
      this.mDrawerTitle = drawerTitle;
    }
    public void setDisplayHomeAsUpEnabled(boolean showHomeAsUp) {
      if (this.mActionBar == null) return;
      this.mActionBar.setDisplayHomeAsUpEnabled(showHomeAsUp);
    }
  }

}
