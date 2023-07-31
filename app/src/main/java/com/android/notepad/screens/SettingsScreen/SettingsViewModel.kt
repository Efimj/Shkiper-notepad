package com.android.notepad.screens.SettingsScreen

import android.Manifest
import android.app.Application
import android.content.ActivityNotFoundException
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.notepad.NotepadApplication
import com.android.notepad.R
import com.android.notepad.database.data.note.NoteMongoRepository
import com.android.notepad.database.data.reminder.ReminderMongoRepository
import com.android.notepad.helpers.localization.LocaleHelper
import com.android.notepad.helpers.localization.Localization
import com.android.notepad.helpers.openRateScreen
import com.android.notepad.services.backup_service.BackupData
import com.android.notepad.services.backup_service.BackupService
import com.android.notepad.services.backup_service.BackupServiceResult
import com.android.notepad.services.statistics_service.StatisticsService
import com.android.notepad.ui.theme.ColorThemes
import com.android.notepad.util.SnackbarHostUtil
import com.android.notepad.util.SnackbarVisualsCustom
import com.android.notepad.util.ThemeUtil
import com.gun0912.tedpermission.coroutine.TedPermission
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import javax.inject.Inject
import kotlin.math.log

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val noteRepository: NoteMongoRepository,
    private val reminderRepository: ReminderMongoRepository,
    private val application: Application,
) : ViewModel() {

    private val _settingsScreenState = mutableStateOf(SettingsScreenState())
    val settingsScreenState: State<SettingsScreenState> = _settingsScreenState

    /*******************
     * App theming
     *******************/

    val themeColorList = mutableStateOf(getThemeColorList())

    fun toggleAppTheme() {
        ThemeUtil.toggleTheme(application.applicationContext)
    }

    fun selectColorTheme(themeName: String) {
        val selectedTheme = ColorThemes.values().find { it.name == themeName } ?: return
        ThemeUtil.changeColorTheme(application.applicationContext, selectedTheme)
    }

    private fun getThemeColorList(): List<ColorThemes> {
        return ColorThemes.values().toList()
    }

    /*******************
     * App languages
     *******************/

    val localizationList = mutableStateOf(getLocalizationList())

    private fun getLocalizationList(): List<String> {
        return Localization.values().filter { it.name != NotepadApplication.currentLanguage.name }
            .map { it.getLocalizedValue(application.applicationContext) }
    }

    fun selectLocalization(selectedIndex: Int) {
        try {
            val newLocalization: Localization =
                Localization.values().filter { it.name != NotepadApplication.currentLanguage.name }[selectedIndex]
            LocaleHelper.setLocale(application.applicationContext, newLocalization)
            localizationList.value = getLocalizationList()
        } catch (e: Exception) {
            Log.d("ChangeLocalizationError", e.message.toString())
        }
    }

    /*******************
     * App backups
     *******************/

    fun saveLocalBackup() {
        if (isBackupHandling()) return
        _settingsScreenState.value = _settingsScreenState.value.copy(isLocalBackupSaving = true)
        viewModelScope.launch() {
            val backupData = BackupData()
            backupData.realmNoteList = noteRepository.getAllNotes()
            backupData.realmReminderList = reminderRepository.getAllReminders()
            backupData.userStatistics = StatisticsService(application.applicationContext).appStatistics.statisticsData
            val result = BackupService().createBackup(backupData, application.applicationContext)
            delay(1000)
            _settingsScreenState.value = _settingsScreenState.value.copy(isLocalBackupSaving = false)
            when (result) {
                BackupServiceResult.UnexpectedError -> showSnackbar(
                    application.applicationContext.getString(R.string.UnspecifiedErrorOccurred),
                    Icons.Default.Error
                )

                BackupServiceResult.WritePermissionDenied -> showSnackbar(
                    application.applicationContext.getString(R.string.CannotPerformedWithoutPermission),
                    Icons.Default.Settings
                )

                else -> showSnackbar(
                    application.applicationContext.getString(R.string.BackupCreated),
                    Icons.Default.Done
                )
            }
        }
    }

    fun uploadLocalBackup(uri: Uri) {
        if (isBackupHandling()) return
        _settingsScreenState.value = _settingsScreenState.value.copy(isLocalBackupUploading = true)
        viewModelScope.launch() {
            val result = BackupService().uploadBackup(uri, application.applicationContext)
            if (result.backupData != null) {
                noteRepository.insertOrUpdateNotes(result.backupData.realmNoteList, false)
                reminderRepository.insertOrUpdateReminders(result.backupData.realmReminderList, false)
                StatisticsService(application.applicationContext).updateStatistics(result.backupData.userStatistics)
            }
            delay(1000)
            _settingsScreenState.value = _settingsScreenState.value.copy(isLocalBackupUploading = false)
            when (result.backupServiceResult) {
                BackupServiceResult.UnexpectedError -> showSnackbar(
                    application.applicationContext.getString(R.string.UnspecifiedErrorOccurred),
                    Icons.Default.Error
                )

                BackupServiceResult.ReadPermissionDenied -> showSnackbar(
                    application.applicationContext.getString(R.string.CannotPerformedWithoutPermission),
                    Icons.Default.Settings
                )

                else -> showSnackbar(
                    application.applicationContext.getString(R.string.BackupUploaded),
                    Icons.Default.Done
                )
            }
        }
    }

    private suspend fun showSnackbar(message: String, icon: ImageVector?) {
        SnackbarHostUtil.snackbarHostState.showSnackbar(
            SnackbarVisualsCustom(
                message = message,
                icon = icon
            )
        )
    }

    fun isBackupHandling(): Boolean {
        return _settingsScreenState.value.isLocalBackupUploading ||
                _settingsScreenState.value.isGoogleDriveBackupUploading ||
                _settingsScreenState.value.isGoogleDriveBackupUpSaving ||
                _settingsScreenState.value.isLocalBackupSaving
    }

    /*******************
     * App Rate
     *******************/

    fun rateTheApp() {
        openRateScreen(application.applicationContext)
    }
}