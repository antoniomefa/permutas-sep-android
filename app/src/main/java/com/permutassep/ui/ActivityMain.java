package com.permutassep.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.facebook.login.LoginManager;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.lalongooo.permutassep.R;
import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.permutassep.presentation.AndroidApplication;
import com.permutassep.presentation.interfaces.FirstLaunchCompleteListener;
import com.permutassep.presentation.interfaces.FloatingActionButtonClickListener;
import com.permutassep.presentation.interfaces.FragmentMenuItemSelectedListener;
import com.permutassep.presentation.interfaces.LoginCompleteListener;
import com.permutassep.presentation.interfaces.PostListListener;
import com.permutassep.presentation.internal.di.HasComponent;
import com.permutassep.presentation.internal.di.components.DaggerPostComponent;
import com.permutassep.presentation.internal.di.components.PostComponent;
import com.permutassep.presentation.internal.di.modules.PostModule;
import com.permutassep.presentation.model.PostModel;
import com.permutassep.presentation.model.UserModel;
import com.permutassep.presentation.navigation.Navigator;
import com.permutassep.presentation.utils.ParseUtils;
import com.permutassep.presentation.utils.PrefUtils;
import com.permutassep.presentation.view.HomeView;
import com.permutassep.push.PushBroadcastReceiver;

import java.util.HashMap;

import javax.inject.Inject;

/**
 * Main application screen. This is the app entry point.
 */
