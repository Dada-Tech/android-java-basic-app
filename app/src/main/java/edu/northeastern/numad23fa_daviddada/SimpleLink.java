package edu.northeastern.numad23fa_daviddada;

import android.os.Parcel;
import android.os.Parcelable;

public class SimpleLink implements Parcelable {
    public String title;
    public String url;

    public SimpleLink(String title, String url) {
        this.title = title;
        this.url = url;
    }

    // Parcel constructor
    protected SimpleLink(Parcel in) {
        title = in.readString();
        url = in.readString();
    }

    // anonymous class for required member
    public static final Creator<SimpleLink> CREATOR = new Creator<SimpleLink>() {
        @Override
        public SimpleLink createFromParcel(Parcel in) {
            return new SimpleLink(in);
        }

        @Override
        public SimpleLink[] newArray(int size) {
            return new SimpleLink[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(url);
    }

    // skip
    @Override
    public int describeContents() {
        return 0;
    }
}
