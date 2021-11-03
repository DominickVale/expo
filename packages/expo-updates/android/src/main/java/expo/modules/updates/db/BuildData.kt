package expo.modules.updates.db

import android.net.Uri
import android.util.Log
import expo.modules.jsonutils.getNullable
import expo.modules.updates.UpdatesConfiguration
import org.json.JSONObject

object BuildData {
    var buildKey = "BUILD_KEY"

    fun ensureBuildDataIsConsistent(
        updatesConfiguration: UpdatesConfiguration,
        database: UpdatesDatabase,
        scopeKey: String
    ){
        Log.i("ASDF","start ensureBuildDataIsConsistnet")
        val buildJSON = getBuildData(database, scopeKey)

        Log.i("ASDF", "build data json: $buildJSON")
        if(buildJSON== null){
            setBuildData(updatesConfiguration,database,scopeKey)
        } else if(!isBuildDataConsistent(updatesConfiguration,buildJSON)){
            clearAllUpdates(database)
            setBuildData(updatesConfiguration,database,scopeKey)
        }
        Log.i("ASDF","DONE" )
    }

    fun clearAllUpdates(database: UpdatesDatabase){
        Log.i("ASDF","clearing all updates")
        val allUpdates = database.updateDao().loadAllUpdates()
        database.updateDao().deleteUpdates(allUpdates)
        database.assetDao().deleteUnusedAssets()
    }

    fun isBuildDataConsistent(
        updatesConfiguration: UpdatesConfiguration,
        buildJSON: JSONObject
    ): Boolean {
        Log.i("ASDF","inside isBuildDataConsistent")
        var essentialBuildDetails = mutableListOf(
            buildJSON.getNullable<String>(UpdatesConfiguration.UPDATES_CONFIGURATION_RELEASE_CHANNEL_KEY) == updatesConfiguration.releaseChannel,
            buildJSON.get(UpdatesConfiguration.UPDATES_CONFIGURATION_UPDATE_URL_KEY)?.let { Uri.parse(it.toString()) } == updatesConfiguration.updateUrl
        )

        val requestHeadersJSON = buildJSON.getJSONObject(UpdatesConfiguration.UPDATES_CONFIGURATION_REQUEST_HEADERS_KEY)
        if (requestHeadersJSON.length()!=updatesConfiguration.requestHeaders.size){
            false
        }
        for((key,value) in updatesConfiguration.requestHeaders){
            essentialBuildDetails.add(value == requestHeadersJSON.optString(key))
        }

        Log.i("ASDF","essentialBuildDetails $essentialBuildDetails")
        return essentialBuildDetails.all { it }
    }

    fun getBuildData(database: UpdatesDatabase, scopeKey: String): JSONObject? {
        val buildJSONString = database.jsonDataDao()?.loadJSONStringForKey( buildKey ,scopeKey)

        return if(buildJSONString==null){
            null
        }else{
            JSONObject(buildJSONString)
        }
    }

    fun setBuildData(
        updatesConfiguration: UpdatesConfiguration,
        database: UpdatesDatabase,
        scopeKey: String
    ) {
        val buildDataJSON = JSONObject()

        buildDataJSON.put(UpdatesConfiguration.UPDATES_CONFIGURATION_RELEASE_CHANNEL_KEY,updatesConfiguration.releaseChannel)
        buildDataJSON.put(UpdatesConfiguration.UPDATES_CONFIGURATION_UPDATE_URL_KEY,updatesConfiguration.updateUrl)

        var requestHeadersJSON = JSONObject()
        for ((key, value) in updatesConfiguration.requestHeaders){
            requestHeadersJSON.put(key,value)
        }
        buildDataJSON.put(UpdatesConfiguration.UPDATES_CONFIGURATION_REQUEST_HEADERS_KEY,requestHeadersJSON)

        database.jsonDataDao()?.setJSONStringForKey( buildKey ,buildDataJSON.toString(), scopeKey )
    }
}