public class ActivityMain extends BaseActivity
        implements HomeView,
        HasComponent<PostComponent>,
        PostListListener,
        Navigator.NavigationListener,
        FragmentManager.OnBackStackChangedListener,
        FragmentSignUp.FacebookSignUpListener,
        LoginCompleteListener,
        FirstLaunchCompleteListener,
        FragmentMenuItemSelectedListener,
        FragmentSearch.SearchPerformer,
        FloatingActionButtonClickListener {


    public static final int DRAWER_IDENTIFIER_HOME = 1;
    public static final int DRAWER_IDENTIFIER_MY_POSTS = 2;

    @Inject
    Toolbar toolbar;

    private Drawer drawer;
    private UserModel userModel;
    private PostComponent postComponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportFragmentManager().addOnBackStackChangedListener(this);

        this.initializeInjector();

        if (getIntent().getBooleanExtra(FragmentNewPassword.EXTRA_RESET_PASSWORD, false)) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new FragmentNewPassword()).commit();
        } else {
            userModel = PrefUtils.getUser(this);
            if (userModel != null) {
                if (getIntent().getStringExtra(PushBroadcastReceiver.PUSH_POST_ID_EXTRA) != null) {
                    int psPostId = Integer.valueOf(getIntent().getStringExtra(PushBroadcastReceiver.PUSH_POST_ID_EXTRA));
                    navigator.navigateToPostDetailsFromNotification(this, psPostId);
                } else {
                    navigator.navigateToPostList(this, true);
                    ParseUtils.setUpParseInstallationUser(userModel.getId());
                }
                renderDrawerOptions();
                PrefUtils.markLoggedUser(this, true);
            } else {
                navigator.navigateToLoginSignUp(this, true);
            }
        }
    }

    private void initializeInjector() {

        this.postComponent = DaggerPostComponent.builder()
                .applicationComponent(getApplicationComponent())
                .activityModule(getActivityModule())
                .postModule(new PostModule())
                .build();
        this.postComponent.inject(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Methods from the {@link HomeView} interface
     */
    @Override
    public void renderDrawerOptions() {

        IProfile profile = new ProfileDrawerItem()
                .withName(userModel.getName())
                .withEmail(userModel.getEmail())
                .withIcon(Uri.parse(userModel.getProfilePictureUrl()));

        AccountHeader accountHeader = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header)
                .addProfiles(profile)
                .build();

        drawer = new DrawerBuilder()
                .withActivity(this)
                .withActionBarDrawerToggle(true)
                .withAccountHeader(accountHeader)
                .withToolbar(toolbar)
                .addDrawerItems(
                        new PrimaryDrawerItem()
                                .withName(getString(R.string.app_nav_drawer_1))
                                .withIdentifier(ActivityMain.DRAWER_IDENTIFIER_HOME)
                                .withIcon(GoogleMaterial.Icon.gmd_home),

                        new PrimaryDrawerItem()
                                .withName(getString(R.string.app_nav_drawer_2))
                                .withIdentifier(ActivityMain.DRAWER_IDENTIFIER_MY_POSTS)
                                .withIcon(GoogleMaterial.Icon.gmd_content_paste))
                .withSelectedItem(DRAWER_IDENTIFIER_HOME)
                .build();

        drawer.setOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
            @Override
            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                onDrawerItemSelected(drawerItem.getIdentifier());
                return false;
            }
        });
    }

    @Override
    public void onDrawerItemSelected(int drawerItemId) {
        switch (drawerItemId) {
            case DRAWER_IDENTIFIER_HOME:

                ((AndroidApplication) getApplication())
                        .getTracker()
                        .send(new HitBuilders.EventBuilder()
                                .setCategory(getString(R.string.ga_event_category_ux))
                                .setAction(getString(R.string.ga_event_action_click))
                                .setLabel(getString(R.string.ga_app_home))
                                .build());
                if (!(getCurrentDisplayedFragment() instanceof FragmentPagedNewsFeed)) {
                    getSupportFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    navigator.navigateToPostList(this, true);
                    setActionBarTitle(R.string.app_name);
                }

                break;
            case DRAWER_IDENTIFIER_MY_POSTS:

                ((AndroidApplication) getApplication())
                        .getTracker()
                        .send(new HitBuilders.EventBuilder()
                                .setCategory(getString(R.string.ga_event_category_ux))
                                .setAction(getString(R.string.ga_event_action_click))
                                .setLabel(getString(R.string.ga_app_my_posts))
                                .build());
                if (!(getCurrentDisplayedFragment() instanceof FragmentMyPosts)) {
                    navigator.navigateToUserPostList(this, this.userModel.getId(), true);
                    setActionBarTitle(R.string.app_main_toolbar_title_my_posts);
                }

                break;
        }
    }

    @Override
    public PostComponent getComponent() {
        return postComponent;
    }

    @Override
    public void onBackPressed() {
        if (drawer != null && drawer.isDrawerOpen()) {
            drawer.closeDrawer();
        } else if (getIntent().getStringExtra(PushBroadcastReceiver.PUSH_POST_ID_EXTRA) != null) {
            getIntent().removeExtra(PushBroadcastReceiver.PUSH_POST_ID_EXTRA);
            getSupportFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            navigator.navigateToPostList(this, true);
            navigator.navigateToPostList(this, true);
        } else {
            super.onBackPressed();
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                finish();
            }
        }
    }

    /**
     * Method from {@link PostListListener}
     */
    @Override
    public void onPostClicked(PostModel postModel) {
        navigator.navigateToPostDetails(this, postModel.getId());
    }

    /**
     * Method from {@link Navigator.NavigationListener}
     */
    @Override
    public void onNextFragment(Class c) {
        if (c == FragmentLogin.class) {
            navigator.navigateToLogin(this);
        }

        if (c == FragmentSignUp.class) {
            navigator.navigateToSignUp(this);
        }

        if (c == FragmentPagedNewsFeed.class) {
            navigator.navigateToPostList(this, true);
            setActionBarTitle(R.string.app_name);
        }

        if (c == FragmentLoginSignUp.class) {
            navigator.navigateToLoginSignUp(this, true);
        }
    }

    /**
     * Method from {@link FragmentManager.OnBackStackChangedListener}
     */
    @Override
    public void onBackStackChanged() {

        Fragment currentFragment = getCurrentDisplayedFragment();
        if (currentFragment instanceof FragmentLoginSignUp) {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.hide();
            }
        } else if (currentFragment instanceof FragmentPagedNewsFeed) {
            if (drawer != null) drawer.setSelection(DRAWER_IDENTIFIER_HOME, false);
            setActionBarTitle(R.string.app_name);
        } else if (currentFragment instanceof FragmentSearch) {
            setActionBarTitle(R.string.app_main_toolbar_title_search);
        } else if (currentFragment instanceof FragmentMyPosts) {
            setActionBarTitle(R.string.app_main_toolbar_title_my_posts);
        } else if (currentFragment instanceof FragmentPostDetail) {
            setActionBarTitle(R.string.app_main_toolbar_title_post_details);
        }
    }

    /**
     * Method from {@link FragmentSignUp.FacebookSignUpListener}
     */
    @Override
    public void onFacebookSignUp(Bundle bundle) {
        navigator.navigateToCompleteFbData(this, bundle);
    }

    /**
     * Method from the {@link LoginCompleteListener}
     */
    @Override
    public void onLoginComplete(UserModel userModel) {
        this.userModel = userModel;
        PrefUtils.putUser(this, userModel);
        ParseUtils.setUpParseInstallationUser(userModel.getId());
        PrefUtils.markLoggedUser(this, true);
        renderDrawerOptions();
    }

    /**
     * Method from {@link FirstLaunchCompleteListener}
     */
    @Override
    public void onFirstLaunchComplete() {
        if (PrefUtils.isFirstTimeLogin(this) && userModel != null) {
            PrefUtils.setNotFirstTimeLogin(this);
            drawer.openDrawer();
        }
    }

    /**
     * Method from {@link FragmentMenuItemSelectedListener}
     */
    @Override
    public void onMenuItemSelected(int menuId) {

        switch (menuId) {

            case R.id.action_search:
                this.navigator.navigateToSearchPosts(this, true);
                setActionBarTitle(R.string.app_main_toolbar_title_search);
                break;

            case R.id.action_logout:

                Tracker t1 = ((AndroidApplication) getApplication()).getTracker();
                t1.send(new HitBuilders.EventBuilder()
                        .setCategory(getString(R.string.ga_event_category_ux))
                        .setAction(getString(R.string.ga_event_action_click))
                        .setLabel(getString(R.string.ga_app_logout))
                        .build());

                PrefUtils.markLoggedUser(this, false);
                PrefUtils.clearApplicationPreferences(this);
                ParseUtils.clearParseInstallationUser();
                LoginManager.getInstance().logOut();
                navigator.navigateToStart(this);

                break;

            case R.id.action_about:

                Tracker t2 = ((AndroidApplication) getApplication()).getTracker();
                t2.send(new HitBuilders.EventBuilder()
                        .setCategory(getString(R.string.ga_event_category_ux))
                        .setAction(getString(R.string.ga_event_action_click))
                        .setLabel(getString(R.string.ga_app_about))
                        .build());

                new LibsBuilder()
                        .withFields(R.string.class.getFields())
                        .withActivityTitle(getString(R.string.about_activity_title))
                        .withAboutIconShown(true)
                        .withAboutDescription(getString(R.string.about_activity_description))
                        .withActivityStyle(Libs.ActivityStyle.LIGHT_DARK_TOOLBAR)
                        .start(this);

                break;
        }
    }

    /**
     * Method from {@link FragmentSearch.SearchPerformer}
     */
    @Override
    public void onPerformSearch(HashMap<String, String> searchParams) {
        navigator.navigateToSearchPostsResults(this, true, searchParams);
        setActionBarTitle(R.string.app_main_toolbar_title_search_results);
    }

    private Fragment getCurrentDisplayedFragment() {
        return getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
    }

    private void setActionBarTitle(int resId) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(resId);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getStringExtra(PushBroadcastReceiver.PUSH_POST_ID_EXTRA) != null) {
            int psPostId = Integer.valueOf(intent.getStringExtra(PushBroadcastReceiver.PUSH_POST_ID_EXTRA));
            this.navigator.navigateToPostDetails(this, psPostId);
        }
    }

    /**
     * Method from {@link FloatingActionButtonClickListener}
     */

    @Override
    public void onFloatingActionButtonClick() {
        this.navigator.navigateToWritePost(this);
    }
}