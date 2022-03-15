package com.team_3.accountbook;

import static androidx.wear.tiles.ColorBuilders.argb;
import static androidx.wear.tiles.DimensionBuilders.degrees;
import static androidx.wear.tiles.DimensionBuilders.dp;
import static androidx.wear.tiles.DimensionBuilders.expand;
import static androidx.wear.tiles.LayoutElementBuilders.ARC_ANCHOR_START;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.wear.tiles.DeviceParametersBuilders;
import androidx.wear.tiles.LayoutElementBuilders;
import androidx.wear.tiles.RequestBuilders;
import androidx.wear.tiles.ResourceBuilders;
import androidx.wear.tiles.TileBuilders;
import androidx.wear.tiles.TileService;
import androidx.wear.tiles.TimelineBuilders;
import androidx.wear.tiles.material.Text;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

public class MyTileService extends TileService {
    private static final String RESOURCES_VERSION = "1";

    AppDatabase db;

    @NonNull
    @Override
    protected ListenableFuture<TileBuilders.Tile> onTileRequest(@NonNull RequestBuilders.TileRequest requestParams) {
        db = AppDatabase.getInstance(this);
        GoalProgress goalProgress = GoalsRepository.getGoalProgress();
        DeviceParametersBuilders.DeviceParameters deviceParams = requestParams.getDeviceParameters();

        return Futures.immediateFuture(new TileBuilders.Tile.Builder()
                .setResourcesVersion(RESOURCES_VERSION)
                .setTimeline(new TimelineBuilders.Timeline.Builder()
                        .addTimelineEntry(new TimelineBuilders.TimelineEntry.Builder()
                                .setLayout(new LayoutElementBuilders.Layout.Builder()
                                        .setRoot(myLayout(goalProgress,deviceParams)).build()
                                ).build()
                        ).build()
                ).build());
    }

    @NonNull
    @Override
    protected ListenableFuture<ResourceBuilders.Resources> onResourcesRequest(@NonNull RequestBuilders.ResourcesRequest requestParams) {
        return Futures.immediateFuture(new ResourceBuilders.Resources.Builder()
                .setVersion(RESOURCES_VERSION)
                .build()
        );
    }
    @SuppressLint("WrongConstant")
    private LayoutElementBuilders.LayoutElement myLayout(GoalProgress goalProgress, DeviceParametersBuilders.DeviceParameters deviceParameters) {
        int amount = Integer.parseInt(db.dao().get("test"));
                goalProgress.setCurrent(amount);
        return new LayoutElementBuilders.Box.Builder()
                .setWidth(expand())
                .setHeight(expand())
                .addContent(new LayoutElementBuilders.Arc.Builder()             //테두리
                        .addContent(
                                new LayoutElementBuilders.ArcLine.Builder()    //테두리에 선
                                        .setLength(degrees(goalProgress.percentage()*360f))  //게이지 = 360f가 만땅
                                        .setColor(argb(ContextCompat.getColor(this, R.color.green)))
                                        .setThickness(dp(6f)) //선 두께
                                        .build()
                        )
                        .setAnchorAngle(degrees(0.0f))  //위에 공백
                        .setAnchorType(ARC_ANCHOR_START)           //게이지 시작점(?)
                        .build())
                .addContent(
                        new LayoutElementBuilders.Column.Builder()
                                .addContent(new Text.Builder()              //텍스트
                                        .setColor(argb(0xFFFFFFFF))
                                        .setText(db.dao().get("test"))
                                        .build()
                                )
                                .addContent(new Text.Builder()              //텍스트
                                        .setColor(argb(0xFFFFFFFF))
                                        .setText("/"+goalProgress.goal+"원")
                                        .setTypography(10)                  //글씨 크기(?)
                                        .build()
                                ).build()
                ).build();

    }
}
