package com.liwy.lifeutils.mvp.qrcode

import android.graphics.Bitmap
import com.liwy.library.base.view.IView

interface QrCodeView : IView{
    fun updateImageView(bitmap:Bitmap?)
}
