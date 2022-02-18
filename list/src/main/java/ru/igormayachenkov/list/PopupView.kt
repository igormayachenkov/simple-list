package ru.igormayachenkov.list

import android.graphics.Rect
import android.view.Gravity
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.RelativeLayout

//----------------------------------------------------------------------------------------------
// CUSTOM POPUP VIEW/MENU (instead of PopupMenu, PopupWindow)
// Why? There are two issues:
//  - standard menu doesn't show icons!!! http://developer.alexanderklimov.ru/android/theory/menu.php
//  - standard popup menu breaks down fullscreen mode https://stackoverflow.com/questions/54140793/how-to-fix-navigation-bar-icons-still-showing-when-pop-up-menu-is-opened-ful
//
// Menus and Popups https://guides.codepath.com/android/menus-and-popups
// Control the system UI visibility https://developer.android.com/training/system-ui

class PopupView (
        val container:RelativeLayout
){
    enum class HORIZ_ALIGNMENT{LEFT, RIGHT}
    enum class VERT_ALIGNMENT {TOP, BOTTOM}

    // Must be called in Activity.onCreate / Fragment.onViewCreated
    init{
        container.setOnClickListener { hide() }
        hide()
    }

    val isVisible:Boolean
        get() = container.visibility== VISIBLE

    fun show(view: View, anchor: View, vert:VERT_ALIGNMENT, horiz:HORIZ_ALIGNMENT){
        container.removeAllViews()

        // Size
        val params = RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        )

        // Window frame
        val frame = Rect()
        anchor.getWindowVisibleDisplayFrame(frame)
        val hTopbar = frame.top

        // Anchor position
        val loc = IntArray(2)
        anchor.getLocationInWindow(loc)  //anchor.getLocationOnScreen(loc)
        val yAnchor = loc[1]

        // Vert alignment
        var vGravity:Int=0
        if(vert==VERT_ALIGNMENT.TOP) {
            // AT THE TOP OF CONTAINER
            vGravity = Gravity.BOTTOM
            params.bottomMargin = frame.height() - yAnchor + hTopbar
        }else {
            // AT THE BOTTOM OF CONTAINER
            vGravity = Gravity.TOP
            params.topMargin = yAnchor + anchor.height - hTopbar
        }
        // Horiz alignment
        container.gravity = vGravity or
            when(horiz){
                HORIZ_ALIGNMENT.LEFT  -> Gravity.START
                HORIZ_ALIGNMENT.RIGHT -> Gravity.END
            }

        // Place in container and show
        container.addView(view, params)
        container.visibility = VISIBLE
    }

    fun hide(){
        container.visibility = GONE
        container.removeAllViews()
    }
}