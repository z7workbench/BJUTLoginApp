package party.iobserver.bjutloginapp.util

import android.app.Activity
import party.iobserver.bjutloginapp.LoginApp

/**
 * Created by ZeroGo on 2017/11/6.
 */

val Activity.app: LoginApp
    get() = this.application as LoginApp