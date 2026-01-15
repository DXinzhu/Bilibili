package com.example.bilibili.presentation.accountprofile

import android.content.Context
import com.example.bilibili.data.model.User
import com.google.gson.Gson
import java.io.InputStreamReader

/**
 * 账号资料的Presenter
 * 按照MVP模式实现，负责处理账号资料相关的业务逻辑
 */
class AccountProfilePresenter(private val context: Context) {

    private val gson = Gson()

    /**
     * 加载用户数据
     */
    fun loadUserData(): User? {
        return try {
            val inputStream = context.assets.open("data/user.json")
            val reader = InputStreamReader(inputStream)
            gson.fromJson(reader, User::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
