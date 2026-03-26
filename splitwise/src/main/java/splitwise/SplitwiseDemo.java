package splitwise;

import splitwise.balance.BalanceLedger;
import splitwise.expense.*;
import splitwise.model.*;
import splitwise.observer.*;
import splitwise.service.ExpenseService;
import splitwise.split.SplitType;

import java.math.BigDecimal;
import java.util.*;

public class SplitwiseDemo {

    public static void main(String[] args) {

        // ── Bootstrap ────────────────────────────────────────────────────────
        var ledger   = new BalanceLedger();
        var notify   = new NotificationService();
        notify.subscribe(new ConsoleNotifier());
        var service  = new ExpenseService(ledger, notify);

        var alice   = new User("Alice",   "alice@example.com");
        var bob     = new User("Bob",     "bob@example.com");
        var charlie = new User("Charlie", "charlie@example.com");
        var all     = List.of(alice, bob, charlie);

        Map<UserId, String> names = Map.of(
            alice.id(),   "Alice",
            bob.id(),     "Bob",
            charlie.id(), "Charlie"
        );

        separator("1. EQUAL SPLIT — EUR 90 dinner, Alice paid");

        service.addExpense(
            "Dinner",
            Money.of(90, "EUR"),
            alice, all,
            SplitType.EQUAL, Map.of(),
            new FoodExpense(), Optional.empty());

        System.out.printf("  Bob owes Alice:     %.2f%n", ledger.getDebt(bob.id(),     alice.id()));
        System.out.printf("  Charlie owes Alice: %.2f%n", ledger.getDebt(charlie.id(), alice.id()));

        separator("2. PERCENTAGE SPLIT — EUR 200 road trip, Bob paid (50/30/20)");

        Map<UserId, Object> pct = Map.of(
            alice.id(),   new BigDecimal("50"),
            bob.id(),     new BigDecimal("30"),
            charlie.id(), new BigDecimal("20")
        );
        service.addExpense(
            "Road Trip",
            Money.of(200, "EUR"),
            bob, all,
            SplitType.PERCENTAGE, pct,
            new TravelExpense(), Optional.empty());

        System.out.printf("  Alice owes Bob:   %.2f%n", ledger.getDebt(alice.id(),   bob.id()));
        System.out.printf("  Charlie owes Bob: %.2f%n", ledger.getDebt(charlie.id(), bob.id()));

        separator("3. EXACT SPLIT — EUR 60 utilities, Charlie paid (25/20/15)");

        Map<UserId, Object> exact = Map.of(
            alice.id(),   new BigDecimal("25.00"),
            bob.id(),     new BigDecimal("20.00"),
            charlie.id(), new BigDecimal("15.00")
        );
        service.addExpense(
            "Utilities",
            Money.of(60, "EUR"),
            charlie, all,
            SplitType.EXACT, exact,
            new UtilityExpense(), Optional.empty());

        System.out.printf("  Alice owes Charlie:  %.2f%n", ledger.getDebt(alice.id(),   charlie.id()));
        System.out.printf("  Bob owes Charlie:    %.2f%n", ledger.getDebt(bob.id(),     charlie.id()));

        separator("4. SHARE SPLIT — EUR 120 movie night, Alice paid (2:1:1)");

        Map<UserId, Object> shares = Map.of(
            alice.id(),   2,
            bob.id(),     1,
            charlie.id(), 1
        );
        service.addExpense(
            "Movie Night",
            Money.of(120, "EUR"),
            alice, all,
            SplitType.SHARE, shares,
            new EntertainmentExpense(), Optional.empty());

        System.out.printf("  Bob owes Alice (movie):     %.2f%n", ledger.getDebt(bob.id(),     alice.id()));
        System.out.printf("  Charlie owes Alice (movie): %.2f%n", ledger.getDebt(charlie.id(), alice.id()));

        ledger.printBalances(names);

        separator("5. MINIMUM SETTLEMENT PLAN");
        var settlements = ledger.simplifyAllDebts();
        settlements.forEach(s -> System.out.printf("  %-10s  pays  %-10s  EUR %.2f%n",
            names.get(s.from()), names.get(s.to()), s.amount()));

        separator("6. CategoryFormatter (sealed interface demo)");
        List<ExpenseCategory> cats = List.of(
            new FoodExpense(), new TravelExpense(), new UtilityExpense(),
            new EntertainmentExpense(), new SettlementExpense(),
            new CustomExpense("Groceries", "[GROCERY]")
        );
        cats.forEach(c -> System.out.println("  " + CategoryFormatter.format(c)));
    }

    private static void separator(String title) {
        System.out.println("\n" + "─".repeat(55));
        System.out.println("  " + title);
        System.out.println("─".repeat(55));
    }
}
