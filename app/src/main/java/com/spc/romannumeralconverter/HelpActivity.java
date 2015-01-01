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
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This is the Help activity for the application.
 * It displays some basic help information. Clicking on the icons takes the user to a detailed description.
 */

public class HelpActivity extends Activity {

    static public final String ARG_TEXT_ID = "text_id";

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

        // Set up so that formatted text can be in the help_page_intro text and so that html links are handled.
        TextView textView = (TextView) findViewById(R.id.help_page_intro);
        if (textView != null) {
            textView.setMovementMethod(LinkMovementMethod.getInstance());
            textView.setText(Html.fromHtml(getString(R.string.help_page_intro_html)));
        }
    }


/**
 */
// Methods

    /**
     * Handle the click of one of the help buttons on the page.
     * Start an activity to display the help text for the topic selected.
     *
     * @param v View
     * @return void
     */

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

    /**
     * Start a TopicActivity and show the text indicated by argument 1.
     *
     * @param textId int - resource id of the text to show
     * @return void
     */

    public void startInfoActivity(int textId) {
        if (textId >= 0) {
            Intent intent = (new Intent(this, TopicActivity.class));
            intent.putExtra(ARG_TEXT_ID, textId);
            startActivity(intent);
        } else {
            toast("No information is available for topic: " + textId, true);
        }
    } // end startInfoActivity

    /**
     * Show a string on the screen via Toast.
     *
     * @param msg        String
     * @param longLength boolean - show message a long time
     * @return void
     */

    public void toast(String msg, boolean longLength) {
        Toast.makeText(getApplicationContext(), msg,
                (longLength ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT)
        ).show();
    }

} // end class
