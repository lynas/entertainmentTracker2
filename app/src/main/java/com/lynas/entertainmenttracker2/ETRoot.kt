package com.lynas.entertainmenttracker2

import android.app.Application
import android.content.Context
import com.facebook.stetho.DumperPluginsProvider
import com.facebook.stetho.Stetho
import com.facebook.stetho.dumpapp.DumperPlugin
import com.uphyca.stetho_realm.RealmInspectorModulesProvider
import io.realm.Realm

/**
 * Created by lynas
 * on 5/29/2017..
 */

open class ETRoot : Application() {

    override fun onCreate() {
        super.onCreate()
        Realm.init(applicationContext)
        Stetho.initialize(
                Stetho.newInitializerBuilder(applicationContext)
                        .enableDumpapp(PP(applicationContext))
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(applicationContext))
                        .enableWebKitInspector(RealmInspectorModulesProvider.builder(applicationContext).build())
                        .build()
        )

    }


    class PP(val context: Context) : DumperPluginsProvider {
        val plugins = mutableListOf<DumperPlugin>()
        override fun get(): MutableIterable<DumperPlugin> {
            plugins += Stetho.defaultDumperPluginsProvider(context).get()
            return plugins
        }

    }

}