package com.chenantao.playtogether.mvc.view.activity.invitation;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVUser;
import com.chenantao.autolayout.AutoLinearLayout;
import com.chenantao.playtogether.R;
import com.chenantao.playtogether.mvc.controller.invitation.InvitationDetailController;
import com.chenantao.playtogether.mvc.model.bean.Invitation;
import com.chenantao.playtogether.mvc.model.bean.User;
import com.chenantao.playtogether.mvc.view.activity.user.PersonalCenterActivity;
import com.chenantao.playtogether.mvc.view.common.BaseActivity;
import com.chenantao.playtogether.mvc.view.common.ShowImageActivity;
import com.chenantao.playtogether.utils.DialogUtils;
import com.chenantao.playtogether.utils.FileUtils;
import com.chenantao.playtogether.utils.PicassoUtils;
import com.chenantao.playtogether.utils.ScreenUtils;
import com.gc.materialdesign.views.AutoHideButtonFloat;
import com.gc.materialdesign.views.ButtonRectangle;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;

/**
 * Created by Chenantao_gg on 2016/1/25.
 */
public class InvitationDetailActivity extends BaseActivity implements View.OnClickListener {
    //内容中图片的宽度最大能占屏幕宽度的多少，高同理
    public static final double CONTENT_PIC_WIDTH_MAX_RATIO = 0.83;
    public static final double CONTENT_PIC_HEIGHT_MAX_RATIO = 0.5;
    @Bind(R.id.llAcceptUser)
    LinearLayout mLlAcceptUser;
    @Bind(R.id.llRoot)
    LinearLayout mLlRoot;
    @Bind(R.id.tvTitle)
    TextView mTvTitle;
    @Bind(R.id.ivAuthorAvatar)
    ImageView mIvAvatar;
    @Bind(R.id.tvAuthorName)
    TextView mTvAuthorName;
    @Bind(R.id.tvAuthorDesc)
    TextView mTvAuthorDesc;
    @Bind(R.id.tvContent)
    TextView mTvContent;
    @Bind(R.id.tvExpire)
    TextView mTvExpire;
    @Bind(R.id.llContentContainer)
    AutoLinearLayout mLlContentContainer;
    @Bind(R.id.btnInvite)
    AutoHideButtonFloat mBtnInvite;
    @Bind(R.id.tvAcceptUserNum)
    TextView mTvAcceptUserNum;
    @Bind(R.id.btnMenu)
    ButtonRectangle mBtnMenu;
    @Bind(R.id.progressBar)
    ProgressBar mProgressBar;
    public static final String EXTRA_INVITATION_ID = "invitationId";
    public static final String EXTRA_INVITATION_TITLE = "title";
    public static final String EXTRA_INVITATION_USERNAME = "username";
    public static final String EXTRA_INVITATION_AVATAR = "avatar";

    private Invitation mInvitation;

    private User mAuthor;//这篇邀请的作者

    private boolean mIsContentPicLoaded = false;//内容里的图片是否加载完毕
    private int mAcceptInviteNums = 0;//接受邀请的用户数量，主要是用于判断数据是否改变去更新显示下面的用户列表
    private boolean mIsAcceptUserSet = false;//接受邀请的用户inflate过了没
    //共享元素的数据
    private Bitmap mAuthorAvatar;

    @Inject
    public InvitationDetailController mController;

    @Override
    public int getLayoutId() {
        return R.layout.activity_invitation_detail;
    }

    @Override
    public void injectActivity() {
        mActivityComponent.inject(this);
    }

