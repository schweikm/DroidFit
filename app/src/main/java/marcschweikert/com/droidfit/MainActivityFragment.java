package marcschweikert.com.droidfit;

import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

import marcschweikert.com.database.Account;
import marcschweikert.com.database.DatabaseFacade;

/**
 * Created by Marc on 4/18/2015.
 */
public class MainActivityFragment extends ListFragment {
    private int itemSelected = -1;
    private Account myAccount;
    private List<DroidFitActivity> myActivities;

    @Override
    public void onResume() {
        super.onResume();
        setListAdapter(getAllActivities(myAccount));
    }

    @Override
    public void onPause() {
        super.onPause();
        //TODO: save activities?
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        // get the account from the intent
        final Bundle bundle = getActivity().getIntent().getExtras();
        myAccount = (Account) bundle.getSerializable("account");

        Log.i(getClass().getSimpleName(), "FRAGMENT STARTED FOR ->" + myAccount.getEmail() + "<-");
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setEmptyText(getResources().getString(R.string.main_no_activities));
        setListAdapter(getAllActivities(myAccount));
        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_activity_menu, menu);
    }

    @Override
    public void onPrepareOptionsMenu(final Menu menu) {
        super.onPrepareOptionsMenu(menu);

        // UI references
        final MenuItem newMI = menu.findItem(R.id.menu_main_new);
        final MenuItem editMI = menu.findItem(R.id.menu_main_edit);
        final MenuItem detailsMI = menu.findItem(R.id.menu_main_details);
        final MenuItem logoutMI = menu.findItem(R.id.menu_main_logout);

        // these are always enabled
        newMI.setEnabled(true);
        logoutMI.setEnabled(true);

        if (itemSelected != -1) {
            editMI.setEnabled(true);
            detailsMI.setEnabled(true);
        } else {
            editMI.setEnabled(false);
            detailsMI.setEnabled(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        // logout is easy
        if (item.getItemId() == R.id.menu_main_logout) {
            // make sure values are invalidated
            itemSelected = -1;
            myAccount = null;
            myActivities = null;

            // start the login screen
            final Intent intent = new Intent(getActivity(), LoginActivity.class);

            // we're done here
            getActivity().finish();
            return true;
        }

        // other types will launch a new activity
        final Bundle bundle = new Bundle();
        final Intent intent = new Intent(getActivity(), DroidFitActivityActivity.class);

        DroidFitActivity activity = null;
        if (itemSelected >= 0) {
            activity = myActivities.get(itemSelected);
        }

        // hooray Strategy pattern!
        DroidFitActivityCreateBehavior createBehavior = null;
        DroidFitActivityExecuteBehavior executeBehavior = null;

        if (item.getItemId() == R.id.menu_main_new) {
            // no create behavior
            executeBehavior = new NewActivityExecuteBehavior();
        } else if (item.getItemId() == R.id.menu_main_edit) {
            createBehavior = new EditActivityCreateBehavior();
            executeBehavior = new EditActivityExecuteBehavior();
        } else if (item.getItemId() == R.id.menu_main_details) {
            createBehavior = new ViewActivityCreateBehavior();
            // no execute behavior
        }

        // pass the attributes to the main activity
        bundle.putSerializable("account", myAccount);
        bundle.putSerializable("activity", activity);
        bundle.putSerializable("createBehavior", createBehavior);
        bundle.putSerializable("executeBehavior", executeBehavior);
        intent.putExtras(bundle);
        startActivity(intent);

        return true;
    }

    @Override
    public void onListItemClick(final ListView l, final View v, final int position, final long id) {
        itemSelected = position;
        getActivity().invalidateOptionsMenu();
    }

    public ArrayAdapter<String> getAllActivities(final Account account) {
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_single_choice);

        // pull activities from the database for the current account
        final DatabaseFacade helper = new DatabaseFacade(getActivity());
        myActivities = helper.getUserActivities(account);

        for (final DroidFitActivity activity : myActivities) {
            adapter.add(activity.getText());
        }

        return adapter;
    }
}
