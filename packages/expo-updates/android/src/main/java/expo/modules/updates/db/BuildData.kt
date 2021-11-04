package expo.modules.updates.db

import android.net.Uri
import expo.modules.jsonutils.getNullable
import expo.modules.updates.UpdatesConfiguration
import expo.modules.updates.db.enums.UpdateStatus
import org.json.JSONObject

/**
 * The build data stored by the configuration is subject to change when
 * a user updates the binary.
 *
 * This can lead to inconsistent update loading behavior, for
 * example: https://github.com/expo/expo/issues/14372
 *
 * This singleton wipes the updates when any of the tracked build data
 * changes. This leaves the user in the same situation as a fresh install.
 *
 * So far we only know that `releaseChannel` and
 * `requestHeaders[expo-channel-name]` are dangerous to change, but have
 * included a few more that both seem unlikely to change (so we clear
 * the updates cache rarely) and likely to
 * cause bugs when they do. The tracked fields are:
 *
 *   UPDATES_CONFIGURATION_RELEASE_CHANNEL_KEY
 *   UPDATES_CONFIGURATION_UPDATE_URL_KEY
 *
 * and all of the values in json
 *
 *   UPDATES_CONFIGURATION_REQUEST_HEADERS_KEY
 */
object BuildData {
  var buildKey = "STATIC_BUILD_DATA"

  fun ensureBuildDataIsConsistent(
    updatesConfiguration: UpdatesConfiguration,
    database: UpdatesDatabase,
    scopeKey: String
  ) {
    val buildJSON = getBuildData(database, scopeKey)
    if (buildJSON == null) {
      setBuildData(updatesConfiguration, database, scopeKey)
    } else if (!isBuildDataConsistent(updatesConfiguration, buildJSON)) {
      clearAllReadyUpdates(database)
      setBuildData(updatesConfiguration, database, scopeKey)
    }
  }

  fun clearAllReadyUpdates(database: UpdatesDatabase) {
    val allUpdates = database.updateDao().loadAllUpdatesWithStatus(UpdateStatus.READY)
    database.updateDao().deleteUpdates(allUpdates)
  }

  fun isBuildDataConsistent(
    updatesConfiguration: UpdatesConfiguration,
    buildJSON: JSONObject
  ): Boolean {
    val requestHeadersJSON = buildJSON.getJSONObject(UpdatesConfiguration.UPDATES_CONFIGURATION_REQUEST_HEADERS_KEY)

    if (requestHeadersJSON.length() != updatesConfiguration.requestHeaders.size) return false

    return mutableListOf<Boolean>().apply {
      add(buildJSON.getNullable<String>(UpdatesConfiguration.UPDATES_CONFIGURATION_RELEASE_CHANNEL_KEY) == updatesConfiguration.releaseChannel)
      add(buildJSON.get(UpdatesConfiguration.UPDATES_CONFIGURATION_UPDATE_URL_KEY)?.let { Uri.parse(it.toString()) } == updatesConfiguration.updateUrl)
      for ((key, value) in updatesConfiguration.requestHeaders) {
        add(value == requestHeadersJSON.optString(key))
      }
    }.all { it }
  }

  fun getBuildData(database: UpdatesDatabase, scopeKey: String): JSONObject? {
    val buildJSONString = database.jsonDataDao()?.loadJSONStringForKey(buildKey, scopeKey)
    return if (buildJSONString == null) null else JSONObject(buildJSONString)
  }

  fun setBuildData(
    updatesConfiguration: UpdatesConfiguration,
    database: UpdatesDatabase,
    scopeKey: String
  ) {
    val requestHeadersJSON = JSONObject().apply {
      for ((key, value) in updatesConfiguration.requestHeaders) put(key, value)
    }

    val buildDataJSON = JSONObject().apply {
      put(UpdatesConfiguration.UPDATES_CONFIGURATION_RELEASE_CHANNEL_KEY, updatesConfiguration.releaseChannel)
      put(UpdatesConfiguration.UPDATES_CONFIGURATION_UPDATE_URL_KEY, updatesConfiguration.updateUrl)
      put(UpdatesConfiguration.UPDATES_CONFIGURATION_REQUEST_HEADERS_KEY, requestHeadersJSON)
    }

    database.jsonDataDao()?.setJSONStringForKey(buildKey, buildDataJSON.toString(), scopeKey)
  }
}
