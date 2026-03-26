package splitwise.balance;

import splitwise.expense.Expense;
import splitwise.model.UserId;
import splitwise.split.Split;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public final class BalanceLedger {

    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();

    // balances.get(creditor).get(debtor) = amount debtor owes creditor
    private final Map<UserId, Map<UserId, BigDecimal>> balances = new ConcurrentHashMap<>();

    public void recordExpense(Expense expense) {
        rwLock.writeLock().lock();
        try {
            var creditor    = expense.paidBy();
            var totalAmount = expense.amount().amount();
            for (Split s : expense.splits()) {
                if (!s.userId().equals(creditor))
                    addDebt(creditor, s.userId(), s.computeShare(totalAmount, expense.splits()));
            }
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    public void recordSettlement(UserId debtor, UserId creditor, BigDecimal amount) {
        rwLock.writeLock().lock();
        try {
            addDebt(creditor, debtor, amount.negate());
            simplifyPair(debtor, creditor);
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    private void addDebt(UserId creditor, UserId debtor, BigDecimal delta) {
        balances.computeIfAbsent(creditor, k -> new ConcurrentHashMap<>())
                .merge(debtor, delta, BigDecimal::add);
    }

    private void simplifyPair(UserId a, UserId b) {
        var aOwesB = getDebt(a, b);
        var bOwesA = getDebt(b, a);
        var net    = aOwesB.subtract(bOwesA);
        if (net.compareTo(BigDecimal.ZERO) > 0) {
            setDebt(a, b, net);
            setDebt(b, a, BigDecimal.ZERO);
        } else if (net.compareTo(BigDecimal.ZERO) < 0) {
            setDebt(b, a, net.negate());
            setDebt(a, b, BigDecimal.ZERO);
        } else {
            setDebt(a, b, BigDecimal.ZERO);
            setDebt(b, a, BigDecimal.ZERO);
        }
    }

    public BigDecimal getDebt(UserId debtor, UserId creditor) {
        rwLock.readLock().lock();
        try {
            return balances.getOrDefault(creditor, Map.of())
                           .getOrDefault(debtor, BigDecimal.ZERO);
        } finally {
            rwLock.readLock().unlock();
        }
    }

    private void setDebt(UserId debtor, UserId creditor, BigDecimal amt) {
        balances.computeIfAbsent(creditor, k -> new ConcurrentHashMap<>()).put(debtor, amt);
    }

    /**
     * Greedy O(n log n) debt minimization using a max-heap.
     * Returns the minimum set of payments to clear all balances.
     */
    public List<Settlement> simplifyAllDebts() {
        rwLock.readLock().lock();
        try {
            var net = new HashMap<UserId, BigDecimal>();
            for (var creditorEntry : balances.entrySet()) {
                var creditor = creditorEntry.getKey();
                creditorEntry.getValue().forEach((debtor, amount) -> {
                    net.merge(creditor, amount,           BigDecimal::add);
                    net.merge(debtor,   amount.negate(),  BigDecimal::add);
                });
            }

            Comparator<Map.Entry<UserId, BigDecimal>> byValue =
                Comparator.<Map.Entry<UserId, BigDecimal>, BigDecimal>comparing(Map.Entry::getValue).reversed();

            var creditorQ = new PriorityQueue<>(11, byValue);
            var debtorQ   = new PriorityQueue<>(11, byValue);

            for (var entry : net.entrySet()) {
                int cmp = entry.getValue().compareTo(BigDecimal.ZERO);
                if (cmp > 0)
                    creditorQ.add(new AbstractMap.SimpleEntry<>(entry.getKey(), entry.getValue()));
                else if (cmp < 0)
                    debtorQ.add(new AbstractMap.SimpleEntry<>(entry.getKey(), entry.getValue().negate()));
            }

            var result = new ArrayList<Settlement>();
            while (!creditorQ.isEmpty() && !debtorQ.isEmpty()) {
                var cred   = creditorQ.poll();
                var debt   = debtorQ.poll();
                var settle = cred.getValue().min(debt.getValue());
                result.add(new Settlement(debt.getKey(), cred.getKey(), settle));
                var cr = cred.getValue().subtract(settle);
                var dr = debt.getValue().subtract(settle);
                if (cr.compareTo(BigDecimal.ZERO) > 0)
                    creditorQ.add(new AbstractMap.SimpleEntry<>(cred.getKey(), cr));
                if (dr.compareTo(BigDecimal.ZERO) > 0)
                    debtorQ.add(new AbstractMap.SimpleEntry<>(debt.getKey(), dr));
            }
            return result;
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public void printBalances(Map<UserId, String> nameMap) {
        rwLock.readLock().lock();
        try {
            System.out.println("\n=== Current Balances ===");
            balances.forEach((creditor, debtors) ->
                debtors.forEach((debtor, amount) -> {
                    if (amount.compareTo(BigDecimal.ZERO) > 0) {
                        System.out.printf("  %-12s  owes  %-12s  %.2f%n",
                            nameMap.getOrDefault(debtor,   debtor.toString()),
                            nameMap.getOrDefault(creditor, creditor.toString()),
                            amount);
                    }
                }));
        } finally {
            rwLock.readLock().unlock();
        }
    }
}
