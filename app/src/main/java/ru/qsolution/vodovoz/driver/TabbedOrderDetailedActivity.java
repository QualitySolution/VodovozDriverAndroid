package ru.qsolution.vodovoz.driver;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import java.util.concurrent.ExecutionException;

import ru.qsolution.vodovoz.driver.AsyncTasks.GetOrderDetailedTask;
import ru.qsolution.vodovoz.driver.DTO.Order;
import ru.qsolution.vodovoz.driver.Workers.ServiceWorker;

public class TabbedOrderDetailedActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private SharedPreferences sharedPref;
    private Order order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabbed_order_detailed);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        Context context = this.getApplicationContext();
        sharedPref = context.getSharedPreferences(getString(R.string.auth_file_key), Context.MODE_PRIVATE);
        Bundle extras = getIntent().getExtras();

        try {
            order = new GetOrderDetailedTask()
                    .execute(sharedPref.getString("Authkey", ""), extras.getString("OrderId")).get();

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return OrderInfoFragmentActivity.newInstance(position, order);
                case 1:
                    return OrderContactsFragmentActivity.newInstance(position, order);
                default:
                    return OrderItemsFragmentActivity.newInstance(position, order);
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Общее";
                case 1:
                    return "Контакты";
                case 2:
                    return "Заказ";
            }
            return null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate your main_menu into the menu
        getMenuInflater().inflate(R.menu.route_lists_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        if (item.getItemId() == R.id.taskChangeUserBtn)
        {
            ServiceWorker.StopLocationService(this);

            SharedPreferences.Editor editor = sharedPref.edit();
            editor.remove("Authkey");
            editor.apply();

            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
        } else if (item.getItemId() == R.id.taskExitBtn) {
            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.putExtra("EXIT", true);
            startActivity(i);
            finish();
        } else if (item.getItemId() == R.id.taskShutdownBtn) {
            ServiceWorker.StopLocationService(this);
            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.putExtra("EXIT", true);
            startActivity(i);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
