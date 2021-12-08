package com.astudent.partner.Utils;

import android.content.Context;
import androidx.appcompat.widget.AppCompatTextView;
import android.util.AttributeSet;

/**
 * Created by Freeware Sys on 3/27/2017.
 */

public class ClanProTextView extends AppCompatTextView {


    public ClanProTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        if(!isInEditMode())
            applyFont(context);
    }

    private void applyFont(Context context){
        setTypeface(Typefaces.get(context, "ClanPro-NarrNews.otf"));
    }


}
