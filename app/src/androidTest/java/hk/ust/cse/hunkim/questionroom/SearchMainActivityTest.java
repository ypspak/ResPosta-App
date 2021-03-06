package hk.ust.cse.hunkim.questionroom;

import android.app.Instrumentation;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.widget.EditText;
import android.widget.ImageButton;

import com.firebase.client.Firebase;

import junit.framework.TestCase;

import hk.ust.cse.hunkim.questionroom.question.Question;

/**
 * Created by CAI on 21/11/2015.
 */
public class SearchMainActivityTest extends ActivityInstrumentationTestCase2<SearchMainActivity> {
    private String roomBaseUrl;
    private SearchMainActivity currentActivity;
    private ImageButton searchBtn;
    private EditText searchInput;



    public SearchMainActivityTest() {
        super(SearchMainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        roomBaseUrl =  "https://cmkquestionsdb.firebaseio.com//rooms/TestRoom/";
        Intent mStartIntent = new Intent(Intent.ACTION_MAIN);
        mStartIntent.putExtra("ROOM_NAME", "TestRoom");
        mStartIntent.putExtra("ROOM_BASE_URL", roomBaseUrl);
        setActivityIntent(mStartIntent);

        currentActivity = getActivity();
        Instrumentation.ActivityMonitor activityMonitor = getInstrumentation().addMonitor(SearchMainActivity.class.getName(), null, false);
        Firebase.setAndroidContext(currentActivity);
        Firebase testQuestionUrl = new Firebase(roomBaseUrl).child("questions");
        Question question1 = new Question("Question 1 q1", "");
        Question question2 = new Question("Question 2 #q2 ", "This is question 2 \n # dealwith it haha.");
        Question question3 = new Question("Question 3 #  q3 ", "question 3 at here #abc look out");
        Question question4 = new Question("Question 1 q1", "");
        Question question5 = new Question("Question 2 #q2 ", "This is question 2 \n # dealwith it haha.");
        Question question6 = new Question("Question 3 #  q3 ", "question 3 at here #abc look out");
        testQuestionUrl.push().setValue(question1);
        testQuestionUrl.push().setValue(question2);
        testQuestionUrl.push().setValue(question3);
        testQuestionUrl.push().setValue(question4);
        testQuestionUrl.push().setValue(question5);
        testQuestionUrl.push().setValue(question6);

        searchBtn = (ImageButton) currentActivity.findViewById(R.id.searchButton);
        searchInput = (EditText) currentActivity.findViewById(R.id.searchInput);

    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        if(roomBaseUrl!=null){
            Firebase mFirebaseRef = new Firebase(roomBaseUrl);
            mFirebaseRef.removeValue();
        }
    }

    public void testSearchWithoutInput(){
        getInstrumentation().waitForIdleSync();
        TouchUtils.clickView(this, searchBtn);
        getInstrumentation().waitForIdleSync();
    }

    public void testSearchWithNormalInput() {
        Instrumentation.ActivityMonitor activityMonitor = getInstrumentation().addMonitor(SearchMainActivity.class.getName(), null, false);

        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                searchInput.requestFocus();
            }
        });
    }
}