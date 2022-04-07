package com.team_3.accountbook;

import static androidx.wear.tiles.ColorBuilders.argb;
import static androidx.wear.tiles.DimensionBuilders.degrees;
import static androidx.wear.tiles.DimensionBuilders.dp;
import static androidx.wear.tiles.DimensionBuilders.expand;
import static androidx.wear.tiles.LayoutElementBuilders.ARC_ANCHOR_START;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.wear.tiles.DimensionBuilders;
import androidx.wear.tiles.LayoutElementBuilders;
import androidx.wear.tiles.RequestBuilders;
import androidx.wear.tiles.ResourceBuilders;
import androidx.wear.tiles.TileBuilders;
import androidx.wear.tiles.TileService;
import androidx.wear.tiles.TimelineBuilders;
import androidx.wear.tiles.material.Text;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.YearMonth;

public class MyTileService extends TileService {
    private static final String RESOURCES_VERSION = "1";
    DimensionBuilders.DpProp Tdp = dp(12f);      //두께
    AppDatabase db;
    private DecimalFormat myFormatter = new DecimalFormat("###,###");
    @NonNull
    @Override
    protected ListenableFuture<TileBuilders.Tile> onTileRequest(@NonNull RequestBuilders.TileRequest requestParams) {
        db = AppDatabase.getInstance(this);
        GoalProgress amountProgress = GoalsRepository.getAmountProgress();
        GoalProgress dateProgress = GoalsRepository.getDateProgress();

        return Futures.immediateFuture(new TileBuilders.Tile.Builder()
                .setResourcesVersion(RESOURCES_VERSION)
                .setTimeline(new TimelineBuilders.Timeline.Builder()
                        .addTimelineEntry(new TimelineBuilders.TimelineEntry.Builder()
                                .setLayout(new LayoutElementBuilders.Layout.Builder()
                                        .setRoot(myLayout(amountProgress, dateProgress)).build()
                                ).build()
                        ).build()
                ).build());
    }

