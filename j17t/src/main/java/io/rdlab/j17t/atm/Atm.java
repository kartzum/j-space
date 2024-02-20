package io.rdlab.j17t.atm;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Atm {
    private final Map<Bill, Integer> bills;
    private final List<Bill> orderedBills;

    public Atm() {
        this.bills = new HashMap<>();
        this.orderedBills =
            Stream.of(Bill.values())
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
    }

    public void deposit(Map<Bill, Integer> bills) {
        bills.forEach(this::addBill);
    }

    public Map<Bill, Integer> getCurrentBills() {
        return Collections.unmodifiableMap(bills);
    }

    public Map<Bill, Integer> withdraw(int amount) {
        Map<Bill, Integer> result = new HashMap<>();
        int leftToWithdraw = amount;
        for (Bill bill : orderedBills) {
            if (bills.containsKey(bill)) {
                int maximumAmount = Math.min(
                    leftToWithdraw / bill.getNominalValue(),
                    bills.get(bill)
                );
                leftToWithdraw -= maximumAmount * bill.getNominalValue();
                result.put(bill, maximumAmount);
            }
        }
        if (leftToWithdraw == 0) {
            result.forEach((b, c) -> bills.merge(b, -c, Integer::sum));
        }
        return result;
    }

    private void addBill(Bill bill, int count) {
        bills.merge(bill, count, Integer::sum);
    }
}
