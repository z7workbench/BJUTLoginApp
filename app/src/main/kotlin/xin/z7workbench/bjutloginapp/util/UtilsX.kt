package xin.z7workbench.bjutloginapp.util

import android.app.Activity
import xin.z7workbench.bjutloginapp.LoginApp

/**
 * Created by ZeroGo on 2017/11/6.
 */

val Activity.app: LoginApp
    get() = this.application as LoginApp