    @NonNull
    @Override
    protected ListenableFuture<ResourceBuilders.Resources> onResourcesRequest(@NonNull RequestBuilders.ResourcesRequest requestParams) {
        return Futures.immediateFuture(new ResourceBuilders.Resources.Builder()
                .setVersion(RESOURCES_VERSION)
                .addIdToImageMapping(       //이미지 파일 가져오기 
                        "money",
                        new ResourceBuilders.ImageResource.Builder()
                                .setAndroidResourceByResId(
                                        new ResourceBuilders.AndroidImageResourceByResId.Builder()
                                                .setResourceId(R.drawable.money)
                                                .build()
                                )
                                .build()
                )
                .addIdToImageMapping(       //이미지 파일 가져오기
                        "empty",
                        new ResourceBuilders.ImageResource.Builder()
                                .setAndroidResourceByResId(
                                        new ResourceBuilders.AndroidImageResourceByResId.Builder()
                                                .setResourceId(R.drawable.empty)
                                                .build()
                                )
                                .build()
                )
                .addIdToImageMapping(       //이미지 파일 가져오기
                        "warning",
                        new ResourceBuilders.ImageResource.Builder()
                                .setAndroidResourceByResId(
                                        new ResourceBuilders.AndroidImageResourceByResId.Builder()
                                                .setResourceId(R.drawable.warning)
                                                .build()
                                )
                                .build()
                )
                .build()
        );
    }
    @SuppressLint("WrongConstant")
    private LayoutElementBuilders.LayoutElement myLayout(GoalProgress amountProgress, GoalProgress dateProgress) {
        int amount =0;
        int goal = 300000;
        try {
            amount = Integer.parseInt(db.dao().get("amount"));
        }catch (Exception e){}

        try {
            goal = Integer.parseInt(db.dao().get("goal"));
        }catch (Exception e){}
        LocalDate date = LocalDate.now();
        YearMonth yearMonth = YearMonth.from(date);

        amountProgress.setCurrent(amount);                   //이번달 쓴돈
        amountProgress.setGoal(goal);   // 가격 목표값 설정
        dateProgress.setCurrent(date.getDayOfMonth());      //현재 날짜 값
        dateProgress.setGoal(yearMonth.lengthOfMonth());    //현재 월의 길이

        String image;
        /** 금액에 따른 이미지 변경*/
        if (amountProgress.percentage() >= 0.7 && amountProgress.percentage() < 1) image = "warning";
        else if (amountProgress.percentage() == 1) image = "empty";
        else image = "money";

        return new LayoutElementBuilders.Box.Builder()
                .setWidth(expand())
                .setHeight(expand())
                /**첫번째(금액,오른쪽) 호의 투명게이지*/
                .addContent(new LayoutElementBuilders.Arc.Builder()             //테두리

                        .addContent(
                                new LayoutElementBuilders.ArcLine.Builder()    //테두리에 첫번째 선 투명한 게이지
                                        .setLength(degrees(100f))  //게이지 = 360f가 만땅
                                        .setColor(argb(0x1F00C853))  //1F -투명도
                                        .setThickness(Tdp)                    //선 두께
                                        .build()
                        )
                        .setAnchorAngle(degrees(40f))              //위에 공백
                                .setAnchorType(ARC_ANCHOR_START)             //게이지 시작점(?)(왼쪽 오른쪽)
                                .build())
                /**첫번째(금액,오른쪽)호의 게이지*/
                .addContent(new LayoutElementBuilders.Arc.Builder()             //테두리
                        .addContent(
                                new LayoutElementBuilders.ArcLine.Builder()    //테두리에 첫번째 선(금액)
                                        .setLength(degrees(amountProgress.percentage()*100f))  //게이지 = 360f가 만땅
                                        .setColor(argb(ContextCompat.getColor(this, R.color.green)))
                                        .setThickness(Tdp) //선 두께
                                        .build()
                        )
                        .setAnchorAngle(degrees(140f))                //위에 공백
                        .setAnchorType(LayoutElementBuilders.ARC_ANCHOR_END)                        //게이지 시작점(?)(왼쪽 오른쪽)
                        .build())
                /**첫번째(금액,오른쪽) 호의 글씨*/
                .addContent(new LayoutElementBuilders.Arc.Builder()
                        .addContent(
                                new LayoutElementBuilders.ArcText.Builder()
                                        .setText("\uD83D\uDCB5")
                                        .build()
                        )
                        .setAnchorAngle(degrees(145f))  //위에 공백
                        .setAnchorType(ARC_ANCHOR_START)
                        .build())
                /**두번째(날짜,왼쪽) 호 투명게이지*/
                .addContent(new LayoutElementBuilders.Arc.Builder()             //테두리
                        .addContent(
                                new LayoutElementBuilders.ArcLine.Builder()    //테두리에 두번째 선 투명한 게이지
                                        .setLength(degrees(100f))  //게이지 = 360f가 만땅
                                        .setColor(argb(0x1FFF0000))  //1F -투명도
                                        .setThickness(Tdp) //선 두께
                                        .build()
                        )
                        .setAnchorAngle(degrees(-40f))                //위에 공백
                        .setAnchorType(LayoutElementBuilders.ARC_ANCHOR_END)                        //게이지 시작점(?)(왼쪽 오른쪽)
                        .build()
                )
                /**두번째(날짜,왼쪽) 호의 게이지*/
                .addContent(new LayoutElementBuilders.Arc.Builder()             //테두리
                        .addContent(
                                new LayoutElementBuilders.ArcLine.Builder()    //테두리에 두번째 선(날짜)
                                        .setLength(degrees(dateProgress.percentage()*100f))  //게이지 = 360f가 만땅
                                        .setColor(argb(0xFFFF0000))  //1F -투명도
                                        .setThickness(Tdp)                      //선 두께
                                        .build()
                        )
                        .setAnchorAngle(degrees(-140f))                //위에 공백
                        .setAnchorType(ARC_ANCHOR_START)                        //게이지 시작점(?)(왼쪽 오른쪽)
                        .build()
                )
                /**두번째(날짜,왼쪽) 호의 글씨()*/
                .addContent(new LayoutElementBuilders.Arc.Builder()             //테두리
                        .addContent(
                                new LayoutElementBuilders.ArcText.Builder()    //테두리에 첫번째 선 투명한 게이지
                                        .setText("\uD83D\uDD66")
                                        .build()
                        )
                        .setAnchorAngle(degrees(-150f))  //위에 공백
                        .setVerticalAlign(LayoutElementBuilders.VERTICAL_ALIGN_BOTTOM)
                        .build())


                /**중앙의 글씨와 그림*/
                .addContent(
                        new LayoutElementBuilders.Column.Builder()
                                .addContent(new Text.Builder()              //텍스트
                                        .setColor(argb(0xFFFFFFFF))
                                        .setText(myFormatter.format(amount))
                                        .build()
                                )
                                .addContent(new Text.Builder()              //텍스트
                                        .setColor(argb(0xFFFFFFFF))
                                        .setText("/"+myFormatter.format(amountProgress.goal)+"원")
                                        .setTypography(10)                  //글씨 크기(?)
                                        .build()
                                )
                                //공간 띄우기 padding 같은 역할
                                .addContent(new LayoutElementBuilders.Spacer.Builder().setHeight(dp(20f)).setWidth(dp(100f)).build())
                                .addContent(new LayoutElementBuilders.Image.Builder()
                                        .setWidth(dp(208f))
                                        .setHeight(dp(48f))
                                        .setResourceId(image)
                                        .build()
                )
                                .build())
                .build();

    }
}
