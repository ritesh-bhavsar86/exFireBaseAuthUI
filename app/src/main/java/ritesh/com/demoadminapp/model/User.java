package ritesh.com.demoadminapp.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable{
    String uid;
    String email_id;
    String mobile;
    String uname;
    String provider;
    String photo_url;
    private Uri mPhotoUri;
    String storage_url;
    boolean isAdmin;
    boolean isAuthor;
    boolean isActivated;
    boolean isEmailVerified;



    public User(){
        this.isActivated = false;
        this.isAdmin = false;
        this.isAuthor = false;
        this.isEmailVerified = false;
    }
    public User(String uid, String email_id, String mobile
            , String uname, String provider, String photo_url
            , String storage_url, boolean isAdmin, boolean isAuthor) {
        this.uid = uid;
        this.email_id = email_id;
        this.mobile = mobile;
        this.uname = uname;
        this.provider = provider;
        this.photo_url = photo_url;
        this.storage_url = storage_url;
        this.isAdmin = isAdmin;
        this.isAuthor = isAuthor;
        this.isActivated = false;
        this.isEmailVerified = false;
//        this.isAdmin = false;
//        this.isAuthor = false;

    }

    protected User(Parcel in) {
        uid = in.readString();
        email_id = in.readString();
        mobile = in.readString();
        uname = in.readString();
        provider = in.readString();
        photo_url = in.readString();
        mPhotoUri = in.readParcelable(Uri.class.getClassLoader());
        storage_url = in.readString();
        isAdmin = in.readByte() != 0;
        isAuthor = in.readByte() != 0;
        isActivated = in.readByte() != 0;
        isEmailVerified = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uid);
        dest.writeString(email_id);
        dest.writeString(mobile);
        dest.writeString(uname);
        dest.writeString(provider);
        dest.writeString(photo_url);
        dest.writeParcelable(mPhotoUri, flags);
        dest.writeString(storage_url);
        dest.writeByte((byte) (isAdmin ? 1 : 0));
        dest.writeByte((byte) (isAuthor ? 1 : 0));
        dest.writeByte((byte) (isActivated ? 1 : 0));
        dest.writeByte((byte) (isEmailVerified ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public boolean isEmailVerified() {
        return isEmailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        isEmailVerified = emailVerified;
    }

    public Uri getmPhotoUri() {
        return mPhotoUri;
    }

    public void setmPhotoUri(Uri mPhotoUri) {
        this.mPhotoUri = mPhotoUri;
    }

    public boolean isActivated() {
        return isActivated;
    }

    public void setActivated(boolean activated) {
        isActivated = activated;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail_id() {
        return email_id;
    }

    public void setEmail_id(String email_id) {
        this.email_id = email_id;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getPhoto_url() {
        return photo_url;
    }

    public void setPhoto_url(String photo_url) {
        this.photo_url = photo_url;
    }

    public String getStorage_url() {
        return storage_url;
    }

    public void setStorage_url(String storage_url) {
        this.storage_url = storage_url;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public boolean isAuthor() {
        return isAuthor;
    }

    public void setAuthor(boolean author) {
        isAuthor = author;
    }
}