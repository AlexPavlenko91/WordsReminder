package com.alex.wordsreminder.fragments;

import static android.content.Context.ALARM_SERVICE;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.alex.wordsreminder.R;
import com.alex.wordsreminder.activities.MainActivity;
import com.alex.wordsreminder.models.AnswerModel;
import com.alex.wordsreminder.models.UserModel;
import com.alex.wordsreminder.services.AnswerDataService;
import com.alex.wordsreminder.utils.DbHelper;
import com.alex.wordsreminder.utils.NotificationReceiver;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class UserFragment extends Fragment {

    private static ArrayList<UserModel> users = new ArrayList<>();
    public FloatingActionButton fab;
    protected EditText etName, etEmail;
    protected Spinner spinDailyLoad, spinLearningLang, spinMainLang, spinInterfaceLang;
    protected Button btnSave;
    private final TextWatcher textWatcher = new TextWatcher() {

        public void afterTextChanged(Editable s) {
            btnSave.setVisibility(View.VISIBLE);
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
        }
    };
    private TimePicker notificationTimePicker;
    private PendingIntent pendingIntent;
    private ProgressBar progressBarDailyGoal;
    private TextView tvDailyProgress;
    private ToggleButton toggleButtonNotifications;
    private String notificationsTime = "OFF";
    private UserModel currentUser = UserModel.newInstance();


    public UserFragment() {}

    public static UserFragment newInstance() {
        return new UserFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setInitialData();
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.fragment_user, container, false);
        fab = requireActivity().findViewById(R.id.floatingAddButton);
        fab.setVisibility(View.INVISIBLE);
        progressBarDailyGoal = requireActivity().findViewById(R.id.progress_bar_daily_goal);
        tvDailyProgress = requireActivity().findViewById(R.id.tv_daily_goal);
        btnSave = myView.findViewById(R.id.save_profile_changes);
        btnSave.setOnClickListener(this::onSaveClick);
        etName = myView.findViewById(R.id.editTextPersonName);
        etName.addTextChangedListener(textWatcher);
        etEmail = myView.findViewById(R.id.editTextEmailAddress);
        etEmail.addTextChangedListener(textWatcher);
        spinInterfaceLang = myView.findViewById(R.id.spinner_interface_language);
        spinLearningLang = myView.findViewById(R.id.spinner_learning_language);
        spinMainLang = myView.findViewById(R.id.spinner_main_language);
        spinDailyLoad = myView.findViewById(R.id.spinner_daily_load_profile);
        notificationTimePicker = myView.findViewById(R.id.timePicker);
        notificationTimePicker.setIs24HourView(true);
        notificationTimePicker.setOnTimeChangedListener(this::onTimeChange);
        toggleButtonNotifications = myView.findViewById(R.id.toggle_btn_set_time);

        toggleButtonNotifications.setOnClickListener(this::onToggleClicked);

        return myView;
    }


    private void setInitialData() {
        users = DbHelper.getInstance(requireActivity()).getAllUsers();
        if (users.size() > 0) {
            currentUser = users.get(users.size() - 1);
        }
    }

    @Override
    public void onResume() {
        ArrayAdapter<CharSequence> adapterInterfaceLang = ArrayAdapter.createFromResource(requireActivity(),
                R.array.interface_lang_arr, android.R.layout.simple_spinner_dropdown_item);
        spinInterfaceLang.setAdapter(adapterInterfaceLang);

        ArrayAdapter<CharSequence> adapterMainLang = ArrayAdapter.createFromResource(requireActivity(),
                R.array.learn_main_lang_arr, android.R.layout.simple_spinner_dropdown_item);
        spinMainLang.setAdapter(adapterMainLang);

        ArrayAdapter<CharSequence> adapterLearnLang = ArrayAdapter.createFromResource(requireActivity(),
                R.array.learn_main_lang_arr, android.R.layout.simple_spinner_dropdown_item);
        spinLearningLang.setAdapter(adapterLearnLang);

        ArrayAdapter<CharSequence> adapterDailyLoad = ArrayAdapter.createFromResource(requireActivity(),
                R.array.daily_load_arr, android.R.layout.simple_spinner_dropdown_item);
        spinDailyLoad.setAdapter(adapterDailyLoad);


        if (users.size() > 0) {
            etName.setText(currentUser.getUser_name());
            etEmail.setText(currentUser.getEmail());
            if (!currentUser.getNotificationsTime().equals("OFF")) {
                toggleButtonNotifications.setChecked(true);

                String[] subs = currentUser.getNotificationsTime().split(":");
                int hours = Integer.parseInt(subs[0]);
                int minutes = Integer.parseInt(subs[1]);
                notificationTimePicker.setHour(hours);
                notificationTimePicker.setMinute(minutes);
            }

            int positionInterfaceLang = adapterInterfaceLang.getPosition(currentUser.getInterfaceLanguage());
            int positionLearnLang = adapterLearnLang.getPosition(currentUser.getLearning_language());
            int positionMainLang = adapterLearnLang.getPosition(currentUser.getMain_language());
            int positionDailyLoad = adapterDailyLoad.getPosition(String.valueOf(currentUser.getDaily_load()));
            spinInterfaceLang.setSelection(positionInterfaceLang);
            spinLearningLang.setSelection(positionLearnLang);
            spinMainLang.setSelection(positionMainLang);
            spinDailyLoad.setSelection(positionDailyLoad);

            spinInterfaceLang.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                    String selectedLang = spinInterfaceLang.getSelectedItem().toString();
                    String lang = currentUser.getInterfaceLanguage();
                    if (!lang.equals(selectedLang)) {
                        btnSave.setVisibility(View.VISIBLE);

                    } else btnSave.setVisibility(View.INVISIBLE);
                }
                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    btnSave.setVisibility(View.INVISIBLE);
                }

            });
            spinLearningLang.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    String selectedLang = spinLearningLang.getSelectedItem().toString();
                    String lang = currentUser.getLearning_language();
                    if (!lang.equals(selectedLang)) {
                        btnSave.setVisibility(View.VISIBLE);
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    btnSave.setVisibility(View.INVISIBLE);
                }

            });
            spinMainLang.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    String selectedLang = spinMainLang.getSelectedItem().toString();
                    String lang = currentUser.getMain_language();
                    if (!lang.equals(selectedLang)) {
                        btnSave.setVisibility(View.VISIBLE);
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // your code here
                    btnSave.setVisibility(View.INVISIBLE);
                }
            });
            spinDailyLoad.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    String selectedLoad = spinDailyLoad.getSelectedItem().toString();
                    String load = String.valueOf(currentUser.getDaily_load());
                    if (!load.equals(selectedLoad)) {
                        btnSave.setVisibility(View.VISIBLE);
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // your code here
                    btnSave.setVisibility(View.INVISIBLE);
                }

            });
        }
        //save_btn.setVisibility(View.INVISIBLE);
        super.onResume();
    }

    public void setLocale(String langInterface) {
        String languageCode = "en";
        switch (langInterface) {
            case "English":
                languageCode = "en";
                break;
            case "Українська":
                languageCode = "uk";
                break;
        }
        Resources resources = requireActivity().getResources();
        Configuration config = resources.getConfiguration();
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        config.setLocale(locale);

        resources.updateConfiguration(config, resources.getDisplayMetrics());
        startActivity(new Intent(requireActivity(), MainActivity.class));
        requireActivity().finish();

    }

    public void updateData(UserModel user) {
        boolean insertData = DbHelper.getInstance(requireActivity()).updateUser(user);

        if (insertData) {
            Toast.makeText(getActivity(),
                    "Data Successfully Updated!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getActivity(), "Something went wrong :(.", Toast.LENGTH_LONG).show();
        }
    }

    public void addData() {
        currentUser.setUser_name(etName.getText().toString());
        currentUser.setEmail(etEmail.getText().toString());
        currentUser.setInterfaceLanguage(spinInterfaceLang.getSelectedItem().toString());
        currentUser.setMain_language(spinMainLang.getSelectedItem().toString());
        currentUser.setLearning_language(spinLearningLang.getSelectedItem().toString());
        currentUser.setDaily_load(Integer.parseInt(spinDailyLoad.getSelectedItem().toString()));
        currentUser.setDateCreated(Calendar.getInstance().getTime().toString());
        currentUser.setNotificationsTime(notificationsTime);
        boolean insertData = DbHelper.getInstance(requireActivity()).addUserToDB(currentUser);
        if (insertData) {
            Toast.makeText(getActivity(),
                    "Data Successfully Inserted!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getActivity(), "Something went wrong :(.", Toast.LENGTH_LONG).show();
        }
    }

    public boolean check_fields() {
        return !etName.getText().toString().trim().equals("") &&
                !etEmail.getText().toString().trim().equals("");
    }

    public void onSaveClick(View v) {
        boolean all_fields_filled = check_fields();
        if (users.size() == 0 && all_fields_filled) {
            addData();
            users = DbHelper.getInstance(requireActivity()).getAllUsers();
            btnSave.setVisibility(View.INVISIBLE);
            updateAppBar();
        } else if (all_fields_filled) {
            String langInterface = spinInterfaceLang.getSelectedItem().toString();
            if (!langInterface.equals(currentUser.getInterfaceLanguage())) {
                setLocale(langInterface);
            }
            if (!currentUser.getNotificationsTime().equals(notificationsTime)) {
                setNotifications();
            }
            currentUser.setUser_name(etName.getText().toString());
            currentUser.setEmail(etEmail.getText().toString());
            currentUser.setInterfaceLanguage(langInterface);
            currentUser.setMain_language(spinMainLang.getSelectedItem().toString());
            currentUser.setLearning_language(spinLearningLang.getSelectedItem().toString());
            int dailyLoad = Integer.parseInt(spinDailyLoad.getSelectedItem().toString());
            currentUser.setDaily_load(dailyLoad);
            currentUser.setNotificationsTime(notificationsTime);
            updateData(currentUser);
            updateAppBar();
            users = DbHelper.getInstance(requireActivity()).getAllUsers();
            btnSave.setVisibility(View.INVISIBLE);
        } else {
            Toast.makeText(getActivity(), "Please fill all fields at first", Toast.LENGTH_LONG).show();
        }
    }

    public void updateAppBar() {
        ArrayList<AnswerModel> answers = DbHelper.getInstance(getContext()).getAllAnswers();
        int rightAns = AnswerDataService.rightAnsForToday(answers);
        int dailyLoad = currentUser.getDaily_load();
        int dailyProgress = (int) (((float) rightAns / (float) dailyLoad) * 100);
        progressBarDailyGoal.setProgress(dailyProgress);
        String tvDailyProgText = rightAns + "/" + dailyLoad;
        tvDailyProgress.setText(tvDailyProgText);
        if (rightAns >= dailyLoad) {
            tvDailyProgress.setTextColor(ContextCompat.getColor(requireContext(), R.color.orange));
        } else tvDailyProgress.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
    }

    public void setNotifications() {
        AlarmManager alarmManager = (AlarmManager) requireActivity().getSystemService(ALARM_SERVICE);
        long time;
        Intent intent = new Intent(requireActivity(), NotificationReceiver.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getBroadcast(requireActivity(), 0, intent, PendingIntent.FLAG_MUTABLE);
        }
        if (toggleButtonNotifications.isChecked()) {
            int hour = notificationTimePicker.getHour();
            int minute = notificationTimePicker.getMinute();
            notificationsTime = hour + ":" + minute;
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(SystemClock.currentThreadTimeMillis());
            calendar.set(Calendar.HOUR, hour);
            calendar.set(Calendar.MINUTE, minute);
            time = calendar.getTimeInMillis();
            //Toast.makeText(requireActivity(), "NOTIFICATIONS ON", Toast.LENGTH_SHORT).show();
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time, AlarmManager.INTERVAL_DAY, pendingIntent);
        } else {
            alarmManager.cancel(pendingIntent);
            notificationsTime = "OFF";
            //Toast.makeText(requireActivity(), "NOTIFICATIONS OFF", Toast.LENGTH_SHORT).show();
        }
    }

    private void onTimeChange(TimePicker timePicker, int i, int i1) {
        notificationsTime = i + ":" + i1;
        if (!currentUser.getNotificationsTime().equals(notificationsTime)) {
            btnSave.setVisibility(View.VISIBLE);
        } else btnSave.setVisibility(View.INVISIBLE);
    }

    public void onToggleClicked(View view) {
        setNotifications();
        if (!currentUser.getNotificationsTime().equals(notificationsTime)) {
            btnSave.setVisibility(View.VISIBLE);
        } else btnSave.setVisibility(View.INVISIBLE);
    }
}