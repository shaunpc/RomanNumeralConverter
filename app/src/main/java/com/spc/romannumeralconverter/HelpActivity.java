/**
 * Copyright (C) 2012 Wglxy.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.spc.romannumeralconverter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This is the Help activity for the application.
 * It displays some basic help information. Clicking on the icons takes the user to a detailed description.
 */

public class HelpActivity extends Activity {

    static public final String ARG_TEXT_ID = "text_id";
    private static final String TAG = "Help";

    /**
     * onCreate - called when the activity is first created.
     * Called when the activity is first created.
     * This is where you should do all of your normal static set up: create views, bind data to lists, etc.
     * This method also provides you with a Bundle containing the activity's previously frozen state, if there was one.
     * <p/>
     * Always followed by onStart().
     */

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help);

        Log.v(TAG, "Creating HELP screens");

        // Set up so that formatted text can be in the help_page_intro text and so that html links are handled.
        String help_page_intro = getResources().getString(R.string.help_page_intro);
        if (help_page_intro.contains("<")) {
            TextView help_text_intro = (TextView) findViewById(R.id.help_page_intro);
            if (help_text_intro != null) {
                help_text_intro.setMovementMethod(LinkMovementMethod.getInstance());
                help_text_intro.setText(getSpannedHtml(getString(R.string.help_page_intro)));
            }
        }

        // If the section text contains "<" ie HTML then inject as HTML
        String help_text_section1 = getResources().getString(R.string.help_text_section1);
        if (help_text_section1.contains("<")) {
            TextView helpText1 = (TextView) findViewById(R.id.help_text1);
            if (helpText1 != null) {
                helpText1.setMovementMethod(LinkMovementMethod.getInstance());
                helpText1.setText(getSpannedHtml(getString(R.string.help_text_section1)));
            }
        }

        // If the section text contains "CDATA" ie HTML then inject as HTML
        String help_text_section2 = getResources().getString(R.string.help_text_section2);
        if (help_text_section2.contains("<")) {
            TextView helpText2 = (TextView) findViewById(R.id.help_text2);
            if (helpText2 != null) {
                helpText2.setMovementMethod(LinkMovementMethod.getInstance());
                helpText2.setText(getSpannedHtml(getString(R.string.help_text_section2)));
            }
        }

        // If the section text contains "CDATA" ie HTML then inject as HTML
        String help_text_section3 = getResources().getString(R.string.help_text_section3);
        if (help_text_section3.contains("<")) {
            TextView helpText3 = (TextView) findViewById(R.id.help_text3);
            if (helpText3 != null) {
                helpText3.setMovementMethod(LinkMovementMethod.getInstance());
                helpText3.setText(getSpannedHtml(getString(R.string.help_text_section3)));
            }
        }

        // If the section text contains "CDATA" ie HTML then inject as HTML
        String help_text_section4 = getResources().getString(R.string.help_text_section4);
        if (help_text_section4.contains("<")) {
            TextView helpText4 = (TextView) findViewById(R.id.help_text4);
            if (helpText4 != null) {
                helpText4.setMovementMethod(LinkMovementMethod.getInstance());
                helpText4.setText(getSpannedHtml(getString(R.string.help_text_section4)));
            }
        }

    }

    private Spanned getSpannedHtml(String string) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            return Html.fromHtml(string, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(string);
        }
    }

    /**
     */
// Methods

//    /**
//     * Handle the click of one of the help buttons on the page.
//     * Start an activity to display the help text for the topic selected.
//     *
//     * @param v View
//     * @return void
//     */
    public void onClickHelp(View v) {
        int id = v.getId();
        int textId = -1;
        switch (id) {
            case R.id.help_button1:
                textId = R.string.topic_section1;
                break;
            case R.id.help_button2:
                textId = R.string.topic_section2;
                break;
            case R.id.help_button3:
                textId = R.string.topic_section3;
                break;
            case R.id.help_button4:
                textId = R.string.topic_section4;
                break;
            default:
                break;
        }

        if (textId >= 0) startInfoActivity(textId);
        else toast("Detailed Help for that topic is not available.", true);
    }

//    /**
//     * Start a TopicActivity and show the text indicated by argument 1.
//     *
//     * @param textId int - resource id of the text to show
//     * @return void
//     */

    public void startInfoActivity(int textId) {
        if (textId >= 0) {
            Intent intent = (new Intent(this, TopicActivity.class));
            intent.putExtra(ARG_TEXT_ID, textId);
            startActivity(intent);
        } else {
            toast("No information is available for topic: " + textId, true);
        }
    } // end startInfoActivity

//    /**
//     * Show a string on the screen via Toast.
//     *
//     * @param msg        String
//     * @param longLength boolean - show message a long time
//     * @return void
//     */

    public void toast(String msg, boolean longLength) {
        Toast.makeText(getApplicationContext(), msg,
                (longLength ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT)
        ).show();
    }

} // end class
