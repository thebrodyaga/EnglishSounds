package com.thebrodyaga.englishsounds.navigation

import android.view.View
import ru.terrakok.cicerone.Screen
import ru.terrakok.cicerone.commands.Forward
import ru.terrakok.cicerone.commands.Replace

class ForwardWithTransition constructor(
        screen: Screen,
        override val transitionBox: TransitionBox?,
        override val sharedElement: List<Pair<View, String>>
) : Forward(screen), TransitionCommand

data class TransitionBox(
        val sharedElementEnterTransition: Any? = null,
        val enterTransition: Any? = null,
        val exitTransition: Any? = null,
        val sharedElementReturnTransition: Any? = null
)

class ReplaceWithTransition constructor(
        screen: Screen,
        override val transitionBox: TransitionBox?,
        override val sharedElement: List<Pair<View, String>>
) : Replace(screen), TransitionCommand

interface TransitionCommand {
    val transitionBox: TransitionBox?
    val sharedElement: List<Pair<View, String>>
}