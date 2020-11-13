package kr.co.golearn.view.course;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import kr.co.golearn.R;
import kr.co.golearn.adaptor.CourseIndexAdapter;
import kr.co.golearn.domain.Course;
import kr.co.golearn.domain.Index;
import kr.co.golearn.domain.UseIndexDto;
import kr.co.golearn.util.CommonUtils;
import kr.co.golearn.viewmodel.CourseViewModel;

import static android.widget.Toast.LENGTH_SHORT;

public class CourseIndexActivity extends AppCompatActivity {
    @BindView(R.id.course_index_img_thumbnail)
    ImageView courseThumbnail;
    @BindView(R.id.course_index_top_txt_title)
    TextView courseTopTitle;
    @BindView(R.id.course_index_txt_title)
    TextView courseTitle;
    @BindView(R.id.course_index_txt_nickname)
    TextView courseAuthorNickname;
    @BindView(R.id.course_index_img_profile)
    ImageView courseAuthorProfile;
    @BindView(R.id.course_index_course_content)
    TextView courseContent;
    @BindView(R.id.course_index_txt_video_count)
    TextView courseVideoCount;
    @BindView(R.id.course_index_recycler_view_video)
    RecyclerView videoRecyclerView;
    @BindView(R.id.course_index_layout_content)
    ConstraintLayout courseContentLayout;

    private Course course;
    private int thumbnailHeight;
    private int layoutHeight;
    private CourseViewModel courseViewModel;
    private GridLayoutManager gridLayoutManager;
    private CourseIndexAdapter courseIndexAdapter;
    private ArrayList<Index> indexDtoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_index);

        ButterKnife.bind(this);
        course = (Course) getIntent().getSerializableExtra("course_info");
        initUI();

        init();
        actionViewModel();
    }

    private void init() {
        indexDtoList = new ArrayList<>();
        courseIndexAdapter = new CourseIndexAdapter(indexDtoList);
        gridLayoutManager = new GridLayoutManager(this, 1);
        videoRecyclerView.setLayoutManager(gridLayoutManager);
        videoRecyclerView.setAdapter(courseIndexAdapter);
    }

    private void actionViewModel() {
        courseViewModel = ViewModelProviders.of(this).get(CourseViewModel.class);
        courseViewModel.indexes().observe(this, indexes -> {
            courseVideoCount.setText("동영상 " + indexes.size() + "개");
            for(Index index : indexes){
                indexDtoList.add(index);
            }
            videoRecyclerView.post(() -> courseIndexAdapter.notifyDataSetChanged());
        });
        courseViewModel.getIndexes(course.getCosNo());
    }

    private void initUI() {
        Glide.with(this)
                .load(course.getCosThumbnail())
                .centerCrop()
                .into(courseThumbnail);
        Glide.with(this)
                .load(course.getMbrProfile())
                .circleCrop()
                .into(courseAuthorProfile);
        courseTopTitle.setText(course.getCosTitle());
        courseTitle.setText(course.getCosTitle());
        courseAuthorNickname.setText(course.getMbrNickname());
        courseContent.setText(course.getCosContent());

        thumbnailHeight = courseThumbnail.getLayoutParams().height;
        layoutHeight = courseContent.getHeight();
        courseContent.post(() -> {
            layoutHeight = courseContent.getHeight();
        });
    }

    @OnClick(R.id.course_index_btn_back)
    void clickToBack() {
        finish();
    }

    @OnCheckedChanged(R.id.course_index_checkbox_more)
    void checkBoxDetailMore(boolean isChecked) {
        if (isChecked) {
            slideView(courseThumbnail, thumbnailHeight, 1);
            slideView(courseContent, layoutHeight, 1);
        } else {
            slideView(courseThumbnail, 1, thumbnailHeight);
            slideView(courseContent, 1, layoutHeight);
        }
    }

    public static void slideView(View view, int currentHeight, int newHeight) {
        ValueAnimator slideAnimator = ValueAnimator.ofInt(currentHeight, newHeight).setDuration(500);

        slideAnimator.addUpdateListener(animation1 -> {
            Integer value = (Integer) animation1.getAnimatedValue();
            view.getLayoutParams().height = value.intValue();
            view.requestLayout();
        });

        AnimatorSet animationSet = new AnimatorSet();
        animationSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animationSet.play(slideAnimator);
        animationSet.start();
    }
}