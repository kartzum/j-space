package io.rdlab.j17t.atm;

public enum Bill {
    B_5(5),
    B_10(10),
    ;

    private final int nominalValue;

    public int getNominalValue() {
        return nominalValue;
    }

    Bill(int nominalValue) {
        this.nominalValue = nominalValue;
    }
}
