package dajver.com.videorecyclerview.player.views;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import dajver.com.videorecyclerview.player.views.holder.VideoHolder;
import dajver.com.videorecyclerview.player.views.manager.AutoPlayLinearLayoutManager;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

import java.util.concurrent.TimeUnit;

public class AutoPlayVideoRecyclerView extends RecyclerView {

    private PublishSubject<Integer> subject;

    private VideoHolder handingVideoHolder;
    private int handingPosition = 0;
    private int newPosition = -1;

    private int heightScreen;

    public AutoPlayVideoRecyclerView(Context context) {
        super(context);
        initView(context);
    }

    public AutoPlayVideoRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public AutoPlayVideoRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    private void initView(Context context) {
        setLayoutManager(new AutoPlayLinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        getRecycledViewPool().setMaxRecycledViews(0, 5);

        heightScreen = getHeightScreen((Activity) context);
        subject = createSubject();
        addOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                checkPositionHandingViewHolder();
                subject.onNext(dy);
            }
        });
    }

    private void checkPositionHandingViewHolder() {
        if (handingVideoHolder == null) return;
        Observable.just(handingVideoHolder)
                .map(this::getPercentViewHolderInScreen)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Float>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                    }

                    @Override
                    public void onNext(@NonNull Float aFloat) {
                        if (aFloat < 50 && handingVideoHolder != null) {
                            handingVideoHolder.stopVideo();
                            handingVideoHolder = null;
                            handingPosition = -1;
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    private int getHeightScreen(Activity context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    @SuppressLint("CheckResult")
    private PublishSubject<Integer> createSubject() {
        subject = PublishSubject.create();
        subject.debounce(300, TimeUnit.MILLISECONDS)
                .filter(value -> true)
                .switchMap((Function<Integer, ObservableSource<Integer>>) Observable::just)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(integer -> {
                    playVideo(integer);
                })
                .subscribe();
        return subject;
    }

    private void playVideo(float value) {
        Observable.just(value)
                .map(aFloat -> {
                    VideoHolder videoHolder = getViewHolderCenterScreen();
                    if (videoHolder == null) return null;
                    if (videoHolder.equals(handingVideoHolder) && handingPosition == newPosition)
                        return null;
                    handingPosition = newPosition;
                    return videoHolder;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<VideoHolder>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                    }

                    @Override
                    public void onNext(@NonNull VideoHolder videoHolder) {
                        if (handingVideoHolder != null)
                            handingVideoHolder.stopVideo();

                        videoHolder.playVideo();

                        handingVideoHolder = videoHolder;
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    private VideoHolder getViewHolderCenterScreen() {
        int[] limitPosition = getLimitPositionInScreen();
        int min = limitPosition[0];
        int max = limitPosition[1];

        VideoHolder viewHolderMax = null;
        float percentMax = 0;

        for (int i = min; i <= max; i++) {
            ViewHolder viewHolder = findViewHolderForAdapterPosition(i);
            if (!(viewHolder instanceof VideoHolder)) continue;
            float percentViewHolder = getPercentViewHolderInScreen((VideoHolder) viewHolder);
            if (percentViewHolder > percentMax && percentViewHolder >= 50) {
                percentMax = percentViewHolder;
                viewHolderMax = (VideoHolder) viewHolder;
                newPosition = i;
            }
        }
        return viewHolderMax;
    }

    private float getPercentViewHolderInScreen(VideoHolder viewHolder) {
        if (viewHolder == null) return 0;
        View view = viewHolder.getVideoLayout();

        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int viewHeight = view.getHeight();
        int viewFromY = location[1];
        int viewToY = location[1] + viewHeight;

        if (viewFromY >= 0 && viewToY <= heightScreen) return 100;
        if (viewFromY < 0 && viewToY > heightScreen) return 100;
        if (viewFromY < 0 && viewToY <= heightScreen)
            return ((float) (viewToY - (-viewFromY)) / viewHeight) * 100;
        if (viewFromY >= 0 && viewToY > heightScreen)
            return ((float) (heightScreen - viewFromY) / viewHeight) * 100;
        return 0;
    }

    private int[] getLimitPositionInScreen() {
        int findFirstVisibleItemPosition = ((LinearLayoutManager) getLayoutManager()).findFirstVisibleItemPosition();
        int findFirstCompletelyVisibleItemPosition = ((LinearLayoutManager) getLayoutManager()).findFirstCompletelyVisibleItemPosition();
        int findLastVisibleItemPosition = ((LinearLayoutManager) getLayoutManager()).findLastVisibleItemPosition();
        int findLastCompletelyVisibleItemPosition = ((LinearLayoutManager) getLayoutManager()).findLastCompletelyVisibleItemPosition();

        int min = Math.min(Math.max(findFirstVisibleItemPosition, findFirstCompletelyVisibleItemPosition),
                Math.min(findLastVisibleItemPosition, findLastCompletelyVisibleItemPosition));
        int max = Math.max(Math.min(findFirstVisibleItemPosition, findFirstCompletelyVisibleItemPosition),
                Math.max(findLastVisibleItemPosition, findLastCompletelyVisibleItemPosition));
        return new int[]{min, max};
    }
}