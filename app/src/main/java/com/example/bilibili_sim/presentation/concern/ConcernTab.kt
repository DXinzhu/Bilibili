package com.example.bilibili_sim.presentation.concern

import android.content.Context
import com.example.bilibili_sim.common.utils.BilibiliAutoTestLogger
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.bilibili_sim.data.model.UPMaster
import com.example.bilibili_sim.presentation.concern.components.*
import com.example.bilibili_sim.presentation.concern.ConcernPresenter
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

/**
 * 关注页面
 * 展示用户关注的UP主列表
 */
@Composable
fun ConcernTab(
    context: Context,
    onBack: () -> Unit,
    onNavigateToUp: (String) -> Unit = {}
) {
    val presenter = remember { ConcernPresenter(context) }
    var upMasters by remember { mutableStateOf<List<UPMaster>>(emptyList()) }
    var selectedTab by remember { mutableStateOf(0) } // 0=关注, 1=粉丝
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        upMasters = presenter.getConcernPageUPMasters()
        // 指令14,15: 记录进入关注页
        BilibiliAutoTestLogger.logFollowPageEntered()
        // 指令16: 记录进入关注列表/最近访问
        BilibiliAutoTestLogger.logFollowListEntered()

        // 指令14: 导出关注动态数据供自动化测试使用
        try {
            val dynamics = presenter.getFollowingDynamics()
            val dynamicsArray = JSONArray()

            dynamics.forEach { post ->
                // 从videoPlayCount字符串中提取数字（如"1439播放" -> 1439）
                val playCount = if (post.videoPlayCount.isNotEmpty()) {
                    post.videoPlayCount.replace(Regex("[^0-9]"), "").toIntOrNull() ?: 0
                } else {
                    0
                }

                val dynamicObj = JSONObject().apply {
                    put("post_id", post.postId)
                    put("type", post.type.name)
                    put("up_master_id", post.upMasterId)
                    put("up_master_name", post.upMasterName)
                    put("like_count", post.likeCount)
                    put("play_count", playCount)
                    put("comment_count", post.commentCount)
                }
                dynamicsArray.put(dynamicObj)
            }

            val dynamicsData = JSONObject().apply {
                put("dynamics", dynamicsArray)
            }

            val dynamicsFile = File(context.filesDir, "following_dynamics.json")
            dynamicsFile.writeText(dynamicsData.toString())

            // 记录到日志
            val totalLikesAndPlays = dynamics.sumOf {
                val playCount = if (it.videoPlayCount.isNotEmpty()) {
                    it.videoPlayCount.replace(Regex("[^0-9]"), "").toIntOrNull() ?: 0
                } else {
                    0
                }
                it.likeCount + playCount
            }
            android.util.Log.d("BilibiliAutoTest", "FOLLOWING_DYNAMICS_TOTAL: $totalLikesAndPlays")
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // 指令15: 导出关注列表数据供自动化测试使用
        try {
            val followingList = JSONArray()
            var mutualFollowCount = 0
            // 只导出已关注的UP主
            upMasters.filter { it.isFollowed }.forEach { upMaster ->
                if (upMaster.isMutualFollow) {
                    mutualFollowCount++
                }
                val userObj = JSONObject().apply {
                    put("up_master_id", upMaster.upMasterId)
                    put("name", upMaster.name)
                    put("is_followed", upMaster.isFollowed)
                    put("is_mutual_follow", upMaster.isMutualFollow)
                    put("fans_count", upMaster.fansCount)
                }
                followingList.put(userObj)
            }

            val jsonData = JSONObject().apply {
                put("following_list", followingList)
                put("mutual_follow_count", mutualFollowCount)
            }

            val file = File(context.filesDir, "following_list.json")
            file.writeText(jsonData.toString())

            // 记录互粉数量到日志
            android.util.Log.d("BilibiliAutoTest", "MUTUAL_FOLLOW_COUNT: $mutualFollowCount")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // 顶部栏
        ConcernTopBar(onBack = onBack)

        // Tab切换栏
        ConcernFilterBar(
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it }
        )

        // UP主列表
        ConcernVideoList(
            upMasters = upMasters,
            onUpClick = { up -> onNavigateToUp(up.upMasterId) }
        )
    }
}
