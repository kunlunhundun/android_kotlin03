/*
 * Copyright © 2020 WireGuard LLC. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.wireguard.android.alutils


import android.widget.ImageView

/**
 * 帧动画加载工具类，减少内存占用
 *
 *
 */
class FrameAnimation {

    private var mIsRepeat: Boolean = false

    private var mAnimationListener: AnimationListener? = null

    private var mImageView: ImageView? = null

    private var mFrameRess: IntArray? = null

    /**
     * 每帧动画的播放间隔数组
     */
    private var mDurations: IntArray? = null

    /**
     * 每帧动画的播放间隔
     */
    private var mDuration: Int = 0

    /**
     * 下一遍动画播放的延迟时间
     */
    private var mDelay: Int = 0

    private var mLastFrame: Int = 0

    private var mNext: Boolean = false

    var isPause: Boolean = false
        private set

    private var mCurrentSelect: Int = 0

    private var mCurrentFrame: Int = 0


    /**
     * @param iv       播放动画的控件
     * @param frameRes 播放的图片数组
     * @param duration 每帧动画的播放间隔(毫秒)
     * @param isRepeat 是否循环播放
     */
    constructor(iv: ImageView, frameRes: IntArray, duration: Int, isRepeat: Boolean) {
        this.mImageView = iv
        this.mFrameRess = frameRes
        this.mDuration = duration
        this.mLastFrame = frameRes.size - 1
        this.mIsRepeat = isRepeat
        play(0)
    }

    /**
     * @param iv        播放动画的控件
     * @param frameRess 播放的图片数组
     * @param durations 每帧动画的播放间隔(毫秒)
     * @param isRepeat  是否循环播放
     */
    constructor(iv: ImageView, frameRess: IntArray, durations: IntArray, isRepeat: Boolean) {
        this.mImageView = iv
        this.mFrameRess = frameRess
        this.mDurations = durations
        this.mLastFrame = frameRess.size - 1
        this.mIsRepeat = isRepeat
        playByDurations(0)
    }

    /**
     * 循环播放动画
     *
     * @param iv        播放动画的控件
     * @param frameRess 播放的图片数组
     * @param duration  每帧动画的播放间隔(毫秒)
     * @param delay     循环播放的时间间隔
     */
    constructor(iv: ImageView, frameRess: IntArray, duration: Int, delay: Int) {
        this.mImageView = iv
        this.mFrameRess = frameRess
        this.mDuration = duration
        this.mDelay = delay
        this.mLastFrame = frameRess.size - 1
        playAndDelay(0)
    }

    /**
     * 循环播放动画
     *
     * @param iv        播放动画的控件
     * @param frameRess 播放的图片数组
     * @param durations 每帧动画的播放间隔(毫秒)
     * @param delay     循环播放的时间间隔
     */
    constructor(iv: ImageView, frameRess: IntArray, durations: IntArray, delay: Int) {
        this.mImageView = iv
        this.mFrameRess = frameRess
        this.mDurations = durations
        this.mDelay = delay
        this.mLastFrame = frameRess.size - 1
        playByDurationsAndDelay(0)
    }

    private fun playByDurationsAndDelay(i: Int) {
        mImageView!!.postDelayed(Runnable {
            if (isPause) {   // 暂停和播放需求
                mCurrentSelect = SELECTED_A
                mCurrentFrame = i
                return@Runnable
            }
            if (0 == i) {
                if (mAnimationListener != null) {
                    mAnimationListener!!.onAnimationStart()
                }
            }
            mImageView!!.setBackgroundResource(mFrameRess!![i])
            if (i == mLastFrame) {
                if (mAnimationListener != null) {
                    mAnimationListener!!.onAnimationRepeat()
                }
                mNext = true
                playByDurationsAndDelay(0)
            } else {
                playByDurationsAndDelay(i + 1)
            }
        }, (if (mNext && mDelay > 0) mDelay else mDurations!![i]).toLong())

    }

