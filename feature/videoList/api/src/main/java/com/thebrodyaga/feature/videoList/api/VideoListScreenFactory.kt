package com.thebrodyaga.feature.videoList.api

import com.thebrodyaga.core.navigation.api.cicerone.Screen

interface VideoListScreenFactory {

    fun videoListScreen(): Screen
    fun allVideoScreen(): Screen
}