package expo.modules.updates.db

import android.net.Uri
import androidx.room.Room
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.platform.app.InstrumentationRegistry
import expo.modules.updates.UpdatesConfiguration
import expo.modules.updates.db.entity.UpdateEntity
import expo.modules.updates.db.enums.UpdateStatus
import io.mockk.spyk
import io.mockk.verify
import org.junit.Assert.*

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@RunWith(AndroidJUnit4ClassRunner::class)
class BuildDataTest {
  private lateinit var db: UpdatesDatabase
  private val scopeKey = "test"
  private val buildMapDefault = mapOf(
    "updateUrl" to Uri.parse("https://exp.host/@test/test"),
    "requestHeaders" to mapOf("expo-channel-name" to "test")
  )
  private val buildMapTestChannel = mapOf(
    "updateUrl" to Uri.parse("https://exp.host/@test/test"),
    "requestHeaders" to mapOf("expo-channel-name" to "testTwo")
  )
  private val updatesConfigDefault = UpdatesConfiguration().loadValuesFromMap(
    buildMapDefault
  )
  private val updatesConfigTestChannel = UpdatesConfiguration().loadValuesFromMap(
    buildMapTestChannel
  )
  private lateinit var spyBuildData: BuildData

  @Before
  fun setUp() {
    val context = InstrumentationRegistry.getInstrumentation().targetContext
    db = Room.inMemoryDatabaseBuilder(context, UpdatesDatabase::class.java).build()
    spyBuildData = spyk(BuildData)
  }

  @After
  fun tearDown() {
    db.close()
  }

  @Test
  fun ensureBuildDataIsConsistent_buildDataIsNull() {
    val nullBuildDataJSON = spyBuildData.getBuildData(db, scopeKey)
    assertNull(nullBuildDataJSON)

    spyBuildData.ensureBuildDataIsConsistent(updatesConfigDefault, db, scopeKey)

    val buildDataJSON = spyBuildData.getBuildData(db, scopeKey)
    assertNotNull(buildDataJSON)
    verify(exactly = 0) { spyBuildData.clearAllReadyUpdates(any()) }
    verify(exactly = 1) { spyBuildData.setBuildData(any(), any(), any()) }
  }

  @Test
  fun ensureBuildDataIsConsistent_buildDataIsInconsistent() {
    spyBuildData.setBuildData(updatesConfigDefault, db, scopeKey)
    val buildDataJSONDefault = spyBuildData.getBuildData(db, scopeKey)!!
    val isConsistentDefault = spyBuildData.isBuildDataConsistent(updatesConfigDefault, buildDataJSONDefault)
    val isConsistentTestChannel = spyBuildData.isBuildDataConsistent(updatesConfigTestChannel, buildDataJSONDefault)
    assertTrue(isConsistentDefault)
    assertFalse(isConsistentTestChannel)
    verify(exactly = 0) { spyBuildData.clearAllReadyUpdates(any()) }
    verify(exactly = 1) { spyBuildData.setBuildData(any(), any(), any()) }

    spyBuildData.ensureBuildDataIsConsistent(updatesConfigTestChannel, db, scopeKey)

    val buildDataJSONTestChannel = spyBuildData.getBuildData(db, scopeKey)!!
    val isConsistentDefaultAfter = spyBuildData.isBuildDataConsistent(updatesConfigDefault, buildDataJSONTestChannel)
    val isConsistentTestChannelAfter = spyBuildData.isBuildDataConsistent(updatesConfigTestChannel, buildDataJSONTestChannel)
    assertFalse(isConsistentDefaultAfter)
    assertTrue(isConsistentTestChannelAfter)
    verify(exactly = 1) { spyBuildData.clearAllReadyUpdates(any()) }
    verify(exactly = 2) { spyBuildData.setBuildData(any(), any(), any()) }
  }

  @Test
  fun ensureBuildDataIsConsistent_buildDataIsConsistent() {
    spyBuildData.setBuildData(updatesConfigDefault, db, scopeKey)
    val buildDataJSONDefault = spyBuildData.getBuildData(db, scopeKey)!!
    val isConsistentDefault = spyBuildData.isBuildDataConsistent(updatesConfigDefault, buildDataJSONDefault)
    val isConsistentTestChannel = spyBuildData.isBuildDataConsistent(updatesConfigTestChannel, buildDataJSONDefault)
    assertTrue(isConsistentDefault)
    assertFalse(isConsistentTestChannel)
    verify(exactly = 0) { spyBuildData.clearAllReadyUpdates(any()) }
    verify(exactly = 1) { spyBuildData.setBuildData(any(), any(), any()) }

    spyBuildData.ensureBuildDataIsConsistent(updatesConfigDefault, db, scopeKey)

    val buildDataJSONTestChannel = spyBuildData.getBuildData(db, scopeKey)!!
    val isConsistentDefaultAfter = spyBuildData.isBuildDataConsistent(updatesConfigDefault, buildDataJSONTestChannel)
    val isConsistentTestChannelAfter = spyBuildData.isBuildDataConsistent(updatesConfigTestChannel, buildDataJSONTestChannel)
    assertTrue(isConsistentDefaultAfter)
    assertFalse(isConsistentTestChannelAfter)
    verify(exactly = 0) { spyBuildData.clearAllReadyUpdates(any()) }
    verify(exactly = 1) { spyBuildData.setBuildData(any(), any(), any()) }
  }

  @Test
  fun clearAllReadyUpdates() {
    val uuid = UUID.randomUUID()
    val date = Date()
    val runtimeVersion = "1.0"
    val projectId = "https://exp.host/@esamelson/test-project"
    val testUpdate = UpdateEntity(uuid, date, runtimeVersion, projectId)
    testUpdate.status = UpdateStatus.READY

    db.updateDao().insertUpdate(testUpdate)
    val byId = db.updateDao().loadUpdateWithId(uuid)
    assertNotNull(byId)

    BuildData.clearAllReadyUpdates(db)

    val shouldBeNull = db.updateDao().loadUpdateWithId(uuid)
    assertNull(shouldBeNull)
  }
}
