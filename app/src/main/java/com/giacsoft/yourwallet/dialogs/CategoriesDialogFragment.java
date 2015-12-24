package com.giacsoft.yourwallet.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.giacsoft.yourwallet.R;
import com.giacsoft.yourwallet.Utils;
import com.giacsoft.yourwallet.adapters.MyColorAdapter;
import com.giacsoft.yourwallet.adapters.MyCategoryAdapter;
import com.giacsoft.yourwallet.db.MyDatabase;
import com.giacsoft.yourwallet.types.Category;

import java.util.ArrayList;
import java.util.Random;

public class CategoriesDialogFragment extends DialogFragment implements OnClickListener {

    static final int COLORI = 14; // n-2, serve per selezionare casualmente un
    static boolean edit = false;
    static Category cat;
    static int col_position, position;
    MyDatabase db;
    Button add_btn;
    EditText nameET;
    ImageView deleteIV;
    LinearLayout container_category_info, container_category_delete, del_layout;
    Spinner colore, move_cat;
    boolean isDeleteMode = false;
    boolean doubleBackToExitPressedOnce = false;
    Random rand;
    // colore con le posizioni da 1 a 15(lo 0 �
    // il bianco)

    public static CategoriesDialogFragment newInstance() {
        CategoriesDialogFragment frag = new CategoriesDialogFragment();
        edit = false;
        return frag;
    }

    public static CategoriesDialogFragment newInstance(Category c, int col_p, int p) {
        CategoriesDialogFragment frag = new CategoriesDialogFragment();
        cat = c;
        col_position = col_p;
        position = p;
        edit = true;
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        db = new MyDatabase(getActivity().getApplicationContext());
        db.open();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view_dialog = inflater.inflate(R.layout.dialog_add_categoria_restyle, null);

        colore = (Spinner) view_dialog.findViewById(R.id.spin_col);
        nameET = (EditText) view_dialog.findViewById(R.id.newcatnome);
        add_btn = (Button) view_dialog.findViewById(R.id.add_btn);
        move_cat = (Spinner) view_dialog.findViewById(R.id.spin_move_cat);
        deleteIV = (ImageView) view_dialog.findViewById(R.id.delete);
        container_category_info = (LinearLayout) view_dialog.findViewById(R.id.container_category_info);
        container_category_delete = (LinearLayout) view_dialog.findViewById(R.id.container_category_delete);
        del_layout = (LinearLayout) view_dialog.findViewById(R.id.del_layout);

        rand = new Random();

        MyColorAdapter colorAdapter = new MyColorAdapter(getActivity().getApplicationContext(), R.layout.item_colore, getResources().getStringArray(R.array.colorarray));
        colore.setAdapter(colorAdapter);
        colore.setSelection(rand.nextInt(COLORI) + 1);
        if (edit) {
            nameET.setText(cat.name);
            colore.setSelection(col_position);
            add_btn.setText(R.string.done);

            ArrayList<Category> arraycat = db.getCategories();
            if (arraycat.size() <= 1) {
                del_layout.setVisibility(View.INVISIBLE);
            } else {
                deleteIV.setOnClickListener(this);
            }

            arraycat.remove(position);

            MyCategoryAdapter spincat = new MyCategoryAdapter(getActivity(), R.layout.item_categoria_spinner, arraycat);
            move_cat.setAdapter(spincat);
        } else {
            del_layout.setVisibility(View.INVISIBLE);

        }

        add_btn.setOnClickListener(this);
        builder.setView(view_dialog);
        return builder.create();
    }

    @Override
    public void onClick(View v) {
        if (v == add_btn) {
            if (isDeleteMode == false) {
                if (nameET.length() > 0) {
                    if (edit) {
                        db.editCategory(cat.id, nameET.getText().toString(), (String) colore.getSelectedItem());
                        Toast.makeText(getActivity().getApplicationContext(), R.string.toast_successful_category_edit, Toast.LENGTH_SHORT).show();
                        OnCategorieDialogListener listener = (OnCategorieDialogListener) getActivity();
                        listener.doCategory(Utils.EDIT,new Category(cat.id, nameET.getText().toString(), (String) colore.getSelectedItem()), position);
                    } else {
                        long new_id = db.addCategory(nameET.getText().toString(), (String) colore.getSelectedItem());
                        Toast.makeText(getActivity().getApplicationContext(), R.string.toast_successful_category_add, Toast.LENGTH_SHORT).show();
                        OnCategorieDialogListener listener = (OnCategorieDialogListener) getActivity();
                        listener.doCategory(Utils.ADD,new Category(new_id, nameET.getText().toString(), (String) colore.getSelectedItem()),0);
                    }

                    dismiss();
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), R.string.toast_alert_invalid_name, Toast.LENGTH_SHORT).show();
                }
            } else {
                if (doubleBackToExitPressedOnce) {

                    db.moveTransactionsByCategories(cat.id, move_cat.getSelectedItemId());
                    db.delCategory(cat.id);
                    Toast.makeText(getActivity().getApplicationContext(), R.string.toast_successful_category_delete, Toast.LENGTH_SHORT).show();

                    OnCategorieDialogListener listener = (OnCategorieDialogListener) getActivity();
                    listener.doCategory(Utils.DELETE,null,position);
                    dismiss();
                    return;
                }

                doubleBackToExitPressedOnce = true;
                Toast.makeText(getActivity().getApplicationContext(), R.string.toast_confirm_deletecategory, Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                }, 3000);
            }
        } else if (v == deleteIV) {
            if (isDeleteMode == false) {

                add_btn.setText(R.string.delete);
                container_category_info.setVisibility(View.INVISIBLE);
                container_category_delete.setVisibility(View.VISIBLE);
                del_layout.setVisibility(View.INVISIBLE);
                isDeleteMode = true;
            }
        }
    }

    public interface OnCategorieDialogListener {
        /*void onAddCategoria(Category c);
        void onEditCategoria(Category c, int p);
        void onDeleteCategoria(int p);*/
        void doCategory(int mode, Category c, int p);
    }
}