    @Override
    public void afterCreate() {
        ActionBar toolbar = getSupportActionBar();
        if (toolbar != null) {
            toolbar.setTitle("邀请详情");
        }
        String invitationId = getIntent().getStringExtra(EXTRA_INVITATION_ID);
        String title = getIntent().getStringExtra(EXTRA_INVITATION_TITLE);
        String username = getIntent().getStringExtra(EXTRA_INVITATION_USERNAME);
        mAuthorAvatar = getIntent().getParcelableExtra(EXTRA_INVITATION_AVATAR);
        mTvTitle.setText(title);
        mTvAuthorName.setText(username);
        //如果当前登录用户为作者，显示菜单按钮，隐藏接受邀请的按钮
        if (username.equals(AVUser.getCurrentUser().getUsername())) {
            mBtnMenu.setVisibility(View.VISIBLE);
            mBtnInvite.setVisibility(View.GONE);
        }
        if (mAuthorAvatar != null) {
            mIvAvatar.setImageBitmap(mAuthorAvatar);
        }
        if (invitationId == null) {
            Toast.makeText(this, "加载不到数据::>_<:: ", Toast.LENGTH_SHORT).show();
            return;
        } else {
            mProgressBar.setVisibility(View.VISIBLE);
            loadData(invitationId);
        }
        initEvent();

    }

    private void initEvent() {
        mBtnInvite.setOnClickListener(this);
        mIvAvatar.setOnClickListener(this);
        if (mBtnMenu.getVisibility() == View.VISIBLE) {
            mBtnMenu.setOnClickListener(this);
        }
    }

    private void loadData(String invitationId) {
        mController.loadData(invitationId);
    }

    /**
     * 文本信息加载完毕
     * 作者头像、姓名，标题是共享元素，不用设置了
     */
    public void loadTextDataSuccess(Invitation invitation) {
        mProgressBar.setVisibility(View.GONE);
        mInvitation = invitation;
        DialogUtils.dismissProgressDialog();
        mAuthor = invitation.getAuthor();
//		mTvAuthorName.setText(author.getUsername());
        mTvAuthorDesc.setText(mAuthor.getSimpleDesc());
//		mTvTitle.setText(invitation.getTitle());
        mTvContent.setText(invitation.getContent());
        if (invitation.getIsExpire())//如果已过期，禁用设为到期按钮
        {
            mBtnMenu.setEnabled(false);
            mTvExpire.setText("已过期");
        } else {
            mTvExpire.setText(invitation.getExpire());
        }
        mTvAcceptUserNum.setText(getString(R.string.accept_user_num, invitation
                .getAcceptInviteUsers().size()));
        //设置受约的用户姓名
        setAcceptInviteUsers();
        //下载作者头像
        if (mAuthorAvatar == null) {
            String authorAvatarUrl = mAuthor.getAvatarUrl();
            if (authorAvatarUrl != null) {
                PicassoUtils.displayFitImage(this, Uri.parse(authorAvatarUrl), mIvAvatar, null);
            }
        }
        //下载图片
        downloadPic();
    }

