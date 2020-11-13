package kr.co.golearn.view.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kr.co.golearn.R;
import kr.co.golearn.util.PreferenceManager;
import kr.co.golearn.view.main.article.ArticleFragment;
import kr.co.golearn.view.main.channel.ChannelActivity;
import kr.co.golearn.view.main.home.HomeFragment;
import kr.co.golearn.view.main.subscription.SubscriptionFragment;
import kr.co.golearn.viewmodel.AccountViewModel;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.main_bottom_navigation)
    BottomNavigationView bottomNavigationView;
    @BindView(R.id.main_profile)
    ImageView imgProfile;
    @BindView(R.id.main_app_name)
    TextView mainAppName;
    @BindView(R.id.main_layout)
    ConstraintLayout mainLayout;
    @BindView(R.id.main_btn_search)
    ImageView mainBtnSearch;
    @BindView(R.id.main_edt_search)
    EditText mainEdtSearch;

    private FragmentManager fragmentManager;
    private HomeFragment homeFragment;
    private SubscriptionFragment subscriptionFragment;
    private ArticleFragment articleFragment;
    private AccountViewModel mainViewModel;

    private long pressedBackButtonTime;
    private boolean isSearchMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        actionViewModel();
        addEventListener();
    }

    private void addEventListener() {
        mainEdtSearch.setOnKeyListener((view, keyCode, keyEvent) -> {
            if (keyCode == keyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_UP) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mainEdtSearch.getWindowToken(), 0);
                searchToCourse(mainEdtSearch.getText().toString());
            }
            return false;
        });
    }

    private void actionViewModel() {
        mainViewModel = ViewModelProviders.of(this).get(AccountViewModel.class);
        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(this);
        circularProgressDrawable.setStrokeWidth(5f);
        circularProgressDrawable.setCenterRadius(30f);
        circularProgressDrawable.start();
        mainViewModel.getMember().observe(this, member -> {
            Glide.with(this).load(member.getProfile())
                    .placeholder(circularProgressDrawable)
                    .circleCrop()
                    .into(imgProfile);
            new PreferenceManager().setLong(this, PreferenceManager.USER_NO, member.getNo());
            new PreferenceManager().setString(this, PreferenceManager.USER_NICKNAME, member.getNickname());
            new PreferenceManager().setString(this, PreferenceManager.USER_PROFILE, member.getProfile());
        });

        mainViewModel.getMember(this);
    }

    private void init() {
        ButterKnife.bind(this);

        fragmentManager = getSupportFragmentManager();
        homeFragment = new HomeFragment();
        fragmentManager.beginTransaction().replace(R.id.main_frame, homeFragment).commit();
    }

    @OnClick(R.id.page_home)
    public void clickToHome() {
        if (homeFragment == null) {
            homeFragment = new HomeFragment();
            fragmentManager.beginTransaction().add(R.id.main_frame, homeFragment).commit();
        }

        if (homeFragment != null) {
            fragmentManager.beginTransaction().show(homeFragment).commit();
        }
        if (subscriptionFragment != null) {
            fragmentManager.beginTransaction().hide(subscriptionFragment).commit();
        }
        if (articleFragment != null) {
            fragmentManager.beginTransaction().hide(articleFragment).commit();
        }
    }

    @OnClick(R.id.page_subscription)
    public void clickToSubscription() {
        if (subscriptionFragment == null) {
            subscriptionFragment = new SubscriptionFragment();
            fragmentManager.beginTransaction().add(R.id.main_frame, subscriptionFragment).commit();
        }

        if (homeFragment != null) {
            fragmentManager.beginTransaction().hide(homeFragment).commit();
        }
        if (subscriptionFragment != null) {
            fragmentManager.beginTransaction().show(subscriptionFragment).commit();
        }
        if (articleFragment != null) {
            fragmentManager.beginTransaction().hide(articleFragment).commit();
        }
    }

    @OnClick(R.id.page_article)
    public void clickToArticle() {
        if (articleFragment == null) {
            articleFragment = new ArticleFragment();
            fragmentManager.beginTransaction().add(R.id.main_frame, articleFragment).commit();
        }

        if (homeFragment != null) {
            fragmentManager.beginTransaction().hide(homeFragment).commit();
        }
        if (subscriptionFragment != null) {
            fragmentManager.beginTransaction().hide(subscriptionFragment).commit();
        }
        if (articleFragment != null) {
            fragmentManager.beginTransaction().show(articleFragment).commit();
        }
    }

    @OnClick(R.id.main_profile)
    public void clickToProfile() {
        Intent intent = new Intent(this, ChannelActivity.class);
        intent.putExtra("mbrNo", new PreferenceManager().getLong(this, PreferenceManager.USER_NO));
        startActivity(intent);
    }

    @OnClick(R.id.main_btn_search)
    public void clickToSearch() {
        if (!isSearchMode) {
            isSearchMode = true;
            mainAppName.setVisibility(View.INVISIBLE);
            mainEdtSearch.setVisibility(View.VISIBLE);
//            bottomNavigationView.setVisibility(View.GONE);
            mainEdtSearch.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(mainEdtSearch, InputMethodManager.SHOW_IMPLICIT);
        } else {
            searchToCourse(mainEdtSearch.getText().toString());
        }
    }

    @Override
    public void onBackPressed() {
        // Search 모드가 아닐 경우
        if (!isSearchMode) {
            if (pressedBackButtonTime == 0) {
                Snackbar.make(mainLayout, "한 번 더 누르면 종료됩니다.", Snackbar.LENGTH_SHORT).show();
                pressedBackButtonTime = System.currentTimeMillis();
            } else {
                int seconds = (int) (System.currentTimeMillis() - pressedBackButtonTime);
                if (seconds > 2000) {
                    Snackbar.make(mainLayout, "한 번 더 누르면 종료됩니다.", Snackbar.LENGTH_SHORT).show();
                    pressedBackButtonTime = 0;
                } else {
                    super.onBackPressed();
                    finish();
                }
            }
        } else {
            isSearchMode = false;
            mainAppName.setVisibility(View.VISIBLE);
            mainEdtSearch.setVisibility(View.GONE);
//            bottomNavigationView.setVisibility(View.VISIBLE);
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mainEdtSearch.getWindowToken(), 0);
        }
    }

    private void searchToCourse(String text) {
        homeFragment.searchToCourse(text);
    }
}