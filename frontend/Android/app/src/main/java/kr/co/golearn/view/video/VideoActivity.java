package kr.co.golearn.view.video;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.util.Util;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kr.co.golearn.R;
import kr.co.golearn.adaptor.VideoCommentAdapter;
import kr.co.golearn.domain.Comment;
import kr.co.golearn.domain.Course;
import kr.co.golearn.domain.Member;
import kr.co.golearn.domain.Video;
import kr.co.golearn.domain.response.VideoResponse;
import kr.co.golearn.util.CommonUtils;
import kr.co.golearn.util.PreferenceManager;
import kr.co.golearn.viewmodel.TextViewModel;
import kr.co.golearn.viewmodel.VideoViewModel;

public class VideoActivity extends AppCompatActivity {
    private PlaybackStateListener playbackStateListener;
    private String TAG = this.getClass().toString();

    @BindView(R.id.video_view)
    PlayerView playerView;
    @BindView(R.id.video_btn_screen_change)
    ImageView btnScreenChange;
    @BindView(R.id.video_recycler_view)
    RecyclerView videoRecyclerView;
    @BindView(R.id.video_card_view)
    MaterialCardView cardViewMore;

    private SimpleExoPlayer player;
    private Boolean playWhenReady = true;
    private int currentWindow = 0;
    private Long playbackPosition = 0L;
    private boolean isFullScreen = false;
    private VideoViewModel videoViewModel;
    private TextViewModel textViewModel;
    private VideoCommentAdapter videoCommentAdapter;
    private GridLayoutManager gridLayoutManager;
    private ArrayList<Comment> originComments;
    private long vidNo;
    private int page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        ButterKnife.bind(this);

        vidNo = getIntent().getLongExtra("vidNo", 0L);
        playbackStateListener = new PlaybackStateListener();
        btnScreenChange.setBackgroundResource(R.drawable.exo_controls_fullscreen_enter);

        init();
        actionViewModel();
    }

    private void init() {
        page = 1;
        videoViewModel = ViewModelProviders.of(this).get(VideoViewModel.class);
        textViewModel = ViewModelProviders.of(this).get(TextViewModel.class);
        originComments = new ArrayList<>();
        videoCommentAdapter = new VideoCommentAdapter(originComments, textViewModel, this, vidNo);
        gridLayoutManager = new GridLayoutManager(this, 1);
        videoRecyclerView.setLayoutManager(gridLayoutManager);
    }

    private void actionViewModel() {
        videoViewModel.getVideo().observe(this, videoResponse -> {
            Video setVideo = videoResponse.getVideo();
            setVideo.setViewCount(CommonUtils.convertCount(setVideo.getVidView()));
            setVideo.setDate(CommonUtils.calcTimeDate(setVideo.getRegDt()));
            videoCommentAdapter.setVideoResponse(videoResponse);
            videoRecyclerView.setAdapter(videoCommentAdapter);
            initializePlayer(setVideo.getVidUrl());
        });

        videoViewModel.getComments().observe(this, comments -> {
            List<Comment> list = comments;
            if(list.size() == 0){
                Snackbar.make(cardViewMore, "모든 댓글을 들고왔습니다", Snackbar.LENGTH_SHORT).show();
                cardViewMore.setVisibility(View.GONE);
                return;
            }
            for(Comment comment : list){
                comment.setDate(CommonUtils.calcTimeDate(comment.getRegDt()));
                originComments.add(comment);
            }
            videoRecyclerView.post(() -> videoCommentAdapter.notifyDataSetChanged());
        });

        videoViewModel.getOkComment().observe(this, comment ->{
            comment.setMember(new Member());
            comment.getMember().setMbrProfile(new PreferenceManager().getString(this, PreferenceManager.USER_PROFILE));
            comment.getMember().setMbrNickname(new PreferenceManager().getString(this, PreferenceManager.USER_NICKNAME));
            comment.setDate(CommonUtils.calcTimeDate(comment.getRegDt()));
            originComments.add(0, comment);
            videoCommentAdapter.notifyItemInserted(0);
        });

        // intent 로 받기 no
        videoViewModel.getVideoFromServer(vidNo);
        videoViewModel.getCommentsFromServer(vidNo, page++);
        textViewModel.getReplyText().observe(this, txt->{
            videoViewModel.postCommentToServer(this, vidNo, txt);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(player != null) {
            player.play();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(player != null) {
            player.pause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @OnClick(R.id.video_card_view)
    void clickToMore(){
        videoViewModel.getCommentsFromServer(vidNo, page++);
    }

    private void initializePlayer(String videoUrl) {
        if (player == null) {
            DefaultTrackSelector trackSelector = new DefaultTrackSelector(this);
            trackSelector.setParameters(trackSelector.buildUponParameters().setMaxVideoSizeSd());
            player = new SimpleExoPlayer.Builder(this)
                    .setTrackSelector(trackSelector)
                    .build();
            player.addListener(playbackStateListener);
        }

        playerView.setPlayer(player);

        MediaItem mediaItem = MediaItem.fromUri(videoUrl);
        player.setMediaItem(mediaItem);

        player.setPlayWhenReady(playWhenReady);
        player.seekTo(0, playbackPosition);
        player.prepare();
    }

    @SuppressLint("InlinedApi")
    private void hideSystemUi() {
        playerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    private void showSystemUi(){
        playerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    private class PlaybackStateListener implements Player.EventListener {

        @Override
        public void onPlaybackStateChanged(int playbackState) {
            String stateString;
            switch (playbackState) {
                // 플레이어가 인스턴스화되었지만 아직 준비되지 않음
                case ExoPlayer.STATE_IDLE:
                    stateString = "ExoPlayer.STATE_IDLE      -";
                    break;
                // 충분한 데이터가 버퍼링되지 않았기 때문에 플레이어는 현재 위치에서 재생할 수 없음
                case ExoPlayer.STATE_BUFFERING:
                    stateString = "ExoPlayer.STATE_BUFFERING -";
                    break;
                // 플레이어는 현재 위치에서 즉시 플레이 가능, 플레이어의 playWhenReady 속성이
                //                                                    true  -> 플레이어가 미디어 재생을 자동으로 시작
                //                                                    false -> 플레이어가 일시 중지
                case ExoPlayer.STATE_READY:
                    stateString = "ExoPlayer.STATE_READY     -";
                    break;
                // 플레이어가 미디어 재생을 마침
                case ExoPlayer.STATE_ENDED:
                    stateString = "ExoPlayer.STATE_ENDED     -";
                    break;
                default:
                    stateString = "UNKNOWN_STATE             -";
                    break;
            }
        }
    }

    private void releasePlayer() {
        if (player != null) {
            playWhenReady = player.getPlayWhenReady();
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            player.removeListener(playbackStateListener);
            player.release();
            player = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }

    @OnClick(R.id.video_btn_screen_change)
    void clickToChangeScreen(){
        if(isFullScreen){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            btnScreenChange.setBackgroundResource(R.drawable.exo_controls_fullscreen_enter);
            showSystemUi();
            cardViewMore.setVisibility(View.VISIBLE);
        }else{
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            btnScreenChange.setBackgroundResource(R.drawable.exo_controls_fullscreen_exit);
            hideSystemUi();
            cardViewMore.setVisibility(View.INVISIBLE);
        }
        isFullScreen = !isFullScreen;
    }
}