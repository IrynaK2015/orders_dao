package order_pack.model;

public class Client {
    @Id
    private int id;
    private String fullname;
    private String email;

    public Client() {}

    public Client(String fullname, String email) {
        this.fullname = fullname;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return String.format(
            "Client %s with ID=%d. Contact email %s", fullname, id, email
        );
    }
}
