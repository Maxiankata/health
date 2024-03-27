package com.example.healthtracker.ui.home.walking

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.healthtracker.AuthImpl
import com.example.healthtracker.MainActivity
import com.example.healthtracker.MyApplication
import com.example.healthtracker.ui.account.friends.challenges.Challenge
import com.example.healthtracker.data.room.RoomToUserMegaInfoAdapter
import com.example.healthtracker.data.user.UserAutomaticInfo
import com.example.healthtracker.data.user.UserDays
import com.example.healthtracker.data.user.UserPutInInfo
import com.example.healthtracker.ui.calendarToString
import com.example.healthtracker.ui.startStepCounterService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.Calendar

class Alarmer:AlarmScheduler {
    val context = MyApplication.getContext()
    val alarmManager = context.getSystemService(AlarmManager::class.java)
    override fun schedule(item: AlarmItem) {
        val intent = Intent(context, AlarmRecieverer::class.java)
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 22)
        calendar.set(Calendar.MINUTE, 9)
        calendar.set(Calendar.SECOND, 0)
        val currentTime = System.currentTimeMillis()
        if (calendar.timeInMillis < currentTime) {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            PendingIntent.getBroadcast(context,item.hashCode(),intent,PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        )
    }

    override fun cancel(item: AlarmItem) {
        alarmManager.cancel(
            PendingIntent.getBroadcast(
            context,
            item.hashCode(),
            Intent(context,AlarmRecieverer::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
        )
    }

}
class AlarmRecieverer:BroadcastReceiver(){
    private val customCoroutineScope = CoroutineScope(Dispatchers.IO)
    private val authImpl = AuthImpl()
    override fun onReceive(context: Context?, intent: Intent?) {
        val userDao = MainActivity.getDatabaseInstance().dao()
        val roomToUserMegaInfoAdapter = RoomToUserMegaInfoAdapter()
        Log.d("Launching alarm reciever", "")
        val stepCounterService = StepCounterService()
        customCoroutineScope.launch {
            val userPutInInfo = userDao.getPutInInfo()
            val userAutomaticInfo = userDao.getAutomaticInfo()
            val userChallenges = userDao.getEntireUser()?.challenges as ArrayList<Challenge>
            val userDays = userDao.getEntireUser()?.userDays as ArrayList<UserDays>
            val sendToday = calendarToString(Calendar.getInstance())
            val day = UserDays( userPutInInfo, userAutomaticInfo,userChallenges,
                sendToday
            )
            userDays.add(day)
            userDao.updateDays(userDays)
            val syncer = roomToUserMegaInfoAdapter.adapt(userDao.getEntireUser()!!)
            authImpl.sync(syncer)
            authImpl.clearChallenges()
            Log.d("Updated days from val", userDays.toString())
            Log.d("Updated days from base", userDao.getEntireUser()!!.userDays.toString())
            userDao.wipeUserAutomaticInfo()
            userDao.wipeUserPutInInfo()
            userDao.wipeChallenges()
            userDao.updateUserAutomaticInfo(UserAutomaticInfo())
            userDao.updateUserPutInInfo(UserPutInInfo())
            stepCounterService.nullifySteps()
            stepCounterService.stopSelf()
            startStepCounterService()
            Log.d("wiped days from base", userDao.getEntireUser()!!.userAutomaticInfo.toString())

        }
    }
}
data class AlarmItem(
    val date:LocalDateTime,
    val message:String
)
interface AlarmScheduler{
    fun schedule(item:AlarmItem)
    fun cancel(item:AlarmItem)
}