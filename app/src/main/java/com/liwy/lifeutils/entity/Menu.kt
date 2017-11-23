package com.liwy.lifeutils.entity

/**
 * Created by liwy on 2017/11/20.
 */
class Menu(name:String){
    var name:String? = name
    var id:String? = null
    var logo:String? = null
    var resId:Int? = -1
    var desActivity:Class<Any>? = null

    constructor(name1: String,resId:Int):this(name = name1){
        this.resId = resId
    }

    constructor(name1: String,resId:Int,des:Class<Any>):this(name = name1){
        this.resId = resId
        this.desActivity = des
    }
}