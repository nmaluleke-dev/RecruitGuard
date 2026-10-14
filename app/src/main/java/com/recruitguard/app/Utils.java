package com.recruitguard.app;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class Utils
{

    public static void showToast(Context context, String message)
    {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        View view = toast.getView();

        if (view != null) {
            view.setBackgroundColor(Color.WHITE);

            TextView text = view.findViewById(android.R.id.message);
            if (text != null) {
                text.setTextColor(Color.BLACK);
                text.setTextSize(16);
                text.setPadding(32, 16, 32, 16);
            }
        }

        // Center it if needed
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 100);

        toast.show();


        /*
        view.getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);

        TextView text = view.findViewById(android.R.id.message);
        text.setTextColor(Color.BLACK);
        text.setTextSize(16);

        toast.show();

         */
    }
}

