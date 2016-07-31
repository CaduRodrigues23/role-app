package br.com.mytho.role.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import br.com.mytho.role.R;
import br.com.mytho.role.activity.delegate.AccessTokenDelegate;
import br.com.mytho.role.activity.delegate.EventDelegate;
import br.com.mytho.role.adapter.ViewPagerAdapter;
import br.com.mytho.role.facade.AccessTokenFacade;
import br.com.mytho.role.facade.EventFacade;
import br.com.mytho.role.fragments.HighlightedFragment;
import br.com.mytho.role.fragments.NearYouFragment;
import br.com.mytho.role.fragments.SuggestedFragment;
import br.com.mytho.role.model.Event;
import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by leonardocordeiro on 26/06/16.
 */
public class MainActivity extends AppCompatActivity implements AccessTokenDelegate, EventDelegate {

    @BindView(R.id.toolbar_main)
    Toolbar toolbar;
    @BindView(R.id.tabanim_tabs)
    TabLayout tabLayout;
    @BindView(R.id.viewpager)
    ViewPager mViewPager;

    private ArrayList<Event> events;
    private AccessTokenFacade accessTokenFacade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.accessTokenFacade = new AccessTokenFacade(this);
        accessTokenFacade.getAccessToken();

        ButterKnife.bind(this);

        setupToolbar();
        setupNavigationDrawer();

        EventBus.getDefault().register(this);
    }

    @Subscribe
    public void handleUnauthorize(Throwable t) {
        accessTokenFacade.retry();
    }

    @Override
    public void onReceiveAccessToken() {
        EventFacade eventFacade = new EventFacade(this);
        eventFacade.getEvents();
    }


    @Override
    public void onEvents(List<Event> events) {
        this.events = (ArrayList<Event>) events;
        setupViewPager();
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setupNavigationDrawer() {
        AccountHeader accountHeader = new AccountHeaderBuilder()
                .withActivity(this)
                //Imagem do account header
                .withHeaderBackground(R.drawable.account_header_image)
                .addProfiles(
                        new ProfileDrawerItem().withName("Ban Ban")
                                .withEmail("BamBam@monstro.com")
                                //Imagem do perfil
                                .withIcon((R.drawable.perfil_image_3)
                                ))
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        //
                        Toast.makeText(getApplication(), "acc", Toast.LENGTH_SHORT);
                        return false;
                    }
                })
                .build();
        PrimaryDrawerItem item1 = new PrimaryDrawerItem()
                .withIdentifier(1)
                .withName("Minha conta");

        SecondaryDrawerItem item2 = (SecondaryDrawerItem) new SecondaryDrawerItem()
                .withIdentifier(2)
                .withIcon(R.drawable.ic_history_black_24dp)
                .withName("Historico");

        SecondaryDrawerItem item3 = (SecondaryDrawerItem) new SecondaryDrawerItem()
                .withIdentifier(3)
                .withIcon(R.drawable.ic_settings_black_24dp)
                .withName("Configurções");

        SecondaryDrawerItem item4 = (SecondaryDrawerItem) new SecondaryDrawerItem()
                .withIdentifier(4)
                .withIcon(R.drawable.ic_close_black_24dp)
                .withName("Sair");

        Drawer result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(accountHeader)
                .addDrawerItems(
                        item1,
                        new DividerDrawerItem(),
                        item2,
                        item3,
                        item4
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        // do something with the clicked item :D
                        Toast.makeText(getApplication(), "biurl :" + position, Toast.LENGTH_SHORT).show();
                        return false;
                    }
                })
                .build();
    }

    private void setupViewPager() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        Bundle bundle = new Bundle();
        bundle.putSerializable("events", events);

        SuggestedFragment suggestedFragment = new SuggestedFragment();
        suggestedFragment.setArguments(bundle);

        adapter.addFragment(suggestedFragment, getResources().getString(R.string.suggested));
        adapter.addFragment(new HighlightedFragment(), getResources().getString(R.string.highlighted));
        adapter.addFragment(new NearYouFragment(), getResources().getString(R.string.near_you));

        mViewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(mViewPager);
    }

}