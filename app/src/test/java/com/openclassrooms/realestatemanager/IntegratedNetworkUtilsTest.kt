package com.openclassrooms.realestatemanager

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build
import androidx.core.content.getSystemService
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import com.openclassrooms.realestatemanager.ui.MainApplication
import com.openclassrooms.realestatemanager.ui.utils.Utils.isInternetAvailable
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode
import org.robolectric.shadows.ShadowConnectivityManager
import org.robolectric.shadows.ShadowNetworkInfo


/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [Build.VERSION_CODES.M], application = Application::class)
class IntegratedNetworkUtilsTest {
    // For connectivityManager and shadowOfIsInternetAvailableNetworkInfo and the context
    private lateinit var connectivityManager: ConnectivityManager

    @Before
    fun setup() {
        connectivityManager = RuntimeEnvironment.getApplication().baseContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    @Test
    fun checkHasNoInternet() {
        val shadowNetworkCapabilities = shadowOf(connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork))
        shadowNetworkCapabilities.removeTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        shadowNetworkCapabilities.removeTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        shadowNetworkCapabilities.removeTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)
        shadowNetworkCapabilities.removeCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        assertFalse(isInternetAvailable(RuntimeEnvironment.getApplication().baseContext))
    }

    @Test
    fun checkHasInternet() {
        val shadowNetworkCapabilities = shadowOf(connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork))
        shadowNetworkCapabilities.addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        shadowNetworkCapabilities.addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        shadowNetworkCapabilities.addCapability(NetworkCapabilities.TRANSPORT_ETHERNET)
        assertTrue(isInternetAvailable(RuntimeEnvironment.getApplication().baseContext))
    }
}



