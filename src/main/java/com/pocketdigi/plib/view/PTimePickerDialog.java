package com.pocketdigi.plib.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;

import com.pocketdigi.plib.R;


/**
 * 修复两次调onTimeSet方法bug的TimePickerDialog
 * Created by fhp on 14/11/11.
 */
public class PTimePickerDialog extends AlertDialog
        implements DialogInterface.OnClickListener, TimePicker.OnTimeChangedListener {

    /**
     * The callback interface used to indicate the user is done filling in
     * the time (they clicked on the 'Set' button).
     */
    public interface OnTimeSetListener {

        /**
         * @param view The view associated with this listener.
         * @param hourOfDay The hour that was set.
         * @param minute The minute that was set.
         */
        void onTimeSet(TimePicker view, int hourOfDay, int minute);
    }

    private static final String HOUR = "hour";
    private static final String MINUTE = "minute";
    private static final String IS_24_HOUR = "is24hour";

    private final TimePicker mTimePicker;
    private final OnTimeSetListener mCallback;

    int mInitialHourOfDay;
    int mInitialMinute;
    boolean mIs24HourView;
    //最小时间
    int miniTimeHour=0,miniTimeMinute=0;
    //最大时间
    int maxTimeHour=24,maxTimeMinute=59;
    /**
     * @param context Parent.
     * @param callBack How parent is notified.
     * @param hourOfDay The initial hour.
     * @param minute The initial minute.
     * @param is24HourView Whether this is a 24 hour view, or AM/PM.
     */
    public PTimePickerDialog(Context context,
                             OnTimeSetListener callBack,
                             int hourOfDay, int minute, boolean is24HourView) {
        this(context, 0, callBack, hourOfDay, minute, is24HourView);
    }

    /**
     * @param context Parent.
     * @param theme the theme to apply to this dialog
     * @param callBack How parent is notified.
     * @param hourOfDay The initial hour.
     * @param minute The initial minute.
     * @param is24HourView Whether this is a 24 hour view, or AM/PM.
     */
    public PTimePickerDialog(Context context,
                             int theme,
                             OnTimeSetListener callBack,
                             int hourOfDay, int minute, boolean is24HourView) {
        super(context, theme);
        mCallback = callBack;
        mInitialHourOfDay = hourOfDay;
        mInitialMinute = minute;
        mIs24HourView = is24HourView;

        setIcon(0);
        setTitle(R.string.dialog_timepicker_title);

        Context themeContext = getContext();
        setButton(BUTTON_POSITIVE, getContext().getString(R.string.dialog_timepicker_complete), this);

        LayoutInflater inflater =
                (LayoutInflater) themeContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_time_picker, null);
        setView(view);
        mTimePicker = (TimePicker) view.findViewById(R.id.timePicker);

        // initialize state
        mTimePicker.setIs24HourView(mIs24HourView);
        mTimePicker.setCurrentHour(mInitialHourOfDay);
        mTimePicker.setCurrentMinute(mInitialMinute);
        mTimePicker.setOnTimeChangedListener(this);
    }

    public void onClick(DialogInterface dialog, int which) {
        tryNotifyTimeSet();
    }

    public void updateTime(int hourOfDay, int minutOfHour) {
        mTimePicker.setCurrentHour(hourOfDay);
        mTimePicker.setCurrentMinute(minutOfHour);
    }

    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
        if(hourOfDay<miniTimeHour)
        {
            view.setCurrentHour(miniTimeHour);
        }else if(hourOfDay>maxTimeHour)
        {
            view.setCurrentHour(maxTimeHour);
        }else if(hourOfDay==miniTimeHour)
        {
            if(minute<miniTimeMinute)
                view.setCurrentMinute(miniTimeMinute);
        }else if(hourOfDay==maxTimeHour)
        {
            if(minute>maxTimeMinute)
                view.setCurrentMinute(maxTimeMinute);
        }

    }

    private void tryNotifyTimeSet() {
        if (mCallback != null) {
            mTimePicker.clearFocus();
            mCallback.onTimeSet(mTimePicker, mTimePicker.getCurrentHour(),
                    mTimePicker.getCurrentMinute());
        }
    }

    @Override
    protected void onStop() {
//        tryNotifyTimeSet();
        super.onStop();
    }

    @Override
    public Bundle onSaveInstanceState() {
        Bundle state = super.onSaveInstanceState();
        state.putInt(HOUR, mTimePicker.getCurrentHour());
        state.putInt(MINUTE, mTimePicker.getCurrentMinute());
        state.putBoolean(IS_24_HOUR, mTimePicker.is24HourView());
        return state;
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        int hour = savedInstanceState.getInt(HOUR);
        int minute = savedInstanceState.getInt(MINUTE);
        mTimePicker.setIs24HourView(savedInstanceState.getBoolean(IS_24_HOUR));
        mTimePicker.setCurrentHour(hour);
        mTimePicker.setCurrentMinute(minute);
    }

    public TimePicker getTimePicker() {
        return mTimePicker;
    }

    /**
     * 设置最小时间
     * @param hour 时 0-23
     * @param minute 分 0-59
     */
    public void setMiniTime(int hour,int minute)
    {
        miniTimeHour=hour;
        miniTimeMinute=minute;
    }

    /**
     * 设置最大时间
     * @param hour 时 0-23
     * @param minute 分 0-59
     */
    public void setMaxTime(int hour,int minute)
    {
        maxTimeHour=hour;
        maxTimeMinute=minute;
    }

}