    public void loadDataFail(String msg) {
        DialogUtils.dismissProgressDialog();
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 为了避免重复的inflate以及addview，先判断是否是第一次设置，
     * 不是的话则拿到上次更新到的下标，从这个下标开始，添加新更新的数据
     */
    public void setAcceptInviteUsers() {
        if (mIsAcceptUserSet && mLlAcceptUser.getChildCount() > 5)//最多只同时显示5个受约的小伙伴
        {
            return;
        }
        List<User> acceptInviteUsers = mInvitation.getAcceptInviteUsers();
        int count = Math.min(acceptInviteUsers.size(), 5);
        int startIndex = 0;
        if (mIsAcceptUserSet)//如果已经设置过了
        {
            startIndex = mAcceptInviteNums;
        }
        for (int i = startIndex; i < count; i++) {
            User user = acceptInviteUsers.get(i);
            View view = LayoutInflater.from(this).inflate(R.layout.item_accept_invite_user,
                    mLlAcceptUser, false);
            mLlAcceptUser.addView(view, mLlAcceptUser.getChildCount());
            ((TextView) view.findViewById(R.id.tvUsername)).setText(user.getUsername());
            ImageView ivAvatar = (ImageView) view.findViewById(R.id.ivAvatar);
            //设置头像
            AVFile avFile = user.getAvatar();
            if (avFile != null) {
                PicassoUtils.displayFitImage(this, Uri.parse(avFile.getThumbnailUrl(true, 100,
                        100)), ivAvatar, null);
            }
        }
        mIsAcceptUserSet = true;
        mAcceptInviteNums = count;
    }

    /**
     * 下载图片，包含内容中的图片以及受约用户的头像
     */
    private void downloadPic() {
        if (!mIsContentPicLoaded)//如果已经加载过就不要继续加载了
        {
            int screenWidth = ScreenUtils.getScreenWidth(this);
            int screenHeight = ScreenUtils.getScreenHeight(this);
            List<AVFile> contentFile = mInvitation.getPics();
            if (contentFile != null && contentFile.size() > 0) {
                for (int i = 0; i < contentFile.size(); i++) {
                    final AVFile file = contentFile.get(i);
                    final int originalImageWidth = (int) file.getMetaData("width");
                    final int originalImageHeight = (int) file.getMetaData("height");
                    double[] ratio = FileUtils.compressIfMoreThanDesireHeightWidth
                            (originalImageWidth, originalImageHeight, CONTENT_PIC_WIDTH_MAX_RATIO,
                                    CONTENT_PIC_HEIGHT_MAX_RATIO, this);
                    final int width = (int) (screenWidth * ratio[0]);
                    final int height = (int) (screenHeight * ratio[1]);
                    ImageView imageView = getContentImageView(width, height);
                    //将图片添加到内容容器的末尾
                    mLlContentContainer.addView(imageView, mLlContentContainer.getChildCount());
                    //点击图片查看高清无码大图
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(InvitationDetailActivity.this,
                                    ShowImageActivity.class);
                            intent.putExtra(ShowImageActivity.EXTRA_URI, Uri.parse(file.getUrl()))
                                    .putExtra(ShowImageActivity.EXTRA_WIDTH, originalImageWidth)
                                    .putExtra(ShowImageActivity.EXTRA_HEIGHT, originalImageHeight);
                            startActivity(intent);
                        }
                    });
                    PicassoUtils.displayFitImage(this, Uri.parse(file.getThumbnailUrl(false, width,
                            height)),
                            imageView, null);
                }
            }
            mIsContentPicLoaded = true;
        }
    }

    /**
     * 约炮成功
     */
    public void acceptInviteSuccess(Invitation invitation) {
//		DialogUtils.dismissProgressDialog();
        Toast.makeText(this, "受约成功", Toast.LENGTH_SHORT).show();
        loadData(mInvitation.getObjectId());
    }


    /**
     * 约炮失败
     */
    public void acceptInviteFail(String msg) {
        DialogUtils.dismissProgressDialog();
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    public void setExpireSuccess() {
        Toast.makeText(this, "设置成功", Toast.LENGTH_SHORT).show();
        mBtnMenu.setEnabled(false);
        mTvExpire.setText("已过期");
    }

    /**
     * 根据屏占比创建内容中的imageview
     */
    public ImageView getContentImageView(int width, int height) {
        int screenHeight = ScreenUtils.getScreenHeight(this);
        ImageView imageView = new ImageView(this);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
        params.topMargin = screenHeight / 40;
        params.gravity = Gravity.CENTER_HORIZONTAL;
        imageView.setLayoutParams(params);
        return imageView;
    }

    @Override
    public void onClick(View v) {
        if (mInvitation == null) {
            return;
        }
        switch (v.getId()) {
            case R.id.btnInvite:
                if (mInvitation.getAuthor().getObjectId().equals(AVUser.getCurrentUser().getObjectId())) {
                    Toast.makeText(this, "请不要自导自演", Toast.LENGTH_LONG).show();
                    return;
                }
                Snackbar.make(mLlRoot, "约吗英雄", Snackbar.LENGTH_LONG)
                        .setAction("约起来", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mController.acceptInvite(mInvitation);
                            }
                        })
                        .show();
                break;
            case R.id.ivAuthorAvatar://点击头像查看用户信息
                PersonalCenterActivity.startActivity(this, mAuthor.getObjectId());
                break;
            case R.id.btnMenu://将邀请设置为到期，仅作者可做
                Snackbar.make(mLlRoot, "确定设置为已过期吗?", Snackbar.LENGTH_LONG)
                        .setAction("确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mController.setExpire(mInvitation);
                            }
                        })
                        .show();
                break;
        }
    }

}
