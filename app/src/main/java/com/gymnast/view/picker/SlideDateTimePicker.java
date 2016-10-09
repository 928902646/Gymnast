package com.gymnast.view.picker;
import java.util.Date;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
public class SlideDateTimePicker{
    public static final int HOLO_DARK = 1;
    public static final int HOLO_LIGHT = 2;
    private FragmentManager mFragmentManager;
    private SlideDateTimeListener mListener;
    private Date mInitialDate;
    private Date mMinDate;
    private Date mMaxDate;
    private boolean mIsClientSpecified24HourTime;
    private boolean mIs24HourTime;
    private int mTheme;
    private int mIndicatorColor;
    public SlideDateTimePicker(FragmentManager fm){
        // See if there are any DialogFragments from the FragmentManager
        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag(SlideDateTimeDialogFragment.TAG_SLIDE_DATE_TIME_DIALOG_FRAGMENT);
        // Remove if found
        if (prev != null){
            ft.remove(prev);
            ft.commit();
        }
        mFragmentManager = fm;
    }
    public void setListener(SlideDateTimeListener listener)
    {
        mListener = listener;
    }
    public void setInitialDate(Date initialDate)
    {
        mInitialDate = initialDate;
    }
    public void setMinDate(Date minDate)
    {
        mMinDate = minDate;
    }
    public void setMaxDate(Date maxDate)
    {
        mMaxDate = maxDate;
    }
    private void setIsClientSpecified24HourTime(boolean isClientSpecified24HourTime){
        mIsClientSpecified24HourTime = isClientSpecified24HourTime;
    }
    public void setIs24HourTime(boolean is24HourTime){
        setIsClientSpecified24HourTime(true);
        mIs24HourTime = is24HourTime;
    }
    public void setTheme(int theme)
    {
        mTheme = theme;
    }
    public void setIndicatorColor(int indicatorColor)
    {
        mIndicatorColor = indicatorColor;
    }
    public void show(){
        if (mListener == null){
            throw new NullPointerException(
                    "Attempting to bind null listener to SlideDateTimePicker");
        }
        if (mInitialDate == null){
            setInitialDate(new Date());
        }
        SlideDateTimeDialogFragment dialogFragment =
                SlideDateTimeDialogFragment.newInstance(
                        mListener,
                        mInitialDate,
                        mMinDate,
                        mMaxDate,
                        mIsClientSpecified24HourTime,
                        mIs24HourTime,
                        mTheme,
                        mIndicatorColor);
        dialogFragment.show(mFragmentManager,
                SlideDateTimeDialogFragment.TAG_SLIDE_DATE_TIME_DIALOG_FRAGMENT);
    }
    /*
     * The following implements the builder API to simplify
     * creation and display of the dialog.
     */
    public static class Builder{
        // Required
        private FragmentManager fm;
        private SlideDateTimeListener listener;
        // Optional
        private Date initialDate;
        private Date minDate;
        private Date maxDate;
        private boolean isClientSpecified24HourTime;
        private boolean is24HourTime;
        private int theme;
        private int indicatorColor;
        public Builder(FragmentManager fm)
        {
            this.fm = fm;
        }
        /**
         * @see SlideDateTimePicker#setListener(SlideDateTimeListener)
         */
        public Builder setListener(SlideDateTimeListener listener){
            this.listener = listener;
            return this;
        }
        /**
         * @see SlideDateTimePicker#setInitialDate(Date)
         */
        public Builder setInitialDate(Date initialDate){
            this.initialDate = initialDate;
            return this;
        }
        /**
         * @see SlideDateTimePicker#setMinDate(Date)
         */
        public Builder setMinDate(Date minDate){
            this.minDate = minDate;
            return this;
        }
        /**
         * @see SlideDateTimePicker#setMaxDate(Date)
         */
        public Builder setMaxDate(Date maxDate){
            this.maxDate = maxDate;
            return this;
        }
        /**
         * @see SlideDateTimePicker#setIs24HourTime(boolean)
         */
        public Builder setIs24HourTime(boolean is24HourTime){
            this.isClientSpecified24HourTime = true;
            this.is24HourTime = is24HourTime;
            return this;
        }
        /**
         * @see SlideDateTimePicker#setTheme(int)
         */
        public Builder setTheme(int theme) {
            this.theme = theme;
            return this;
        }
        /**
         * @see SlideDateTimePicker#setIndicatorColor(int)
         */
        public Builder setIndicatorColor(int indicatorColor) {
            this.indicatorColor = indicatorColor;
            return this;
        }
        /**
         * <p>Build and return a {@code SlideDateTimePicker} object based on the previously
         * supplied parameters.</p>
         * <p>You should call {@link #show()} immediately after this.</p>
         * @return
         */
        public SlideDateTimePicker build(){
            SlideDateTimePicker picker = new SlideDateTimePicker(fm);
            picker.setListener(listener);
            picker.setInitialDate(initialDate);
            picker.setMinDate(minDate);
            picker.setMaxDate(maxDate);
            picker.setIsClientSpecified24HourTime(isClientSpecified24HourTime);
            picker.setIs24HourTime(is24HourTime);
            picker.setTheme(theme);
            picker.setIndicatorColor(indicatorColor);
            return picker;
        }
    }
}
