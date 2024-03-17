package ca.dmi.uqtr.applicationchat;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;
import androidx.databinding.InverseBindingAdapter;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class BinderExtensions {

    @BindingAdapter("app:textValue")
    public static void setTextValue(EditText view, String value) {
        if (value != null && !value.equals(view.getText().toString())) {
            view.setText(value);
        }
    }

    @InverseBindingAdapter(attribute = "app:textValue", event = "android:textAttrChanged")
    public static String getTextValue(EditText view) {
        return view.getText().toString();
    }


    @BindingAdapter("app:imageUrl")
    public static void setUrl(ImageView imageView, String url) {
        if (url != null && !url.isEmpty()) {
            Glide.with(imageView.getContext())
                    .load(url)
                    .into(imageView);

        }
    }

    @BindingAdapter("formattedTime")
    public static void setFormattedTime(TextView textView, Date date) {
        if (date != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            String formattedTime = dateFormat.format(date);
            textView.setText(formattedTime);
        }
    }


}
