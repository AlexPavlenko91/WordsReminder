package com.alex.wordsreminder.services;

import android.content.Context;
import android.text.format.DateUtils;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.alex.wordsreminder.R;
import com.alex.wordsreminder.models.AnswerModel;
import com.alex.wordsreminder.models.UserModel;
import com.alex.wordsreminder.models.WordModel;
import com.alex.wordsreminder.utils.DbHelper;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class AnswerDataService {

    private UserModel currentUser;
    private Context context;
    private DbHelper dbHelper;

    public AnswerDataService(Context context) {
        this.context = context;
        dbHelper = DbHelper.getInstance(context);
        currentUser = UserModel.newInstance();
        if (!dbHelper.getAllUsers().isEmpty()) {
            currentUser = dbHelper.getAllUsers().get(0);
        }
    }

    public static int countRightAnswers(ArrayList<AnswerModel> answers) {
        int counter = 0;
        for (AnswerModel ans : answers) {
            Date d1 = ans.getDateTime();
            if (d1 != null) {
                if (DateUtils.isToday(d1.getTime()) && ans.getIsRight().equals("1")) {
                    counter++;
                }
            }
        }
        return counter;
    }

    public static int countActiveDays(ArrayList<AnswerModel> answers) {
        answers.sort(Comparator.comparing(AnswerModel::getDateTime).reversed());
        LocalDate currentDate = LocalDate.now();
        int counter = 0, dif;
        ArrayList<LocalDate> dates = new ArrayList<>();

        for (AnswerModel ans : answers) {
            if (ans.getIsRight().equals("1")) {
                LocalDate ansDate1 = ans.getDateTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                dates.add(ansDate1);
            }
        }
        /*for (int i = 0; i < answers.size(); i++) {
            AnswerModel ans = answers.get(i);
            if (ans.getIsRight().equals("1")) {
                LocalDate ansDate1 = ans.getDateTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                dates.add(ansDate1);
               */
        /* if (i < answers.size() - 1 && i > 0) {
                    AnswerModel ansOlder = answers.get(i + 1);
                    LocalDate ansDateOlder = ansOlder.getDateTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    if (ansDateOlder.isBefore(ansDate1) && ansOlder.getIsRight().equals("1")) {
                        dif = (int) ChronoUnit.DAYS.between(ansDateOlder, ansDate1);
                        if (dif == 1) {
                            counter++;
                        } else break;
                    }
                } else {
                    if (ansDate1.isEqual(currentDate)) {
                        counter++;
                    }
                }*//*
            }
        }*/
        dates = (ArrayList<LocalDate>) dates.stream().distinct().collect(Collectors.toList());
        for (int i = 0; i < dates.size(); i++) {
            LocalDate date = dates.get(i);
            if (counter == 0 && date.isEqual(currentDate))
                counter++;
            if (i < dates.size() - 1) {
                LocalDate dateOld = dates.get(i + 1);
                dif = (int) ChronoUnit.DAYS.between(dateOld, date);
                if (dif == 1) {
                    counter++;
                } else break;
            }

        }

        return counter;
    }

    public static boolean isAnsweredFor5Hours(ArrayList<AnswerModel> answers, String wordId) {
        List<AnswerModel> theWordAnswers = new ArrayList<>();
        for (AnswerModel a : answers) {
            if (a.getWordId().equals(wordId)) {
                theWordAnswers.add(a);
            }
        }
        boolean res = false;
        for (AnswerModel a : theWordAnswers) {
            try {
                Date d1 = a.getDateTime();
                Date d2 = Calendar.getInstance().getTime();
                long differenceInHours = 0;
                if (d1 != null) {
                    differenceInHours = ((d2.getTime() - d1.getTime()) / (1000 * 60 * 60));
                }
                if (differenceInHours <= 5) {
                    res = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return res;
    }

    public AnswerModel lastAnswerItem(ArrayList<AnswerModel> answers, String wordId) {
        AnswerModel lastAnswerModel = null;
        answers.sort(Comparator.comparing(AnswerModel::getDateTime).reversed());
        for (AnswerModel a : answers) {
            if (a.getWordId().equals(wordId)) {
                lastAnswerModel = a;
                break;
            }
        }
        return lastAnswerModel;
    }


    public String lastAnswerRes(ArrayList<AnswerModel> answers, String wordId) {
        String res = "";
        answers.sort(Comparator.comparing(AnswerModel::getDateTime).reversed());
        for (AnswerModel a : answers) {
            if (a.getWordId().equals(wordId)) {
                res = a.getResult();
                break;
            }
        }
        return res;
    }

    public void onCheckAnsClick(WordModel wordModel, String answerStr,
                                TextView tvDailyProgress, TextView tvActiveDays,
                                ProgressBar progressBarDailyGoal) {
        Date date = new Date();
        int isRightAns = 0;
        if (wordModel.getTranslation().equals(answerStr)) {
            isRightAns = 1;
        }
        AnswerModel answerModel = new AnswerModel(
                "1", currentUser.getId(),
                wordModel.getId(), date, answerStr, String.valueOf(isRightAns)
        );
        dbHelper.addAnswerToDB(answerModel);
        int right_answers = wordModel.getRightAnswers();
        right_answers += isRightAns;
        wordModel.setRightAnswers(right_answers);
        dbHelper.updateWord(wordModel);

        updateProgress(tvDailyProgress, tvActiveDays, progressBarDailyGoal);
    }

    public void updateProgress(TextView tvDailyProgress, TextView tvActiveDays,
                               ProgressBar progressBarDailyGoal) {
        ArrayList<AnswerModel> answers = dbHelper.getAllAnswers();
        int rightAns = countRightAnswers(answers);
        int dailyLoad = currentUser.getDaily_load();
        int dailyProgress = (int) (((float) rightAns / (float) dailyLoad * 100));
        int activeDays = countActiveDays(answers);

        progressBarDailyGoal.setProgress(dailyProgress);
        String tvDailyProgText = rightAns + "/" + dailyLoad;
        tvDailyProgress.setText(tvDailyProgText);
        tvActiveDays.setText(String.valueOf(activeDays));
        if (rightAns >= dailyLoad) {
            tvDailyProgress.setTextColor(ContextCompat.getColor(context, R.color.orange));
        } else tvDailyProgress.setTextColor(ContextCompat.getColor(context, R.color.black));
        if (rightAns > 0) {
            tvActiveDays.setTextColor(ContextCompat.getColor(context, R.color.orange));
        } else tvActiveDays.setTextColor(ContextCompat.getColor(context, R.color.black));
    }
}
