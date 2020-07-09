import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.annotation.LayoutRes
import androidx.appcompat.widget.AppCompatTextView
import com.example.lightweightalerter.R
import kotlin.math.cos
import kotlin.math.pow

typealias Completion = () -> Unit

object Alerter {
    enum class AlertLevel {
        SUCCESS,
        INFO,
        WARNING,
        DANGER
    }

    fun showTopDialog(
            context: Context,
            title: String,
            shortMessage: String,
            alertLevel: AlertLevel
    ): Dialog {
        val dialog = Dialog(context)
        try {

            dialog.inflateOnTop(
                when (alertLevel) {
                    AlertLevel.SUCCESS -> R.layout.layout_top_success_message
                    AlertLevel.INFO -> R.layout.layout_top_info_message
                    AlertLevel.WARNING -> R.layout.layout_top_warning_message
                    AlertLevel.DANGER -> R.layout.layout_top_error_message
                }
            )

            dialog.findViewById<AppCompatTextView>(R.id.txtTitle).text = title
            dialog.findViewById<AppCompatTextView>(R.id.txtShortMessage).text = shortMessage
            dialog.show()

            //Animation
            dialog.findViewById<View>(R.id.dialogRootView).slideInFromTop()
            Handler(Looper.getMainLooper()).postDelayed({
                try {
                    dialog.findViewById<View>(R.id.dialogRootView).slideOutToTop {
                        dialog.dismiss()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, 3000)

            dialog.findViewById<View>(R.id.imgIcon).startBounceAnimation()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return dialog
    }

    /**
     * Inflate layout to the dialog and adjust the dialog view to the top of screen
     */
    private fun Dialog.inflateOnTop(@LayoutRes layoutResId: Int): Dialog{
        this.requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.setContentView(layoutResId)
        this.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        this.setCancelable(true)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(this.window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.TOP

        this.window!!.attributes = lp
        return this
    }

    private fun View.slideInFromTop() {
        val shake = AnimationUtils.loadAnimation(this.context, R.anim.slide_in_from_top)
        this.startAnimation(shake)
    }

    private fun View.slideOutToTop(completion: Completion? = null) {
        val shake = AnimationUtils.loadAnimation(this.context, R.anim.slide_out_to_top)
        shake.setAnimationListener(object: Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {
            }
            override fun onAnimationEnd(animation: Animation?) {
                completion?.invoke()
            }
            override fun onAnimationStart(animation: Animation?) {
            }
        })
        this.startAnimation(shake)
    }

    private fun View.startBounceAnimation() {
        val myAnim = AnimationUtils.loadAnimation(this.context, R.anim.bounce)
        val interpolator = MyBounceInterpolator(0.2, 20.0)
        myAnim.interpolator = interpolator
        this.startAnimation(myAnim)
    }

    internal class MyBounceInterpolator(amplitude: Double, frequency: Double) :
        android.view.animation.Interpolator {
        private var mAmplitude = 1.0
        private var mFrequency = 10.0

        init {
            mAmplitude = amplitude
            mFrequency = frequency
        }

        override fun getInterpolation(time: Float): Float {
            return (-1.0 * Math.E.pow(-time / mAmplitude) *
                    cos(mFrequency * time) + 1).toFloat()
        }
    }
}