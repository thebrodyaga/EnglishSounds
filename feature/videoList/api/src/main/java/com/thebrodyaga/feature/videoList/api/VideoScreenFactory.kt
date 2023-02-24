package com.thebrodyaga.feature.videoList.api

import com.thebrodyaga.core.navigation.api.cicerone.Screen

interface VideoScreenFactory {

    fun videoListScreen(): Screen
    fun allVideoScreen(showPage: VideoListType): Screen
}

enum class VideoListType {
    ContrastingSounds, MostCommonWords, AdvancedExercises, VowelSounds, RControlledVowels, ConsonantSounds;
}