package kr.co.golearn.view.video;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import kr.co.golearn.R;
import kr.co.golearn.adaptor.VideoReplyAdapter;
import kr.co.golearn.domain.Comment;
import kr.co.golearn.domain.Member;
import kr.co.golearn.util.CommonUtils;
import kr.co.golearn.util.PreferenceManager;
import kr.co.golearn.viewmodel.TextViewModel;
import kr.co.golearn.viewmodel.VideoViewModel;

public class CommentReplyBottomFragment extends BottomSheetDialogFragment {
    @BindView(R.id.comment_reply_member_recycler_view)
    RecyclerView videoReplyRecyclerView;
    @BindView(R.id.comment_reply_member_profile)
    ImageView memberProfile;
    @BindView(R.id.comment_reply_main_layout)
    ConstraintLayout mainLayout;

    private View view;
    private long vidNo;
    private long vidCmtPno;
    private VideoViewModel videoViewModel;
    private TextViewModel textViewModel;
    private VideoReplyAdapter videoReplyAdapter;
    private GridLayoutManager gridLayoutManager;
    private ArrayList<Comment> originComments;

    private CommentReplyBottomFragment(long vidNo, long vidCmtPno) {
        this.vidNo = vidNo;
        this.vidCmtPno = vidCmtPno;
    }

    public static CommentReplyBottomFragment newInstance(long vidNo, long vidCmtPno) {
        return new CommentReplyBottomFragment(vidNo, vidCmtPno);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_bottom_comment_reply, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init();
        actionViewModel();
    }

    private void actionViewModel() {
        videoViewModel.getReplies().observe(this.getActivity(), comments -> {
            List<Comment> list = comments;
            for (Comment comment : list) {
                comment.setDate(CommonUtils.calcTimeDate(comment.getRegDt()));
                originComments.add(comment);
            }
            videoReplyRecyclerView.setAdapter(videoReplyAdapter);
            videoReplyAdapter.notifyDataSetChanged();
        });

        videoViewModel.getOkComment().observe(this.getActivity(), comment -> {
            comment.setMember(new Member());
            comment.getMember().setMbrProfile(new PreferenceManager().getString(this.getActivity(), PreferenceManager.USER_PROFILE));
            comment.getMember().setMbrNickname(new PreferenceManager().getString(this.getActivity(), PreferenceManager.USER_NICKNAME));
            comment.setDate(CommonUtils.calcTimeDate(comment.getRegDt()));
            originComments.add(originComments.size(), comment);
            videoReplyAdapter.notifyItemInserted(originComments.size());
        });

        textViewModel.getReplyText().observe(this.getActivity(), txt -> {
            videoViewModel.postCommentReplyToServer(this.getActivity(), vidNo, vidCmtPno, txt);
        });

        videoViewModel.getRepliesFromServer(vidNo, vidCmtPno);
    }

    private void init() {
        videoViewModel = ViewModelProviders.of(this).get(VideoViewModel.class);
        textViewModel = ViewModelProviders.of(this).get(TextViewModel.class);

        Glide.with(this)
                .load(new PreferenceManager().getString(this.getContext(), PreferenceManager.USER_PROFILE))
                .circleCrop()
                .into(memberProfile);

        originComments = new ArrayList<>();
        videoReplyAdapter = new VideoReplyAdapter(originComments, this.getContext());
        gridLayoutManager = new GridLayoutManager(this.getContext(), 1);
        videoReplyRecyclerView.setLayoutManager(gridLayoutManager);
    }

    @OnClick(R.id.comment_reply_btn_close)
    void clickToClose() {
        this.dismiss();
    }

    @OnClick(R.id.comment_reply_btn_reply)
    void clickToReply() {
        // bottom sheet 하나 더 추가
        ReplyBottomFragment fragment = ReplyBottomFragment.newInstance(textViewModel);
        fragment.show(((FragmentActivity) getContext()).getSupportFragmentManager(), "test");
    }
}