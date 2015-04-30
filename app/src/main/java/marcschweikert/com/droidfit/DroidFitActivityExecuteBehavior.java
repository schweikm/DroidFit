package marcschweikert.com.droidfit;

import android.app.Activity;
import android.os.Bundle;

import java.io.Serializable;

import marcschweikert.com.database.Account;

/**
 * Created by Marc on 4/26/2015.
 */
public abstract class DroidFitActivityExecuteBehavior implements Serializable {
    public abstract boolean doOnExecute(final Bundle savedInstanceState, final Activity androidActivity, final Account account);
}
