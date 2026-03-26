package splitwise.model;

import java.util.Objects;

public final class User {
    private final UserId id;
    private final String name;
    private final String email;

    public User(String name, String email) {
        this.id    = UserId.generate();
        this.name  = Objects.requireNonNull(name, "Name cannot be null");
        this.email = Objects.requireNonNull(email, "Email cannot be null");
    }

    public UserId id()    { return id; }
    public String name()  { return name; }
    public String email() { return email; }

    @Override public boolean equals(Object o) { return o instanceof User u && id.equals(u.id); }
    @Override public int hashCode()            { return id.hashCode(); }
    @Override public String toString()         { return "User[" + name + "]"; }
}
