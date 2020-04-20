package com.cordyho.utils

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

object ImageLoader {

    private var errorImg = 0  //错误时显示的图片
    private var placeholderImg = 0 //占位图

    fun init(errorImg: Int, placeholderImg: Int) {
        this.errorImg = errorImg
        this.placeholderImg = placeholderImg
    }

    fun setImageFromUrlFC(url: Any?, iv_image: ImageView) {  //fitCenter
        val options = RequestOptions()
                .error(errorImg)
                .placeholder(placeholderImg)
                .dontAnimate()
                .fitCenter()
        iv_image.context?.let {
            Glide.with(it)
                    .load(CordyUtils.baseUrl + url)
                    .apply(options)
                    .into(iv_image)
        }
    }

    fun setImageFromUrlNoBaseUrlFC(url: Any?, iv_image: ImageView) {  //fitCenter
        val options = RequestOptions()
                .error(errorImg)
                .placeholder(placeholderImg)
                .dontAnimate()
                .fitCenter()
        iv_image.context?.let {
            Glide.with(it)
                    .load(url)
                    .apply(options)
                    .into(iv_image)
        }
    }

    fun setImageFromUrlCC(url: Any?, iv_image: ImageView) {  //centerCrop
        val options = RequestOptions()
                .error(errorImg)
                .placeholder(placeholderImg)
                .dontAnimate()
                .centerCrop()
        iv_image.context?.let {
            Glide.with(it)
                    .load(CordyUtils.baseUrl + url)
                    .apply(options)
                    .into(iv_image)
        }
    }

    fun setImageFromUrlNoBaseUrlCC(url: Any?, iv_image: ImageView) {  //centerCrop
        val options = RequestOptions()
                .error(errorImg)
                .placeholder(placeholderImg)
                .dontAnimate()
                .centerCrop()
        iv_image.context?.let {
            Glide.with(it)
                    .load(url)
                    .apply(options)
                    .into(iv_image)
        }
    }

    fun setImageFromResource(id: Int?, iv_image: ImageView) {
        val options = RequestOptions()
                .dontAnimate()
                .fitCenter()
        iv_image.context?.let {
            Glide.with(it)
                    .load(id)
                    .apply(options)
                    .into(iv_image)
        }
    }
}