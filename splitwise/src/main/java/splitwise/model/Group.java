package splitwise.model;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public final class Group {
    private final GroupId id;
    private final String name;
    private final Set<UserId> members;
    private final List<ExpenseId> expenseIds;

    public Group(String name, Set<UserId> initialMembers) {
        this.id         = GroupId.generate();
        this.name       = Objects.requireNonNull(name, "Name cannot be null");
        Objects.requireNonNull(initialMembers, "Initial members cannot be null");
        this.members    = ConcurrentHashMap.newKeySet();
        this.members.addAll(initialMembers);
        this.expenseIds = new CopyOnWriteArrayList<>();
    }

    public void addMember(UserId uid) {
        Objects.requireNonNull(uid, "UserId cannot be null");
        members.add(uid);
    }

    public void addExpense(ExpenseId eid) {
        Objects.requireNonNull(eid, "ExpenseId cannot be null");
        expenseIds.add(eid);
    }

    public GroupId id()                  { return id; }
    public String name()                 { return name; }
    public Set<UserId> members()         { return Collections.unmodifiableSet(members); }
    public List<ExpenseId> expenseIds()  { return Collections.unmodifiableList(expenseIds); }

    @Override public String toString() { return "Group[" + name + "]"; }
}
