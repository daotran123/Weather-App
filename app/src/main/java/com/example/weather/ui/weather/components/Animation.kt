package com.plcoding.weatherapp.ui.weather.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition

@Composable
fun Animation(
    modifier: Modifier = Modifier,
    animation: Int
){
    //Truy cập
    val composisition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(animation)
    )

    //Theo dõi, tạo State có thể thay đổi
    val progress by animateLottieCompositionAsState(
        composition = composisition,
        iterations = LottieConstants.IterateForever,
        isPlaying = true,
        speed = 2f
    )

    //Hiển thị
    LottieAnimation(
        composition = composisition,
        progress = {progress},
        modifier = modifier
    )


}