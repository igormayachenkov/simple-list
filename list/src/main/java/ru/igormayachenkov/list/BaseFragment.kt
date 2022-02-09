package ru.igormayachenkov.list

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment(){
    //----------------------------------------------------------------------------------------------
    // ANIMATION
    val useAnimation: Boolean = true
    val animationDuration:Long = 200

    fun showFragment() {
        view?.apply {
            if(visibility != VISIBLE) {
                visibility = VISIBLE
                if (useAnimation) {
                    alpha = 0f
                    animate()
                        .alpha(1f)
                        .setDuration(animationDuration)
                        .setListener(null)
                }
            }
        }
    }

    fun hideFragment() {
        view?.apply {
            if(visibility==VISIBLE) {
                Utils.hideSoftKeyboard(activity)

                if (useAnimation) {
                    animate()
                        .alpha(0f)
                        .setDuration(animationDuration)
                        .setListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator) {
                                visibility = GONE
                            }
                        })
                } else {
                    visibility = GONE
                }
            }
        }
    }
}