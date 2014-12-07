package com.TutorTimer.ui;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import com.TutorTimer.R;

import java.util.concurrent.TimeUnit;

/**
 * Factory to create the reset time dialogues
 */
public class ChangeResetTimeDialogueFactory
{
    public interface DialogueCallbacks
    {
        public void onOkayClicked(long min, long sec);

        public void onCancelClicked();
    }

    public static Dialog getResetDialogue(Context context, long startTime, final DialogueCallbacks callbacks)
    {
        return new ChangeResetTimeDialogue(context, startTime, callbacks);
    }

    private static class ChangeResetTimeDialogue extends Dialog
    {
        public ChangeResetTimeDialogue(Context context, long time, final DialogueCallbacks callbacks)
        {
            super(context);
            setTitle("Reset Time Picker");
            setContentView(R.layout.reset_time_dialogue);

            // reset time pickers
            m_minPicker = (NumberPicker) findViewById(R.id.min_reset_time_picker);
            m_secPicker = (NumberPicker) findViewById(R.id.sec_reset_time_picker);

            // set the values
            m_minPicker.setMinValue(0);
            m_minPicker.setMaxValue(60);
            m_secPicker.setMinValue(0);
            m_secPicker.setMaxValue(1);
            m_secPicker.setDisplayedValues(SECOND_RESET_STRINGS);

            // disable the soft keyboard
            m_minPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
            m_secPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

            // okay and cancel buttons
            m_okayButton = (Button) findViewById(R.id.okay_button);
            m_cancelButton = (Button) findViewById(R.id.cancel_button);

            m_okayButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    long min = m_minPicker.getValue();
                    long sec = SECOND_RESET_VALUES[m_secPicker.getValue()];

                    callbacks.onOkayClicked(min, sec);
                    dismiss();
                }
            });

            m_cancelButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    callbacks.onCancelClicked();
                    dismiss();
                }
            });

            setResetPickers(time);
        }

        private void setResetPickers(long time)
        {
            long timeSeconds = TimeUnit.MILLISECONDS.toSeconds(time);
            m_minPicker.setValue((int) TimeUnit.SECONDS.toMinutes(timeSeconds));

            long remainderSeconds = timeSeconds % 60;
            for (int i = 0; i < SECOND_RESET_VALUES.length; ++i)
            {
                if (remainderSeconds == SECOND_RESET_VALUES[i])
                {
                    m_secPicker.setValue(i);
                    break;
                }
            }
        }

        private final NumberPicker m_secPicker;
        private final NumberPicker m_minPicker;
        private final Button       m_okayButton;
        private final Button       m_cancelButton;

        private static final String[] SECOND_RESET_STRINGS = {"00", "30"};
        private static final long[]   SECOND_RESET_VALUES  = {0, 30};
    }

    private ChangeResetTimeDialogueFactory()
    {
    }
}
