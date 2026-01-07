package Model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserBean {
	public int userId;
	public String userName;
	public String email;
	public String password;
	public Integer channelId; 
	public String userRole;
	public String userStatus;
	
	public UserBean() {
		
	}
	
	public UserBean(String name, String email, String password) {
        this.userName = name;
        this.email = email;
        this.password = password;
    }
}
