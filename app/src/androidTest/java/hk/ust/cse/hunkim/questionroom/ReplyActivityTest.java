package hk.ust.cse.hunkim.questionroom;

import android.content.Intent;
import android.media.Image;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import org.w3c.dom.Text;

import java.util.Date;

import hk.ust.cse.hunkim.questionroom.question.Question;
import hk.ust.cse.hunkim.questionroom.reply.Reply;

/**
 * Created by CAI on 2/11/2015.
 */


public class ReplyActivityTest extends ActivityInstrumentationTestCase2<ReplyActivity> {
    private String roomBaseUrl = "https://cmkquestionsdb.firebaseio.com/rooms/TestRoom/";
    private String QuestionKey;

    private ListView listview;
    private EditText replyInput;
    private ImageButton sendButton;
    private ImageButton parentLike;
    private ImageButton parentDislike;
    private TextView timeText;
    private Button titleText;
    private TextView descText;
    private TextView likeText;
    private TextView dislikeText;



    private Intent mStartIntent;
    private ReplyActivity activity;

    public ReplyActivityTest() {
        super(ReplyActivity.class);
    }

    protected void setUp() throws Exception{
        super.setUp();
        QuestionKey = "pretendingKey";
        Question tempQuestion = new Question("This is a temp question title", "This is a #temp #question body");

        mStartIntent = new Intent(Intent.ACTION_MAIN);
        mStartIntent.putExtra("PUSHED_ID", QuestionKey);
        mStartIntent.putExtra("ROOM_NAME", "TEST");
        mStartIntent.putExtra("ROOM_BASE_URL", roomBaseUrl);
        mStartIntent.putExtra("NUM_LIKE", tempQuestion.getLike());
        mStartIntent.putExtra("NUM_DISLIKE", tempQuestion.getDislike());
        mStartIntent.putExtra("NUM_REPLY", tempQuestion.getReplies());
        mStartIntent.putExtra("HEAD", tempQuestion.getHead());
        mStartIntent.putExtra("DESC", tempQuestion.getDesc());
        mStartIntent.putExtra("TIMESTAMP", tempQuestion.getTimestamp());
        mStartIntent.putExtra("TAGS", tempQuestion.getTags());
        setActivityIntent(mStartIntent);

        Firebase.setAndroidContext(getActivity());
        Firebase testQuestionUrl = new Firebase(roomBaseUrl).child("questions").child(QuestionKey);
        testQuestionUrl.setValue(tempQuestion);


        replyInput = (EditText) getActivity().findViewById(R.id.reply_input_field);
        sendButton = (ImageButton) getActivity().findViewById(R.id.send_reply_button);
        parentDislike=(ImageButton) getActivity().findViewById(R.id.parent_question_dislike_button);
        parentLike=(ImageButton) getActivity().findViewById(R.id.parent_question_like_button);
        timeText = (TextView) getActivity().findViewById((R.id.parent_question_time_text));
        titleText = (Button) getActivity().findViewById((R.id.parent_question_head));
        descText = (TextView) getActivity().findViewById((R.id.parent_question_desc));
        likeText = (TextView) getActivity().findViewById((R.id.parent_question_like_text));
        dislikeText = (TextView) getActivity().findViewById(R.id.parent_question_dislike_text);
        listview = getActivity().getListView();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        Firebase mFirebaseRef = new Firebase("https://cmkquestionsdb.firebaseio.com/rooms/TestRoom/");
        mFirebaseRef.removeValue();
    }

    public void testPrecondition()throws Throwable {
        assertNotNull("ReplyActivity is null", getActivity());
        assertNotNull("ReplyMessage field is null", replyInput);
        assertNotNull("ReplySending Button is null", sendButton);
        assertNotNull(timeText);
        assertNotNull(titleText);
        assertNotNull(descText);
        assertNotNull(likeText);
        assertNotNull(dislikeText);

        getActivity().finish();
    }

    public void testParentLikeButton() throws Throwable{
        assertNotNull("Parent Question has no like button", parentLike);
        getInstrumentation().waitForIdleSync();
        TouchUtils.clickView(this, parentLike);

        getInstrumentation().waitForIdleSync();
        TouchUtils.clickView(this, parentLike);
        getInstrumentation().waitForIdleSync();

        getActivity().finish();
    }

    public void testParentDislikeButton() throws Throwable{
        assertNotNull("Parent Question has no like button", parentDislike);
        getInstrumentation().waitForIdleSync();
        TouchUtils.clickView(this, parentDislike);
        getInstrumentation().waitForIdleSync();
        TouchUtils.clickView(this, parentDislike);
        getInstrumentation().waitForIdleSync();
        getActivity().finish();
    }

