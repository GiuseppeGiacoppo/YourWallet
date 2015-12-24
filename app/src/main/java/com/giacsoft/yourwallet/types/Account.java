package com.giacsoft.yourwallet.types;

public class Account {
    public long id;
    public String name;
    public double total;
    public int transactions_number;

    public Account() {
        super();
    }

    public Account(String nome, double totale) {
        this.name = nome;
        this.total = totale;
    }

    public Account(long id, String nome, double totale) {
        this.id = id;
        this.name = nome;
        this.total = totale;
    }

    public Account(long id, String nome, double totale, int tot_transazioni) {
        this.id = id;
        this.name = nome;
        this.total = totale;
        this.transactions_number = tot_transazioni;
    }

}
