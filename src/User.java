import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "users") //Define SQL Table
public class User
{
    @DatabaseField(generatedId = true) //auto-increment
    private int id;

    @DatabaseField
    private String name;

    @DatabaseField
    private String email;

    @DatabaseField
    private String password; //This should be encrypted!!!

    public User() {} //ORMLite requires an empty constructor to do its magic

    public int getId() {return this.id;}

    public String getName() {return this.name;}
    public void setName(String name) {this.name = name;}

    public String getEmail() {return this.email;}
    public void setEmail(String email) {this.email = email;}

    public String getPassword() {return password;}
    public void setPassword(String password) {this.password = password;}

}