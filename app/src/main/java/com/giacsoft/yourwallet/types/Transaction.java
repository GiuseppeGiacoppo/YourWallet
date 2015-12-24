package com.giacsoft.yourwallet.types;

public class Transaction {
    public long id;
    public String note;
    public double amount;
    public long category;
    public long accountID;
    public int giorno, day, month, year;

    public Transaction() {
        super();
    }

    public Transaction(String nota, double importo, long categoria, long conto, int giorno, int day, int month, int year) {
        this.note = nota;
        this.amount = importo;
        this.category = categoria;
        this.giorno = giorno;
        this.accountID = conto;
        this.day = day;
        this.month = month;
        this.year = year;
    }

    public Transaction(long id, String nota, double importo, long categoria, long conto, int giorno, int day, int month, int year) {
        this.id = id;
        this.note = nota;
        this.amount = importo;
        this.category = categoria;
        this.giorno = giorno;
        this.accountID = conto;
        this.day = day;
        this.month = month;
        this.year = year;
    }

}
