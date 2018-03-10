package com.koresuniku.poyehavshyjbudilnik

import android.app.DatePickerDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.widget.DatePicker
import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.text.format.Time
import android.widget.Button
import android.text.format.Time.getCurrentTimezone
import android.widget.TimePicker
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent




class MainActivity : AppCompatActivity(),
        DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener {

    private lateinit var mSetTimeButton: Button
    private lateinit var mSetAlarmButton: Button

    private var mAlarmTime: Time = Time()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mSetTimeButton = findViewById(R.id.set_time_button)
        mSetAlarmButton = findViewById(R.id.set_alarm_button)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.title = Html.fromHtml(
                "<font color='#000000'>${getString(R.string.app_name)}</font>")
    }

    override fun onResume() {
        super.onResume()
        val prefs = getSharedPreferences(getString(R.string.narabotu), Context.MODE_PRIVATE)
        if (prefs.getBoolean(getString(R.string.is_alarm_set_key), false)) {
            val rawString = prefs.getString(getString(R.string.alarm_string_key), "")
            val array = rawString.split(Regex("[^0-9]+"))
            mAlarmTime.monthDay = array[0].toInt()
            mAlarmTime.month = array[1].toInt()
            mAlarmTime.year = array[2].toInt()
            mAlarmTime.hour = array[3].toInt()
            mAlarmTime.minute = array[4].toInt()

            mSetTimeButton.text = rawString
            mSetAlarmButton.text = getString(R.string.reset_text)

            mSetAlarmButton.isEnabled = true
            mSetTimeButton.isEnabled = false
            mSetAlarmButton.setOnClickListener {
                it.isEnabled = false
                mSetTimeButton.isEnabled = true
                mSetTimeButton.text = getString(R.string.set_time_text)
                mSetAlarmButton.text = getString(R.string.set_text)
                // cancel alarm
                cancelAlarm()

                mSetAlarmButton.isEnabled = false
                mSetTimeButton.setOnClickListener {
                    val today = Time(getCurrentTimezone())
                    today.setToNow()
                    val datePickerDialog = DatePickerDialog(
                            this, this@MainActivity, today.year, today.month, today.monthDay)
                    datePickerDialog.show()
                }
            }
        } else {
            mSetTimeButton.text = getString(R.string.set_time_text)
            mSetAlarmButton.text = getString(R.string.set_text)

            mSetTimeButton.isEnabled = true
            mSetAlarmButton.isEnabled = false
            mSetTimeButton.setOnClickListener {
                val today = Time(getCurrentTimezone())
                today.setToNow()
                val datePickerDialog = DatePickerDialog(
                        this, this@MainActivity, today.year, today.month, today.monthDay)
                datePickerDialog.show()
            }

        }
    }

    override fun onDateSet(p0: DatePicker?, year: Int, month: Int, day: Int) {
        mAlarmTime.year = year
        mAlarmTime.month = month
        mAlarmTime.monthDay = day

        val today = Time(getCurrentTimezone())
        today.setToNow()
        val timePickerDialog = TimePickerDialog(
                this, this, today.hour, today.minute, true)
        timePickerDialog.setOnCancelListener { mAlarmTime = Time() }
        timePickerDialog.show()
    }

    override fun onTimeSet(view: TimePicker?, hour: Int, minutes: Int) {
        mAlarmTime.hour = hour
        mAlarmTime.minute = minutes

        mSetTimeButton.isEnabled = false
        mSetAlarmButton.isEnabled = true
        writeInTimeButton()

        mSetAlarmButton.setOnClickListener {

            // set alarm
            setAlarm()
            mSetAlarmButton.text = getString(R.string.reset_text)

            mSetAlarmButton.setOnClickListener {
                it.isEnabled = false
                mSetTimeButton.isEnabled = true
                mSetTimeButton.text = getString(R.string.set_time_text)
                mSetAlarmButton.text = getString(R.string.set_text)
                // cancel alarm
                cancelAlarm()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun writeInTimeButton() {
        mSetTimeButton.text = getButtonString()
    }

    private fun getButtonString(): String {
        return "${mAlarmTime.monthDay}." +
                "${mAlarmTime.month + 1}." +
                "${mAlarmTime.year} " +
                "${if (mAlarmTime.hour > 9) mAlarmTime.hour else "0" + mAlarmTime.hour}:" +
                "${if (mAlarmTime.minute > 9) mAlarmTime.minute else "0" + mAlarmTime.minute}"
    }

    private fun setAlarm() {
        cancelAlarm()

        val myIntent = Intent(applicationContext, PoehavshyjReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
                applicationContext, 1, myIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarmManager.set(AlarmManager.RTC_WAKEUP, mAlarmTime.toMillis(true), pendingIntent)

        val prefs = getSharedPreferences(getString(R.string.narabotu), Context.MODE_PRIVATE)
        prefs.edit().putBoolean(getString(R.string.is_alarm_set_key), true).apply()
        prefs.edit().putString(getString(R.string.alarm_string_key), getButtonString()).apply()
    }

    private fun cancelAlarm() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val myIntent = Intent(applicationContext, PoehavshyjReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
                applicationContext, 1, myIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        alarmManager.cancel(pendingIntent)

        val prefs = getSharedPreferences(getString(R.string.narabotu), Context.MODE_PRIVATE)
        prefs.edit().putBoolean(getString(R.string.is_alarm_set_key), false).apply()
        prefs.edit().putString(getString(R.string.alarm_string_key), "").apply()
    }
}
