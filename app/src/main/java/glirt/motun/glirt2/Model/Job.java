package glirt.motun.glirt2.Model;

public class Job {
    private String UserName;
    private String UserAddress;
    private String UserNumber;
    private String UserDescription;
    private String UserProfession;
    private String UserLocation;


    public Job() {
    }

    public Job(String userName, String userAddress, String userNumber, String userDescription, String userProfession, String userLocation) {
        UserName = userName;
        UserAddress = userAddress;
        UserNumber = userNumber;
        UserDescription = userDescription;
        UserProfession = userProfession;
        UserLocation = userLocation;

    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getUserAddress() {
        return UserAddress;
    }

    public void setUserAddress(String userAddress) {
        UserAddress = userAddress;
    }

    public String getUserNumber() {
        return UserNumber;
    }

    public void setUserNumber(String userNumber) {
        UserNumber = userNumber;
    }

    public String getUserDescription() {
        return UserDescription;
    }

    public void setUserDescription(String userDescription) {
        UserDescription = userDescription;
    }

    public String getUserProfession() {
        return UserProfession;
    }

    public void setUserProfession(String userProfession) {
        UserProfession = userProfession;
    }

    public String getUserLocation() {
        return UserLocation;
    }

    public void setUserLocation(String userLocation) {
        UserLocation = userLocation;
    }
}
