package other;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import www.ntej.com.worldclocks.R;

/**
 * Created by navatejareddy on 1/9/17.
 */

public class CommonFunctionalityClass {

    Context context;
    AlertDialog.Builder alertBuilder;
    AlertDialog alertDialog;

    public CommonFunctionalityClass(Context mContext)
    {
        context = mContext;
        alertBuilder = new AlertDialog.Builder(context);
    }


    public  void ShowAboutDialog()
    {

        alertBuilder.setTitle("About");
        alertBuilder.setMessage(R.string.about_app);

        alertBuilder.setNeutralButton("Dismiss", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });

        alertDialog = alertBuilder.create();
        alertDialog.show();
    }

}
