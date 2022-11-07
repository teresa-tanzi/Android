package com.example.teresa.orologiomondiale;

public class MySingleton {
    private static MySingleton istanza = null;
    private CitiesSavedAdapter a=null;
    private CitiesSavedAdapter b=null;

    //Il costruttore private impedisce l'istanza di oggetti da parte di classi esterne
    private MySingleton() {}

    // Metodo della classe impiegato per accedere al singleton
    public static synchronized MySingleton getInstance() {
        if (istanza == null) {
            istanza = new MySingleton();
        }
        return istanza;
    }

    public CitiesSavedAdapter getAdapter() {
        return a;
    }

    public void setAdapter (CitiesSavedAdapter a) {
        this.a = a;
    }

    public CitiesSavedAdapter getNewAdapter() {
        return b;
    }

    public void setNewAdapter(CitiesSavedAdapter b) {
        this.b=b;
    }

    public void notifyMyAdapter() {
        a.notifyDataSetChanged();
    }

}
