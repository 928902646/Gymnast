package com.gymnast.view.picker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.gymnast.R;
import com.gymnast.view.ImmersiveActivity;
import com.gymnast.view.dialog.PicSelectDialogUtils;
import com.gymnast.view.widget.photoview.HackyViewPager;
import com.gymnast.view.widget.photoview.PhotoView;
import com.gymnast.view.widget.photoview.PhotoViewAttacher;
import com.nostra13.universalimageloader.core.ImageLoader;
import java.util.List;
/**
 * 大图片预览
 * 用法：传递参数images字符串数组和position
 * @author summer
 * @description
 * @date 2014年5月7日 下午3:38:11
 */
public class BigImageActivity extends ImmersiveActivity {
    private static final String STATE_POSITION = "STATE_POSITION";
    HackyViewPager mPager;
    TextView mTextView;
    Context mContext;
    private int pagerPosition; //当前选择位置
    private String[] imageUrls; //图片地址数组
    ProgressBar mProgressBar;//图片加载进度 
    private Bitmap defBitmap, failBitmap;//默认图片，失败图片
    private List<Bitmap> imageBitmaps;
    private boolean mIsUrl;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout frameLayout = new FrameLayout(this);
        mPager = new HackyViewPager(this);
        mTextView = new TextView(this);
        mTextView.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        mTextView.setPadding(5, 5, 5, 5);
        mTextView.setTextColor(0xffffffff);
        frameLayout.addView(mPager);
        frameLayout.addView(mTextView);
        setContentView(frameLayout);
        mContext = this;
        Bundle bundle = getIntent().getBundleExtra("data");
        assert bundle != null;
        imageUrls = bundle.getStringArray("images");
        imageBitmaps = PicSelectDialogUtils.BITMAPS;
        pagerPosition = bundle.getInt("position", 0);
        mIsUrl = bundle.getBoolean("isUrl");
        //图片加载工具类配置存放位置
        defBitmap = failBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        if (savedInstanceState != null) {
            pagerPosition = savedInstanceState.getInt(STATE_POSITION);
        }
        if (mIsUrl) {
            mPager.setAdapter(new ImagePagerAdapter(imageUrls));
        } else if (!mIsUrl && imageBitmaps != null) {
            mPager.setAdapter(new ImagePagerAdapter(imageBitmaps));
        }
        mPager.setCurrentItem(pagerPosition);
        mPager.setOnPageChangeListener(mPageChangeListener);
        updateShowNum();
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_POSITION, mPager.getCurrentItem());
    }
    //显示页码
    private void updateShowNum() {
        if (mIsUrl) {
            if (imageUrls.length > 0) {
                mTextView.setText(String.format("%d/%d", pagerPosition + 1, imageUrls.length));
            }
        } else if (!mIsUrl && imageBitmaps != null) {
            if (imageBitmaps.size() > 0) {
                mTextView.setText(String.format("%d/%d", pagerPosition + 1, imageBitmaps.size()));
            }
        }
    }
    ViewPager.OnPageChangeListener mPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }
        @Override
        public void onPageScrollStateChanged(int state) {
        }
        @Override
        public void onPageSelected(int position) {
            pagerPosition = position;
            updateShowNum();
        }
    };
    //图片适配器
    private class ImagePagerAdapter extends PagerAdapter {
        private String[] images;
        private List<Bitmap> imageBitmaps;
        private LayoutInflater inflater;
        ImagePagerAdapter(String[] images) {
            this.images = images;
            inflater = getLayoutInflater();
        }
        public ImagePagerAdapter(List<Bitmap> imageBitmaps) {
            this.imageBitmaps = imageBitmaps;
            inflater = getLayoutInflater();
        }
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
        @Override
        public int getCount() {
            if (images != null)
                return images.length;
            else if (imageBitmaps != null)
                return imageBitmaps.size();
            else
                return 0;
        }
        @Override
        public Object instantiateItem(ViewGroup view, int position) {
            View imageLayout = inflater.inflate(R.layout.item_pager_image, view, false);
            assert imageLayout != null;
            PhotoView imageView = (PhotoView) imageLayout.findViewById(R.id.image);
        /*	mProgressBar = (ProgressBar) imageLayout.findViewById(R.id.loading);
            mProgressBar.setVisibility(View.GONE);*/
            if (mIsUrl) {
                ImageLoader.getInstance().displayImage(images[position], imageView);
            } else if (!mIsUrl && imageBitmaps != null) {
                imageView.setImageBitmap(imageBitmaps.get(position));
            }
            //单击退出
            imageView.setOnPhotoTapListener(onPhotoTapListener);
            view.addView(imageLayout, 0);
            return imageLayout;
        }
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }
        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
        }
        @Override
        public Parcelable saveState() {
            return null;
        }
    }
    //单击事件处理
    private PhotoViewAttacher.OnPhotoTapListener onPhotoTapListener = new PhotoViewAttacher.OnPhotoTapListener() {

        @Override
        public void onPhotoTap(View view, float x, float y) {
            finish();
        }
    };
    @Override
    protected void onDestroy() {
        super.onDestroy();
        PicSelectDialogUtils.BITMAPS = null;
    }
}
