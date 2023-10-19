package com.cofbro.qian.account.manager

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.cofbro.hymvvmutils.base.BaseActivity
import com.cofbro.hymvvmutils.base.saveUsedSp
import com.cofbro.qian.account.adapter.AccountsAdpater
import com.cofbro.qian.databinding.ActivityAccountmanagerBinding
import com.cofbro.qian.login.LoginActivity
import com.cofbro.qian.profile.LogoutDialog
import com.cofbro.qian.utils.CacheUtils

/**
 * 关联账号，实现一起签到
 */
class AccountManagerActivity :  BaseActivity<AccountManagerViewModel, ActivityAccountmanagerBinding>(){
   private var AccountsAdpater: AccountsAdpater? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        initArgs()
        initView()
        initViewClick()
    }
    private fun initArgs(){
         viewModel.accountsList = CacheUtils.cacheUser["userLists"]?: arrayListOf()
    }
    private fun initView(){
        binding?.accounts?.apply {
            AccountsAdpater = AccountsAdpater(context = applicationContext,viewModel.accountsList)
            AccountsAdpater?.setItemClickListener {user->
                /**
                 * 设计点击切换账号,更换cache,弹出dialog
                 */
                viewModel.dialog = LogoutDialog(applicationContext, confirmText = "确定切换账户吗？").apply {
                    setConfirmClickListener {
                        CacheUtils.cache["uid"] = user.uid
                        CacheUtils.cache["cookies"] = user.cookie
                        CacheUtils.cache["fid"] = user.fid
                        saveUserInfo(user,context)
                    }
                    setCancelClickListener {
                        this.dismiss()
                    }
                }

            }
            adapter = AccountsAdpater
            layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }


    }
    private fun saveUserInfo(user: User,context: Context) {
        clearUserInfo(context)
        if (user.user.isNotEmpty() && user.pwd.isNotEmpty()) {
            saveUsedSp("username", user.user)
            saveUsedSp("password", user.pwd)
        }

    }
    private fun clearUserInfo(context: Context){
        context.saveUsedSp("username", "")
        context.saveUsedSp("password", "")
        Toast.makeText(context, "数据删除成功", Toast.LENGTH_SHORT).show()
    }
    private fun initViewClick(){
        binding?.addaccount?.setOnClickListener {
            /**
             * 跳转登录界面，并保存信息回到manager 并判定是否为拓展账号
             */
            val intent = Intent(this,LoginActivity(extents = true)::class.java)
            startActivity(intent)
        }
    }

}