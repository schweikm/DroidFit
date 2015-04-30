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

    @Override
    public void onResume() {
        super.onResume();
        //TODO: get current user's activities
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
        final MenuItem deleteMI = menu.findItem(R.id.menu_main_delete);
        final MenuItem editMI = menu.findItem(R.id.menu_main_edit);
        final MenuItem newMI = menu.findItem(R.id.menu_main_new);
        final MenuItem detailsMI = menu.findItem(R.id.menu_main_details);

        if (itemSelected != -1) {
            deleteMI.setEnabled(true);
            editMI.setEnabled(true);
            detailsMI.setEnabled(true);
            newMI.setEnabled(true);
        } else {
            newMI.setEnabled(true);
            deleteMI.setEnabled(false);
            editMI.setEnabled(false);
            detailsMI.setEnabled(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        // pass the account to the main activity
        final Bundle bundle = new Bundle();
        bundle.putSerializable("account", myAccount);

        Intent intent = null;

        // hooray Strategy pattern!
        DroidFitActivityCreateBehavior createBehavior = null;
        DroidFitActivityExecuteBehavior executeBehavior = null;

        if (item.getItemId() == R.id.menu_main_new) {
            intent = new Intent(getActivity(), DroidFitActivityActivity.class);
            createBehavior = new NewActivityCreateBehavior();
            executeBehavior = new NewActivityExecuteBehavior();

            bundle.putSerializable("createBehavior", createBehavior);
            bundle.putSerializable("executeBehavior", executeBehavior);
        }
        if (item.getItemId() == R.id.menu_main_delete) {
            Log.i(getClass().getSimpleName(), "DELETE ACTIVITY " + itemSelected);
        }
        if (item.getItemId() == R.id.menu_main_edit) {
            Log.i(getClass().getSimpleName(), "EDIT ACTIVITY " + itemSelected);
        }
        if (item.getItemId() == R.id.menu_main_details) {
            Log.i(getClass().getSimpleName(), "DETAILS ACTIVITY " + itemSelected);
        }

        if (null == intent) {
            Log.e(getClass().getSimpleName(), "No intent to execute!");
            return false;
        }

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
        final List<DroidFitActivity> activities = helper.getUserActivities(account);

        for (final DroidFitActivity activity : activities) {
            adapter.add(activity.getText());
        }

        return adapter;
    }
}
