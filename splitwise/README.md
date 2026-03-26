# Splitwise — Java 21 LLD

A clean, extensible Splitwise low-level design using Java 21+ features:
`sealed interface`, `record`, pattern matching `switch`, Builder, Strategy, Factory, Observer.

## Project Structure

```
src/main/java/splitwise/
├── model/            — Value objects (UserId, Money, User, Group)  [records]
├── split/            — Sealed Split hierarchy + SplitStrategyFactory
├── expense/          — Sealed ExpenseCategory + Expense (Builder)
├── balance/          — BalanceLedger + Settlement + debt simplification
├── observer/         — ExpenseObserver, NotificationService, ConsoleNotifier
├── service/          — ExpenseService (orchestration)
└── SplitwiseDemo.java
```

## Prerequisites
- Java 21+

## Compile & Run

```bash
chmod +x compile.sh run.sh compile_and_run.sh
./compile_and_run.sh
```

Or step by step:
```bash
./compile.sh
./run.sh
```

## Extending

**New split type** (e.g., `TieredSplit`):
1. Add `TieredSplit` to `Split permits` list
2. Create `record TieredSplit(...) implements Split` in `splitwise.split`
3. Add `case TIERED ->` in `SplitStrategyFactory`
4. Add `TIERED` to `SplitType` enum

**New expense category** (e.g., `MedicalExpense`):
1. Add `MedicalExpense` to `ExpenseCategory permits` list
2. Create `record MedicalExpense() implements ExpenseCategory`
3. Add `case MedicalExpense m ->` in `CategoryFormatter` (compiler will warn if missed!)
