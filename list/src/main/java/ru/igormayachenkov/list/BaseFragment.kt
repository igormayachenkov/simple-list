package ru.igormayachenkov.list

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment(){
    //val isVisible : Boolean
      //  get() = {return view?.visibility == VISIBLE}

    //val useAnimation: Boolean = true
    val animationDuration:Long = 200

    // Load data to controls
    abstract fun load():Boolean

    private fun show(useAnimation: Boolean) {
        view?.apply {
            // Show fragment
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

    private fun hide(useAnimation: Boolean) {
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

    // Update UI according to the data
    open fun update(useAnimation: Boolean){
        if(load())
            show(useAnimation)
        else
            hide(useAnimation)
    }
}