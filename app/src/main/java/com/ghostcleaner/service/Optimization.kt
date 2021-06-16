package com.ghostcleaner.service

interface Optimization<T> {

    fun beforeOptimize()

    fun onOptimize(value: T)

    fun afterOptimize()
}