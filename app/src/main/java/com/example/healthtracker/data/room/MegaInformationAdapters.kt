package com.example.healthtracker.data.room

import com.example.healthtracker.data.user.UserMegaInfo

interface Adapter<T, K> {
    fun adapt(t: T): K?
}
class UserMegaInfoToRoomAdapter : Adapter<UserMegaInfo,UserData>{
    override fun adapt(t: UserMegaInfo): UserData? {
        return t.userInfo.uid?.let { UserData(it,t.userInfo,t.userSettingsInfo,t.userAutomaticInfo,t.userPutInInfo,t.userFriends, t.userDays, t.challenges, t.achievements) }
    }

}

class RoomToUserMegaInfoAdapter:Adapter<UserData,UserMegaInfo>{
    override fun adapt(t: UserData): UserMegaInfo {
        return UserMegaInfo(t.userInfo,t.userAutomaticInfo,t.userFriends,t.userPutInInfo,t.userSettingsInfo,t.userDays)
    }
}