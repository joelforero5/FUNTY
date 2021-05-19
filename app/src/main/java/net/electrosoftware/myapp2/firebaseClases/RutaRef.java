package net.electrosoftware.myapp2.firebaseClases;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by CARLOS MAESTRE on 25/05/2017.
 */

public class RutaRef {

    DatabaseReference databaseReference;
    ValueEventListener valueEventListener;

    public RutaRef() {
    }

    public RutaRef(DatabaseReference databaseReference, ValueEventListener valueEventListener) {
        this.databaseReference = databaseReference;
        this.valueEventListener = valueEventListener;
    }

    public DatabaseReference getdatabaseReference() {
        return databaseReference;
    }

    public void setdatabaseReference(DatabaseReference databaseReference) {
        this.databaseReference = databaseReference;
    }

    public ValueEventListener getvalueEventListener() {
        return valueEventListener;
    }

    public void setvalueEventListener(ValueEventListener valueEventListener) {
        this.valueEventListener = valueEventListener;
    }
}
