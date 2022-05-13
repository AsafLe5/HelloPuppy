package com.kingslayer.hellopuppy;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import org.jetbrains.annotations.NotNull;

public class EditNameDialog extends AppCompatDialogFragment {
    private EditNameDialogListener listener;
    private EditText editTextName;
    private String titleText;
    private String textViewToChange;

    public EditNameDialog(String hintText, String textViewToChange) {
        this.titleText = hintText;
        this.textViewToChange = textViewToChange;
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog_edit_name,null);
        builder.setView(view).setTitle(titleText)
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // don't have to put nothing in here since it just cancel.
                    }
                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String name = editTextName.getText().toString();
                        listener.goToApplyText(name, textViewToChange);
                    }
                });
        editTextName = view.findViewById(R.id.edit_name_text);
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        try {
            listener=(EditNameDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()+
                    "must implement EditNameDialogListener");
        }
    }

    public interface EditNameDialogListener{
//        void applyText(String name, String textViewToChange);
        void goToApplyText(String newText, String textViewToApply);
    }
}