    public void testReplyWithoutMessage() throws Throwable {
        TouchUtils.clickView(this, sendButton);
        getInstrumentation().waitForIdleSync();
        assertEquals("There s not error message from the reply input", "This field is required", replyInput.getError());

        getActivity().finish();
    }

    public void testReplyWithMessage() throws Throwable{
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                replyInput.setText("Testing < Reply >");
            }
        });
        //Wait until all events from the MainHandler's queue are processed
        getInstrumentation().waitForIdleSync();

        //getInstrumentation().sendStringSync("Testing Reply");
        getInstrumentation().waitForIdleSync();

        String actualText = replyInput.getText().toString();
        while(actualText==null){
            actualText = replyInput.getText().toString();
        }

        TouchUtils.clickView(this, sendButton);
        getInstrumentation().waitForIdleSync();
        assertEquals("There s not error message from the reply input", null, replyInput.getError());

        getActivity().finish();
    }

    public void testReplyWithDifferentTimeAndOrder() throws Throwable {
        Firebase replyHour = new Firebase(roomBaseUrl).child("replies").child("reply_with_hours_ago");
        Firebase replyDay = new Firebase(roomBaseUrl).child("replies").child("reply_with_actual_time");

        replyHour.setValue(new Reply("Test reply", QuestionKey));
        replyDay.setValue(new Reply("Test reply", QuestionKey));
        long hourTime = new Date().getTime() - (DateUtils.HOUR_IN_MILLIS) * 3;
        replyHour.child("timestamp").setValue(hourTime);
        replyHour.child("order").setValue(3);

        long dayTime = new Date().getTime() - (DateUtils.DAY_IN_MILLIS) * 3;
        replyDay.child("timestamp").setValue(dayTime);
        replyDay.child("order").setValue(9);

        getInstrumentation().waitForIdleSync();
        TouchUtils.clickView(this, sendButton);
        getInstrumentation().waitForIdleSync();
        getActivity().finish();
    }

    public void testQuestionTitleNoTag() throws Throwable {
        Firebase testQuestionUrl = new Firebase(roomBaseUrl).child("questions").child(QuestionKey);
        testQuestionUrl.child("desc").setValue("");
        testQuestionUrl.child("tags").removeValue();

        getInstrumentation().waitForIdleSync();
        TouchUtils.clickView(this, sendButton);
        getInstrumentation().waitForIdleSync();
        getActivity().finish();
    }

    public void testReplyLike() throws Throwable{
        Firebase testQuestionUrl = new Firebase("https://cmkquestionsdb.firebaseio.com/rooms/TestRoom/").child("replies");
        testQuestionUrl.push().setValue(new Reply("Reply One", "pretendingKey"));

        Thread.sleep(3000);

        View listElement = (View) getActivity().getListView().getChildAt(1);

        ImageButton replyLikeBtn = (ImageButton) listElement.findViewById(R.id.reply_like_button);
        getInstrumentation().waitForIdleSync();
        TouchUtils.clickView(this, replyLikeBtn);
        getInstrumentation().waitForIdleSync();

        getActivity().updateOrder((String) listElement.getTag(), 1);
        getActivity().updateOrder((String) listElement.getTag(), -1);

        TextView orderText = (TextView) listElement.findViewById(R.id.reply_order);
        assertEquals("Click dislike at first have some problem", "1", orderText.getText());
        getActivity().finish();
    }

    public void testReplyDislike() throws Throwable{
        Firebase testQuestionUrl = new Firebase("https://cmkquestionsdb.firebaseio.com/rooms/TestRoom/").child("replies");
        testQuestionUrl.push().setValue(new Reply("Reply Two", "pretendingKey"));

        Thread.sleep(3000);

        View listElement = (View) getActivity().getListView().getChildAt(1);
        ImageButton replyDislikeBtn = (ImageButton) listElement.findViewById(R.id.reply_dislike_button);
        getInstrumentation().waitForIdleSync();
        TouchUtils.clickView(this, replyDislikeBtn);
        getInstrumentation().waitForIdleSync();
        getActivity().updateOrder((String) listElement.getTag(), 1);
        getActivity().updateOrder((String) listElement.getTag(), -1);
        TextView orderText = (TextView) listElement.findViewById(R.id.reply_order);
        assertEquals("Click dislike at first have some problem", "-1", orderText.getText());
        getActivity().finish();
    }

}

