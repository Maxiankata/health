package com.example.healthtracker.ui.home.walking

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.healthtracker.AuthImpl
import com.example.healthtracker.MainActivity
import com.example.healthtracker.MyApplication
import com.example.healthtracker.data.room.RoomToUserMegaInfoAdapter
import com.example.healthtracker.data.user.UserAutomaticInfo
import com.example.healthtracker.data.user.UserDays
import com.example.healthtracker.data.user.UserInfo
import com.example.healthtracker.data.user.UserPutInInfo
import com.example.healthtracker.ui.account.friends.challenges.Challenge
import com.example.healthtracker.ui.calendarToString
import com.example.healthtracker.ui.getStepsValue
import com.example.healthtracker.ui.nullifyStepCounter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.Calendar

class Alarmer : AlarmScheduler {
    val context = MyApplication.getContext()
    val alarmManager = context.getSystemService(AlarmManager::class.java)
    override fun schedule(item: AlarmItem) {
        val intent = Intent(context, AlarmRecieverer::class.java)
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 30)
        val currentTime = System.currentTimeMillis()
        if (calendar.timeInMillis < currentTime) {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            PendingIntent.getBroadcast(
                context,
                item.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }

    override fun cancel(item: AlarmItem) {
        alarmManager.cancel(
            PendingIntent.getBroadcast(
                context,
                item.hashCode(),
                Intent(context, AlarmRecieverer::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }

}

class AlarmRecieverer : BroadcastReceiver() {
    private val customCoroutineScope = CoroutineScope(Dispatchers.IO)
    private val authImpl = AuthImpl()
    override fun onReceive(context: Context?, intent: Intent?) {
        val userDao = MainActivity.getDatabaseInstance().dao()
        val roomToUserMegaInfoAdapter = RoomToUserMegaInfoAdapter()
        customCoroutineScope.launch {
            val user = userDao.getEntireUser()
            val userPutInInfo = user.userPutInInfo
            val userAutomaticInfo = user.userAutomaticInfo
            userAutomaticInfo?.steps?.stepsGoal = userDao.getUserSettings()?.userGoals?.stepGoal
            userAutomaticInfo?.steps?.caloriesGoal =
                userDao.getUserSettings()?.userGoals?.calorieGoal
            userPutInInfo?.waterInfo?.waterGoal = userDao.getUserSettings()?.userGoals?.waterGoal
            val userChallenges = user.challenges as ArrayList<Challenge>
            val userDays = user.userDays as ArrayList<UserDays>
            val newUserInfo = UserInfo(
                uid = user.userInfo.uid,
                mail = user.userInfo.mail,
                theme =user.userInfo.theme,
                bgImage = user.userInfo.bgImage,
                image = user.userInfo.image,
                totalSteps = user.userInfo.totalSteps?.plus(getStepsValue()),
                username = user.userInfo.username
                )
            val sendToday = calendarToString(Calendar.getInstance())
            val day = UserDays(
                userPutInInfo, userAutomaticInfo, userChallenges,
                sendToday
            )
            userDays.add(day)
            async {
                userDao.updateDays(userDays)
                userDao.updateUserInfo(newUserInfo)
            }.await()
            val syncer = roomToUserMegaInfoAdapter.adapt(userDao.getEntireUser())
            authImpl.sync(syncer, syncer.userInfo.uid!!)
            authImpl.clearChallenges()
            userDao.wipeUserAutomaticInfo()
            userDao.wipeUserPutInInfo()
            userDao.wipeChallenges()
            userDao.updateUserAutomaticInfo(UserAutomaticInfo())
            userDao.updateUserPutInInfo(UserPutInInfo())
            nullifyStepCounter()
        }
    }
}

data class AlarmItem(
    val date: LocalDateTime,
    val message: String
)

interface AlarmScheduler {
    fun schedule(item: AlarmItem)
    fun cancel(item: AlarmItem)
}