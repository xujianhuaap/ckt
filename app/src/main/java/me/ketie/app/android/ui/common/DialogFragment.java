package me.ketie.app.android.ui.common;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by henjue on 2015/4/7.
 */
public class DialogFragment extends android.app.DialogFragment {
    private NegativeListener nListener;
    private PositiveListener pListener;

    public static DialogFragment newInstance(String title, String message) {
        DialogFragment adf = new DialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("alert-title", title);
        bundle.putString("alert-message", message);
        adf.setArguments(bundle);
        return adf;
    }

    public void setNegativeListener(NegativeListener nListener) {
        this.nListener = nListener;
    }

    public void setPositiveListener(PositiveListener pListener) {
        this.pListener = pListener;
    }

    @Override
    public android.app.Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getArguments().getString("alert-message"));
        builder.setTitle(getArguments().getString("alert-title"));
        if (this.pListener != null) {
            builder.setPositiveButton(pListener.positiveText(), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    pListener.onPositiveClick(dialog);
                }
            });
        }
        if (nListener != null) {
            builder.setNegativeButton(nListener.negativeText(), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    nListener.onNegativeClick(dialog);
                }
            });
        }

        return builder.create();
    }

    public interface NegativeListener {
        public void onNegativeClick(DialogInterface dialog);

        public String negativeText();
    }

    public interface PositiveListener {
        public void onPositiveClick(DialogInterface dialog);

        public String positiveText();
    }
}
