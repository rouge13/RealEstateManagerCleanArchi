package com.openclassrooms.realestatemanager.ui.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt


/**
 * Created by Philippe on 21/02/2018.
 */
object Utils {
    /**
     * Conversion d'un prix d'un bien immobilier (Dollars vers Euros)
     * NOTE : NE PAS SUPPRIMER, A MONTRER DURANT LA SOUTENANCE
     * @param dollars
     * @return
     */
//    @JvmStatic
//    fun convertDollarToEuro(dollars: Int): Int {
//        return (dollars * 0.812).roundToInt()
//    }

    @JvmStatic
    fun convertDollarsToEuros(dollars: Int): Int {
        return (dollars * 0.93028).roundToInt()
    }

    @JvmStatic
    fun convertEurosToDollars(euros: Int): Int {
        return (euros * 1.07473).roundToInt()
    }

    /**
     * Conversion de la date d'aujourd'hui en un format plus approprié
     * NOTE : NE PAS SUPPRIMER, A MONTRER DURANT LA SOUTENANCE
     * @return
     */
//    val todayDateFranceFormat: String
//        get() {
//            val dateFormat: DateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE)
//            return dateFormat.format(Date())
//        }

    // I want to return a SimpleDateFormat and not a String so I changed the return type of the function and then I can change the SimpleDateFormat in the fragments
    val todayDateFranceFormat: SimpleDateFormat
        get() {
            return SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE)
        }

    val todayDateUsaFormat: SimpleDateFormat
        get() {
            return SimpleDateFormat("yyyy/MM/dd", Locale.US)
        }

    /**
     * Vérification de la connexion réseau
     * NOTE : NE PAS SUPPRIMER, A MONTRER DURANT LA SOUTENANCE
     * @param context
     * @return
     */
    // Here the code is commented because it is not used in the project but was kept to show it in exam
//    fun isInternetAvailable(context: Context): Boolean {
//        val wifi = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
//        return wifi.isWifiEnabled
//    }

    fun isInternetAvailable(context: Context): Boolean {
        // register activity with the connectivity manager service
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        // Returns a Network object corresponding to the Transport specified or false if no transport is satisfied and no network.
        // Keeping VERSION_CODES.M because it is the minimum version of android that the project supports and it is the version that has the NetworkCapabilities class correctly running for my integrated test
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            // Representation the capabilities of an active network.
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
            return when {
                // Uses a Wi-Fi transport
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                // Uses a CELLULAR transport
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                // Uses ETHERNET transport or ETHERNET network
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                // else return false
                else -> false
            }
        } else {
            // if the android version is below M
            @Suppress("DEPRECATION")
            val networkInfo = connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }

    }
}