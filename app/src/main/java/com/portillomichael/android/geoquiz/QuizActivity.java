package com.portillomichael.android.geoquiz;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import com.portillomichael.android.geoquiz.R;

public class QuizActivity extends AppCompatActivity {

    private static final String TAG = "QuizActivity";
    private static final String KEY_INDEX = "index";
    private static final String KEY_QUESTION_COUNTER = "question_counter";
    private static final String KEY_ARE_ANSWERED_ARRAY = "are_answered_array";
    private static final String KEY_USER_CHEATED_ARRAY = "user_cheated_array";
    private static final String KEY_CHEATER_COUNTER = "cheater_counter";
    private static final int REQUEST_CODE_CHEAT = 0;

    private Button mTrueButton;
    private Button mFalseButton;
    private Button mCheatButton;
    private ImageButton mNextButton;
    private ImageButton mPrevButton;
    private TextView mQuestionTextView;
    private TextView mPercentGrade;
    private TextView mCheaterCounterTextView;

    private Question[] mQuestionBank = new Question[]{
            new Question(R.string.question_australia, true),
            new Question(R.string.question_oceans, true),
            new Question(R.string.question_mideast, false),
            new Question(R.string.question_africa, false),
            new Question(R.string.question_americas, true),
            new Question(R.string.question_asia, true),
    };

    private int mCurrentIndex = 0;
    private int[] mAreAnswered = new int[mQuestionBank.length];
    //int[] '0' = not answered '1' = answered correctly, '2' = answered incorrectly
    private int mFinalQuestionCounter = 0;
    private int mCheaterCounter = 0;
    private boolean[] mDidUserCheatArray = new boolean[mQuestionBank.length];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate(Bundle) called");
        setContentView(R.layout.activity_quiz);

        if (savedInstanceState != null){
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
            mFinalQuestionCounter = savedInstanceState.getInt(KEY_QUESTION_COUNTER, 0);
            mAreAnswered = savedInstanceState.getIntArray(KEY_ARE_ANSWERED_ARRAY);
            mDidUserCheatArray = savedInstanceState.getBooleanArray(KEY_USER_CHEATED_ARRAY);
            mCheaterCounter = savedInstanceState.getInt(KEY_CHEATER_COUNTER, 0);
        }

        mQuestionTextView = (TextView) findViewById(R.id.question_text_view);
        mCheaterCounterTextView = (TextView) findViewById(R.id.cheater_counter_text_view);


        mTrueButton = (Button) findViewById(R.id.true_buton);
        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               checkAnswer(true);

            }
        });
        mFalseButton = (Button) findViewById(R.id.false_button);
        mFalseButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                checkAnswer(false);

            }

        });

        mCheatButton = (Button) findViewById(R.id.cheat_button);
        mCheatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
                Intent intent = CheatActivity.newIntent(QuizActivity.this, answerIsTrue);
                startActivityForResult(intent, REQUEST_CODE_CHEAT);
            }
        });

        mNextButton = (ImageButton) findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(mCurrentIndex < (mQuestionBank.length-1)){
                    mCurrentIndex = (mCurrentIndex + 1);}
                updateQuestion();
                setButtons();
            }
        });

        mPrevButton = (ImageButton) findViewById(R.id.prev_button);
        mPrevButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if (mCurrentIndex > 0){
                    mCurrentIndex = (mCurrentIndex - 1) % mQuestionBank.length;}
                updateQuestion();
                setButtons();
            }
        });

        updateQuestion();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode != Activity.RESULT_OK){
            return;
        }
        if(requestCode == REQUEST_CODE_CHEAT){
            if(data == null){
                return;
            }
            mCheaterCounter += 1;
            mDidUserCheatArray[mCurrentIndex] = CheatActivity.wasAnswerShown(data);
            updateQuestion();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() called");
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.i(TAG, "onSaveInstanceState");
        savedInstanceState.putInt(KEY_INDEX, mCurrentIndex);
        savedInstanceState.putInt(KEY_QUESTION_COUNTER, mFinalQuestionCounter);
        savedInstanceState.putIntArray(KEY_ARE_ANSWERED_ARRAY, mAreAnswered);
        savedInstanceState.putBooleanArray(KEY_USER_CHEATED_ARRAY, mDidUserCheatArray);
        savedInstanceState.putInt(KEY_CHEATER_COUNTER, mCheaterCounter);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() called");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() called");
    }

    private void updateQuestion(){
        int question = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(question);
        //if user cheated the cheat button will be deactivated beyond this point for that question
        setButtons();
        int mCheatsRemaining = 3 - mCheaterCounter;
        if(mCheaterCounter >= 1){mCheaterCounterTextView.setText(getString(R.string.cheat_counter_text, mCheatsRemaining));}
        if(mFinalQuestionCounter == mQuestionBank.length){gradeQuiz();}
    }

    private void checkAnswer(boolean userPressedTrue){
        boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();

        int messageResId = 0;

        if(mDidUserCheatArray[mCurrentIndex]){
            messageResId = R.string.judgment_toast;
            mFinalQuestionCounter += 1;
            if(userPressedTrue == answerIsTrue){mAreAnswered[mCurrentIndex] = 1;}
            else{mAreAnswered[mCurrentIndex] = 2;}
        }else {
            if (userPressedTrue == answerIsTrue) {
                messageResId = R.string.correct_toast;
                mAreAnswered[mCurrentIndex] = 1;
                mFinalQuestionCounter += 1;
            } else {
                messageResId = R.string.incorrect_toast;
                mAreAnswered[mCurrentIndex] = 2;
                mFinalQuestionCounter += 1;
            }
        }

            setButtons();

            //could return double and save it in 'messageResId' variable and toast it
            if (mFinalQuestionCounter == mQuestionBank.length){gradeQuiz();}

            Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show();
    }

    private void setButtons(){
        if(mAreAnswered[mCurrentIndex] >= 1){
            mTrueButton.setEnabled(false);
            mFalseButton.setEnabled(false);
            mCheatButton.setEnabled(false);
        }else{
            mTrueButton.setEnabled(true);
            mFalseButton.setEnabled(true);
            mCheatButton.setEnabled(true);
        }
        if(mCheaterCounter >= 3 || mDidUserCheatArray[mCurrentIndex] || mFinalQuestionCounter == mQuestionBank.length){
            mCheatButton.setEnabled(false);
        }


    }

    private void gradeQuiz(){
        mPercentGrade = (TextView) findViewById(R.id.percent_grade_text_view);
        double currentGrade = 0.0;
        for (int i = 0; i < mQuestionBank.length; i++){
            if(mAreAnswered[i] == 1) {
                currentGrade += 1.0;
            }
        }

        currentGrade = (currentGrade / mQuestionBank.length) * 100;
        mPercentGrade.setText(getString(R.string.percent_grade_text, String.format("%.2f", currentGrade)));
    }
}
