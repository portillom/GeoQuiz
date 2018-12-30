package com.portillomichael.android.geoquiz;

/**
 * Created by USER on 4/30/18.
 */

public class Question {

    private int mTextResId;
    private boolean mAnswerTrue;
    private int mAlreadyAnswered;
    //0 = not answered, 1 = answered correctly, 2 = answered incorrectly

    public Question (int textResId, boolean answerTrue){
        mTextResId = textResId;
        mAnswerTrue = answerTrue;
        mAlreadyAnswered = 0;
    }

    public int getTextResId() {
        return mTextResId;
    }

    public void setTextResId(int textResId) {
        mTextResId = textResId;
    }

    public boolean isAnswerTrue() {
        return mAnswerTrue;
    }

    public void setAnswerTrue(boolean answerTrue) {
        mAnswerTrue = answerTrue;
    }

}