    private fun playAndDelay(i: Int) {
        mImageView!!.postDelayed(Runnable {
            if (isPause) {
                if (isPause) {
                    mCurrentSelect = SELECTED_B
                    mCurrentFrame = i
                    return@Runnable
                }
                return@Runnable
            }
            mNext = false
            if (0 == i) {
                if (mAnimationListener != null) {
                    mAnimationListener!!.onAnimationStart()
                }
            }
            mImageView!!.setBackgroundResource(mFrameRess!![i])
            if (i == mLastFrame) {
                if (mAnimationListener != null) {
                    mAnimationListener!!.onAnimationRepeat()
                }
                mNext = true
                playAndDelay(0)
            } else {
                playAndDelay(i + 1)
            }
        }, (if (mNext && mDelay > 0) mDelay else mDuration).toLong())

    }

    private fun playByDurations(i: Int) {
        mImageView!!.postDelayed(Runnable {
            if (isPause) {
                if (isPause) {
                    mCurrentSelect = SELECTED_C
                    mCurrentFrame = i
                    return@Runnable
                }
                return@Runnable
            }
            if (0 == i) {
                if (mAnimationListener != null) {
                    mAnimationListener!!.onAnimationStart()
                }
            }
            mImageView!!.setBackgroundResource(mFrameRess!![i])
            if (i == mLastFrame) {
                if (mIsRepeat) {
                    if (mAnimationListener != null) {
                        mAnimationListener!!.onAnimationRepeat()
                    }
                    playByDurations(0)
                } else {
                    if (mAnimationListener != null) {
                        mAnimationListener!!.onAnimationEnd()
                    }
                }
            } else {

                playByDurations(i + 1)
            }
        }, mDurations!![i].toLong())

    }

     fun play(i: Int) {
        mImageView!!.postDelayed(Runnable {
            if (isPause) {
                if (isPause) {
                    mCurrentSelect = SELECTED_D
                    mCurrentFrame = i
                    return@Runnable
                }
                return@Runnable
            }
            if (0 == i) {
                if (mAnimationListener != null) {
                    mAnimationListener!!.onAnimationStart()
                }
            }
            mImageView!!.setBackgroundResource(mFrameRess!![i])
            if (i == mLastFrame) {

                if (mIsRepeat) {
                    if (mAnimationListener != null) {
                        mAnimationListener!!.onAnimationRepeat()
                    }
                    play(0)
                } else {
                    if (mAnimationListener != null) {
                        mAnimationListener!!.onAnimationEnd()
                    }
                }

            } else {

                play(i + 1)
            }
        }, mDuration.toLong())
    }

    interface AnimationListener {

        /**
         *
         * Notifies the start of the animation.
         */
        fun onAnimationStart()

        /**
         *
         * Notifies the end of the animation. This callback is not invoked
         * for animations with repeat count set to INFINITE.
         */
        fun onAnimationEnd()

        /**
         *
         * Notifies the repetition of the animation.
         */
        fun onAnimationRepeat()
    }

    /**
     *
     * Binds an animation listener to this animation. The animation listener
     * is notified of animation events such as the end of the animation or the
     * repetition of the animation.
     *
     * @param listener the animation listener to be notified
     */
    fun setAnimationListener(listener: AnimationListener) {
        this.mAnimationListener = listener
    }

    fun release() {
        pauseAnimation()
    }

    fun pauseAnimation() {
        this.isPause = true
    }

    fun restartAnimation() {
        if (isPause) {
            isPause = false
            when (mCurrentSelect) {
                SELECTED_A -> playByDurationsAndDelay(mCurrentFrame)
                SELECTED_B -> playAndDelay(mCurrentFrame)
                SELECTED_C -> playByDurations(mCurrentFrame)
                SELECTED_D -> play(mCurrentFrame)
                else -> {
                }
            }
        }
    }

    companion object {

        private val SELECTED_A = 1

        private val SELECTED_B = 2

        private val SELECTED_C = 3

        private val SELECTED_D = 4
    }

}
