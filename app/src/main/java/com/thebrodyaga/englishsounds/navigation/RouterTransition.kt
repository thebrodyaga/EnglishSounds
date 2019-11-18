package com.thebrodyaga.englishsounds.navigation

import android.view.View
import ru.terrakok.cicerone.Router
import ru.terrakok.cicerone.Screen

class RouterTransition : Router() {

    fun navigateToWithTransition(
            screen: Screen,
            transitionBox: TransitionBox,
            vararg sharedElement: Pair<View, String>
    ) {
        executeCommands(ForwardWithTransition(screen, transitionBox, sharedElement.toList()))
    }

    fun replaceWithTransition(
            screen: Screen,
            transitionBox: TransitionBox,
            vararg sharedElement: Pair<View, String>
    ) {
        executeCommands(ReplaceWithTransition(screen, transitionBox, sharedElement.toList()))
    }

    fun newRootScreenWithTransition(
            screen: Screen,
            transitionBox: TransitionBox,
            vararg sharedElement: Pair<View, String>
    ) {
        executeCommands(ReplaceWithTransition(screen, transitionBox, sharedElement.toList()))
    }
}
