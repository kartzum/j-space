package io.rdlab.j17t.atm;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AtmTest {

    @Test
    public void testDeposit() {
        Atm atm = new Atm();

        Map<Bill, Integer> initialBills = Map.of(
            Bill.B_5, 2,
            Bill.B_10, 3
        );

        atm.deposit(initialBills);

        Map<Bill, Integer> additionalBills = Map.of(
            Bill.B_5, 1,
            Bill.B_10, 2
        );

        atm.deposit(additionalBills);

        Map<Bill, Integer> currentBills = atm.getCurrentBills();

        assertThat(currentBills)
            .usingRecursiveComparison().isEqualTo(
                Map.of(
                    Bill.B_5, 3,
                    Bill.B_10, 5
                )
            );
    }

    @Test
    public void testWithdraw() {
        Atm atm = new Atm();

        Map<Bill, Integer> initialBills = new HashMap<>();
        initialBills.put(Bill.B_5, 2);
        initialBills.put(Bill.B_10, 3);

        atm.deposit(initialBills);

        Map<Bill, Integer> resultBills = atm.withdraw(10);

        assertThat(resultBills)
            .usingRecursiveComparison().isEqualTo(
                Map.of(
                    Bill.B_5, 0,
                    Bill.B_10, 1
                )
            );
    }
}
