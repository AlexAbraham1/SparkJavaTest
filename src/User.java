import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "users") //Define SQL Table
public class User
{
    @DatabaseField(generatedId = true) //auto-incrament
    private int id;

    @DatabaseField
    private String username;

    @DatabaseField
    private String email;

    public User() {} //ORMLite requires an empty constructor to do its magic

    public int getId() {return this.id;}

    public String getUsername() {return this.username;}
    public void setUsername(String username) {this.username = username;}

    public String getEmail() {return this.email;}
    public void setEmail(String email) {this.email = email;}


}
