package com.giacsoft.yourwallet.types;

import android.content.Context;

import com.giacsoft.yourwallet.R;

import java.util.Arrays;

public class Category {

    public long id;
    public String name;
    public double totalAmount;
    public String color;

    public Category(String nome) {
        this.name = nome;
    }

    public Category(long id, double imp) {
        this.id = id;
        this.totalAmount = imp;
    }

    public Category(long id, String nome) {
        this.id = id;
        this.name = nome;
    }

    public Category(long id, String nome, String colore) {
        this.id = id;
        this.name = nome;
        this.color = colore;
    }

    public Category(long id, String nome, double imp) {
        this.id = id;
        this.name = nome;
        this.totalAmount = imp;
    }

    public Category(long id, String nome, double imp, String colore) {
        this.id = id;
        this.name = nome;
        this.totalAmount = imp;
        this.color = colore;
    }

    public int getColoreId(Context context) {
        String[] colori = context.getResources().getStringArray(R.array.colorarray);
        int i = Arrays.asList(colori).indexOf(color);

        if (i > 0 && i < colori.length)
            return i;
        else
            return 0;
    }
}
